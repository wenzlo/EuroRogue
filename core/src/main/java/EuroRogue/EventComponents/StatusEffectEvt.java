package EuroRogue.EventComponents;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class StatusEffectEvt implements IEventComponent
{
    public Integer tick;
    public Integer duration;
    public StatusEffect effect;
    public Skill skill;
    public Integer performerID;
    public Integer targetID;
    public boolean adding;
    public SERemovalType seRemovalType;
    public boolean processed = false;

    public StatusEffectEvt (){}

    public StatusEffectEvt (int tick, Integer duration, StatusEffect effect, Skill skill, Integer performerID, Integer targetID, boolean adding)
    {
        this.tick = tick;
        this.duration = duration;
        this.effect = effect;
        this.skill = skill;
        this.performerID = performerID;
        this.targetID = targetID;
        this.seRemovalType = SERemovalType.TIMED;
        this.adding = adding;
    }

    public StatusEffectEvt (int tick, Integer duration, StatusEffect effect, Skill skill, Integer performerID, Integer targetID, SERemovalType seRemovalType)
    {
        this.tick = tick;
        this.duration = duration;
        this.effect = effect;
        this.skill = skill;
        this.performerID = performerID;
        this.targetID = targetID;
        this.seRemovalType = seRemovalType;
        this.adding = true;
    }

    @Override
    public boolean isProcessed()
    {
        return processed;
    }

    @Override
    public void setProcessed(boolean bool) {
        processed = bool;
    }

}
