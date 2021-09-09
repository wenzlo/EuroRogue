package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class PlateArmorListener extends StatusEffectListener
{
    public PlateArmorListener(EuroRogue game){
        super(game);
        effect = StatusEffect.P_ARMOR_EFCT;
    }
}
