package EuroRogue.EventComponents;

public class LevelEvt implements IEventComponent
{
    public boolean processed = false;
    @Override
    public boolean isProcessed() {
        return processed;
    }

    @Override
    public void setProcessed(boolean bool) {
        processed = bool;
    }

}
