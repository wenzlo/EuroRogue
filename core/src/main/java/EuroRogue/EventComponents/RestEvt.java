package EuroRogue.EventComponents;

public class RestEvt implements IEventComponent
{
    public boolean processed = false;
    public int actorID;

    public RestEvt(int actorID)
    {
        this.actorID=actorID;
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
