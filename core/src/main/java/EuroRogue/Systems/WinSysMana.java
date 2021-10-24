package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.panel.IColoredString;
import squidpony.squidgrid.gui.gdx.SColor;

public class WinSysMana extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;


    public WinSysMana()
    {
        super.priority = 10;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(FocusCmp.class, FocusTargetCmp.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        if(!((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().focusManaWindow)).display.isVisible()) return;
        EuroRogue game = getGame();
        MySparseLayers display = null;
        Stage stage = null;
        StatsCmp statsCmp;
        for(Entity entity:entities)
        {

            if(game.getFocus()==entity)
            {

                display = (MySparseLayers)game.focusManaWindow.getComponent(WindowCmp.class).display;
                stage = game.focusManaWindow.getComponent(WindowCmp.class).stage;
                statsCmp= (StatsCmp)CmpMapper.getComp(CmpType.STATS, getGame().getFocus());



            } else {
                display = (MySparseLayers)game.targetManaWindow.getComponent(WindowCmp.class).display;
                stage = game.targetManaWindow.getComponent(WindowCmp.class).stage;
                statsCmp= (StatsCmp)CmpMapper.getComp(CmpType.STATS, getGame().getFocusTarget());
            }
            ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, entity);
            display.clear();
            //display.putBorders(SColor.BRONZE.toFloatBits(), "Weary");

            display.put(1,0,manaPoolCmp.activeToIColoredString());
            display.put(1,1, "──────", SColor.BRONZE);
            display.put(1,5, "──────", SColor.BRONZE);
            int yOffset = 2;
            for(IColoredString<SColor> line : manaPoolCmp.spentToIColoredStrings())
            {
                display.put(1,yOffset,line);
                yOffset++;
            }
            yOffset = 6;
            for(IColoredString<SColor> line : manaPoolCmp.attunedToIColoredStrings(statsCmp.getNumAttunedSlots()))
            {
                display.put(1,yOffset,line);
                yOffset++;
            }

            stage.getViewport().apply(false);
            stage.act();
            stage.draw();
        }
    }
}
