package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class CalescentListener extends StatusEffectListener
{
    public CalescentListener(EuroRogue game){
        super(game);
        effect= StatusEffect.CALESCENT;}
}
