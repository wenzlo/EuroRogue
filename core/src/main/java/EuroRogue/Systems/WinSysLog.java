package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import java.util.Comparator;

import EuroRogue.CmpMapper;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.CmpType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.IColoredString;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;

import squidpony.squidgrid.gui.gdx.SColor;

public class WinSysLog extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public WinSysLog()
    {
        super.priority = 0;
    }


    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(LogEvt.class).get());
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
        if(!((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().logWindow)).display.isVisible()) return;
        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, game.getFocus());

        for(Entity actor : entities)
        {
            if(aiCmp.visibleEnemies.contains(actor.hashCode()))
            {
                LogEvt logEvt = (LogEvt) CmpMapper.getComp(CmpType.LOG_EVT, actor);
                processEvent(logEvt);
            }
        }

        MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.logWindow)).display;
        Stage stage = game.logWindow.getComponent(WindowCmp.class).stage;
        LogCmp logCmp = (LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow);
        Integer currentTick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, game.ticker)).tick;

        display.clear();
        display.putBorders(SColor.BROWN.toFloatBits(), String.valueOf(getGame().depth));
        display.fillBackground(display.defaultBackground);
        display.put(1, 18, currentTick.toString(),SColor.WHITE);


        for(int i=0;i<Math.min(logCmp.logEntries.size(),16);i++)
        {
            IColoredString.Impl<SColor> entry = logCmp.logEntries.get(logCmp.logEntries.size()-1-i);


            display.put(1, 17-i, logCmp.logEntries.get(logCmp.logEntries.size()-1-i));
        }

        stage.getViewport().apply(false);
        stage.act();
        stage.draw();
    }
    private void processEvent(LogEvt logEvt)
    {
        if(logEvt.isProcessed()) return;
        LogCmp logCmp = (LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow);
        logCmp.logEntries.add(logEvt.entry);
        //logCmp.logEntries.sort(new SortByTick());
        logEvt.processed=true;
    }
    public static class SortByTick implements Comparator<LogEvt> {


        public SortByTick (){

        }

        @Override
        public int compare(LogEvt entry1, LogEvt entry2) {
            int t1 = (int) entry1.tick;
            int t2 = (int) entry2.tick;
            return Double.compare(t1,t2);
        }
    }

}
