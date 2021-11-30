package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import javax.print.attribute.standard.MediaSize;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.IColoredString;
import EuroRogue.MyEntitySystem;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedSet;


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
        entities = engine.getEntitiesFor(Family.all(CampEvt.class).get());
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
            NameCmp nameCmp = (NameCmp) CmpMapper.getComp(CmpType.NAME, entity);
            System.out.println(entity.getComponents());
            System.out.println(levelCmp.actors.positions());
            for(Coord coord : levelCmp.actors.positions())
            {
                if(coord==positionCmp.coord)continue;
                if(fovCmp.visible.contains(coord))
                {
                    tickerCmp.actionQueue.removeAll(tickerCmp.getScheduledActions(entity));
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
