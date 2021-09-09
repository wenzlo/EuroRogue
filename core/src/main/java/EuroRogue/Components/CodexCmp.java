package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.School;

public class CodexCmp implements Component
{
    public List<Skill> known = new ArrayList();
    public List<Skill> prepared = new ArrayList();

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(known+"\n");
        stringBuilder.append(prepared+"\n");
        return stringBuilder.toString();
    }

    public List<Skill> getPreparedActions()
    {
        List<Skill> preparedActions = new ArrayList<>();
        for(Skill skill:prepared)
        {
            if(skill.skillType== Skill.SkillType.ACTION || skill.skillType== Skill.SkillType.BUFF) preparedActions.add(skill);
        }
        return preparedActions;
    }
    public List<Skill> getPreparedReactions()
    {
        List<Skill> preparedReactions = new ArrayList<>();
        for(Skill skill:prepared)
        {
            if(skill.skillType== Skill.SkillType.REACTION) preparedReactions.add(skill);
        }
        return preparedReactions;
    }
    public List<School> getExcludedSchools()
    {
        List<School> schools = new ArrayList<>();
        for(Skill skill:prepared)
        {
            if(!schools.contains(skill.school)) schools.add(School.gtExclusionFor(skill.school));
        }
        return  schools;
    }

}
