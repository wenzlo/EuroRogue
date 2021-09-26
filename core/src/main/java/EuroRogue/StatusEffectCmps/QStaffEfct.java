package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class QStaffEfct extends StatusEffectCmp
{
    public QStaffEfct()
    {

        statMultipliers.put(StatType.TT_MELEE, 0.75f);
        statMultipliers.put(StatType.ATTACK_PWR, 1f);
        statusEffect = StatusEffect.QSTAFF_EFCT;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
    public QStaffEfct(float ttMeleeMult, float attackPwrMult)
    {
        statMultipliers.put(StatType.TT_MELEE, ttMeleeMult);
        statMultipliers.put(StatType.ATTACK_PWR, attackPwrMult);
        statusEffect = StatusEffect.QSTAFF_EFCT;
        name = statusEffect.name;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }
}
