package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

public class LightCmp implements Component
{
    public int level;
    public float color;
    public float flicker;
    public float strobe;

    public LightCmp(int level, float color)
    {
        this.level = level;
        this.color = color;
    }

}
