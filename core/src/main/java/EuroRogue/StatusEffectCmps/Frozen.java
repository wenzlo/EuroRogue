package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Frozen extends StatusEffectCmp
{
    public Frozen()
    {
        statusEffect = StatusEffect.FROZEN;
        name = statusEffect.name;
        statMultipliers.put(StatType.FIRE_DEF, 1.5f);
        statMultipliers.put(StatType.BLUDG_DEF, 0.5f);
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
}
