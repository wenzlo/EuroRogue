package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.AbilityCmpSubSystems.MeleeAttack;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class StaffEffectListener extends StatusEffectListener
{
    public StaffEffectListener(EuroRogue game){
        super(game);
        effect= StatusEffect.STAFF_EFCT;
    }

    @Override
    public void entityAdded(Entity entity) {
        super.entityAdded(entity);
        MeleeAttack meleeAttack = (MeleeAttack) CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, entity);
        meleeAttack.getAOE().setMaxRange(2);
    }

    @Override
    public void entityRemoved(Entity entity) {

        MeleeAttack meleeAttack = (MeleeAttack) CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, entity);
        meleeAttack.getAOE().setMaxRange(1);
    }
}




