package EuroRogue.StatusEffectCmps;

import EuroRogue.DamageType;
import EuroRogue.TargetType;

public class SEParameters
{
    public TargetType targetType;
    public SERemovalType seRemovalType;
    public DamageType resistanceType;

    public SEParameters(TargetType targetType, SERemovalType seRemovalType, DamageType resistanceType)
    {
        this.targetType = targetType;
        this.seRemovalType = seRemovalType;
        this.resistanceType = resistanceType;

    }
}
