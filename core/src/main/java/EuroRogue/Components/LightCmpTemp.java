package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

public class LightCmpTemp implements Component
{
    public int level;
    public float color;
    public float flicker;
    public float strobe;

    public LightCmpTemp(int level, float color)
    {
        this.level = level;
        this.color = color;
        this.flicker = 0f;
        this.strobe = 0f;
    }

    public LightCmpTemp(int level, float color, float flicker, float strobe)
    {
        this.level = level;
        this.color = color;
        this.flicker = flicker;
        this.strobe = strobe;
    }

}
