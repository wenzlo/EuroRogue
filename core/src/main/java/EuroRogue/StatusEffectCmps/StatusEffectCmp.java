package EuroRogue.StatusEffectCmps;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.HashMap;

import EuroRogue.StatType;

public class StatusEffectCmp implements Component
{
    public HashMap<StatType, Float> statMultipliers = new HashMap<>();
    public StatusEffect statusEffect;
    public String name = "default";
    public SERemovalType seRemovalType;
    public Integer lastTick;
    public boolean display = true;
    public boolean lightChange = false;
    public Integer lightLevel = null;
    public Float lightColor = null;
    public Float flicker = 0f;
    public Float strobe = 0f;


    public float getStatMultiplier(StatType stat)
    {
        return statMultipliers.get(stat);
    }
    public ArrayList<StatType> getAffectedStats()
    {
        ArrayList<StatType> affectedStats = new ArrayList<>();
        affectedStats.addAll(statMultipliers.keySet());
        return affectedStats;
    }


}
