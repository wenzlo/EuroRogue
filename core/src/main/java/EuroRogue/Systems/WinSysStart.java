package EuroRogue.Systems;

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

public class WinSysStart extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

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
        MySparseLayers display = window.display;
        if(display.isVisible()==false) return;

        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, getGame().startWindow);

        Stage stage = window.stage;

        display.clear();
        display.putBorders(SColor.SLATE_GRAY.toFloatBits(), "Start Menu");
        display.put(window.columnIndexes[1]-10, 2, "Player Name: "+getGame().playerName, SColor.WHITE);

        for(Coord coord : menuCmp.menuMap.positions())
        {
            display.put(window.columnIndexes[coord.x], coord.y+3, menuCmp.menuMap.get(coord).label);
        }
        getGame().getInput();



        stage.getViewport().apply(false);
        stage.act();
        stage.draw();


    }
}
