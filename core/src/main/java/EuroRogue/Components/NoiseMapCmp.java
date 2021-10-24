package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import squidpony.squidgrid.SoundMap;

public class NoiseMapCmp implements Component
{
    public SoundMap noiseMap;

    public NoiseMapCmp(char[][] level)
    {
        this.noiseMap = new SoundMap(level);
    }

}
