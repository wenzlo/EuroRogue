package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class WinSysInventory extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public WinSysInventory()
    {
        super.priority=11;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(FocusCmp.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        EuroRogue game = getGame();
        MySparseLayers display = (MySparseLayers)game.inventoryWindow.getComponent(WindowCmp.class).display;
        if(!display.isVisible()) return;
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.inventoryWindow);
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, game.inventoryWindow);


        Stage stage = game.inventoryWindow.getComponent(WindowCmp.class).stage;
        String caption = "Inventory─────────────────────────Equipment SLots";




        display.clear();


        for(Coord coord : menuCmp.menuMap.positions())
        {
            display.put(windowCmp.columnIndexes[coord.x], coord.y+1, menuCmp.menuMap.get(coord).label);
        }

        stage.getViewport().apply(false);
        stage.act();
        stage.draw();

    }
}

