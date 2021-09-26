package EuroRogue.StatusEffectCmps;

public class WaterWalking extends StatusEffectCmp
{
    public WaterWalking()

    {

        statusEffect = StatusEffect.WATER_WALKING;
        name = statusEffect.name;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }

}
