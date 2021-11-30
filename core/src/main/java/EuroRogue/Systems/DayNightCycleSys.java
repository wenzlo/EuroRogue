package EuroRogue.Systems;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.Arrays;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.DayNightCycleEvt;
import EuroRogue.MyEntitySystem;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Noise;
import squidpony.squidmath.WhirlingNoise;

public class DayNightCycleSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private boolean rising = false;

    public DayNightCycleSys() {
        super.priority = 7;
    }


    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(DayNightCycleEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        if (entities.size() == 0) return;
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        if(lightingCmp.minAmbientLight == lightingCmp.maxAmbientLight)
        {

            return;
        }

        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        if(windowCmp.display.hasActiveAnimations() || windowCmp.display.hasActions() ) return;


        DayNightCycleEvt dayNightCycleEvt = (DayNightCycleEvt) CmpMapper.getComp(CmpType.DAYNIGHTCYCLE_EVT, entities.get(0));
        if(dayNightCycleEvt.processed) return;
        dayNightCycleEvt.processed =  true;


        if(rising)dayNightCycleForward();
        else dayNightCycleBackward();
        if(lightingCmp.ambientLightLvl == lightingCmp.maxAmbientLight) rising = false;
        else if (lightingCmp.ambientLightLvl == lightingCmp.minAmbientLight) rising = true;



    }
    public void dayNightCycleForward()
    {
        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        if(lightingCmp.ambientLightLvl>=lightingCmp.maxAmbientLight)return;
        double newLevel = Math.round((lightingCmp.ambientLightLvl + 0.04d)*100)/100.0;
        lightingCmp.ambientLightLvl = Math.min(lightingCmp.maxAmbientLight, newLevel);
        lightingCmp.ambientBgLighting = new float[lightingCmp.resistance3x3.length][lightingCmp.resistance3x3[0].length];
        lightingCmp.ambientBgLightLvls = new double[lightingCmp.resistance3x3.length][lightingCmp.resistance3x3[0].length];
        for(double[] line : lightingCmp.ambientBgLightLvls) Arrays.fill(line, lightingCmp.ambientLightLvl);
        lightingCmp.ambientFgLightLvls = new double[lightingCmp.fgLighting.length][lightingCmp.fgLighting[0].length];
        for(double[] line : lightingCmp.ambientFgLightLvls) Arrays.fill(line, lightingCmp.ambientLightLvl);
        Noise.Noise3D variation = new WhirlingNoise();
        for (int x = 0; x < lightingCmp.ambientBgLighting.length; x++) {
            for (int y = 0; y < lightingCmp.ambientBgLighting[0].length; y++) {
                float baseColor = SColor.lerpFloatColors(SColor.FLOAT_BLACK, levelCmp.bgColors[Math.round(x / 3)][Math.round(y / 3)], (float) lightingCmp.ambientBgLightLvls[x][y]);
                lightingCmp.ambientBgLighting[x][y] = SColor.lerpFloatColors(baseColor, SColor.COSMIC_LATTE.toFloatBits(), (float) (0xAAp-9f + (0xC8p-9f * lightingCmp.ambientBgLightLvls[x][y]*0.8 *
                        (1f + 0.25f * (float) variation.getNoise(x * 0.3, y * 0.3, 0.00125))) - 0.3f));

            }
        }

    }

    public void dayNightCycleBackward()
    {
        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        if(lightingCmp.ambientLightLvl<=lightingCmp.minAmbientLight)return;
        double newLevel = Math.round((lightingCmp.ambientLightLvl - 0.04d)*100)/100.0;
        lightingCmp.ambientLightLvl = Math.max(lightingCmp.minAmbientLight, newLevel);
        lightingCmp.ambientBgLighting = new float[lightingCmp.resistance3x3.length][lightingCmp.resistance3x3[0].length];
        lightingCmp.ambientBgLightLvls = new double[lightingCmp.resistance3x3.length][lightingCmp.resistance3x3[0].length];
        for(double[] line : lightingCmp.ambientBgLightLvls) Arrays.fill(line, lightingCmp.ambientLightLvl);
        lightingCmp.ambientFgLightLvls = new double[lightingCmp.fgLighting.length][lightingCmp.fgLighting[0].length];
        for(double[] line : lightingCmp.ambientFgLightLvls) Arrays.fill(line, lightingCmp.ambientLightLvl);
        Noise.Noise3D variation = new WhirlingNoise();
        for (int x = 0; x < lightingCmp.ambientBgLighting.length; x++) {
            for (int y = 0; y < lightingCmp.ambientBgLighting[0].length; y++) {
                float baseColor = SColor.lerpFloatColors(SColor.FLOAT_BLACK, levelCmp.bgColors[Math.round(x / 3)][Math.round(y / 3)], (float) lightingCmp.ambientBgLightLvls[x][y]);
                lightingCmp.ambientBgLighting[x][y] = SColor.lerpFloatColors(baseColor, SColor.COSMIC_LATTE.toFloatBits(), (float) (0xAAp-9f + (0xC8p-9f * lightingCmp.ambientBgLightLvls[x][y]*0.8 *
                        (1f + 0.25f * (float) variation.getNoise(x * 0.3, y * 0.3, 0.00125))) - 0.3f));

            }
        }

    }
}
