package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class PlateArmorEfct extends StatusEffectCmp
{
    public PlateArmorEfct()
    {

        statMultipliers.put(StatType.TT_MELEE, 1.60f);
        statMultipliers.put(StatType.TT_MOVE, 1.60f);
        statMultipliers.put(StatType.TT_CAST, 1.60f);
        statMultipliers.put(StatType.TT_REST, 1.60f);

        statMultipliers.put(StatType.PIERCE_DEF, 1.6f);
        statMultipliers.put(StatType.SLASH_DEF, 1.6f);
        statMultipliers.put(StatType.BLUDG_DEF, 1.6f);

        statMultipliers.put(StatType.MOVE_SND_LVL, 2.5f);
        statusEffect = StatusEffect.P_ARMOR_EFCT;
        name = statusEffect.name;
        seRemovalType = SERemovalType.OTHER;
        lightChange = false;
        display = false;
    }

}
