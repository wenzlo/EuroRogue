package EuroRogue.EventComponents;

public class CharBuildLoadEvt implements IEventComponent
{
    public boolean processed = false;
    public String buildName;


    public CharBuildLoadEvt()
    {


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
