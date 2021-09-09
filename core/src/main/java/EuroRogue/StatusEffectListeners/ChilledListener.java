package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class ChilledListener extends StatusEffectListener
{
    public ChilledListener(EuroRogue game){
        super(game);
        effect= StatusEffect.CHILLED;
    }
}
