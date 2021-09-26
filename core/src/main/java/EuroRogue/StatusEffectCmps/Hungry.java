package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Hungry extends StatusEffectCmp
{

    public Hungry()
    {

        statMultipliers.put(StatType.MAX_HP, .75f);
        statusEffect = StatusEffect.HUNGRY;
        name = statusEffect.name;

        seRemovalType = SERemovalType.OTHER;
        lightChange = false;
    }

}
