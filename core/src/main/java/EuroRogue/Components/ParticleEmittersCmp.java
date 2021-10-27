package EuroRogue.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ParticleEffectActor;

import java.util.ArrayList;
import java.util.HashMap;

import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.TextCellFactory;

public class ParticleEmittersCmp implements Component {
    public HashMap<TextCellFactory.Glyph, HashMap<ParticleEffect, ParticleEffectActor>> particleEffectsMap = new HashMap<>();

    public enum ParticleEffect {
        TORCH_P,
        BLEED_P,
        FIRE_P,
        ICE_P,
        ICE_SHIELD,
        CHILLED_P,
        CALESCENT_P,
        STAGGERED_P,
        BURNING_P,
        ARCANE_P,
        ARCANE_DMG,
        ICE_DMG,
        FIRE_DMG
    }

    public void addEffect(TextCellFactory.Glyph glyph, ParticleEffect effect, MySparseLayers display) {
        ParticleEffectActor pea;
        switch (effect) {
            case TORCH_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/pixelFlame"), Gdx.files.internal(""));
                break;
            case BLEED_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/bleeding"), Gdx.files.internal(""));
                pea.setScale(0.5f);
                break;
            case STAGGERED_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/staggerred"), Gdx.files.internal(""));
                pea.setScale(0.3f);
                break;
            case FIRE_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/fireSpell"), Gdx.files.internal(""));
                pea.setScale(0.75f);
                break;
            case ICE_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/iceSpell"), Gdx.files.internal(""));
                pea.setScale(0.75f);
                break;
            case ICE_SHIELD:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/iceShield"), Gdx.files.internal(""));
                pea.setScale(0.3f);
                pea.setAutoRemove(true);
                pea.start();
                break;
            case CHILLED_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/chilled"), Gdx.files.internal(""));
                pea.setScale(0.5f);
                break;
            case CALESCENT_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/calescent"), Gdx.files.internal(""));
                pea.setScale(0.5f);
                break;
            case BURNING_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/burning"), Gdx.files.internal(""));
                pea.setScale(0.5f);
                break;
            case ARCANE_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/magicMissile"), Gdx.files.internal(""));
                pea.setScale(0.75f);
                break;
            case ARCANE_DMG:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/arcaneDmg"), Gdx.files.internal(""));
                pea.setScale(0.75f);
                pea.setAutoRemove(true);
                pea.start();
                break;
            case ICE_DMG:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/iceDmg"), Gdx.files.internal(""));
                pea.setScale(0.75f);
                pea.setAutoRemove(true);
                pea.start();
                break;
            case FIRE_DMG:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/fireDmg"), Gdx.files.internal(""));
                pea.setScale(0.75f);
                pea.setAutoRemove(true);
                pea.start();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + effect);
        }
        if (particleEffectsMap.keySet().contains(glyph)) {
            particleEffectsMap.get(glyph).put(effect, pea);

        } else {
            HashMap<ParticleEffect, ParticleEffectActor> effects = new HashMap<>();
            effects.put(effect, pea);
            particleEffectsMap.put(glyph, effects);
        }

        display.getStage().addActor(pea);
    }

    public void removeEffect(TextCellFactory.Glyph glyph, ParticleEffect effect, MySparseLayers display) {
        HashMap<ParticleEffect, ParticleEffectActor> effects = particleEffectsMap.get(glyph);
        if (effects == null) return;
        if (effects.containsKey(effect)) {
            display.getStage().getActors().removeValue(effects.get(effect), true);
            effects.remove(effect);
        }

        if (effects.isEmpty()) particleEffectsMap.remove(glyph);

    }

    public void removeEffectsByGlyph(TextCellFactory.Glyph glyph, MySparseLayers display)
    {
        ArrayList<ParticleEffect> effectsToRemove = new ArrayList<>();
        for(ParticleEffect effect : particleEffectsMap.get(glyph).keySet())
        {
            effectsToRemove.add(effect);
        }
        for(ParticleEffect effect : effectsToRemove)
        {
            this.removeEffect(glyph, effect, display);
        }
        particleEffectsMap.remove(glyph);
    }

}
