package EuroRogue.EventComponents;

import EuroRogue.School;

public class ShrineEvt implements IEventComponent
{
    public boolean processed = false;
    public int actorID;
    public int shrineID;
    public School school;

    public ShrineEvt(int actorID, int shrineID, School school)
    {
        this.actorID=actorID;
        this.shrineID = shrineID;
        this.school = school;
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
