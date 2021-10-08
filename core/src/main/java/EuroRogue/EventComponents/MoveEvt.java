package EuroRogue.EventComponents;

import squidpony.squidmath.Coord;

public class MoveEvt implements IEventComponent
{
    public Integer entityID;
    public Coord destination;
    public boolean processed = false;
    public float animSpeed = 1f;


    public MoveEvt(){}

    public MoveEvt(Integer entityID, Coord destination )
    {
        this.entityID = entityID;
        this.destination = destination;

    }
    public MoveEvt(Integer entityID, Coord destination, float animSpeed )
    {
        this.entityID = entityID;
        this.destination = destination;
        this.animSpeed = animSpeed;

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
