package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.AbilityCmpSubSystems.Skill;

public class ScrollCmp implements Component
{
    public Skill skill;
    public boolean consumed = false;


    public ScrollCmp(){}

    public ScrollCmp(Skill skill)
    {
        this.skill = skill;
    }
}
