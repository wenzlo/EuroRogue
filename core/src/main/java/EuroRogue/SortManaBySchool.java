package EuroRogue;

import java.util.Comparator;

public class SortManaBySchool implements Comparator<School>
{
    public int compare(School mana1, School mana2) {

        int sch1Score = 0;
        int sch2Score = 0;
        switch(mana1)
        {

            case FIR:
                sch1Score+=4;
                break;
            case ICE:
                sch1Score+=3;
                break;
            case SUB:
                sch1Score+=5;
                break;
            case ARC:
                sch1Score+=2;
                break;
            case WAR:
                sch1Score+=1;
                break;

        }

        switch(mana2)
        {

            case FIR:
                sch2Score+=4;
                break;
            case ICE:
                sch2Score+=3;
                break;
            case SUB:
                sch2Score+=5;
                break;
            case ARC:
                sch2Score+=2;
                break;
            case WAR:
                sch2Score+=1;
                break;

        }
        return Double.compare(sch1Score, sch2Score);
    }
}
