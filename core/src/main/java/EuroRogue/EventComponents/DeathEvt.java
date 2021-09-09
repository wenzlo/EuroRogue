package EuroRogue.EventComponents;

public class DeathEvt implements IEventComponent
{
    public boolean processed = false;
    public int entityID;

    public DeathEvt(){}

    public DeathEvt(int entityID)
    {
        this.entityID=entityID;
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
