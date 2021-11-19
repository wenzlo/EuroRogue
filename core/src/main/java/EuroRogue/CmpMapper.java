package EuroRogue;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.AI.AIType;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class CmpMapper
{
    public static HashMap<CmpType, ComponentMapper> compMappers = new HashMap<>();
    public static HashMap<Skill, ComponentMapper> abilityMappers = new HashMap<>();
    public static HashMap<StatusEffect, ComponentMapper> statusEffectMappers = new HashMap<>();
    public static HashMap<AIType, ComponentMapper> aiTypeComponentMappers = new HashMap<>();

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
        for(AIType ait: AIType.values())
        {
            aiTypeComponentMappers.put(ait, ComponentMapper.getFor(ait.cls));
        }
    }

    public static Component getComp(CmpType cmpType, Entity entity )
    {
        return compMappers.get(cmpType).get(entity);
    }
    public static boolean detected(Entity entity )
    {
        return compMappers.get(CmpType.DETECTED).get(entity)!=null;
    }

    public static Ability getAbilityComp(Skill skill, Entity entity)
    {
        return (Ability) abilityMappers.get(skill).get(entity);
    }
    public static Component getStatusEffectComp(StatusEffect effectType, Entity entity)
    {
        return statusEffectMappers.get(effectType).get(entity);
    }
    public static AICmp getAIComp(AIType aiType, Entity entity)
    {
        return (AICmp) aiTypeComponentMappers.get(aiType).get(entity);
    }

}
