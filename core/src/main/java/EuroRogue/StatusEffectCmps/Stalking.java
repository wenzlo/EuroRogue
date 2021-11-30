package EuroRogue.StatusEffectCmps;

import EuroRogue.StatType;

public class Stalking extends StatusEffectCmp
{
    double[][] costMap;

    public Stalking()
    {
        statMultipliers.put(StatType.NV_RADIUS, 1.75f);
        statMultipliers.put(StatType.VISIBLE_D_LVL, 65f);
        statMultipliers.put(StatType.SND_D_LVL, 0.5f);
        statMultipliers.put(StatType.LIGHT_D_LVL, 0.5f);
        statMultipliers.put(StatType.MOVE_SND_LVL, 0f);
        statMultipliers.put(StatType.MELEE_SND_LVL, 1f);
        statusEffect = StatusEffect.STALKING;
        name = statusEffect.name;
        lightChange = false;
    }


}
