package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.TickerCmp;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.CodexEvt;
import EuroRogue.EventComponents.DayNightCycleEvt;
import EuroRogue.EventComponents.DeathEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LevelEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.EventComponents.RestEvt;
import EuroRogue.EventComponents.StatEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.GameState;
import EuroRogue.MyEntitySystem;
import EuroRogue.ScheduledEvt;
import EuroRogue.StatusEffectCmps.Bleeding;
import EuroRogue.StatusEffectCmps.Burning;
import EuroRogue.StatusEffectCmps.Calescent;
import EuroRogue.StatusEffectCmps.Chilled;
import EuroRogue.StatusEffectCmps.Frozen;
import EuroRogue.StatusEffectCmps.Hungry;
import EuroRogue.StatusEffectCmps.Staggered;
import EuroRogue.StatusEffectCmps.Starving;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.StatusEffectCmp;

public class TickerSys extends MyEntitySystem
{
    private ImmutableArray<Entity> tickers;
    private ImmutableArray<Entity> entitiesWithSEs;

    public TickerSys()
    {
        super.priority=101;
    }

    /**
     * Initialises the EntitySystem with the priority specified.
     *
     * @param priority The priority to execute this system with (lower means higher priority).
     */


    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {

        entitiesWithSEs = engine.getEntitiesFor(Family.one(Hungry.class, Starving.class, Bleeding.class, Burning.class, Calescent.class, Chilled.class, Frozen.class, Staggered.class).get());

    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        //if(((WindowCmp) CmpMapper.getComp(CmpType.WINDOW,game.dungeonWindow)).display.hasActiveAnimations()) return;
        if(getGame().gameState!= GameState.PLAYING) return;
        ImmutableArray<Entity> eventsFamily = getEngine().getEntitiesFor(Family.one(DeathEvt.class, LevelEvt.class, StatEvt.class, GameStateEvt.class, ActionEvt.class, CodexEvt.class, MoveEvt.class, ItemEvt.class,
                RestEvt.class, StatusEffectEvt.class, AnimateGlyphEvt.class, LevelEvt.class).get());
        if(eventsFamily.size()>0) return;
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);

        if(ticker.getScheduledActions(getGame().getFocus()).isEmpty()) return;
        Collections.sort(ticker.actionQueue, new SortActionsByTick());
        Integer nextActionTick = ticker.tick;
        if(!ticker.actionQueue.isEmpty()) nextActionTick = ticker.actionQueue.get(0).tick;

        while(ticker.tick<nextActionTick && !removeStatusEffects(ticker.tick) && !addStatusEffectEvents(ticker))
        {
            ticker.tick++;
            int interval = 250;
            if(getGame().depth==1) interval = 50;
            if(ticker.tick%interval == 0)
            {

                DayNightCycleEvt dayNightCycleEvt = new DayNightCycleEvt();
                getGame().currentLevel.add(dayNightCycleEvt);

            }
        }

        ArrayList<ScheduledEvt> eventsToProc = new ArrayList();
        for(ScheduledEvt scheduledEvt :ticker.actionQueue)
        {
            if(scheduledEvt.tick<=ticker.tick) eventsToProc.add(scheduledEvt);
            else break;
        }
        ticker.actionQueue.removeAll(eventsToProc);
        for(ScheduledEvt scheduledEvt :eventsToProc)
        {
            if(scheduledEvt.eventComponent.getClass() == CampEvt.class)
            {
                Entity actor = getGame().getEntity(scheduledEvt.entityID);
                actor.add(scheduledEvt.eventComponent);

            } else {

                Entity eventEntity = new Entity();
                eventEntity.add(scheduledEvt.eventComponent);
                getEngine().addEntity(eventEntity);
            }

        }
    }
    public boolean removeStatusEffects(int tick)
    {
        boolean effectsProcessed = false;
        for(Entity entity:entitiesWithSEs)
        {
            for(StatusEffect statusEffect : getGame().getStatusEffects(entity))
            {
                StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, entity);
                if(statusEffectCmp==null || statusEffectCmp.lastTick==null) continue;
                if(statusEffectCmp.lastTick<=tick)
                {
                    entity.remove(statusEffect.cls);
                    effectsProcessed=true;

                }
            }
        }
        return effectsProcessed;
    }
    public boolean addStatusEffectEvents(TickerCmp ticker)
    {
        boolean effectsProcessed = false;
        ArrayList<StatusEffectEvt> eventsTorRemove = new ArrayList<>();
        Collections.sort(ticker.statusEffectEvtQueue, new SortStatusEffectEvtByTick());
        for(StatusEffectEvt statusEffectEvt:ticker.statusEffectEvtQueue)
        {
            if(statusEffectEvt.tick<=ticker.tick)
            {
                Entity eventEntity = new Entity();

                eventEntity.add(statusEffectEvt);
                getEngine().addEntity(eventEntity);


                eventsTorRemove.add(statusEffectEvt);

            }
            else break;
        }
        effectsProcessed = !eventsTorRemove.isEmpty();
        ticker.statusEffectEvtQueue.removeAll(eventsTorRemove);
        return effectsProcessed;
    }


    public ActionEvt getCurrentAction(Entity entity)
    {
        ActionEvt actionEvt = (ActionEvt) CmpMapper.getComp(CmpType.ACTION_EVT, entity);
        if(actionEvt!=null){
            if(actionEvt.processed) actionEvt = null;
        }
        return  actionEvt;
    }

    public ArrayList<ScheduledEvt> removeEntityActions (Entity entity)
    {
        tickers = getEngine().getEntitiesFor(Family.all(TickerCmp.class).get());
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, tickers.get(0));
        ArrayList<ScheduledEvt> entityActions = ticker.getScheduledActions(entity);
        ticker.actionQueue.removeAll(entityActions);
        return entityActions;
    }
    private ScheduledEvt getNextScheduledEvt(Entity entity)
    {
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER,tickers.get(0));
        ArrayList<ScheduledEvt> scheduledEvts = ticker.getScheduledActions(entity);
        if(!ticker.getScheduledActions(entity).isEmpty()) return scheduledEvts.get(0);
        else return  null;
    }

    public static class SortActionsByTick implements Comparator<ScheduledEvt> {


        public SortActionsByTick(){

        }

        @Override
        public int compare(ScheduledEvt action1, ScheduledEvt action2) {
            int t1 = action1.tick;
            int t2 = action2.tick;
            return Double.compare(t1,t2);
        }
    }
    public static class SortStatusEffectEvtByTick implements Comparator<StatusEffectEvt> {


        public SortStatusEffectEvtByTick(){

        }

        @Override
        public int compare(StatusEffectEvt action1, StatusEffectEvt action2) {
            int t1 = action1.tick;
            int t2 = action2.tick;
            return Double.compare(t1,t2);
        }
    }
}
