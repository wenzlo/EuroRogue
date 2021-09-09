package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class FrozenListener extends StatusEffectListener
{
    public FrozenListener(EuroRogue game){
        super(game);
        effect= StatusEffect.FROZEN;}

    @Override
    public void entityAdded(Entity entity)
    {

        super.entityAdded(entity);
        interrupt(entity);
    }

    @Override
    public void entityRemoved(Entity entity) {
        super.entityRemoved(entity);
    }
}
