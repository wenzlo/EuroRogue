package EuroRogue.StatusEffectCmps;

import EuroRogue.Components.StatsCmp;
import EuroRogue.StatType;

public class Enlightened extends StatusEffectCmp
{

    public Enlightened(StatsCmp statsCmp)
    {
        statMultipliers.put(StatType.SPELL_PWR, 1f+(statsCmp.getIntel()*0.1f));
        statMultipliers.put(StatType.TT_MELEE, 2f);
        statMultipliers.put(StatType.TT_MOVE, 2f);
        statMultipliers.put(StatType.TT_REST, 2f);
        statusEffect = StatusEffect.ENLIGHTENED;
        name = statusEffect.name;
        lightChange = false;
    }
    public Enlightened(float spellPowerMult)
    {
        statMultipliers.put(StatType.SPELL_PWR, 1f+spellPowerMult);
        statMultipliers.put(StatType.TT_MELEE, 2f);
        statMultipliers.put(StatType.TT_MOVE, 2f);
        statMultipliers.put(StatType.TT_REST, 2f);
        statusEffect = StatusEffect.ENLIGHTENED;
        name = statusEffect.name;
        lightChange = false;
    }

}
