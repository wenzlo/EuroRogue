package EuroRogue;

import com.badlogic.ashley.core.Component;

import EuroRogue.EventComponents.IEventComponent;

public class ScheduledEvt implements Component {
    public Integer tick;
    public Integer entityID;
    public IEventComponent eventComponent;

    public ScheduledEvt (){}
    public ScheduledEvt(Integer tick, Integer entityID, IEventComponent eventComponent)
    {
        this.tick = tick;
        this.entityID = entityID;
        this.eventComponent = eventComponent;
    }
}
