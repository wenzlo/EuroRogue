package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.WindowCmp;
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
        if(statusEffectEvt==null) return;
        if(statusEffectEvt.effect!=StatusEffect.BLEEDING) return;
        Entity targetEntity = game.getEntity(statusEffectEvt.targetID);
        Bleeding bleeding = (Bleeding) CmpMapper.getStatusEffectComp(StatusEffect.BLEEDING, targetEntity);
        if(bleeding!=null)
        {
            bleeding.damagePerMove ++;
            bleeding.name = bleeding.name+"I";
            return;
        }
        ParticleEffectsCmp peaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, targetEntity);
        GlyphsCmp glyphsCmp = (GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, targetEntity);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW,game.dungeonWindow);
        peaCmp.addEffect(glyphsCmp.glyph, ParticleEffectsCmp.ParticleEffect.BLEED_P, windowCmp.display);
    }


    @Override
    public void entityRemoved(Entity entity) {

        ParticleEffectsCmp peaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
        if(peaCmp==null) return;
        GlyphsCmp glyphsCmp = (GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, entity);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW,game.dungeonWindow);

        peaCmp.removeEffect(glyphsCmp.glyph, ParticleEffectsCmp.ParticleEffect.BLEED_P, windowCmp.display);
    }
}
