package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class StaffEfct extends StatusEffectCmp
{
    public StaffEfct()
    {
        statMultipliers.put(StatType.ATTACK_PWR, 1.25f);
        statusEffect = StatusEffect.STAFF_EFCT;
        name = statusEffect.name;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
    public StaffEfct(float ttMeleeMult, float attackPwrMult)
    {

        statMultipliers.put(StatType.TT_MELEE, ttMeleeMult);
        statMultipliers.put(StatType.ATTACK_PWR, attackPwrMult);
        statusEffect = StatusEffect.STAFF_EFCT;
        name = statusEffect.name;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
}
