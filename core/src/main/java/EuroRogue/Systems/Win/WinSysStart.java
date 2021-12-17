package EuroRogue.Systems.Win;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.StringKit;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class WinSysStart extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private String[] welcomeText =  new String[]
            {"             Welcome to EuroRogue.",
             " ",
             "• Roll new builds until you get one you like.",
             "• Start the tutorial level.",
             "• When you finish the tutorial you will be able to",
             "  save your starting build.",
             "• Select it from the this menu to skip the tutorial."
    };
    public WinSysStart()
    {
        super.priority=100;
    }

    @Override
    public void addedToEngine(Engine engine)
    {
        entities = new ImmutableArray<Entity>(new Array<Entity>(new Entity[]{getGame().startWindow}));
    }

    @Override
    public void update(float deltaTime)
    {

        WindowCmp window = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().startWindow));
        MySparseLayers display = (MySparseLayers)window.display;
        if(!display.isVisible()) return;

        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, getGame().startWindow);

        Stage stage = window.stage;

        display.clear();
        int y = 0;
        for(String string : welcomeText)
        {
            display.put(window.columnIndexes[0], y, string, SColor.LIGHT_YELLOW_DYE);
            y++;
        }
        y++;

        display.put(window.columnIndexes[0], y, "Player Name: "+getGame().playerName, SColor.WHITE);

        for(Coord coord : menuCmp.menuMap.positions())
        {
            display.put(window.columnIndexes[coord.x], coord.y+10, menuCmp.menuMap.get(coord).label);
        }
        getGame().getInput();

        stage.getViewport().apply(false);
        stage.act();
        stage.draw();

    }
}
