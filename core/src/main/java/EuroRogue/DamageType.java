package EuroRogue;

public enum DamageType
{
    PIERCING(DamageClass.PHYSICAL),
    BLUDGEONING(DamageClass.PHYSICAL),
    SLASHING(DamageClass.PHYSICAL),
    ARCANE(DamageClass.MAGICAL),
    FIRE(DamageClass.MAGICAL),
    ICE(DamageClass.MAGICAL),
    NONE(DamageClass.NONE);

    DamageClass damageClass;

    DamageType(DamageClass damageClass)
    {
        this.damageClass=damageClass;
    }

    public enum DamageClass
    {
        PHYSICAL,
        MAGICAL,
        NONE
    }
}
