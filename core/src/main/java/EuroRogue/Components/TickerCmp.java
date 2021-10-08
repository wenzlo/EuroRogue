package EuroRogue.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.ScheduledEvt;

public class TickerCmp implements Component
{
    public Integer tick=0;
    public ArrayList<ScheduledEvt> actionQueue = new ArrayList();
    public ArrayList<StatusEffectEvt> statusEffectEvtQueue = new ArrayList();

    public ArrayList<ScheduledEvt> getScheduledActions(Entity entity)
    {
        ArrayList<ScheduledEvt> entityActions = new ArrayList();
        for(ScheduledEvt action:actionQueue)
        {
            if(action.entityID==entity.hashCode()) entityActions.add(action);
        }
        return entityActions;
    }
    //TODO add log entries for interrupted events
    public void interrupt(Entity actor)
    {
        ArrayList<ScheduledEvt> actorsEvents = new ArrayList<>();
        for(ScheduledEvt scheduledEvt : actionQueue)
        {
            if(scheduledEvt.entityID==actor.hashCode()) actorsEvents.add(scheduledEvt);
        }
        actionQueue.removeAll(actorsEvents);
    }
}
