package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.GreasedRegion;

public class LightingCmp implements Component
{
    public double[][] resistance3x3;
    public float[][] bgLighting;
    public double[][] fgLightLevel;
    public float[][] fgLighting;
    public double[][] focusNightVision3x3;
    public GreasedRegion focusSeen3x3;

    public LightingCmp (char[][] lineDungeon)
    {
        this.resistance3x3 = DungeonUtility.generateResistances3x3(lineDungeon);
        this.bgLighting = new float[lineDungeon[0].length][lineDungeon.length];
        this.fgLighting = new float[lineDungeon[0].length][lineDungeon.length];
        this.fgLightLevel = new double[lineDungeon[0].length][lineDungeon.length];
        this.focusNightVision3x3 = new double[lineDungeon[0].length*3][lineDungeon.length*3];
        this.focusSeen3x3 = new GreasedRegion(focusNightVision3x3,0.0).not();
    }
}
