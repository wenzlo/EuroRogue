package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class StaggeredListener extends StatusEffectListener
{

    public StaggeredListener(EuroRogue game){
        super(game);
        effect= StatusEffect.STAGGERED;
    }


}
