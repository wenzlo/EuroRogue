package EuroRogue;

public enum DamageType
{
    PIERCING(DamageClass.PHYSICAL, "Piercing"),
    BLUDGEONING(DamageClass.PHYSICAL, "Bludgeoning"),
    SLASHING(DamageClass.PHYSICAL, "Slashing"),
    ARCANE(DamageClass.MAGICAL, "Arcane"),
    FIRE(DamageClass.MAGICAL, "Fire"),
    ICE(DamageClass.MAGICAL, "Ice"),
    NONE(DamageClass.NONE, "None");

    public DamageClass damageClass;
    public String name;

    DamageType(DamageClass damageClass, String name)
    {
        this.damageClass=damageClass;
        this.name = name;
    }

    public enum DamageClass
    {
        PHYSICAL,
        MAGICAL,
        NONE
    }
}
