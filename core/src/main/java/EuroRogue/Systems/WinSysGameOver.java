package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.SColor;

public class WinSysGameOver extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public WinSysGameOver()
    {
        super.priority=11;
    }

    @Override
    public void addedToEngine(Engine engine)
    {
        //entities = new ImmutableArray<Entity>(new Array<>(new Entity[]{getGame().gameOverWindow}));
    }

    @Override
    public void update(float deltaTime)
    {

        WindowCmp window = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().gameOverWindow));
        MySparseLayers display = (MySparseLayers) window.display;
        if(display.isVisible()==false) return;

        //getGame().globalMenuIndex = 1; //key=1;

        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, getGame().gameOverWindow);

        Stage stage = window.stage;

        display.clear();
        display.putBorders(SColor.RED.toFloatBits(), "Game Over");
       /* for(Coord coord : menuCmp.menuMap.positions())
        {
            display.put(window.columnIndexes[coord.x], coord.y+1, menuCmp.menuMap.get(coord).label);
        }*/



        stage.getViewport().apply(false);
        stage.act();
        stage.draw();

    }
}
