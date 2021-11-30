package EuroRogue.EventComponents;

public class DeathEvt implements IEventComponent
{
    public boolean processed = false;
    public int delay = 180;
    public float tod;
    public int entityID;

    public DeathEvt(){}

    public DeathEvt(int entityID)
    {
        this.entityID=entityID;
        this.tod = (int)System.currentTimeMillis();
    }


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
