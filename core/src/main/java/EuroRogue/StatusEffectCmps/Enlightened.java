package EuroRogue.StatusEffectCmps;

import EuroRogue.Components.StatsCmp;
import EuroRogue.StatType;

public class Enlightened extends StatusEffectCmp
{
    public Enlightened()
    {
        statMultipliers.put(StatType.SPELL_PWR, 1.25f);
        statMultipliers.put(StatType.TT_CAST, 0.75f);
        statMultipliers.put(StatType.TT_MELEE, 2f);
        statMultipliers.put(StatType.TT_MOVE, 2f);
        statMultipliers.put(StatType.TT_REST, 2f);
        statusEffect = StatusEffect.ENLIGHTENED;
        seRemovalType = SERemovalType.SHORT_REST;
        lightChange = false;
    }

    public Enlightened(SERemovalType removalType, StatsCmp statsCmp)
    {
        statMultipliers.put(StatType.SPELL_PWR, 1f+(statsCmp.getIntel()*0.1f));
        statMultipliers.put(StatType.TT_MELEE, 2f);
        statMultipliers.put(StatType.TT_MOVE, 2f);
        statMultipliers.put(StatType.TT_REST, 2f);
        statusEffect = StatusEffect.ENLIGHTENED;
        seRemovalType = removalType;
        lightChange = false;
    }

}
