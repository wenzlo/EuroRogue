package EuroRogue.Listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.NoiseMapCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.Systems.FOVSys;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Measurement;
import squidpony.squidgrid.gui.gdx.LightingHandler;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
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
        NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, entity);

        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);

        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow);
        if(levelCmp == null) return;

        Coord position = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
        levelCmp.actors.put(position, entity.hashCode(), entity.hashCode());
        LightCmp lightCmp = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, entity);
        CharCmp charCmp = (CharCmp)CmpMapper.getComp(CmpType.CHAR, entity);
        GlyphsCmp glyphsCmp = new GlyphsCmp(windowCmp.display, charCmp.chr, charCmp.lChr,charCmp.rChr,charCmp.color, position.x, position.y);
        entity.add(glyphsCmp);


        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;

        Light light = new Light(Coord.get(position.x*3+1, position.y*3+1), new Radiance(lightCmp.level, lightCmp.color) );
        Light leftLight = new Light(Coord.get(position.x*3+1, position.y*3+1), new Radiance(0, SColor.BLACK.toFloatBits()) );
        Light rightLight = new Light(Coord.get(position.x*3+1, position.y*3+1), new Radiance(0, SColor.BLACK.toFloatBits()) );

        lightHandler.addLight(light.hashCode(), light);
        lightHandler.addLight(leftLight.hashCode(), leftLight);
        lightHandler.addLight(rightLight.hashCode(), rightLight);
        glyphsCmp.glyph.setName(light.hashCode() + " " + entity.hashCode()+ " actor");
        glyphsCmp.leftGlyph.setName(leftLight.hashCode() + " " + entity.hashCode()+ " actorLeft");
        glyphsCmp.rightGlyph.setName(rightLight.hashCode() + " " + entity.hashCode()+ " actorRight");

        entity.add(new FOVCmp(levelCmp.bareDungeon.length, levelCmp.bareDungeon[0].length));
        game.engine.getSystem(FOVSys.class).updateFOV(entity);
        AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, entity);

        aiCmp.dijkstraMap = new DijkstraMap(levelCmp.bareDungeon, Measurement.EUCLIDEAN);
        aiCmp.dijkstraMap.initializeCost(aiCmp.getTerrainCosts(levelCmp.decoDungeon));
        aiCmp.movementCosts = aiCmp.getTerrainCosts(levelCmp.decoDungeon);

        entity.remove(NoiseMapCmp.class);
        entity.add(new NoiseMapCmp(levelCmp.bareDungeon));
        for(Skill skill : ((CodexCmp)CmpMapper.getComp(CmpType.CODEX, entity)).prepared )
        {
            CmpMapper.getAbilityComp(skill, entity).setMap(levelCmp.decoDungeon);
        }


    }

    @Override
    public void entityRemoved(Entity entity)
    {
        NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, entity);

        LevelCmp level = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
        if(level!=null)
        {
            Coord position = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
            level.actors.remove(entity.hashCode());
            LightingHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;
            lightHandler.removeLight(position);
        }



        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow);
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, entity);
        if(glyphsCmp!=null)
        {
            windowCmp.display.removeGlyph(glyphsCmp.glyph);
            windowCmp.display.removeGlyph(glyphsCmp.leftGlyph);
            windowCmp.display.removeGlyph(glyphsCmp.rightGlyph);
        }



    }
}
