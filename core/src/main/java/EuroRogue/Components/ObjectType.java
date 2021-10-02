package EuroRogue.Components;

public enum ObjectType
{
    SHRINE('§', false);

    public char chr;
    public boolean traversable;

    ObjectType(char chr, boolean traversable)
    {
        this.chr = chr;
        this.traversable = traversable;
    }
}
