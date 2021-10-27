package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class ChilledListener extends StatusEffectListener
{
    public ChilledListener(EuroRogue game){
        super(game);
        effect= StatusEffect.CHILLED;
    }

    @Override
    public void entityAdded(Entity entity) {
        super.entityAdded(entity);
        ParticleEffectsCmp peaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
        GlyphsCmp glyphsCmp = (GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, entity);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW,game.dungeonWindow);
        peaCmp.addEffect(glyphsCmp.glyph, ParticleEffectsCmp.ParticleEffect.CHILLED_P, windowCmp.display);
    }

    @Override
    public void entityRemoved(Entity entity) {
        super.entityRemoved(entity);
        ParticleEffectsCmp peaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
        GlyphsCmp glyphsCmp = (GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, entity);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW,game.dungeonWindow);
        peaCmp.removeEffect(glyphsCmp.glyph, ParticleEffectsCmp.ParticleEffect.CHILLED_P, windowCmp.display);
    }


}
