package EuroRogue.EventComponents;

import java.util.ArrayList;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Skill;

public class CodexEvt implements IEventComponent
{
    public boolean processed = false;
    public Integer actorID;
    public List<Skill> addToKnown = new ArrayList<>();
    public List<Skill> unPrepare = new ArrayList<>();
    public List<Skill> prepare = new ArrayList<>();

    public CodexEvt (Integer actorID, List<Skill> addToKnown, List<Skill> unPrepare, List<Skill> prepare)
    {
        this.actorID = actorID;
        if(addToKnown!=null)this.addToKnown = addToKnown;
        if(unPrepare!=null)this.unPrepare = unPrepare;
        if(prepare!=null)this.prepare = prepare;
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
