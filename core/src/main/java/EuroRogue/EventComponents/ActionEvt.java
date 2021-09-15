package EuroRogue.EventComponents;

import java.util.ArrayList;
import java.util.HashMap;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class ActionEvt implements IEventComponent {
    public boolean processed=false;
    public Integer performerID;
    public Integer scrollID;
    public ArrayList<Integer> targetIDs;
    public Integer baseDmg;
    public Integer finalDmg;
    public HashMap<StatusEffect, SEParameters> statusEffects;
    public Skill skill;

    public ActionEvt(){}

    public ActionEvt(Integer performerID, Integer scrollID, Skill skill, ArrayList<Integer> targetIDs, Integer baseDmg, HashMap<StatusEffect, SEParameters> statusEffects)
    {
        this.performerID = performerID;
        this.scrollID=scrollID;
        this.skill=skill;
        this.targetIDs=targetIDs;
        this.baseDmg = baseDmg;
        this.finalDmg = baseDmg;
        this.statusEffects=statusEffects;
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
