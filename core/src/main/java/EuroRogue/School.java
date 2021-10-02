package EuroRogue;

import squidpony.squidgrid.gui.gdx.SColor;

public enum School
{
    PHY("Physical", SColor.LIGHT_YELLOW_DYE),
    ARC("Arcane", SColor.PSYCHEDELIC_PURPLE),
    FIR("Fire", SColor.SAFETY_ORANGE),
    ICE("Ice", SColor.SKY_BLUE);

    public String name;
    public SColor color;



    School(String name, SColor color) {
        this.name = name;
        this.color = color;
    }
    public static School getExclusionFor(School school)
    {
        switch (school)
        {

            case FIR: return ICE;

            case ICE: return FIR;
        }
        return  null;
    }
}
