package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class DaggerListener extends StatusEffectListener
{
    public DaggerListener(EuroRogue game){
        super(game);
        effect= StatusEffect.DAGGER_EFCT;
    }
}
