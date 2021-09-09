package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class EnlightenedListener extends StatusEffectListener
{
    public EnlightenedListener(EuroRogue game){
        super(game);
        effect= StatusEffect.ENLIGHTENED;
    }
}
