package EuroRogue.StatusEffectCmps;

public class Bleeding extends StatusEffectCmp
{
    public int damagePerMove = 1;

    public Bleeding()
    {
        name = "Bleeding I";
        statusEffect = StatusEffect.BLEEDING;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }


}
