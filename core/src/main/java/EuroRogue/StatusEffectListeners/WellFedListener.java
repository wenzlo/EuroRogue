package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class WellFedListener extends StatusEffectListener
{
    public WellFedListener(EuroRogue game){
        super(game);
        effect = StatusEffect.WELL_FED;
    }

    @Override
    public void entityAdded(Entity entity)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);

        super.entityAdded(entity);

        statsCmp.hp = statsCmp.getMaxHP();

    }

    @Override
    public void entityRemoved(Entity entity)
    {

        super.entityRemoved(entity);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
        if(statsCmp.hp>statsCmp.getMaxHP()) statsCmp.hp=statsCmp.getMaxHP();

    }
}
