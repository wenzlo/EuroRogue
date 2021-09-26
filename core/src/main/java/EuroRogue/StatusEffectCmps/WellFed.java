package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class WellFed extends StatusEffectCmp
{

    public WellFed()
    {
        statMultipliers.put(StatType.MAX_HP, 1.25f);
        statusEffect = StatusEffect.WELL_FED;
        name = statusEffect.name;
        seRemovalType = SERemovalType.LONG_REST;
        lightChange = false;
    }

}
