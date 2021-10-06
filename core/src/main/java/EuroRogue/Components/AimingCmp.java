package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.AbilityCmpSubSystems.Skill;
import squidpony.squidmath.Coord;

public class AimingCmp implements Component
{
    public Skill skill;
    public boolean scroll;

    public AimingCmp(){}
    public AimingCmp(Skill skill, boolean scroll)
    {
        this.skill = skill;
        this.scroll = scroll;
    }

}
