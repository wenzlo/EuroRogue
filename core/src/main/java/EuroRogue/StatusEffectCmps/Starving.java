package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Starving extends StatusEffectCmp
{

    public Starving()
    {
        statMultipliers.put(StatType.MAX_HP, .5f);
        statusEffect = StatusEffect.STARVING;
        seRemovalType = SERemovalType.OTHER;
        lightChange = false;
    }

}
