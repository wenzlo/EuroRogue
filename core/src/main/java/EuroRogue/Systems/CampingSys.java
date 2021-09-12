package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.EventComponents.StatEvt;
import EuroRogue.MyEntitySystem;
import EuroRogue.StatType;
import squidpony.squidmath.Coord;
import sun.jvm.hotspot.debugger.win32.coff.COFFException;

public class CampingSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;



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
            for(Coord coord : levelCmp.actors.positions())
            {
                if(fovCmp.visible.contains(coord))
                {
                    tickerCmp.actionQueue.removeAll(tickerCmp.getScheduledActions(entity));
                }
            }
        }
    }
}
