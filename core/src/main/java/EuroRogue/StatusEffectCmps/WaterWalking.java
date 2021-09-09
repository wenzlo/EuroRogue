package EuroRogue.StatusEffectCmps;

public class WaterWalking extends StatusEffectCmp
{
    public WaterWalking()

    {
        statusEffect = StatusEffect.WATER_WALKING;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }

}
