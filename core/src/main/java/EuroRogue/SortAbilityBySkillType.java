package EuroRogue;

import java.util.Comparator;

import EuroRogue.AbilityCmpSubSystems.IAbilityCmpSubSys;

public class SortAbilityBySkillType implements Comparator<IAbilityCmpSubSys>
{
    public int compare(IAbilityCmpSubSys skillType1, IAbilityCmpSubSys skillType2) {

        int st1Score = 0;
        int st2Score = 0;
        switch(skillType1.getSkill().skillType)
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

        switch(skillType2.getSkill().skillType)
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
