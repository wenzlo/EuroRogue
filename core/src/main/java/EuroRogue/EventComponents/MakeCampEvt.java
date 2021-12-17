package EuroRogue.EventComponents;


public class MakeCampEvt implements IEventComponent
{
    public boolean processed = false;
    public int actorID;
    public int tick;

    public MakeCampEvt(int actorID, int tick)
    {
        this.actorID=actorID;
        this.tick = tick;
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
