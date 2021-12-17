package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class LeatherArmorEfct extends StatusEffectCmp
{
    public LeatherArmorEfct()
    {

        statMultipliers.put(StatType.TT_MELEE, 1.20f);
        statMultipliers.put(StatType.TT_MOVE, 1.20f);
        statMultipliers.put(StatType.TT_CAST, 1.20f);
        statMultipliers.put(StatType.TT_REST, 1.20f);
        statMultipliers.put(StatType.PIERCE_DEF, 1.2f);
        statMultipliers.put(StatType.SLASH_DEF, 1.2f);
        statMultipliers.put(StatType.BLUDG_DEF, 1.2f);
        statMultipliers.put(StatType.MOVE_SND_LVL, 1.20f);
        statusEffect = StatusEffect.L_ARMOR_EFCT;
        name = statusEffect.name;
        seRemovalType = SERemovalType.OTHER;
        lightChange = false;
        display = false;
    }

}
