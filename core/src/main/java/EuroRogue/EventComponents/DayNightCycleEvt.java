package EuroRogue.EventComponents;

public class DayNightCycleEvt implements IEventComponent
{
    public boolean processed = false;
    public int entityID;

    public DayNightCycleEvt(){}

    @Override
    public boolean isProcessed()
    {
        return processed;
    }

    @Override
    public void setProcessed(boolean bool) {
        processed = bool;
    }



}
