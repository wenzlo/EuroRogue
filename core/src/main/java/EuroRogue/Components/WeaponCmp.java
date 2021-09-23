package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import EuroRogue.WeaponType;

public class WeaponCmp implements Component
{
    public WeaponType weaponType;
    public boolean throwable = false;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();

    public WeaponCmp (WeaponType weaponType)
    {
        this.weaponType = weaponType;
        if(weaponType.appliedEffect!=null)
            this.statusEffects.put(weaponType.appliedEffect, new SEParameters(TargetType.ENEMY, SERemovalType.TIMED));
    }


}
