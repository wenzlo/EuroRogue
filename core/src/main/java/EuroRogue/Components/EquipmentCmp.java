package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

import EuroRogue.StatType;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.squidgrid.gui.gdx.SColor;


public class EquipmentCmp implements Component
{
    public EquipmentSlot[] slotsOccupied;
    public boolean equipped = false;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public HashMap<StatType, Integer> statReqs = new HashMap<>();
    public int lightLevel;
    public float lightColor;

    public EquipmentCmp(EquipmentSlot[] slots)
    {
        this.slotsOccupied=slots;
        this.lightLevel = 0;
        this.lightColor = SColor.BLACK.toFloatBits();
    }
    public EquipmentCmp(EquipmentSlot[] slots, int lightLevel, float lightColor)
    {
        this.slotsOccupied=slots;
        this.lightLevel = lightLevel;
        this.lightColor = lightColor;
    }

    public boolean canEquip (StatsCmp statsCmp)
    {
        for(StatType statType : statReqs.keySet())
            if(statsCmp.getStat(statType)<statReqs.get(statType)) return false;

        return true;
    }

}
