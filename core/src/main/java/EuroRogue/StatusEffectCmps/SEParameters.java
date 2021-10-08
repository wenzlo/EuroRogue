package EuroRogue.StatusEffectCmps;

import EuroRogue.TargetType;

public class SEParameters
{
    public TargetType targetType;
    public SERemovalType seRemovalType;

    public SEParameters(TargetType targetType, SERemovalType seRemovalType)
    {
        this.targetType = targetType;
        this.seRemovalType = seRemovalType;

    }
}
