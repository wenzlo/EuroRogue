package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.AbilityCmpSubSystems.Skill;
import squidpony.squidmath.Coord;

public class AimingCmp implements Component
{
    public Coord aimCoord;
    public Skill skill;

    public AimingCmp(){}
    public AimingCmp(Coord aimCoord, Skill skill)
    {
        this.aimCoord = aimCoord;
        this.skill = skill;
    }

}
