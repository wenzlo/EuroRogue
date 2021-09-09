package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class ExhaustedListener extends StatusEffectListener
{
    public ExhaustedListener(EuroRogue game){
        super(game);
        effect = StatusEffect.EXHAUSTED;
    }
}
