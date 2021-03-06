package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.School;
import EuroRogue.SortManaBySchool;
import squidpony.panel.IColoredString;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.GWTRNG;

public class ManaPoolCmp implements Component
{
    public List<School> active = new ArrayList<>();
    public List<School> spent = new ArrayList<>();
    public List<School> attuned = new ArrayList<>();
    public int numAttunedSlots;
    private final GWTRNG rng = new GWTRNG();

    public ManaPoolCmp(){}
    public ManaPoolCmp(int numAttunedSlots)
    {
        this.numAttunedSlots = numAttunedSlots;
    }

    public ManaPoolCmp(Collection<School> manaPool, int numAttunedSlots)
    {
        spent.addAll(manaPool);
        this.numAttunedSlots = numAttunedSlots;
    }

    @Override
    public String toString()
    {
        return active + "\n" +
                spent + "\n" +
                attuned + "\n";

    }
    public List<School> allMana()
    {
        List<School> allMana = new ArrayList<>();
        allMana.addAll(active);
        allMana.addAll(spent);
        allMana.addAll(attuned);
        return allMana;
    }
    public List<School> unattunedMana()
    {
        List<School>mana = new ArrayList<>();
        mana.addAll(active);
        mana.addAll(spent);
        return mana;
    }

    public IColoredString.Impl<SColor> activeToIColoredString()
    {
        IColoredString.Impl<SColor> iColoredString = new IColoredString.Impl<>();
        Collections.sort(active, new SortManaBySchool());
        for(School mana:active)
        {
            SColor color = mana.color;
            iColoredString.append('■', color);
        }

        return  iColoredString;
    }
    public List<IColoredString<SColor>> spentToIColoredStrings()
    {
        IColoredString.Impl<SColor> iColoredString = new IColoredString.Impl<>();
        List<IColoredString<SColor>> spentPool = new ArrayList<>();
        Collections.sort(spent, new SortManaBySchool());
        for(School mana:spent)
        {
            SColor color = mana.color;
            iColoredString.append('■', color);
            if (iColoredString.length() == 6) {
                spentPool.add(iColoredString);
                iColoredString = new IColoredString.Impl<>();
            }

        }
        spentPool.add(iColoredString);

        return  spentPool;
    }
    public List<IColoredString<SColor>> attunedToIColoredStrings(int attunedSlots)
    {
        IColoredString.Impl<SColor> iColoredString = new IColoredString.Impl<>();
        List<IColoredString<SColor>> attunedPool = new ArrayList<>();
        Collections.sort(attuned, new SortManaBySchool());
        for(School mana:attuned) {
            SColor color = mana.color;
            iColoredString.append('■', color);
            if (iColoredString.length() == 6) {
                attunedPool.add(iColoredString);
                iColoredString = new IColoredString.Impl<>();
            }
        }
        for(int i=0; i<attunedSlots-attuned.size(); i++)
        {
            iColoredString.append('■', SColor.TRANSPARENT);
            if(iColoredString.length()==6)
            {
                attunedPool.add(iColoredString);
                iColoredString = new IColoredString.Impl<>();
            }
        }

        for(int i=0; i<12-attunedSlots; i++)
        {
            iColoredString.append('■', SColor.WHITE);
            if(iColoredString.length()==6)
            {
                attunedPool.add(iColoredString);
                iColoredString = new IColoredString.Impl<>();
            }
        }

        attunedPool.add(iColoredString);

        return  attunedPool;
    }
    public void attuneMana (School[] manaArray)
    {
        for(School mana:manaArray)
        {
            spent.remove(mana);
            attuned.add(mana);
        }
    }
    public void attuneMana (List<School> manaArray)
    {
        for(School mana:manaArray)
        {
            spent.remove(mana);
            attuned.add(mana);
        }
    }
    public void unattuneMana (School[] manaArray)
    {
        for(School mana:manaArray)
        {
            attuned.remove(mana);
            spent.add(mana);
        }
    }
    public void unattuneMana (List<School> manaArray)
    {
        for(School mana:manaArray)
        {
            attuned.remove(mana);
            spent.add(mana);
        }
    }
    public void spendMana(School[] cost)
    {
        for(School mana:cost)
        {
            active.remove(mana);
            spent.add(mana);
        }
    }
    public void spendMana(List<School> cost)
    {
        for(School mana:cost)
        {
            active.remove(mana);
            spent.add(mana);
        }
    }
    public void recoverMana(int numToRecover)
    {
        numToRecover = Math.min(numToRecover, spent.size());
        numToRecover = Math.min(numToRecover, 6-active.size());
        for(int i=0; i<numToRecover; i++)
        {
            School manaToRecover = rng.getRandomElement(spent);
            spent.remove(manaToRecover);
            active.add(manaToRecover);
        }
    }
    public void recoverMana()
    {
        recoverMana(6-active.size());
    }

    public void removeMana (School[] manaArray, StatsCmp statsCmp)
    {
        removeMana(Arrays.asList(manaArray), statsCmp);
    }
    public void removeMana (Collection<School> manaArray, StatsCmp statsCmp)
    {
        for(School mana:manaArray)
        {
            if(spent.contains(mana)) spent.remove(mana);
            else if(active.contains(mana)) active.remove(mana);
            statsCmp.setSpirit(unattunedMana().size());

        }
    }
    public void addMana (School[] manaArray, StatsCmp statsCmp) { addMana(Arrays.asList(manaArray), statsCmp); }
    public void addMana (Collection<School> manaArray, StatsCmp statsCmp)
    {
        for(School mana:manaArray) spent.add(mana);
        statsCmp.setSpirit(unattunedMana().size());
    }
    public boolean inert(CodexCmp codexCmp)
    {
        for(Skill skill : codexCmp.prepared)
        {
            if(skill.skillType == Skill.SkillType.REACTION) continue;
            if(active.containsAll(Arrays.asList(skill.castingCost)) ) return false;
        }
        return true;
    }

    public boolean canAfford(Skill skill)
    {

        for(School school : skill.castingCost)
        {
            int costFrequency = Collections.frequency(Arrays.asList(skill.castingCost), school);
            int activeFrequency = Collections.frequency(active, school);

            if(activeFrequency<costFrequency) return false;

        }
        return true;
    }
    public void setUnattunedManaSlots(StatsCmp statsCmp)
    {

    }


}
