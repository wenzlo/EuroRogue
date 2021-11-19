package EuroRogue.EventComponents;

public class StorageEvt implements IEventComponent
{
    public boolean processed = false;
    public String buildName;
    public boolean store;

    public StorageEvt(String buildName, boolean store)
    {
        this.buildName = buildName;
        this.store = store;
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
