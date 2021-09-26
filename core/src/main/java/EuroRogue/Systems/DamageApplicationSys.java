package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.AbilityCmpSubSystems.Ability;
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
                Entity performerEntity = getGame().getEntity(actionEvt.performerID);
                if(!actionEvt.isProcessed()) return;
                processActionEvt(actionEvt, performerEntity);
                genStatusEffectEvts(actionEvt, performerEntity);
            }
            DamageEvent damageEvent = (DamageEvent) CmpMapper.getComp(CmpType.DAMAGE_EVT, entity);
            if(damageEvent!=null)
            {
                damageEvent.processed = true;
                processDamageEvt(entity, damageEvent);
            }
        }
    }
    public void genStatusEffectEvts(ActionEvt actionEvt, Entity performer)
    {
        TickerCmp tickerCmp = (TickerCmp)CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        Ability ability = (Ability) CmpMapper.getAbilityComp(actionEvt.skill, performer);
        if(actionEvt.scrollID!=null)
        {
            Entity scroll = getGame().getScrollForSkill(actionEvt.skill, performer);
            ability = (Ability) CmpMapper.getAbilityComp(actionEvt.skill, scroll);
        }

        for(StatusEffect statusEffect: actionEvt.statusEffects.keySet())
        {
            for(Integer targetID : actionEvt.targetsDmg.keySet())
            {
                SEParameters seParameters = actionEvt.statusEffects.get(statusEffect);
                Integer duration = ability.getStatusEffectDuration((StatsCmp) CmpMapper.getComp(CmpType.STATS, performer), statusEffect);

                if(duration!=null && ability.getDamage(performer)!=0)
                {
                    duration = Math.round(duration * (actionEvt.targetsDmg.get(targetID) / (float)ability.getDamage(performer)));
                }
                StatusEffectEvt statusEffectEvt = new StatusEffectEvt(tickerCmp.tick, duration, statusEffect, ability.getSkill(),  performer.hashCode(), targetID, seParameters.seRemovalType);
                Entity eventEntity = new Entity();
                eventEntity.add(statusEffectEvt);
                getEngine().addEntity(eventEntity);
            }
        }
    }
    public void processActionEvt(ActionEvt actionEvt, Entity performerEntity)
    {
        Ability ability = (Ability) CmpMapper.getAbilityComp(actionEvt.skill, performerEntity);
        if(actionEvt.scrollID!=null)
        {
            Entity scrollEntity = getGame().getEntity(actionEvt.scrollID);
            ability = (Ability) CmpMapper.getAbilityComp(actionEvt.skill, scrollEntity);
        }

        for(Integer targetID : actionEvt.targetsDmg.keySet())
        {
            Entity targetEntity = getGame().getEntity(targetID);
            if(targetEntity==null) continue;

            StatsCmp targetStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, targetEntity);

            if(actionEvt.targetsDmg.get(targetID) >0)
            {
                int dmg = Math.round(actionEvt.targetsDmg.get(targetID) *(1+(1-targetStats.getResistMultiplier(ability.getDmgType(performerEntity)))));
                actionEvt.targetsDmg.replace(targetID, dmg);
            }

            if(actionEvt.targetsDmg.get(targetID) >0 || actionEvt.skill.skillType == Skill.SkillType.REACTION)
            {
                Frozen frozen = (Frozen) CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, targetEntity);
                if(frozen!=null) targetEntity.remove(frozen.getClass());


            }
            targetStats.hp = targetStats.hp-actionEvt.targetsDmg.get(targetID);

            if(targetStats.hp<=0)
            {
                if(performerEntity!=targetEntity && actionEvt.skill.skillType != Skill.SkillType.REACTION && ability.getDamage(performerEntity)>0)
                    ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateActionLogEvt(targetEntity, actionEvt, ability).entry);
                targetEntity.add(new DeathEvt(targetEntity.hashCode()));
                return;
            }
            if(actionEvt.skill.skillType != Skill.SkillType.REACTION && ability.getDamage(performerEntity)>0)
                ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateActionLogEvt(targetEntity, actionEvt, ability).entry);
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
    private LogEvt generateActionLogEvt (Entity targetEntity, ActionEvt actionEvt, Ability ability)
    {

        Entity performerEntity = getGame().getEntity(actionEvt.performerID);
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
        coloredEvtText.append(actionEvt.targetsDmg.get(targetEntity.hashCode()).toString()+"/"+ability.getDamage(performerEntity)+" ", SColor.WHITE);
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
