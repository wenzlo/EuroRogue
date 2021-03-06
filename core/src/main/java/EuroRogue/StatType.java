package EuroRogue;

import java.util.List;

import squidpony.StringKit;

public enum StatType
{
    STR,
    DEX,
    CON,
    PERC,
    INTEL,
    TT_MOVE,
    TT_MELEE,
    TT_CAST,
    TT_REST,
    ATTACK_PWR,
    SPELL_PWR,
    ARC_DEF,
    FIRE_DEF,
    ICE_DEF,
    BLUDG_DEF,
    SLASH_DEF,
    PIERCE_DEF,
    MAX_HP,
    MOVE_SND_LVL,
    MELEE_SND_LVL,
    SND_D_LVL,
    SPELL_SND_LVL,
    LIGHT_D_LVL,
    VISIBLE_D_LVL,
    NV_RADIUS
    ;


    StatType () { }

    public static StatType[] CORE_STATS = new StatType[] {STR, DEX, CON, PERC, INTEL};

    public static List<String>  getDescription(StatType stat, int wrapWidth){

        String description = "";
        switch (stat)
        {

        }

        return StringKit.wrap(description, wrapWidth);

    }



}
