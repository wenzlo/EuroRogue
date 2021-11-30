package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.ui.ParticleEffectActor;

import java.util.ArrayList;
import java.util.HashMap;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.MyEntitySystem;
import squidpony.squidgrid.gui.gdx.TextCellFactory;

public class ParticleSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public ParticleSys()
    {
        super.priority=0;
    }


    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(ParticleEffectsCmp.class).get());

    }

    @Override
    public void update(float deltaTime)
    {
       for(Entity entity : entities) {
           ParticleEffectsCmp particleEffectsCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
           HashMap<TextCellFactory.Glyph, ArrayList<ParticleEffectsCmp.ParticleEffect>> effectsToRemove = new HashMap<>();
           for (TextCellFactory.Glyph glyph : particleEffectsCmp.particleEffectsMap.keySet()) {
               ArrayList<ParticleEffectsCmp.ParticleEffect> effects = new ArrayList<>();
               for(ParticleEffectsCmp.ParticleEffect effect : particleEffectsCmp.particleEffectsMap.get(glyph).keySet())
               {
                   ParticleEffectActor pea = particleEffectsCmp.particleEffectsMap.get(glyph).get(effect);
                   pea.setPosition(glyph.getX(), glyph.getY());
                   if(!pea.isRunning() && glyph.isVisible() && !pea.isAutoRemove()) pea.start();
                   else if (!glyph.isVisible()) pea.allowCompletion();
                   if(pea.isAutoRemove() && !pea.isRunning())
                   {
                       effects.add(effect);

                   }

               }
               effectsToRemove.put(glyph, effects);

           }WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
           for(TextCellFactory.Glyph glyph : effectsToRemove.keySet())
           {
               for(ParticleEffectsCmp.ParticleEffect effect : effectsToRemove.get(glyph))
               {
                   particleEffectsCmp.removeEffect(glyph, effect, windowCmp.display);
               }
           }

       }

    }

}
