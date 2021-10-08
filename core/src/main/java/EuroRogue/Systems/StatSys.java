package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EventComponents.StatEvt;
import EuroRogue.MyEntitySystem;
import EuroRogue.StatType;

public class StatSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;



    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(StatEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        for(Entity entity:entities)
        {
            StatEvt statEvt = (StatEvt) CmpMapper.getComp(CmpType.STAT_EVT, entity);
            Entity actorEntity = getGame().getEntity(statEvt.actorID);
            StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, actorEntity);
            ManaPoolCmp manaPoolCmp = (ManaPoolCmp)CmpMapper.getComp(CmpType.MANA_POOL, actorEntity);
            statEvt.processed = true;

            for (StatType statType : statEvt.statChanges.keySet())
            {
                int value = statsCmp.getStat(statType) + statEvt.statChanges.get(statType);
                if(statEvt.statChanges.get(statType)>0)
                {
                    if(statsCmp.afford(statType, manaPoolCmp))
                    {
                       manaPoolCmp.removeMana(statsCmp.getStatCost(statType),statsCmp);
                       statsCmp.setStat(statType, value);
                    }

                }
                else if(statEvt.statChanges.get(statType)<0)
                {
                    manaPoolCmp.addMana(statsCmp.getStatCost(statType), statsCmp);
                    statsCmp.setStat(statType, value);
                }
                manaPoolCmp.numAttunedSlots=statsCmp.getNumAttunedSlots();
            }
        }
    }
}
