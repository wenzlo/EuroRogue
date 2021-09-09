package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.CmpMapper;
import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.StatusEffectCmp;

public class BurningListener extends StatusEffectListener
{
    public BurningListener(EuroRogue game){
        super(game);
        effect= StatusEffect.BURNING;
    }

    @Override
    public void entityAdded(Entity entity) {
        super.entityAdded(entity);
        StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(effect, entity);
        addLightCmpTemp(entity, statusEffectCmp);
    }

    @Override
    public void entityRemoved(Entity entity) {
        super.entityRemoved(entity);
        removeLightCmpTemp(entity);
    }
}
