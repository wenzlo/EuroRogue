package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Chilled extends StatusEffectCmp
{
    public Chilled()
    {
        statMultipliers.put(StatType.TT_MOVE, 1.5f);
        statMultipliers.put(StatType.TT_MELEE, 1.5f);
        statMultipliers.put(StatType.BLUDG_DEF, .75f);
        statMultipliers.put(StatType.PIERCE_DEF, .75f);
        statMultipliers.put(StatType.SLASH_DEF, .75f);
        statMultipliers.put(StatType.ICE_DEF, .75f);
        statMultipliers.put(StatType.FIRE_DEF, 1.25f);
        statusEffect = StatusEffect.CHILLED;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
}
