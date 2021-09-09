package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import squidpony.squidmath.GreasedRegion;

public class FOVCmp implements Component
{
    public GreasedRegion visible;
    public double[][] los;
    public GreasedRegion seen;
    public double[][] nightVision;


    public FOVCmp(){}

    public FOVCmp (int mapWidth, int mapHeight)
    {
        this.visible = new GreasedRegion();
        this.los = new double[mapWidth][mapHeight];
        this.nightVision = new double[mapWidth][mapHeight];
        this.seen = new GreasedRegion();
    }


}
