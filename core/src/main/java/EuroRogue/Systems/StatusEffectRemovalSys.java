package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.TickerCmp;
import EuroRogue.MyEntitySystem;
import EuroRogue.StatusEffectCmps.Hungry;
import EuroRogue.StatusEffectCmps.Starving;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.StatusEffectCmp;
import EuroRogue.StatusEffectCmps.WellFed;


public class StatusEffectRemovalSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public StatusEffectRemovalSys(){super.priority = 5;}

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(WellFed.class, Hungry.class, Starving.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        {
            TickerCmp tickerCmp = ((TickerCmp)CmpMapper.getComp(CmpType.TICKER, getGame().ticker));
            for(Entity entity:entities)
            {
               for(StatusEffect statusEffect : getGame().getStatusEffects(entity))
               {
                   StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, entity);
                   if(statusEffectCmp==null || statusEffectCmp.lastTick==null) continue;
                   if(statusEffectCmp.lastTick<=tickerCmp.tick)
                   {
                       entity.remove(statusEffect.cls);

                   }
               }
            }
        }
    }
}
