package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import squidpony.squidgrid.gui.gdx.SColor;

public class CharCmp implements Component
{
    public char chr;
    public SColor color;
    public Character armorChr = null;
    public SColor armorColor = null;

    public CharCmp(){}
    public CharCmp (char chr, SColor color)
    {
        this.chr = chr;
        this.color = color;
    }
}
