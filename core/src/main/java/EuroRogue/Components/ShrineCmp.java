package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.School;

public class ShrineCmp implements Component
{
    public School school;
    public ArrayList<Skill> skillOffer = new ArrayList<>();
    public int charges = -1;

    public ShrineCmp(){}

    public ShrineCmp(School school) { this.school = school; }
}
