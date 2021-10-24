package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import squidpony.squidgrid.gui.gdx.SColor;

public class LightCmp implements Component
{
    public int level = 0;
    public float color = SColor.BLACK.toFloatBits();
    public float flicker = 0f;
    public float strobe = 0f;

    public LightCmp(){}

    public LightCmp(int level, float color)
    {
        this.level = level;
        this.color = color;
    }

    public LightCmp(int level, float color, float flicker, float strobe)
    {
        this.level = level;
        this.color = color;
        this.flicker = flicker;
        this.strobe = strobe;

    }

}
