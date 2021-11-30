package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Exhausted extends StatusEffectCmp
{


    public Exhausted()
    {
        this.statMultipliers.put(StatType.TT_CAST, 2f);
        this.statMultipliers.put(StatType.TT_MELEE, 2f);
        this.statusEffect = StatusEffect.EXHAUSTED;
        this.name = statusEffect.name;
        this.seRemovalType = SERemovalType.LONG_REST;
        this.lightChange = false;
    }


}
