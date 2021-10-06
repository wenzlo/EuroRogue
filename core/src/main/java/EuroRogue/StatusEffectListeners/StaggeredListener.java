package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.StatType;
import EuroRogue.StatusEffectCmps.Staggered;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class StaggeredListener extends StatusEffectListener
{

    public StaggeredListener(EuroRogue game){
        super(game);
        effect= StatusEffect.STAGGERED;
    }


}
