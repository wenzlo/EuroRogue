package EuroRogue.Listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.SoundMapCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.Systems.FOVSys;
import squidpony.squidgrid.gui.gdx.LightingHandler;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidmath.Coord;

public class ActorListener implements EntityListener {
    EuroRogue game;
    public ActorListener(EuroRogue game)
    {
        this.game = game;
    }
    /**
     * Called whenever an {@link Entity} is added to Engine or a specific Family See
     * Engine#addEntityListener(EntityListener) and Engine#addEntityListener(Family, EntityListener)
     *
     * @param entity
     */
    @Override
    public void entityAdded(Entity entity)
    {
        LevelCmp level = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
        Coord position = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
        GlyphsCmp glyphsCmp = (GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, entity);
        level.actors.add(position, entity.hashCode(), entity.hashCode());
        LightCmp lightCmp = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, entity);

        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;

        Light light = new Light(Coord.get(position.x*3+1, position.y*3+1), new Radiance(lightCmp.level, lightCmp.color) );
        lightHandler.addLight(light.hashCode(), light);
        glyphsCmp.glyph.setName(light.hashCode() + " " + entity.hashCode()+ " actor");

        game.engine.getSystem(FOVSys.class).updatFOV(entity);

        entity.remove(SoundMapCmp.class);
        entity.add(new SoundMapCmp(level.bareDungeon));



    }

    @Override
    public void entityRemoved(Entity entity)
    {
        LevelCmp level = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
        Coord position = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
        level.actors.remove(entity.hashCode());
        LightingHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;
        lightHandler.removeLight(position);
    }
}
