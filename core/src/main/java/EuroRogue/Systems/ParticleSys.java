package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.ui.ParticleEffectActor;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.ParticleEmittersCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class ParticleSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> animations;

    public ParticleSys()
    {
        super.priority=0;
    }


    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(ParticleEmittersCmp.class).get());
        animations = engine.getEntitiesFor(Family.all(AnimateGlyphEvt.class).get());

    }

    @Override
    public void update(float deltaTime)
    {
       for(Entity entity : entities) {
           ParticleEmittersCmp particleEmittersCmp = (ParticleEmittersCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);

           for (TextCellFactory.Glyph glyph : particleEmittersCmp.particleEffectsMap.keySet()) {
               ParticleEffectActor pea = particleEmittersCmp.particleEffectsMap.get(glyph);
               pea.setPosition(glyph.getX(), glyph.getY());
               if(!pea.isRunning() && glyph.isVisible()) pea.start();
               else if (!glyph.isVisible()) pea.allowCompletion();
           }

       }
    }

}
