package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.StatusEffectCmps.StatusEffect;

public class FoodCmp implements Component
{
    StatusEffect[] effects;

    public FoodCmp()
    {
        this.effects = new StatusEffect[0];
    }
}
