package EuroRogue.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ParticleEffectActor;

import java.util.HashMap;

import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.TextCellFactory;

public class ParticleEmittersCmp implements Component
{
    public HashMap<TextCellFactory.Glyph, ParticleEffectActor> particleEffectsMap = new HashMap<>();

    public enum ParticleEffect
    {
        TORCH_P,
        FIRE_P,
        ICE_P,
        CHILLED_P,
        CALESCENT_P,
        BURNING_P,
        MAGIC_MISSILE_P
    }

    public void addEffect(TextCellFactory.Glyph glyph, ParticleEffect effect, MySparseLayers display)
    {
        ParticleEffectActor pea;
        switch (effect)
        {
            case TORCH_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/pixelFlame" ),Gdx.files.internal("" ));
                break;
            case FIRE_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/fireSpell" ),Gdx.files.internal("" ));
                break;
            case ICE_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/iceSpell" ),Gdx.files.internal("" ));
                break;
            case CHILLED_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/chilled" ),Gdx.files.internal("" ));
                pea.setScale(0.5f);
                break;
            case CALESCENT_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/calescent" ),Gdx.files.internal("" ));
                pea.setScale(0.5f);
                break;
            case BURNING_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/burning" ),Gdx.files.internal("" ));
                pea.setScale(0.5f);
                break;
            case MAGIC_MISSILE_P:
                pea = new ParticleEffectActor(Gdx.files.internal("ParticleEmitters/magicMissile" ),Gdx.files.internal("" ));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + effect);
        }
        particleEffectsMap.put(glyph, pea);
        display.getStage().addActor(pea);
        //pea.start();
    }

    public  void removeEffect(TextCellFactory.Glyph glyph, MySparseLayers display)
    {
        ParticleEffectActor pea = particleEffectsMap.get(glyph);
        display.getStage().getActors().removeValue(pea, true);
        particleEffectsMap.remove(glyph);

    }

}
