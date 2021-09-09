package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.School;

public class ManaCmp implements Component
{
    public School school;

    public ManaCmp(){}

    public ManaCmp(School school)
    {
        this.school = school;
    }
}
