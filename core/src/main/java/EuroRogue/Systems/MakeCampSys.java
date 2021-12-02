package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.MakeCampEvt;
import EuroRogue.IColoredString;
import EuroRogue.MyEntitySystem;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;



public class MakeCampSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(MakeCampEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);

        for(Entity entity:entities)
        {
            StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
            AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, entity);

            if(!aiCmp.visibleEnemies.isEmpty())
            {
                System.out.println(tickerCmp.getScheduledActions(entity));
                tickerCmp.actionQueue.removeAll(tickerCmp.getScheduledActions(entity));
                entity.remove(MakeCampEvt.class);
                PositionCmp positionCmp = (PositionCmp)CmpMapper.getComp(CmpType.POSITION, entity);

                ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateCampLogEvt().entry);
                for(Integer id : aiCmp.visibleEnemies)
                {
                    Entity enemy = getGame().getEntity(id);
                    StatsCmp enemyStats = (StatsCmp)CmpMapper.getComp(CmpType.STATS, enemy);
                    AICmp enemyAI = CmpMapper.getAIComp(enemyStats.mobType.aiType, enemy);
                    enemyAI.alerts.put(entity.hashCode(), positionCmp.coord);
                }
                break;
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
