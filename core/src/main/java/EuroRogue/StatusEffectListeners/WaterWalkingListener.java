package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TerrainType;

public class WaterWalkingListener extends StatusEffectListener
{
    public WaterWalkingListener(EuroRogue game){
        super(game);
        effect= StatusEffect.WATER_WALKING;
    }

    @Override
    public void entityAdded(Entity entity)
    {
        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, entity);
        aiCmp.addTraversable(TerrainType.DEEP_WATER);
    }

    @Override
    public void entityRemoved(Entity entity)
    {
        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, entity);
        aiCmp.removeTraversable(TerrainType.DEEP_WATER);
    }
}
