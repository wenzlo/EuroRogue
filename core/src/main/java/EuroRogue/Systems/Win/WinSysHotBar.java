package EuroRogue.Systems.Win;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.IColoredString;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class WinSysHotBar extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private IColoredString.Impl caption = new IColoredString.Impl();


    public WinSysHotBar()
    {
        super.priority=10;
        caption.append("Ability   ", SColor.WHITE);
        caption.append("cost/range/dmg──┼", SColor.SLATE_GRAY);
        caption.append("Scroll    ", SColor.WHITE);
        caption.append("cost/range/dmg──┼", SColor.SLATE_GRAY);
        caption.append("Reaction  ", SColor.WHITE);
        caption.append("cost/range/dmg──┼", SColor.SLATE_GRAY);
        caption.append("Status Effect ", SColor.WHITE);
        caption.append("duration", SColor.SLATE_GRAY);

    }

    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(FocusCmp.class, FocusTargetCmp.class).get());
    }

    @Override
    public void update(float deltaTime)
    {
        if(!((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().focusHotBar)).display.isVisible()) return;
        WindowCmp window;
        MenuCmp menuCmp;
        MySparseLayers display;
        Stage stage;
        boolean focus = true;
        for(Entity entity:entities)
        {
            EuroRogue game = getGame();
            if(focus==true) getGame().globalMenuIndex = 1; //key=1;
            if(game.getFocus()==entity)
            {
                window = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().focusHotBar));
                menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, getGame().focusHotBar);
                display =(MySparseLayers) window.display;
                stage = window.stage;
            }
            else
            {
                window = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().targetHotBar));
                menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, getGame().targetHotBar);
                display = (MySparseLayers)window.display;
                stage = window.stage;
                focus = false;

            }
            display.clear();

            display.put(1,0,caption);

            for(Coord coord : menuCmp.menuMap.positions())
            {
                display.put(window.columnIndexes[coord.x], coord.y+1, menuCmp.menuMap.get(coord).label);
            }

            stage.getViewport().apply(false);
            stage.act();
            stage.draw();
        }
    }

}
