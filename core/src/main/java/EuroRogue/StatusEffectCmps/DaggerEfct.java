package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class DaggerEfct extends StatusEffectCmp
{
    public DaggerEfct()
    {
        statMultipliers.put(StatType.TT_MELEE, 0.75f);
        statMultipliers.put(StatType.ATTACK_PWR, 1f);
        statusEffect = StatusEffect.DAGGER_EFCT;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
}
