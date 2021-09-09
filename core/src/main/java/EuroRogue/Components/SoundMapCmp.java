package EuroRogue.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;

import squidpony.squidgrid.SoundMap;
import squidpony.squidgrid.gui.gdx.SColor;

public class SoundMapCmp implements Component
{
    public SoundMap soundMap;

    public SoundMapCmp(char[][] level)
    {
        this.soundMap = new SoundMap(level);
    }

}
