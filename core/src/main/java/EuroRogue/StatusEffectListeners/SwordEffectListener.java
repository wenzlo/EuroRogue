package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class SwordEffectListener extends StatusEffectListener
{
    public SwordEffectListener(EuroRogue game){
        super(game);
        effect= StatusEffect.SWORD_EFCT;
    }
}
