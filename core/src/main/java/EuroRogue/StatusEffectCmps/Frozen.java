package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Frozen extends StatusEffectCmp
{
    public Frozen()
    {
        statusEffect = StatusEffect.FROZEN;
        statMultipliers.put(StatType.FIRE_DEF, 1.5f);
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
}
