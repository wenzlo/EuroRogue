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
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
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
            if(entity == getGame().getFocus())
            {
                ((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().campWindow)).display.setVisible(true);
                ((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().campWInBg)).display.setVisible(true);
            }

            StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
            AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, entity);
            MakeCampEvt makeCampEvt = (MakeCampEvt) CmpMapper.getComp(CmpType.MAKE_CAMP_EVT, entity);
            System.out.println(tickerCmp.tick);
            if(!aiCmp.visibleEnemies.isEmpty() )
            {
                InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);
                CampEvt campEvt = new CampEvt(entity.hashCode(), inventoryCmp.getEquippedIDs());
                entity.remove(MakeCampEvt.class);
                entity.add(campEvt);
                tickerCmp.actionQueue.removeAll(tickerCmp.getScheduledActions(entity));

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
