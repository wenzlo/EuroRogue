package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.Set;

import EuroRogue.AbilityCmpSubSystems.IAbilityCmpSubSys;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.CmpType;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.IColoredString;
import EuroRogue.MyEntitySystem;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

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

            /*if(moveEvt!=null)
                procMoveEvt(moveEvt);*/

        }
    }

    private void procActionEvt (ActionEvt actionEvt)
    {

        Entity reactor = getGame().getEntity(actionEvt.targetID);
        StatsCmp reactorStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, reactor);
        if(reactor==null) return;
        Entity actor = getGame().getEntity(actionEvt.performerID);
        IAbilityCmpSubSys actionAbility = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(actionEvt.skill, actor);
        if(actionEvt.scrollID!=null) actionAbility = (IAbilityCmpSubSys) CmpMapper.getAbilityComp( actionEvt.skill, getGame().getEntity(actionEvt.scrollID));

        getGame().updateAbilities(reactor);


        IAbilityCmpSubSys reactionAbility = null;

        for (Skill skill : actionAbility.getReactions()) {
            IAbilityCmpSubSys possibleReactionAbility = (IAbilityCmpSubSys)CmpMapper.getAbilityComp(skill, reactor);

            if (possibleReactionAbility!=null)
                if(possibleReactionAbility.isAvailable()) reactionAbility = possibleReactionAbility;

        }
        if (reactionAbility == null) return;
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, reactor);
        actionEvt.finalDmg = Math.round(actionEvt.baseDmg - actionEvt.baseDmg * reactionAbility.getDmgReduction(statsCmp));

        if (!reactionAbility.scroll()) {

            if (reactionAbility.getActive())
            {

                ActionEvt reaction = new ActionEvt(reactor.hashCode(), null, reactionAbility.getSkill(), actor.hashCode(), reactionAbility.getDamage(), reactionAbility.getStatusEffects());
                Entity eventEntity = new Entity();
                eventEntity.add(reaction);
                getEngine().addEntity(eventEntity);
            }
        } else {

            Entity scrollEntity = getGame().getScrollForSkill(reactionAbility.getSkill(), reactor);
            ActionEvt reaction = new ActionEvt(reactor.hashCode(), scrollEntity.hashCode(), reactionAbility.getSkill(), actor.hashCode(), reactionAbility.getDamage(), reactionAbility.getStatusEffects());

            Entity eventEntity = new Entity();
            eventEntity.add(reaction);
            getEngine().addEntity(eventEntity);
        }
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateReactionLogEvt(actionEvt, reactionAbility).entry);
    }
    private void procMoveEvt (MoveEvt moveEvt)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        Entity actor = getGame().getEntity(moveEvt.entityID);
        Coord actorPos = ((PositionCmp)CmpMapper.getComp(CmpType.POSITION, actor)).coord;

        Set<Integer> reactorIDs = levelCmp.getAdjActorIDs(actorPos);
        int deltaX = moveEvt.direction.deltaX;
        int deltaY = moveEvt.direction.deltaY;
        Coord newPos = Coord.get(actorPos.x+deltaX, actorPos.y+deltaY);
        Set<Integer> postMoveIDs = levelCmp.getAdjActorIDs(newPos);
        postMoveIDs.remove(actor.hashCode());
        reactorIDs.removeAll(postMoveIDs);

        for(Integer reactorID:reactorIDs)
        {
            if(reactorID == null) continue;
            if(reactorID.equals(getGame().getFocus().hashCode())) continue;
            Entity reactor = getGame().getEntity(levelCmp.actors.get(reactorID));
            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, reactor);


            getGame().updateAbilities(reactor);
            for(Skill skill:moveReactions)
            {
                IAbilityCmpSubSys reactionAbility = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(skill, reactor);
                if(getGame().getScrollForSkill(skill, reactor)!=null ) reactionAbility = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(skill, getGame().getScrollForSkill(skill, reactor));
                if(reactionAbility!=null && reactionAbility.isAvailable())
                {
                    if (!reactionAbility.scroll()) {

                        if (reactionAbility.getActive()) {

                            ActionEvt reaction = new ActionEvt(reactor.hashCode(), null, reactionAbility.getSkill(), actor.hashCode(), reactionAbility.getDamage(), reactionAbility.getStatusEffects());

                            Entity eventEntity = new Entity();
                            eventEntity.add(reaction);
                            getEngine().addEntity(eventEntity);
                        }
                    } else {

                        Entity scrollEntity = getGame().getScrollForSkill(reactionAbility.getSkill(), reactor);
                        ActionEvt reaction = new ActionEvt(reactor.hashCode(), scrollEntity.hashCode(), reactionAbility.getSkill(), actor.hashCode(), reactionAbility.getDamage(), reactionAbility.getStatusEffects());

                        Entity eventEntity = new Entity();
                        eventEntity.add(reaction);
                        getEngine().addEntity(eventEntity);
                    }
                }
            }
        }
    }

    private LogEvt generateReactionLogEvt (ActionEvt actionEvt, IAbilityCmpSubSys reactionAbility) {
        Entity performerEntity = getGame().getEntity(actionEvt.performerID);
        Entity targetEntity = getGame().getEntity(actionEvt.targetID);
        SColor performerColor = ((CharCmp) CmpMapper.getComp(CmpType.CHAR, performerEntity)).color;
        SColor targetColor = ((CharCmp) CmpMapper.getComp(CmpType.CHAR, targetEntity)).color;
        IColoredString.Impl<SColor> coloredEvtText = new IColoredString.Impl<>();
        Integer tick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        String performerName = ((NameCmp) CmpMapper.getComp(CmpType.NAME, performerEntity)).name;
        String targetName = ((NameCmp) CmpMapper.getComp(CmpType.NAME, targetEntity)).name;
        coloredEvtText.append(tick.toString(), SColor.WHITE);

        coloredEvtText.append(" " + targetName + " ", targetColor);
        coloredEvtText.append(" reacts to ", SColor.SILVER_GREY);
        coloredEvtText.append(" " + performerName + "'s ", performerColor);
        coloredEvtText.append(actionEvt.skill.name, actionEvt.skill.school.color);
        coloredEvtText.append(" with ", SColor.SILVER_GREY);
        coloredEvtText.append(reactionAbility.getSkill().name, reactionAbility.getSkill().school.color);
        return new LogEvt(tick, coloredEvtText);
    }
}
