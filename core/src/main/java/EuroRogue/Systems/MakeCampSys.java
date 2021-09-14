package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.StatEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.IColoredString;
import EuroRogue.MyEntitySystem;
import EuroRogue.StatType;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedSet;
import sun.jvm.hotspot.debugger.win32.coff.COFFException;

public class MakeCampSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public MakeCampSys(){
        this.setProcessing(false);
    }



    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(FocusCmp.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);


        for(Entity entity:entities)
        {
            FOVCmp fovCmp = (FOVCmp) CmpMapper.getComp(CmpType.FOV, entity);
            PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
            OrderedSet<Coord> positions = levelCmp.actors.positions();
            for(Coord coord : levelCmp.actors.positions())
            {
                if(coord==positionCmp.coord)continue;
                Entity visibleEnemy = getGame().getEntity(levelCmp.actors.get(coord));
                String name = ((NameCmp) CmpMapper.getComp(CmpType.NAME, visibleEnemy)).name;
                if(fovCmp.visible.contains(coord))
                {
                    tickerCmp.actionQueue.removeAll(tickerCmp.getScheduledActions(entity));
                    this.setProcessing(false);
                    ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateCampLogEvt().entry);
                    break;
                }
            }
        }
    }

    private LogEvt generateCampLogEvt ()
    {


        IColoredString.Impl<SColor> coloredEvtText = new IColoredString.Impl<>();
        Integer tick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        coloredEvtText.append(tick.toString(), SColor.WHITE);

        coloredEvtText.append(" Make Camp Cancelled: Enemy Detected", SColor.RED);


        return new LogEvt(tick, coloredEvtText);
    }
}
