package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.UiBgLightingCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.MyDungeonUtility;
import EuroRogue.MyEntitySystem;
import EuroRogue.MyFOV;
import EuroRogue.MyMapUtility;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.Noise;
import squidpony.squidmath.WhirlingNoise;


public class WinSysUiBg extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private LightHandler lightHandler;

    public WinSysUiBg()
    {
        super.priority=7;

    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.one(GameStateEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        WindowCmp uiBgWindowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().uiBackgrounds);
        UiBgLightingCmp uiBgLightingCmp = (UiBgLightingCmp) CmpMapper.getComp(CmpType.UIBG_LIGHTING, getGame().uiBackgrounds);
        if (lightHandler == null)
            lightHandler = new LightHandler(DungeonUtility.generateSimpleResistances3x3(uiBgLightingCmp.map));

        if (entities.size() > 0){
            GameStateEvt gameStateEvt = (GameStateEvt)CmpMapper.getComp(CmpType.GAMESTATE_EVT, entities.get(0));
            if(gameStateEvt!=null)
                if(!gameStateEvt.processed) return;

            lightHandler.lightList.clear();
            uiBgWindowCmp.display.clear();

            uiBgLightingCmp.map =new char[uiBgLightingCmp.fgLighting.length][uiBgLightingCmp.fgLighting[0].length];



            //uiBgWindowCmp.display.fillBackground(SColor.BREWED_MUSTARD_BROWN);

            for(Entity windowEntity : getGame().uiBgWindows)
            {
                if(windowEntity == getGame().dungeonWindow)continue;
                WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, windowEntity);
                if(!windowCmp.display.isVisible()) continue;
                int x = windowCmp.stage.getViewport().getScreenX();
                int y = windowCmp.stage.getViewport().getScreenY();
                int w = windowCmp.stage.getViewport().getScreenWidth();
                int h = windowCmp.stage.getViewport().getScreenHeight();


                rectangle(x, y, w, h, uiBgLightingCmp.map);
            }
            uiBgLightingCmp.bgLighting=new float[(uiBgLightingCmp.map.length)*3][(uiBgLightingCmp.map[0].length)*3];
            uiBgLightingCmp.fgColors = MyMapUtility.generateDefaultColorsFloat(uiBgLightingCmp.map);
            uiBgLightingCmp.fgResistances = MyDungeonUtility.generateSimpleResistances(uiBgLightingCmp.map);
            double[][] tempFov = new double[uiBgLightingCmp.map.length][uiBgLightingCmp.map[0].length];
            for(Light light : lightHandler.lightList.values())
            {
                Radiance radiance = light.radiance;
                Coord location = light.position;
                MyFOV.addFOVsInto(uiBgLightingCmp.fgLightLevel, MyFOV.reuseFOV(uiBgLightingCmp.fgResistances, tempFov, location.x/3, location.y/3, radiance.range/3));
            }
            for(int x = 0; x< uiBgLightingCmp.fgColors.length; x++) {
                for (int y = 0; y < uiBgLightingCmp.fgColors[0].length; y++) {
                    uiBgLightingCmp.fgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), uiBgLightingCmp.fgColors[x][y], MathUtils.clamp((float) (uiBgLightingCmp.fgLightLevel[x][y]), 0.4f, 0.9f));
                }
            }

            lightHandler.resistances = DungeonUtility.generateSimpleResistances3x3(uiBgLightingCmp.map);


            Noise.Noise3D flicker = new WhirlingNoise();


            lightHandler.updateAll();



            lightHandler.draw(uiBgLightingCmp.bgLighting);

            char[][] map = DungeonUtility.hashesToLines(uiBgLightingCmp.map);

            for(Coord coord : new GreasedRegion(uiBgLightingCmp.map, '#'))
            {
                uiBgWindowCmp.display.put(coord.x, coord.y, map[coord.x][coord.y], uiBgLightingCmp.fgLighting[coord.x][coord.y]);
            }
            uiBgWindowCmp.display.put(uiBgLightingCmp.bgLighting);

        }
        uiBgWindowCmp.stage.act();
        uiBgWindowCmp.stage.getViewport().apply(false);
        uiBgWindowCmp.stage.draw();
    }

    private void rectangle(int x, int y, int w, int h, char[][]map)
    {
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().uiBackgrounds);

        int x0=x/36;
        int y0 = map[0].length-1-(y/36);
        int x1 = x0 + (w/36);
        int y1 = Math.max(0, y0-(h/36));
        map[x0][y0] = 'X';
        for (int i = y0; i > y1; i--) {
            map[x0][i] = '#';
            map[x1][i] = '#';
        }
        for (int i = x0; i <= x1; i++) {

            map[i][y0] = '#';

            map[i][y1] = '#';

        }
        Light light = new Light(Coord.get((x0+2)*3, (y1+2)*3),new Radiance(((x1-x0)+(y0-y1)/2)+1, SColor.darkenFloat(SColor.SLATE_GRAY.toFloatBits(), 0.3f)));
        lightHandler.addLight(light.hashCode(), light);
        light = new Light(Coord.get((x1-2)*3, (y0-2)*3),new Radiance(((x1-x0)+(y0-y1)/2)+1, SColor.darkenFloat(SColor.COSMIC_LATTE.toFloatBits(), 0.6f)));
        lightHandler.addLight(light.hashCode(), light);

    }
    private void clearRect(int x, int y, int w, int h, char[][]map)
    {
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().uiBackgrounds);

        int x0=x/36;
        int y0 = map[0].length-1-(y/36);
        int x1 = x0 + (w/36);
        int y1 = Math.max(0, y0-(h/36));
        for(int cx=x0; cx<x1; cx++)
            for(int cy=y1; cy>y0; y--)
                windowCmp.display.clear(cx,cy);

    }



}
