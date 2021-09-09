package EuroRogue.EventComponents;

import EuroRogue.ItemEvtType;

public class ItemEvt implements IEventComponent
{
    public boolean processed = false;
    public Integer itemID;
    public Integer actorID;
    public Integer otherActorID = null;
    public ItemEvtType type;

    public ItemEvt (Integer itemID, Integer actorID, ItemEvtType type)
    {
        this.itemID = itemID;
        this.actorID = actorID;
        this.type = type;
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
