package EuroRogue.StatusEffectListeners;

import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class MailArmorListener extends StatusEffectListener
{
    public MailArmorListener(EuroRogue game){
        super(game);
        effect = StatusEffect.M_ARMOR_EFCT;
    }
}
