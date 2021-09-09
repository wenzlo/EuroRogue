package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class EnragedListener extends StatusEffectListener
{
    public EnragedListener(EuroRogue game){
        super(game);
        effect= StatusEffect.ENRAGED;
    }
}
