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
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class WinSysCamp extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private String[] headerText =  new String[]
            {       "                   Camp Menu",
                    " ",
                    "Codex: ?) Prepare/Un-prepare Abilities",
                    "       [SHIFT] + ?) Displays Ability Details in Log",
                    " ",
                    "Stat Increases:  ?) - Pay mana cost to increase stat",
                    " ",
                    "Eat Food: ?) - Consume food ration",
                    " - Removes Hungry(-25% MaxHP) and Starving(-50% MaxHP)",
                    " - Eat 2 rations to gain Well Fed(+25% MaxHP)",
                    " ",
                    "Codex                    Stat Increases"
            };

    public WinSysCamp()
    {
        super.priority=100;
    }

    @Override
    public void addedToEngine(Engine engine)
    {
        entities = new ImmutableArray<Entity>(new Array<Entity>(new Entity[]{getGame().campWindow}));
    }

    @Override
    public void update(float deltaTime)
    {

        WindowCmp window = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().campWindow));
        MySparseLayers display = window.display;
        if(display.isVisible()==false) return;

        getGame().globalMenuIndex = 1; //key=1;

        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, getGame().campWindow);

        Stage stage = window.stage;

        display.clear();

        int y = 0;
        for(String string : headerText)
        {
            display.put(window.columnIndexes[0], y, string, SColor.LIGHT_YELLOW_DYE);
            y++;
        }
        y++;

        for(Coord coord : menuCmp.menuMap.positions())
        {
            display.put(window.columnIndexes[coord.x], coord.y+y, menuCmp.menuMap.get(coord).label);
        }
        getGame().getInput();



        stage.getViewport().apply(false);
        stage.act();
        stage.draw();

    }
}
