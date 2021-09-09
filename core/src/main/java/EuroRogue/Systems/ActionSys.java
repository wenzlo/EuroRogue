package EuroRogue.Systems;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.AbilityCmpSubSystems.IAbilityCmpSubSys;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.DamageType;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.DamageEvent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.IColoredString;

import EuroRogue.ItemEvtType;
import EuroRogue.LightHandler;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.Bleeding;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.CmpType;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;

public class ActionSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public ActionSys()
    {
        super.priority = 2;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(ActionEvt.class).get());
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
            ActionEvt action = (ActionEvt) CmpMapper.getComp(CmpType.ACTION_EVT, entity);
            Entity performerEntity = getGame().getEntity(action.performerID);
            Entity targetEntity = getGame().getEntity(action.targetID);


            if(action.isProcessed()) return;
            action.processed=true;

            IAbilityCmpSubSys abilityCmp = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(action.skill, performerEntity);



            if(action.scrollID!=null)
            {
                Entity scrollEntity = getGame().getEntity(action.scrollID);

                abilityCmp = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(action.skill, scrollEntity);
            }

            getGame().updateAbilities(performerEntity);
            if(abilityCmp==null) System.out.println("Ability comp = Null");
            if(!abilityCmp.isAvailable() && abilityCmp.getSkill().skillType != Skill.SkillType.REACTION || targetEntity==null || performerEntity==null)
            {
                LogEvt logEvt = generateCancelLogEvt(action, performerEntity);
                ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(logEvt.entry);
                entity.remove(action.getClass());
                TextCellFactory.Glyph glyph = abilityCmp.getGlyph();
                if(glyph!=null)
                {
                    WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
                    windowCmp.lightingHandler.removeLightByGlyph(glyph);
                    windowCmp.display.glyphs.remove(abilityCmp.getGlyph());
                }


            } else {

                if(action.scrollID==null) ((ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, performerEntity)).spendMana(abilityCmp.getSkill().castingCost);
                else
                {
                    Entity scrollEntity = getGame().getEntity(action.scrollID);
                    ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, scrollEntity);
                    scrollCmp.consumed= true;
                    Entity eventEntity = new Entity();
                    ItemEvt itemEvt = new ItemEvt(action.scrollID, action.performerID, ItemEvtType.CONSUME);
                    eventEntity.add(itemEvt);
                    getEngine().addEntity(eventEntity);
                }
                //((LogCmp) CmpMapper.getComp(LOG, getGame().logWindow)).logEntries.add(generateActionLogEvt(action, performerEntity).entry);
                MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).display;
                LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;
                if(action.skill.skillType== Skill.SkillType.REACTION) abilityCmp.spawnGlyph(display, lightHandler);
                AnimateGlyphEvt animateGlyphEvt = abilityCmp.genAnimateGlyphEvt(performerEntity, targetEntity, action, display);
                ItemEvt itemEvt = abilityCmp.genItemEvent(performerEntity, targetEntity);
                if (animateGlyphEvt != null) performerEntity.add(animateGlyphEvt);
                if(itemEvt != null) performerEntity.add(itemEvt);
                Bleeding bleeding = (Bleeding) CmpMapper.getStatusEffectComp(StatusEffect.BLEEDING, performerEntity);
                if(bleeding!=null)
                {
                    Entity damageEvtEntity = new Entity();
                    damageEvtEntity.add(new DamageEvent(performerEntity.hashCode(), bleeding.damagePerMove, DamageType.NONE, StatusEffect.BLEEDING));
                    getEngine().addEntity(damageEvtEntity);
                }
            }
        }
    }

    private LogEvt generateActionLogEvt (ActionEvt actionEvt, Entity entity)
    {
        IColoredString.Impl<SColor> coloredEvtText = new IColoredString.Impl<>();
        Integer tick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        String name = ((NameCmp) CmpMapper.getComp(CmpType.NAME, entity)).name;
        coloredEvtText.append(tick.toString(), SColor.WHITE);

        coloredEvtText.append(" "+name, SColor.BROWN);
        coloredEvtText.append(" performs", SColor.WHITE);
        coloredEvtText.append(" "+actionEvt.skill.name, actionEvt.skill.school.color);
        return new LogEvt(tick, coloredEvtText);
    }
    private LogEvt generateCancelLogEvt (ActionEvt actionEvt, Entity entity)
    {
        //IAbilityCmpSubSys abilityCmp = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(actionEvt.skill, entity);
        IColoredString.Impl<SColor> coloredEvtText = new IColoredString.Impl<>();
        Integer tick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        coloredEvtText.append(tick.toString(), SColor.WHITE);
        String name = ((NameCmp) CmpMapper.getComp(CmpType.NAME, getGame().getEntity(actionEvt.performerID))).name;
        coloredEvtText.append(" "+name+"'s", SColor.WHITE);

        String abilityString = actionEvt.skill.name;
        coloredEvtText.append(" "+abilityString, actionEvt.skill.school.color);
        coloredEvtText.append(" cancelled", SColor.WHITE);

        return new LogEvt(tick, coloredEvtText);
    }




}
