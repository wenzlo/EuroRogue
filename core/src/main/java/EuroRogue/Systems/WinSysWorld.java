package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.WindowCmp;
import EuroRogue.MyEntitySystem;
import squidpony.squidgrid.gui.gdx.ICellVisible;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.WildMapView;
import squidpony.squidgrid.mapping.WildMap;


public class WinSysWorld extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private WildMapView wildMapView;
    private WildMap wildMap;
    //private WorldMapGenerator.RotatingSpaceMap mapGen;
    private double centerLongitude = 0;


    public WinSysWorld()
    {
        super.priority = 8;
        //mapGen = new WorldMapGenerator.RotatingSpaceMap(123456, 69, 69);
        wildMapView = new WildMapView(12345, 23, 23, 25);
        wildMapView.generate();
        wildMap = wildMapView.getWildMap();
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {

    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().worldMapWindow);
        if(!windowCmp.display.isVisible()) return;
        //windowCmp.display.fillBackground(SColor.BLACK);
        //centerLongitude = centerLongitude + 0.008;
        //mapGen.setCenterLongitude(centerLongitude);

        putMap(windowCmp.display);
        Stage stage = windowCmp.stage;

        stage.act();
        stage.getViewport().apply(true);
        stage.draw();

    }

    private void putMap(SparseLayers layers)
    {
        //ArrayTools.insert(wcolorMap, layers.backgrounds, 0, 0);
        int c;
        ICellVisible icv;
        for (int x = 0; x < wildMap.width && x < layers.gridWidth; x++) {
            for (int y = 0; y < wildMap.height && y < layers.gridHeight; y++) {
                if((c = wildMap.content[x][y]) >= 0 && (icv = wildMapView.viewer.get(wildMap.contentTypes.get(c))) != null)
                    layers.put(x, y, icv.getSymbol(), SColor.contrastLuma(icv.getPackedColor(), wildMapView.getColorMap()[x][y]), wildMapView.getColorMap()[x][y]);
                else if((icv = wildMapView.viewer.get(wildMap.floorTypes.get(wildMap.floors[x][y]))) != null)
                    layers.put(x, y, icv.getSymbol(), SColor.contrastLuma(icv.getPackedColor(), wildMapView.getColorMap()[x][y]), wildMapView.getColorMap()[x][y]);
            }
        }

    }

}
