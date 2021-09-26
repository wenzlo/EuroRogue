package EuroRogue.StatusEffectCmps;

import com.badlogic.gdx.graphics.Color;

import EuroRogue.StatType;
import squidpony.squidgrid.gui.gdx.SColor;

public class Burning extends StatusEffectCmp
{
    public Burning()
    {

        statMultipliers.put(StatType.TT_MOVE, 0.5f);
        statMultipliers.put(StatType.ARC_DEF, 0.75f);
        statMultipliers.put(StatType.FIRE_DEF, 0.75f);
        statMultipliers.put(StatType.ICE_DEF, 1.5f);
        statusEffect = StatusEffect.BURNING;
        name = statusEffect.name;
        seRemovalType = SERemovalType.TIMED;
        lightChange = true;
        lightColor = SColor.lerpFloatColors(SColor.SAFETY_ORANGE.toFloatBits(), Color.WHITE_FLOAT_BITS, 0.3f);
        lightLevel = 3;
        flicker = 1f;
    }
}
