package EuroRogue;

import java.util.Comparator;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;


public class SortSkillBySkillType implements Comparator<Skill>
{
    public int compare(Skill skill1, Skill skill2) {

        int st1Score = 0;
        int st2Score = 0;
        switch(skill1.skillType)
        {

            case REACTION:
                st1Score+=2;
                break;
            case ACTION:
                st1Score+=1;
                break;
            case BUFF:
                st1Score+=3;
                break;


        }

        switch(skill2.skillType)
        {

            case REACTION:
                st2Score+=2;
                break;
            case ACTION:
                st2Score+=1;
                break;
            case BUFF:
                st2Score+=3;
                break;

        }
        return Double.compare(st1Score, st2Score);
    }

}
