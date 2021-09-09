package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.AbilityCmpSubSystems.IAbilityCmpSubSys;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.EventComponents.CodexEvt;
import EuroRogue.MyEntitySystem;

public class CodexSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;



    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(CodexEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        for(Entity entity:entities)
        {


            CodexEvt codexEvt = (CodexEvt) CmpMapper.getComp(CmpType.CODEX_EVT, entity);
            Entity actorEntity = getGame().getEntity(codexEvt.actorID);
            CodexCmp codex = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, actorEntity);
            codexEvt.processed=true;

            for(Skill skill:codexEvt.addToKnown) addSkill(skill, codex);
            for(Skill skill:codexEvt.prepare) prepareSkill(skill, actorEntity);
            for(Skill skill:codexEvt.unPrepare) unPrepSkill(skill, actorEntity);
        }
    }
    private void addSkill(Skill skill, CodexCmp codex)
    {
        codex.known.add(skill);
    }
    private boolean prepareSkill(Skill skill, Entity entity) {
        CodexCmp codex = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, entity);
        if(codex.prepared.contains(skill) || !codex.known.contains(skill)) return false;
        codex.prepared.add(skill);
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, entity);
        manaPoolCmp.attuneMana(skill.prepCost);
        addAbilityCmp(skill, entity);
        return true;
    }
    private boolean unPrepSkill(Skill skill, Entity entity)
    {
        CodexCmp codex = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, entity);
        if(!codex.prepared.contains(skill)) return false;
        codex.prepared.remove(skill);
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, entity);
        manaPoolCmp.unattuneMana(skill.prepCost);
        removeAbilityCmp(skill, entity);
        return true;
    }

    private void addAbilityCmp (Skill skill, Entity entity)
    {
        entity.add((IAbilityCmpSubSys.newAbilityCmp(skill)));

    }
    private void removeAbilityCmp (Skill skill, Entity entity)
    {
        entity.remove(skill.cls);
    }

}
