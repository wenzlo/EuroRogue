package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import squidpony.squidgrid.gui.gdx.SColor;

public class CharCmp implements Component
{
    public char chr;
    public char lChr;
    public char rChr;
    public SColor color;
    public Character armorChr = null;
    public SColor armorColor = null;

    public CharCmp(){}
    public CharCmp (char chr, SColor color)
    {
        this.chr = chr;
        this. lChr = '•';
        this. rChr = '•';
        this.color = color;
    }
    public CharCmp (char chr, char lChr, char rChr, SColor color)
    {
        this.chr = chr;
        this.lChr = lChr;
        this.rChr = rChr;
        this.color = color;
    }
}
