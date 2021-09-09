package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class QStaffEffectListener extends StatusEffectListener
{
    public QStaffEffectListener(EuroRogue game){
        super(game);
        effect= StatusEffect.QSTAFF_EFCT;
    }
}
