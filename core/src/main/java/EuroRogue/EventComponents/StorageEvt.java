package EuroRogue.EventComponents;

public class StorageEvt implements IEventComponent
{
    public boolean processed = false;
    public String buildName;
    public StorageEvtType storageEvtType;

    public StorageEvt(String buildName, StorageEvtType storageEvtType)
    {
        this.buildName = buildName;
        this.storageEvtType = storageEvtType;
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
