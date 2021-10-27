package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.StatusEffectCmp;

public class BurningListener extends StatusEffectListener
{
    public BurningListener(EuroRogue game){
        super(game);
        effect= StatusEffect.BURNING;
    }

    @Override
    public void entityAdded(Entity entity) {
        super.entityAdded(entity);
        StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(effect, entity);
        addLightCmpTemp(entity, statusEffectCmp);
        ParticleEffectsCmp peaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, entity);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW,game.dungeonWindow);
        peaCmp.addEffect(glyphsCmp.glyph, ParticleEffectsCmp.ParticleEffect.BURNING_P, windowCmp.display);
    }

    @Override
    public void entityRemoved(Entity entity) {
        super.entityRemoved(entity);
        removeLightCmpTemp(entity);
        ParticleEffectsCmp peaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, entity);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW,game.dungeonWindow);
        peaCmp.removeEffect(glyphsCmp.glyph, ParticleEffectsCmp.ParticleEffect.BURNING_P, windowCmp.display);
    }

}
