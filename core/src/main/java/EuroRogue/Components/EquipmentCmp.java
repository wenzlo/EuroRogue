package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

import EuroRogue.StatType;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;


public class EquipmentCmp implements Component
{
    public EquipmentSlot[] slotsOccupied;
    public boolean equipped = false;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public HashMap<StatType, Integer> statReqs = new HashMap<>();

    public EquipmentCmp(EquipmentSlot[] slots)
    {
        this.slotsOccupied=slots;
    }

    public boolean canEquip (StatsCmp statsCmp)
    {
        for(StatType statType : statReqs.keySet())
            if(statsCmp.getStat(statType)<statReqs.get(statType)) return false;

        return true;
    }

}
