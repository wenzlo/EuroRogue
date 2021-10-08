package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.StatusEffectCmps.Bleeding;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class BleedingListener implements EntityListener {
    EuroRogue game;
    public BleedingListener(EuroRogue game) {
        this.game = game;
    }


    @Override
    public void entityAdded(Entity entity)
    {
        StatusEffectEvt statusEffectEvt = (StatusEffectEvt) CmpMapper.getComp(CmpType.STATUS_EFFECT_EVT, entity);
        if(statusEffectEvt.effect!=StatusEffect.BLEEDING) return;
        Entity targetEntity = game.getEntity(statusEffectEvt.targetID);
        Bleeding bleeding = (Bleeding) CmpMapper.getStatusEffectComp(StatusEffect.BLEEDING, targetEntity);
        if(bleeding!=null)
        {
            bleeding.damagePerMove ++;
            bleeding.name = bleeding.name+"I";
        }

    }


    @Override
    public void entityRemoved(Entity entity) {

    }
}
