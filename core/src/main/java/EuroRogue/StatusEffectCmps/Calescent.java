package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Calescent extends StatusEffectCmp
{
    public Calescent()
    {
        statMultipliers.put(StatType.TT_CAST, 1.5f);
        statMultipliers.put(StatType.TT_REST, 1.5f);
        statMultipliers.put(StatType.ARC_DEF, 0.75f);
        statMultipliers.put(StatType.FIRE_DEF, 0.75f);
        statMultipliers.put(StatType.ICE_DEF, 1.5f);
        statusEffect = StatusEffect.CALESCENT;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
}
