package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class LeatherArmorListener extends StatusEffectListener
{
    public LeatherArmorListener(EuroRogue game){
        super(game);
        effect = StatusEffect.L_ARMOR_EFCT;
    }
}
