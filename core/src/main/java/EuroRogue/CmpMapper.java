package EuroRogue;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class CmpMapper
{
    public static HashMap<CmpType, ComponentMapper> compMappers = new HashMap<>();
    public static HashMap<Skill, ComponentMapper> abilityMappers = new HashMap<>();
    public static HashMap<StatusEffect, ComponentMapper> statusEffectMappers = new HashMap<>();

    public CmpMapper()
    {
        for(CmpType ct: CmpType.values())
        {
            compMappers.put(ct, ComponentMapper.getFor(ct.type));
        }
        for(Skill st: Skill.values())
        {
            abilityMappers.put(st, ComponentMapper.getFor(st.cls));
        }
        for(StatusEffect et: StatusEffect.values())
        {
            statusEffectMappers.put(et, ComponentMapper.getFor(et.cls));
        }
    }

    public static Component getComp(CmpType cmpType, Entity entity )
    {
        return compMappers.get(cmpType).get(entity);
    }

    public static Component getAbilityComp(Skill skill, Entity entity)
    {
        return abilityMappers.get(skill).get(entity);
    }
    public static Component getStatusEffectComp(StatusEffect effectType, Entity entity)
    {
        return statusEffectMappers.get(effectType).get(entity);
    }

}
