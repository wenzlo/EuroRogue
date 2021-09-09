package EuroRogue;

import EuroRogue.Components.EquipmentSlot;
import EuroRogue.StatusEffectCmps.StatusEffect;

public enum WeaponType
{
    DAGGER       ("Dagger", DamageType.PIERCING,   '+', new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP}, StatusEffect.BLEEDING, StatusEffect.DAGGER_EFCT),
    QUARTER_STAFF("Quarter Staff", DamageType.BLUDGEONING,'-', new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP},  null, StatusEffect.QSTAFF_EFCT),
    STAFF(        "Staff", DamageType.BLUDGEONING,'|', new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP, EquipmentSlot.LEFT_HAND_WEAP},  StatusEffect.STAGGERED, StatusEffect.STAFF_EFCT),
    SWORD        ("Sword", DamageType.SLASHING,   '†', new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP}, StatusEffect.BLEEDING, StatusEffect.SWORD_EFCT);

    public String name;
    public DamageType damageType;
    public char chr;
    public EquipmentSlot[] slots;
    public StatusEffect appliedEffect;
    public StatusEffect grantedEffect;

    WeaponType(String name, DamageType damageType, char chr, EquipmentSlot[] slots,StatusEffect appliedEffect, StatusEffect grantedEffect)
    {
        this.name = name;
        this.damageType = damageType;
        this.chr = chr;
        this.slots = slots;
        this.appliedEffect = appliedEffect;
        this.grantedEffect  = grantedEffect;
    }
}
