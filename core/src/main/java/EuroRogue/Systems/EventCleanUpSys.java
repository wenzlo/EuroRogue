package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.Arrays;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.CodexEvt;
import EuroRogue.EventComponents.DayNightCycleEvt;
import EuroRogue.EventComponents.DeathEvt;
import EuroRogue.EventComponents.FrozenEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LevelEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.EventComponents.RestEvt;
import EuroRogue.EventComponents.ShrineEvt;
import EuroRogue.EventComponents.StatEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.MyEntitySystem;

public class EventCleanUpSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private List<CmpType> eventCmpTypes = Arrays.asList(CmpType.DAYNIGHTCYCLE_EVT, CmpType.SHRINE_EVT, CmpType.CAMP_EVT, CmpType.FROZEN_EVT, CmpType.LEVEL_EVT, CmpType.DEATH_EVT, CmpType.STAT_EVT, CmpType.GAMESTATE_EVT, CmpType.ACTION_EVT, CmpType.CODEX_EVT, CmpType.ITEM_EVT, CmpType.LOG_EVT, CmpType.MOVE_EVT, CmpType.REST_EVT, CmpType.STATUS_EFFECT_EVT, CmpType.ANIM_GLYPH_EVT);

    public EventCleanUpSys()
    {
        super.priority = 99;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(DayNightCycleEvt.class, ShrineEvt.class, DeathEvt.class, CampEvt.class, FrozenEvt.class, StatEvt.class, GameStateEvt.class, ActionEvt.class, CodexEvt.class, MoveEvt.class, ItemEvt.class,
                                RestEvt.class, StatusEffectEvt.class, AnimateGlyphEvt.class, LevelEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
       /* if(entities.size()>0) getEngine().getSystem(MenuUpdateSys.class).setProcessing(true);
        else getEngine().getSystem(MenuUpdateSys.class).setProcessing(false);*/

        for(Entity entity:entities)
        {
            for(CmpType cmpType: eventCmpTypes)
            {
                IEventComponent eventComponent = (IEventComponent) CmpMapper.getComp(cmpType, entity);
                if(eventComponent != null)
                {
                    if(eventComponent.isProcessed())entity.remove(cmpType.type);
                }
            }
            if(entity.getComponents().size()==0) getEngine().removeEntity(entity);
        }
    }
}
