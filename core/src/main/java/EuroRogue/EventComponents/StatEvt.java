package EuroRogue.EventComponents;

import java.util.HashMap;

import EuroRogue.StatType;

public class StatEvt implements IEventComponent
{
    public boolean processed = false;
    public Integer actorID;
    public HashMap<StatType, Integer> statChanges;


    public StatEvt(Integer actorID, StatType stat, Integer change)
    {
        this.actorID = actorID;
        this.statChanges = new HashMap<>();
        statChanges.put(stat,change);
    }

    @Override
    public boolean isProcessed() {
        return processed;
    }

    @Override
    public void setProcessed(boolean bool) {
        processed = bool;
    }

}
