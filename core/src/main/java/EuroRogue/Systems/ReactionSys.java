package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.HashMap;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.IColoredString;
import EuroRogue.MyEntitySystem;
import squidpony.squidgrid.gui.gdx.SColor;

public class ReactionSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    //private Skill[] moveReactions = new Skill[]{Skill.OPPORTUNITY};
    private Skill[] moveReactions = new Skill[]{null};

    public ReactionSys ()
    {
        super.priority = 3;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.one(ActionEvt.class).get());
    }
    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        for (Entity entity:entities)
        {
            MoveEvt moveEvt = (MoveEvt) CmpMapper.getComp(CmpType.MOVE_EVT, entity);

            ActionEvt actionEvt = (ActionEvt) CmpMapper.getComp(CmpType.ACTION_EVT, entity);


            if(actionEvt!=null)
            {

                procActionEvt(actionEvt);
            }
        }
    }

    private void procActionEvt (ActionEvt actionEvt)
    {
        for(Integer targetID : actionEvt.targetsDmg.keySet())
        {

            Entity reactor = getGame().getEntity(targetID);
            if(reactor==null) return;
            Entity actor = getGame().getEntity(actionEvt.performerID);

            Ability actionAbility = (Ability) CmpMapper.getAbilityComp(actionEvt.skill, actor);
            if(actionEvt.scrollID!=null) actionAbility = (Ability) CmpMapper.getAbilityComp( actionEvt.skill, getGame().getEntity(actionEvt.scrollID));

            getGame().updateAbilities(reactor);


            Ability reactionAbility = null;

            for (Skill skill : actionAbility.getReactions()) {
                Ability possibleReactionAbility = (Ability)CmpMapper.getAbilityComp(skill, reactor);

                if (possibleReactionAbility!=null)
                    if(possibleReactionAbility.isAvailable())
                    {
                        reactionAbility = possibleReactionAbility;
                        break;
                    }
            }
            if (reactionAbility == null)
            {
                for(Entity scrollEntity : getGame().getAvailableScrolls(reactor))
                {
                    ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, scrollEntity);
                    if(actionAbility.getReactions().contains(scrollCmp.skill))
                    {
                        reactionAbility = (Ability) CmpMapper.getAbilityComp(scrollCmp.skill, scrollEntity);
                        break;
                    }
                }
            }
            if (reactionAbility == null) return;
            StatsCmp reactorStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, reactor);
            int dmg = Math.round(actionAbility.getDamage(actor) - actionAbility.getDamage(actor) * reactionAbility.getDmgReduction(reactorStats));
            actionEvt.targetsDmg.replace(targetID, dmg);

            if (!reactionAbility.scroll()) {

                if (reactionAbility.getActive())
                {
                    HashMap<Integer,Integer> targets = new HashMap<>();
                    targets.put(actor.hashCode(),reactionAbility.getDamage(reactor));

                    ActionEvt reaction = new ActionEvt(reactor.hashCode(), null, reactionAbility.getSkill(), targets, reactionAbility.getStatusEffects());
                    Entity eventEntity = new Entity();
                    eventEntity.add(reaction);
                    getEngine().addEntity(eventEntity);
                }
            } else {

                Entity scrollEntity = getGame().getScrollForSkill(reactionAbility.getSkill(), reactor);
                HashMap<Integer,Integer> targets = new HashMap<>();
                targets.put(actor.hashCode(),reactionAbility.getDamage(reactor));
                ActionEvt reaction = new ActionEvt(reactor.hashCode(), scrollEntity.hashCode(), reactionAbility.getSkill(), targets, reactionAbility.getStatusEffects());

                Entity eventEntity = new Entity();
                eventEntity.add(reaction);
                getEngine().addEntity(eventEntity);
            }
            ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateReactionLogEvt(actionEvt, reactionAbility, actor, reactor).entry);
        }
    }


    private LogEvt generateReactionLogEvt (ActionEvt actionEvt, Ability reactionAbility, Entity actor, Entity reactor) {

        SColor performerColor = ((CharCmp) CmpMapper.getComp(CmpType.CHAR, actor)).color;
        SColor targetColor = ((CharCmp) CmpMapper.getComp(CmpType.CHAR, reactor)).color;
        IColoredString.Impl<SColor> coloredEvtText = new IColoredString.Impl<>();
        Integer tick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        String performerName = ((NameCmp) CmpMapper.getComp(CmpType.NAME, actor)).name;
        String targetName = ((NameCmp) CmpMapper.getComp(CmpType.NAME, reactor)).name;
        coloredEvtText.append(tick.toString(), SColor.WHITE);

        coloredEvtText.append(" " + targetName, targetColor);
        coloredEvtText.append(" reacts to ", SColor.SILVER_GREY);
        coloredEvtText.append(performerName + "'s ", performerColor);
        coloredEvtText.append(actionEvt.skill.name, actionEvt.skill.school.color);
        coloredEvtText.append(" with ", SColor.SILVER_GREY);
        coloredEvtText.append(reactionAbility.getSkill().name, reactionAbility.getSkill().school.color);
        return new LogEvt(tick, coloredEvtText);
    }
}
