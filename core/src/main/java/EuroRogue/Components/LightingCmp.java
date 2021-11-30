package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.Arrays;

import EuroRogue.MyDungeonUtility;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.Noise;
import squidpony.squidmath.WaveNoise;
import squidpony.squidmath.WhirlingNoise;

public class LightingCmp implements Component
{
    public double[][] resistance3x3;
    public float[][] ambientBgLighting;
    public float[][] bgLighting;
    public double[][] ambientBgLightLvls;
    public double[][] fgLightLevel;
    public double[][] ambientFgLightLvls;
    public float[][] fgLighting;
    public double[][] focusNightVision3x3;
    public GreasedRegion focusSeen3x3;
    public double minAmbientLight;
    public double maxAmbientLight = 1;
    public double ambientLightLvl;
    public float fowColor;
    public float[][] fow;


    public LightingCmp (char[][] map, float[][] bgColors, double minAmbientLight, double maxAmbientLight, double ambientLightLvl)
    {
        this.ambientLightLvl  = ambientLightLvl;
        this.minAmbientLight = minAmbientLight;
        this.maxAmbientLight = maxAmbientLight;
        this.resistance3x3 = MyDungeonUtility.generateSimpleResistances3x3(map);
        this.bgLighting = new float[map[0].length][map.length];
        this.fgLighting = new float[map[0].length][map.length];
        this.fgLightLevel = new double[map[0].length][map.length];
        this.ambientBgLighting = new float[this.resistance3x3.length][this.resistance3x3[0].length];
        this.ambientBgLightLvls = new double[this.resistance3x3.length][this.resistance3x3[0].length];
        for(double[] line : this.ambientBgLightLvls) Arrays.fill(line, ambientLightLvl);
        this.ambientFgLightLvls = new double[this.fgLighting.length][this.fgLighting[0].length];
        for(double[] line : this.ambientFgLightLvls) Arrays.fill(line, ambientLightLvl);
        Noise.Noise3D variation = new WhirlingNoise();
        for (int x = 0; x < this.ambientBgLighting.length; x++) {
            for (int y = 0; y < this.ambientBgLighting[0].length; y++) {
                float baseColor = SColor.lerpFloatColors(SColor.FLOAT_BLACK, bgColors[Math.round(x / 3)][Math.round(y / 3)], (float) ambientBgLightLvls[x][y]);
                this.ambientBgLighting[x][y] = SColor.lerpFloatColors(baseColor, SColor.COSMIC_LATTE.toFloatBits(), (float) (0xAAp-9f + (0xC8p-9f * ambientBgLightLvls[x][y]*0.8 *
                                        (1f + 0.25f * (float) variation.getNoise(x * 0.3, y * 0.3, 0.00125))) - 0.3f));

            }
        }
        Noise.Noise2D variation2d = new WaveNoise();
        this.fow = new float[this.resistance3x3.length][this.resistance3x3[0].length];
        if(this.maxAmbientLight == 0)
            this.fowColor = SColor.FLOAT_BLACK;
        else
            this.fowColor  = SColor.lerpFloatColors(SColor.FLOAT_BLACK, SColor.WHITE_FLOAT_BITS, 0.35f);

        for (int x = 0; x < this.fow.length; x++) {
            for (int y = 0; y < this.fow[0].length; y++) {

                this.fow[x][y] = SColor.lerpFloatColors(fowColor, SColor.PURE_DARK_GRAY.toFloatBits(), (float) Math.abs( variation2d.getNoise(x*0.2,y*0.2) *0.2f));

            }
        }


        this.focusNightVision3x3 = new double[map[0].length*3][map.length*3];
        this.focusSeen3x3 = new GreasedRegion(focusNightVision3x3,0.0).not();

    }

    public void dayNightCycleForward(LevelCmp levelCmp)
    {
        if(ambientLightLvl>=maxAmbientLight)return;
        ambientLightLvl = Math.min(maxAmbientLight, ambientLightLvl + 0.04d);
        this.ambientBgLighting = new float[this.resistance3x3.length][this.resistance3x3[0].length];
        this.ambientBgLightLvls = new double[this.resistance3x3.length][this.resistance3x3[0].length];
        for(double[] line : this.ambientBgLightLvls) Arrays.fill(line, ambientLightLvl);
        this.ambientFgLightLvls = new double[this.fgLighting.length][this.fgLighting[0].length];
        for(double[] line : this.ambientFgLightLvls) Arrays.fill(line, ambientLightLvl);
        Noise.Noise3D variation = new WhirlingNoise();
        for (int x = 0; x < this.ambientBgLighting.length; x++) {
            for (int y = 0; y < this.ambientBgLighting[0].length; y++) {
                float baseColor = SColor.lerpFloatColors(SColor.FLOAT_BLACK, levelCmp.bgColors[Math.round(x / 3)][Math.round(y / 3)], (float) ambientBgLightLvls[x][y]);
                this.ambientBgLighting[x][y] = SColor.lerpFloatColors(baseColor, SColor.COSMIC_LATTE.toFloatBits(), (float) (0xAAp-9f + (0xC8p-9f * ambientBgLightLvls[x][y]*0.8 *
                        (1f + 0.25f * (float) variation.getNoise(x * 0.3, y * 0.3, 0.00125))) - 0.3f));

            }
        }

    }

    public void dayNightCycleBackward(LevelCmp levelCmp)
    {
        if(ambientLightLvl<=minAmbientLight)return;
        ambientLightLvl = Math.max(minAmbientLight, ambientLightLvl - 0.04d);
        this.ambientBgLighting = new float[this.resistance3x3.length][this.resistance3x3[0].length];
        this.ambientBgLightLvls = new double[this.resistance3x3.length][this.resistance3x3[0].length];
        for(double[] line : this.ambientBgLightLvls) Arrays.fill(line, ambientLightLvl);
        this.ambientFgLightLvls = new double[this.fgLighting.length][this.fgLighting[0].length];
        for(double[] line : this.ambientFgLightLvls) Arrays.fill(line, ambientLightLvl);
        Noise.Noise3D variation = new WhirlingNoise();
        for (int x = 0; x < this.ambientBgLighting.length; x++) {
            for (int y = 0; y < this.ambientBgLighting[0].length; y++) {
                float baseColor = SColor.lerpFloatColors(SColor.FLOAT_BLACK, levelCmp.bgColors[Math.round(x / 3)][Math.round(y / 3)], (float) ambientBgLightLvls[x][y]);
                this.ambientBgLighting[x][y] = SColor.lerpFloatColors(baseColor, SColor.COSMIC_LATTE.toFloatBits(), (float) (0xAAp-9f + (0xC8p-9f * ambientBgLightLvls[x][y]*0.8 *
                        (1f + 0.25f * (float) variation.getNoise(x * 0.3, y * 0.3, 0.00125))) - 0.3f));

            }
        }

    }




}
