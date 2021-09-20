package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.AbilityCmpSubSystems.Skill;
import squidpony.squidmath.Coord;

public class AimingCmp implements Component
{
    public Skill skill;

    public AimingCmp(){}
    public AimingCmp(Skill skill)
    {
        this.skill = skill;
    }

}
