package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.MyMapUtility;
import squidpony.squidgrid.gui.gdx.MapUtility;
import squidpony.squidgrid.gui.gdx.SColor;

public class UiBgLightingCmp implements Component
{
    public char[][] map;
    public float[][] fgColors;
    public float[][] bgColors;
    public float[][] bgLighting;
    public double[][] fgLightLevel;
    public float[][] fgLighting;
    public double[][] fgResistances;
    public float lightColor1 = SColor.SLATE_GRAY.toFloatBits(),
                 lightColor2 = SColor.COSMIC_LATTE.toFloatBits();

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

    public UiBgLightingCmp (int width, int height)
    {
        this.map = new char[width][height];

        this.fgColors = MapUtility.generateDefaultColorsFloat(map);
        this.bgColors = MapUtility.generateDefaultBGColorsFloat(map);
        this.bgLighting = new float[map.length*3][map[0].length*3];
        this.fgLighting = new float[map.length][map[0].length];
        this.fgLightLevel = new double[map.length][map[0].length];
        this.fgResistances = new double[map.length][map[0].length];
    }



}
