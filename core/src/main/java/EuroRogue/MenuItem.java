package EuroRogue;

public class MenuItem
{
    public IColoredString.Impl label;
    private Runnable primaryAction = null;
    private Runnable secondaryAction = null;

    public MenuItem (IColoredString.Impl iColoredString)
    {
        this.label=iColoredString;

    }
    public void addPrimaryAction (Runnable action)
    {
        primaryAction = action;
    }
    public void addSecondaryAction (Runnable action)
    {
        secondaryAction = action;
    }
    public void runPrimaryAction() { if(primaryAction!=null) primaryAction.run();}
    public void runSecondaryAction() {if(secondaryAction!=null) secondaryAction.run();}
}
