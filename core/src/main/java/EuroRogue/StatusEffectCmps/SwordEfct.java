package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class SwordEfct extends StatusEffectCmp
{
    public SwordEfct()
    {
        statMultipliers.put(StatType.TT_MELEE, 1f);
        statMultipliers.put(StatType.ATTACK_PWR, 1.5f);
        statusEffect = StatusEffect.SWORD_EFCT;
        seRemovalType = SERemovalType.TIMED;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
    public SwordEfct(float ttMeleeMult, float attackPwrMult)
    {
        statMultipliers.put(StatType.TT_MELEE, ttMeleeMult);
        statMultipliers.put(StatType.ATTACK_PWR, attackPwrMult);
        statusEffect = StatusEffect.SWORD_EFCT;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
}
