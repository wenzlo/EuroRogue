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
import EuroRogue.EventComponents.ShrineEvt;
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


public class WinSysShrineUiBg extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private LightHandler lightHandler;

    public WinSysShrineUiBg()
    {
        super.priority=10;

    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.one(ShrineEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        WindowCmp uiBgWindowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().shrineWinBG);
        if(!uiBgWindowCmp.display.isVisible())return;

        UiBgLightingCmp uiBgLightingCmp = (UiBgLightingCmp) CmpMapper.getComp(CmpType.UIBG_LIGHTING, getGame().shrineWinBG);
        if(entities.size()>0)
        {
            ShrineEvt shrineEvt = (ShrineEvt)CmpMapper.getComp(CmpType.SHRINE_EVT, entities.get(0));
            uiBgLightingCmp.lightColor2  = shrineEvt.school.color.toFloatBits();
        }

        if (lightHandler == null)
            lightHandler = new LightHandler(DungeonUtility.generateSimpleResistances3x3(uiBgLightingCmp.map));


        lightHandler.lightList.clear();
        uiBgWindowCmp.display.clear();

        uiBgLightingCmp.map =new char[uiBgLightingCmp.fgLighting.length][uiBgLightingCmp.fgLighting[0].length];

        if(!uiBgWindowCmp.display.isVisible()) return;
        int x = uiBgWindowCmp.stage.getViewport().getScreenX();
        int y = uiBgWindowCmp.stage.getViewport().getScreenY();
        int w = uiBgWindowCmp.stage.getViewport().getScreenWidth();
        int h = uiBgWindowCmp.stage.getViewport().getScreenHeight();


        rectangle(x, y, w, h, uiBgLightingCmp.map);

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
        uiBgWindowCmp.display.fillBackground(SColor.SLATE_GRAY);
        for(int x0 = 0; x0< uiBgLightingCmp.fgColors.length; x0++) {
            for (int y0 = 0; y0 < uiBgLightingCmp.fgColors[0].length; y0++) {
                uiBgLightingCmp.fgLighting[x0][y0] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), uiBgLightingCmp.fgColors[x0][y0], MathUtils.clamp((float) (uiBgLightingCmp.fgLightLevel[x0][y0]), 0.5f, 1f));
            }
        }

        lightHandler.resistances = MyDungeonUtility.generateSimpleResistances3x3(uiBgLightingCmp.map);


        Noise.Noise3D flicker = new WhirlingNoise();


        //lightHandler.calculateFOV(Coord.get(8, 10));

        lightHandler.updateAll();



        lightHandler.draw(uiBgLightingCmp.bgLighting);

        char[][] map = DungeonUtility.hashesToLines(uiBgLightingCmp.map);

        for(Coord coord : new GreasedRegion(uiBgLightingCmp.map, '#'))
        {
            uiBgWindowCmp.display.put(coord.x, coord.y, map[coord.x][coord.y], uiBgLightingCmp.fgLighting[coord.x][coord.y]);
        }

        uiBgWindowCmp.display.put(uiBgLightingCmp.bgLighting);





        uiBgWindowCmp.stage.act();
        uiBgWindowCmp.stage.getViewport().apply(false);
        uiBgWindowCmp.stage.draw();
    }

    private void rectangle(int x, int y, int w, int h, char[][]map)
    {
        int x0=0;
        int y0 = map[0].length-1;
        int x1 = x0 + (w/36)-1;
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
        UiBgLightingCmp uiBgLightingCmp = (UiBgLightingCmp) CmpMapper.getComp(CmpType.UIBG_LIGHTING, getGame().shrineWinBG);
        Light light = new Light(Coord.get(3, 3),new Radiance(((x1-x0)+(y0-y1)/2)+3, SColor.lightenFloat(uiBgLightingCmp.lightColor1, 0.2f)));
        lightHandler.addLight(light.hashCode(), light);
        light = new Light(Coord.get(14, 20),new Radiance(((x1-x0)+(y0-y1)/2)+2, SColor.darkenFloat(SColor.lerpFloatColors(uiBgLightingCmp.lightColor2, SColor.COSMIC_LATTE.toFloatBits(), 0.6f), 0.4f)));
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
