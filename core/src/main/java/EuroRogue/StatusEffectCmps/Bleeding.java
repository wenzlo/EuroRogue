package EuroRogue.StatusEffectCmps;

public class Bleeding extends StatusEffectCmp
{
    public Bleeding()
    {
        statusEffect = StatusEffect.BLEEDING;
        seRemovalType = SERemovalType.TIMED;
        lightChange = false;
    }

    public int damagePerMove = 1;
}
