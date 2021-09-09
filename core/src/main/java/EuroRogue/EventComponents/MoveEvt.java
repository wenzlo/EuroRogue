package EuroRogue.EventComponents;

import squidpony.squidgrid.Direction;

public class MoveEvt implements IEventComponent
{
    public Integer entityID;
    public Direction direction;
    public int magnitude;
    public boolean processed = false;


    public MoveEvt(){}

    public MoveEvt(Integer entityID, Direction direction, int magnitude)
    {
        this.entityID = entityID;
        this.direction = direction;
        this.magnitude = magnitude;
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
