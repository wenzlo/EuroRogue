package EuroRogue.Components;

import com.badlogic.ashley.core.Component;


import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

public class PositionCmp implements Component
{
    public Coord coord;
    public Direction orientation;

    public PositionCmp(){}
    public PositionCmp(Coord coord)
    {
        this.coord=coord;
        this.orientation = Direction.UP;
    }

}
