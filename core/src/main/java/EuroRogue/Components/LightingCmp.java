package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.MyDungeonUtility;
import squidpony.squidmath.GreasedRegion;

public class LightingCmp implements Component
{
    public double[][] resistance3x3;
    public float[][] bgLighting;
    public double[][] fgLightLevel;
    public float[][] fgLighting;
    public double[][] focusNightVision3x3;
    public GreasedRegion focusSeen3x3;

    public LightingCmp (char[][] map)
    {
        this.resistance3x3 = MyDungeonUtility.generateResistances3x3(map);
        this.bgLighting = new float[map[0].length][map.length];
        this.fgLighting = new float[map[0].length][map.length];
        this.fgLightLevel = new double[map[0].length][map.length];
        this.focusNightVision3x3 = new double[map[0].length*3][map.length*3];
        this.focusSeen3x3 = new GreasedRegion(focusNightVision3x3,0.0).not();
    }
}
