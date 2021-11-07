package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Stalking extends StatusEffectCmp
{

    public Stalking()
    {

        statMultipliers.put(StatType.VISIBLE_D_LVL, 75f);
        statMultipliers.put(StatType.MOVE_SND_LVL, 0f);
        statMultipliers.put(StatType.MELEE_SND_LVL, 1f);
        statMultipliers.put(StatType.TT_MOVE, 1.5f);
        statusEffect = StatusEffect.STALKING;
        name = statusEffect.name;
        lightChange = false;
    }


}
