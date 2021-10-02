package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.WindowCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class WinSysCamp extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

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
        display.putBorders(SColor.SLATE_GRAY.toFloatBits(), "Prep/Unprep Abilities──┼Purchase Stat Increase─┼Eat Food");
        for(Coord coord : menuCmp.menuMap.positions())
        {
            display.put(window.columnIndexes[coord.x], coord.y+1, menuCmp.menuMap.get(coord).label);
        }
        getGame().getInput();



        stage.getViewport().apply(false);
        stage.act();
        stage.draw();

    }
}
