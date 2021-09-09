package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.AbilityCmpSubSystems.IAbilityCmpSubSys;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.EventComponents.DeathEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.IColoredString;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import EuroRogue.EventComponents.DamageEvent;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.StatusEffectCmps.Frozen;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.MyEntitySystem;
import EuroRogue.CmpType;
import squidpony.squidgrid.gui.gdx.SColor;

public class DamageApplicationSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public DamageApplicationSys(){super.priority=4;}

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.one(ActionEvt.class, DamageEvent.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {

        for(Entity entity:entities)
        {
            ActionEvt actionEvt = (ActionEvt) CmpMapper.getComp(CmpType.ACTION_EVT, entity);

            if(actionEvt!=null)
            {
                if(!actionEvt.isProcessed()) return;
                processActionEvt(actionEvt);
            }
            DamageEvent damageEvent = (DamageEvent) CmpMapper.getComp(CmpType.DAMAGE_EVT, entity);
            if(damageEvent!=null)
            {
                damageEvent.processed = true;
                processDamageEvt(entity, damageEvent);
            }
        }
    }

    public void genStatusEffectEvts(ActionEvt actionEvt, Entity performer, Entity target)
    {

        StatsCmp targetStats = (StatsCmp)CmpMapper.getComp(CmpType.STATS, target);
        TickerCmp tickerCmp = (TickerCmp)CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        IAbilityCmpSubSys ability = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(actionEvt.skill, performer);
        if(actionEvt.scrollID!=null)
        {
            Entity scroll = getGame().getScrollForSkill(actionEvt.skill, performer);

            ability = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(actionEvt.skill, scroll);
        }

        for(StatusEffect statusEffect: actionEvt.statusEffects.keySet())
        {

            int targetID = target.hashCode();
            SEParameters seParameters = actionEvt.statusEffects.get(statusEffect);
            if(seParameters.targetType == TargetType.SELF) targetID = performer.hashCode();
            Integer duration = ability.getStatusEffectDuration((StatsCmp) CmpMapper.getComp(CmpType.STATS, performer), statusEffect);
            if(duration!=null)
                duration = Math.round(duration*(1+(1-targetStats.getResistMultiplier(seParameters.resistanceType))));
            StatusEffectEvt statusEffectEvt = new StatusEffectEvt(tickerCmp.tick, duration, statusEffect, ability.getSkill(),  performer.hashCode(), targetID, seParameters.seRemovalType);
            Entity eventEntity = new Entity();
            eventEntity.add(statusEffectEvt);
            getEngine().addEntity(eventEntity);

        }


    }
    public void processActionEvt(ActionEvt actionEvt)
    {
        Entity targetEntity = getGame().getEntity(actionEvt.targetID);
        Entity performerEntity = getGame().getEntity(actionEvt.performerID);
        IAbilityCmpSubSys ability = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(actionEvt.skill, performerEntity);
        if(actionEvt.scrollID!=null)
        {
            Entity scrollEntity = getGame().getEntity(actionEvt.scrollID);
            ability = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(actionEvt.skill, scrollEntity);
        }

        StatsCmp targetStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, targetEntity);

        if(actionEvt.finalDmg >0)
            actionEvt.finalDmg = Math.round(actionEvt.finalDmg *(1+(1-targetStats.getResistMultiplier(ability.getDmgType(performerEntity)))));
        if(actionEvt.finalDmg >0 || actionEvt.skill.skillType == Skill.SkillType.REACTION)
        {
            Frozen frozen = (Frozen) CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, targetEntity);
            if(frozen!=null) targetEntity.remove(frozen.getClass());


        }
        targetStats.hp = targetStats.hp-actionEvt.finalDmg;

        if(targetStats.hp<=0)
        {
            if(performerEntity!=targetEntity && actionEvt.skill.skillType != Skill.SkillType.REACTION && actionEvt.baseDmg>0)
                ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateActionLogEvt(actionEvt, ability).entry);
            targetEntity.add(new DeathEvt(targetEntity.hashCode()));
            return;
        }
        if(performerEntity!=targetEntity && actionEvt.skill.skillType != Skill.SkillType.REACTION && actionEvt.baseDmg>0)
            ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateActionLogEvt(actionEvt, ability).entry);
        if(!actionEvt.statusEffects.isEmpty())
        {

            if(ability.getSkill() == Skill.MELEE_ATTACK && actionEvt.finalDmg <=0) return;

            else
            {
                genStatusEffectEvts(actionEvt, performerEntity, targetEntity);
                //((LogCmp) CmpMapper.getComp(LOG, getGame().logWindow)).logEntries.add(generateSEffectLogEvt(actionEvt).entry);
            }

        }



    }
    public void processDamageEvt(Entity entity, DamageEvent damageEvt)
    {
        getEngine().removeEntity(entity);
        Entity targetEntity = getGame().getEntity(damageEvt.targetID);

        StatsCmp targetStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, targetEntity);

        targetStats.hp = targetStats.hp-damageEvt.damage;

        ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateDamageLogEvt(damageEvt).entry);
        if(entity.getComponents().size()==0)getEngine().removeEntity(entity);
        if(targetStats.hp<=0 )
        {

            targetEntity.add(new DeathEvt(targetEntity.hashCode()));
            return;
        }

    }
    private LogEvt generateActionLogEvt (ActionEvt actionEvt, IAbilityCmpSubSys ability)
    {

        Entity performerEntity = getGame().getEntity(actionEvt.performerID);
        Entity targetEntity = getGame().getEntity(actionEvt.targetID);
        SColor performerColor = ((CharCmp) CmpMapper.getComp(CmpType.CHAR, performerEntity)).color;
        SColor targetColor = ((CharCmp) CmpMapper.getComp(CmpType.CHAR, targetEntity)).color;

        IColoredString.Impl<SColor> coloredEvtText = new IColoredString.Impl<>();
        Integer tick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        String performerName = ((NameCmp) CmpMapper.getComp(CmpType.NAME, performerEntity)).name;
        String targetName = ((NameCmp) CmpMapper.getComp(CmpType.NAME, targetEntity)).name;
        coloredEvtText.append(tick.toString(), SColor.WHITE);

        coloredEvtText.append(" "+performerName+"'s ", performerColor);
        coloredEvtText.append(actionEvt.skill.name, actionEvt.skill.school.color);
        coloredEvtText.append(" hits ", SColor.SILVER_GREY);
        coloredEvtText.append(targetName, targetColor);
        coloredEvtText.append(" ", SColor.SILVER_GREY);
        coloredEvtText.append(actionEvt.finalDmg.toString()+"/"+actionEvt.baseDmg.toString()+" ", SColor.WHITE);
        coloredEvtText.append(ability.getDmgType(performerEntity).toString(), actionEvt.skill.school.color);
        coloredEvtText.append(" damage!", SColor.WHITE);
        return new LogEvt(tick, coloredEvtText);
    }
    private LogEvt generateDamageLogEvt (DamageEvent damageEvt)
    {

        Entity targetEntity = getGame().getEntity(damageEvt.targetID);
        SColor targetColor = ((CharCmp) CmpMapper.getComp(CmpType.CHAR, targetEntity)).color;
        IColoredString.Impl<SColor> coloredEvtText = new IColoredString.Impl<>();
        Integer tick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        String targetName = ((NameCmp) CmpMapper.getComp(CmpType.NAME, targetEntity)).name;
        coloredEvtText.append(tick.toString(), SColor.WHITE);

        coloredEvtText.append(" "+targetName+" is ", targetColor);
        coloredEvtText.append(damageEvt.statusEffect.name+": ", SColor.WHITE);
        coloredEvtText.append(Integer.toString(damageEvt.damage)+" damage", SColor.WHITE);

        return new LogEvt(tick, coloredEvtText);
    }



}
