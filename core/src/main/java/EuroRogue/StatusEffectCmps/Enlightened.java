package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Enlightened extends StatusEffectCmp
{
    public Enlightened()
    {
        statMultipliers.put(StatType.SPELL_PWR, 1.25f);
        statMultipliers.put(StatType.TT_MELEE, 2f);
        statMultipliers.put(StatType.TT_MOVE, 2f);
        statMultipliers.put(StatType.TT_REST, 2f);
        statusEffect = StatusEffect.ENLIGHTENED;
        seRemovalType = SERemovalType.SHORT_REST;
        lightChange = false;
    }
}
