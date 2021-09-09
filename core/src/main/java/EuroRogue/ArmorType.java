package EuroRogue;

import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.squidgrid.gui.gdx.SColor;

public enum ArmorType
{
    LEATHER(StatusEffect.L_ARMOR_EFCT, SColor.BROWNER, "Leather Armor", 15),
    PLATE(StatusEffect.P_ARMOR_EFCT, SColor.LIGHT_GRAY, "Plate Armor", 25),
    MAIL(StatusEffect.M_ARMOR_EFCT, SColor.WILLOW_GREY, "Mail Armor", 20);

    StatusEffect grantedEffect;
    SColor color;
    String name;
    int soundLvl;

    ArmorType(StatusEffect grantedEffect, SColor color, String name, int soundLvl)
    {
        this.grantedEffect = grantedEffect;
        this.color = color;
        this.name = name;
        this.soundLvl = soundLvl;
    }
}
