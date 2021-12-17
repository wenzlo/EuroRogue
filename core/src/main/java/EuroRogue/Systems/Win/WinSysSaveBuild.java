package EuroRogue.Systems.Win;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import EuroRogue.Storage;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.LevelEvt;
import EuroRogue.EventComponents.StorageEvt;
import EuroRogue.EventComponents.StorageEvtType;
import EuroRogue.IColoredString;
import EuroRogue.LevelType;
import EuroRogue.MenuItem;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class WinSysSaveBuild extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    public String buildName = "";

    private String[] headerText =  new String[]
            {       "                   Camp Menu",
                    " ",
                    "• Codex: ?) Prepare/Un-prepare Abilities",
                    "  [SHIFT] + ?) Displays Ability Details in Log",
                    " ",
                    "• Stat Increases:  ?) - Pay mana cost to increase stat",
                    " ",
                    "• Eat Food: ?) - Consume food ration",
                    "  - Removes Hungry(-25% MaxHP) and Starving(-50% MaxHP)",
                    "  - Eat 2 rations to gain Well Fed(+25% MaxHP)",
                    " ",
                    "Codex                  Stat Increases"
            };

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
        display.put(window.columnIndexes[1]+7, 2, "|                 ", SColor.WHITE, SColor.SILVER_GREY);
        display.put(window.columnIndexes[1]-10, 2, "Type Build Name: "+buildName+"|", SColor.WHITE);

        updateMenu();

        for(Coord coord : menuCmp.menuMap.positions())
        {
            display.put(window.columnIndexes[coord.x], coord.y+3, menuCmp.menuMap.get(coord).label);
        }
        display.put(1, 15, "Saved Builds:", SColor.WHITE, SColor.SILVER_GREY);
        int y=16;
        for(String buildName : getGame().buildStorage.buildKeys)
        {
            display.put(1, y, buildName, SColor.WHITE, SColor.SILVER_GREY);
            y++;

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
                buildName = "";


            }
        };
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, entities.get(0));



            {
                char key = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                String action = " Save ";
                if(getGame().buildStorage.buildKeys.contains(buildName))
                    action = " Overwrite ";
                MenuItem menuItem = new MenuItem(new IColoredString.Impl(key+")"+action + buildName+ " Build", SColor.WHITE));
                menuItem.addPrimaryAction(saveBuild);
                menuCmp.menuMap.put(Coord.get(0,3),key, menuItem);
                getGame().keyLookup.put(key, menuCmp);;
                getGame().globalMenuIndex++;


            }




        Runnable nextLevel = new Runnable() {
            @Override
            public void run() {
                Entity focus = getGame().getFocus();
                getGame().depth++;
                InventoryCmp inventoryCmp = ( InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, focus);
                Entity evtEntity = new Entity();
                List<LevelType> levelTypes = new ArrayList<>();
                Collections.addAll(levelTypes, LevelType.values());

                levelTypes.remove(LevelType.START);
                //TODO move rng level tye selection to level sys
                LevelEvt levelEvt = new LevelEvt(getGame().rng.getRandomElement(levelTypes));
                CampEvt campEvt = new CampEvt(focus.hashCode(), inventoryCmp.getEquippedIDs());
                evtEntity.add(levelEvt);
                focus.add(campEvt);
                getEngine().addEntity(evtEntity);
            }
        };

        char key = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];

        MenuItem menuItem = new MenuItem(new IColoredString.Impl(key+") Continue to Next Level ", SColor.WHITE));
        menuItem.addPrimaryAction(nextLevel);
        menuCmp.menuMap.put(Coord.get(0,5),key, menuItem);
        getGame().keyLookup.put(key, menuCmp);;
        getGame().globalMenuIndex++;






    }
}
