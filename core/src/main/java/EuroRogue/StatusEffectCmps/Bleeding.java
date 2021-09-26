package EuroRogue.StatusEffectCmps;

public class Bleeding extends StatusEffectCmp
{
    public int damagePerMove = 1;

    public Bleeding()
    {
        name = "Bleeding 1";
        statusEffect = StatusEffect.BLEEDING;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }


}
