package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Staggered extends StatusEffectCmp
{


    public Staggered()
    {

        statMultipliers.put(StatType.TT_MOVE, 1.25f);
        statMultipliers.put(StatType.TT_MELEE, 1.25f);
        statMultipliers.put(StatType.TT_CAST, 1.25f);
        statMultipliers.put(StatType.TT_REST, 1.25f);
        statusEffect = StatusEffect.STAGGERED;
        name = "Staggered I";
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }

}
