package EuroRogue.EventComponents;

import EuroRogue.Components.LevelCmp;
import EuroRogue.LevelType;

public class LevelEvt implements IEventComponent
{
    public boolean processed = false;
    public LevelType type;

    public LevelEvt(LevelType type) { this.type = type; };
    @Override
    public boolean isProcessed() {
        return processed;
    }

    @Override
    public void setProcessed(boolean bool) {
        processed = bool;
    }

}
