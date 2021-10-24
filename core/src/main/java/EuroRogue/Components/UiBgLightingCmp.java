package EuroRogue.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

import EuroRogue.MyMapUtility;
import EuroRogue.MyDungeonUtility;
import squidpony.squidgrid.gui.gdx.MapUtility;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.GreasedRegion;

public class UiBgLightingCmp implements Component
{
    public char[][] map;
    public float[][] fgColors;
    public float[][] bgColors;
    public float[][] bgLighting;
    public double[][] fgLightLevel;
    public float[][] fgLighting;
    public double[][] fgResistances;

    public UiBgLightingCmp (char[][] map)
    {
        this.map = map;
        this.fgColors = MapUtility.generateDefaultColorsFloat(map);
        this.bgColors = MyMapUtility.generateDefaultBGColorsFloat(map);
        this.bgLighting = new float[map.length*3][map[0].length*3];
        this.fgLighting = new float[map.length][map[0].length];
        this.fgLightLevel = new double[map.length][map[0].length];
        this.fgResistances = new double[map.length][map[0].length];
    }

    public UiBgLightingCmp ()
    {
        this.map = new char[45][28];

        this.fgColors = MapUtility.generateDefaultColorsFloat(map);
        this.bgColors = MapUtility.generateDefaultBGColorsFloat(map);
        this.bgLighting = new float[map.length*3][map[0].length*3];
        this.fgLighting = new float[map.length][map[0].length];
        this.fgLightLevel = new double[map.length][map[0].length];
        this.fgResistances = new double[map.length][map[0].length];
    }



}
