package EuroRogue.EventComponents;


import EuroRogue.IColoredString;
import squidpony.squidgrid.gui.gdx.SColor;

public class LogEvt implements  IEventComponent
{
    public int tick;
    public IColoredString.Impl<SColor> entry;
    public boolean processed = false;

    public LogEvt(){}
    public LogEvt (int tick, IColoredString.Impl<SColor> entry)
    {
        this.tick=tick;
        this.entry=entry;
    }
    @Override
    public boolean isProcessed() {
        return processed;
    }

    @Override
    public void setProcessed(boolean bool) {
        processed = bool;
    }

}
