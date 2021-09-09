package EuroRogue.EventComponents;

import EuroRogue.DamageType;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class DamageEvent implements IEventComponent
{
    public boolean processed = false;
    public int targetID;
    public int damage;
    public DamageType damageType;
    public StatusEffect statusEffect;

    public DamageEvent(int targetID, int damage, DamageType damageType, StatusEffect statusEffect)
    {
        this.targetID = targetID;
        this.damage = damage;
        this.damageType = damageType;
        this.statusEffect = statusEffect;
    }


    @Override
    public boolean isProcessed() {
        return processed;
    }

    @Override
    public void setProcessed(boolean bool)
    {
        processed = bool;
    }
}
