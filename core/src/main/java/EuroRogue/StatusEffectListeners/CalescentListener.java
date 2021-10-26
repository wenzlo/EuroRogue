package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.ParticleEmittersCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class CalescentListener extends StatusEffectListener
{
    public CalescentListener(EuroRogue game){
        super(game);
        effect= StatusEffect.CALESCENT;}

    @Override
    public void entityAdded(Entity entity) {
        super.entityAdded(entity);
        ParticleEmittersCmp peaCmp = (ParticleEmittersCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, entity);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW,game.dungeonWindow);
        peaCmp.addEffect(glyphsCmp.glyph, ParticleEmittersCmp.ParticleEffect.CALESCENT_P, windowCmp.display);
    }

    @Override
    public void entityRemoved(Entity entity) {
        super.entityRemoved(entity);
        ParticleEmittersCmp peaCmp = (ParticleEmittersCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, entity);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW,game.dungeonWindow);
        peaCmp.removeEffect(glyphsCmp.glyph, ParticleEmittersCmp.ParticleEffect.CALESCENT_P,windowCmp.display);
    }
}
