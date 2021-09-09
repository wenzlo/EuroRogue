package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class HungryListener extends StatusEffectListener
{
    public HungryListener(EuroRogue game){
        super(game);
        effect = StatusEffect.HUNGRY;
    }

    @Override
    public void entityAdded(Entity entity) {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
        super.entityAdded(entity);

        statsCmp.hp=statsCmp.getMaxHP();

    }
    @Override
    public void entityRemoved(Entity entity)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
        super.entityRemoved(entity);
        statsCmp.hp = statsCmp.getMaxHP();

    }

}
