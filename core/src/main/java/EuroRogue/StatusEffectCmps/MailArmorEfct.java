package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class MailArmorEfct extends StatusEffectCmp
{
    public MailArmorEfct()
    {

        statMultipliers.put(StatType.TT_MELEE, 1.5f);
        statMultipliers.put(StatType.TT_MOVE, 1.5f);
        statMultipliers.put(StatType.TT_CAST, 1.5f);
        statMultipliers.put(StatType.TT_REST, 1.5f);
        statMultipliers.put(StatType.BLUDG_DEF, 1.20f);
        statMultipliers.put(StatType.PIERCE_DEF, 1.4f);
        statMultipliers.put(StatType.SLASH_DEF, 1.4f);

        statMultipliers.put(StatType.MOVE_SND_LVL, 2.0f);

        statusEffect = StatusEffect.M_ARMOR_EFCT;
        name = statusEffect.name;
        seRemovalType = SERemovalType.OTHER;
        lightChange = false;
    }

}
