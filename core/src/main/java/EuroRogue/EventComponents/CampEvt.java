package EuroRogue.EventComponents;

import java.util.ArrayList;

public class CampEvt implements IEventComponent
{
    public boolean processed = false;
    public int actorID;
    public ArrayList<Integer> equippedIDs;

    public CampEvt(int actorID, ArrayList<Integer> equippedIDs)
    {
        this.actorID=actorID;
        this.equippedIDs = equippedIDs;
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
