package EuroRogue.StatusEffectCmps;

import EuroRogue.Components.StatsCmp;
import EuroRogue.StatType;

public class Enraged extends StatusEffectCmp
{
    public Enraged(StatsCmp statsCmp)
    {

        statMultipliers.put(StatType.ATTACK_PWR, 1f+(statsCmp.getStr()*0.1f));
        statMultipliers.put(StatType.TT_CAST, 2f);
        statMultipliers.put(StatType.TT_REST, 2f);
        statusEffect = StatusEffect.ENRAGED;
        name = statusEffect.name;
        seRemovalType = SERemovalType.SHORT_REST;
        lightChange = false;
    }
}
