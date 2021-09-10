package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import squidpony.squidgrid.SoundMap;

public class NoiseMap implements Component
{
    public SoundMap noiseMap;

    public NoiseMap(char[][] level)
    {
        this.noiseMap = new SoundMap(level);
    }

}
