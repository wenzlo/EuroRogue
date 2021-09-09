package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Enraged extends StatusEffectCmp
{
    public Enraged()
    {
        statMultipliers.put(StatType.ATTACK_PWR, 1.25f);
        statMultipliers.put(StatType.TT_CAST, 2f);
        statMultipliers.put(StatType.TT_REST, 2f);
        statusEffect = StatusEffect.ENRAGED;
        seRemovalType = SERemovalType.SHORT_REST;
        lightChange = false;
    }
}
