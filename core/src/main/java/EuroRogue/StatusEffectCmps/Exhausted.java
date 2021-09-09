package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Exhausted extends StatusEffectCmp
{


    public Exhausted()
    {
        statMultipliers.put(StatType.TT_MOVE, 1.5f);
        statusEffect = StatusEffect.EXHAUSTED;
        seRemovalType = SERemovalType.SHORT_REST;
        lightChange = false;
    }

}
