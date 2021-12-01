package EuroRogue.Systems.Win;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.StorageEvt;
import EuroRogue.EventComponents.StorageEvtType;
import EuroRogue.IColoredString;
import EuroRogue.MenuItem;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class WinSysSaveBuild extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    public String buildName;

    public WinSysSaveBuild()
    {
        super.priority=100;
    }

    @Override
    public void addedToEngine(Engine engine)
    {
        entities = new ImmutableArray<Entity>(new Array<Entity>(new Entity[]{getGame().saveBuildWindow}));
    }

    @Override
    public void update(float deltaTime)
    {

        WindowCmp window = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().saveBuildWindow));
        MySparseLayers display = (MySparseLayers)window.display;
        if(display.isVisible()==false) return;

        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, getGame().saveBuildWindow);

        Stage stage = window.stage;

        display.clear();

        display.put(window.columnIndexes[1]-10, 2, "Build Name: "+buildName, SColor.WHITE);
        updateMenu();

        for(Coord coord : menuCmp.menuMap.positions())
        {
            display.put(window.columnIndexes[coord.x], coord.y+3, menuCmp.menuMap.get(coord).label);
        }
        getGame().getInput();

        stage.getViewport().apply(false);
        stage.act();
        stage.draw();


    }

    private void updateMenu()
    {
        Runnable saveBuild = new Runnable() {
            @Override
            public void run() {

                Entity eventEntity = new Entity();

                eventEntity.add( new StorageEvt(buildName, StorageEvtType.SAVE_BUILD) );
                getGame().engine.addEntity(eventEntity);


            }
        };
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, entities.get(0));
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, entities.get(0));


        char key = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];

        MenuItem menuItem = new MenuItem(new IColoredString.Impl(key+") Save "+buildName, SColor.WHITE));
        menuItem.addPrimaryAction(saveBuild);
        menuCmp.menuMap.put(Coord.get(0,0),key, menuItem);
        getGame().keyLookup.put(key, menuCmp);;
        getGame().globalMenuIndex++;
    }
}
