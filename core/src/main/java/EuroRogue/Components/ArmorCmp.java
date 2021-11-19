package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.ArmorType;



public class ArmorCmp implements Component
{
    public ArmorType armorType;

    public ArmorCmp(){}
    public ArmorCmp(ArmorType armorType)
    {
        this.armorType=armorType;
    }

}
