package EuroRogue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import EuroRogue.Components.ManaPoolCmp;

public enum StatType
{
    STR(Arrays.asList(School.PHY, School.PHY, School.ARC)),
    DEX(Arrays.asList(School.PHY, School.PHY, School.FIR)),
    CON(Arrays.asList(School.PHY, School.FIR, School.ICE)),
    PERC(Arrays.asList(School.PHY, School.ARC, School.ICE)),
    INTEL(Arrays.asList(School.ARC, School.ICE, School.FIR)),
    TT_MOVE(null),
    TT_MELEE(null),
    TT_CAST(null),
    TT_REST(null),
    ATTACK_PWR(null),
    SPELL_PWR(null),
    ARC_DEF(null),
    FIRE_DEF(null),
    ICE_DEF(null),
    BLUDG_DEF(null),
    SLASH_DEF(null),
    MAX_HP(null),
    MOVE_SND_LVL(null),
    MELEE_SND_LVL(null),
    SPELL_SND_LVL(null),
    PIERCE_DEF(null);

    public List<School> cost;
    StatType (List<School> cost)
    {
        this.cost = cost;
    }

    public static StatType[] CORE_STATS = new StatType[] {STR, DEX, CON, PERC, INTEL};

    public static boolean afford(StatType statType, ManaPoolCmp manaPoolCmp)
    {
        for(School mana : statType.cost)
        {
            if(Collections.frequency(statType.cost, mana)> Collections.frequency(manaPoolCmp.spent, mana))
            {
                return false;
            }
        }
        return  true;
    }

}
