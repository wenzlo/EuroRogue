package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.GameState;
import EuroRogue.MyEntitySystem;
import EuroRogue.MyFOV;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.Radius;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;


public class FOVSys extends MyEntitySystem {

    private ImmutableArray<Entity> entities;


    public FOVSys() {
        super.priority = 8;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.one(FOVCmp.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        if(getGame().gameState!= GameState.PLAYING) return;
        for (int i = 0; i < entities.size(); ++i)
        {
            updateFOV(entities.get(i));
        }
    }

    public void updateFOV(Entity entity)
    {
        FOVCmp fovCmp = (FOVCmp) CmpMapper.getComp(CmpType.FOV, entity);
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
        LightingCmp lightingCmp = (LightingCmp)CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);

        Coord origin = Coord.get(positionCmp.coord.x,positionCmp.coord.y);
        if(entity == getGame().getFocus())
        {
            GlyphsCmp glyphsCmp = (GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, getGame().getFocus());
            int x = windowCmp.display.gridX(glyphsCmp.glyph.getX());
            int y = windowCmp.display.gridY(glyphsCmp.glyph.getY());
            origin =  Coord.get(x,y);
        }

        MyFOV.reuseFOV(levelCmp.resistance, fovCmp.fov, origin.x, origin.y, 10, Radius.CIRCLE);

        boolean stalking = CmpMapper.getStatusEffectComp(StatusEffect.STALKING, entity)!=null;
        boolean brightLight = lightingCmp.fgLightLevel[positionCmp.coord.x][positionCmp.coord.y] >0.6;
        double radiusMultiplier = brightLight ? 0.5 : 1;
        MyFOV.reuseFOV(levelCmp.resistance, fovCmp.nightVision, origin.x, origin.y, statsCmp.getNVRadius(), Radius.CIRCLE,
                        orientationToAngle(positionCmp.orientation), radiusMultiplier, radiusMultiplier, 1.01*radiusMultiplier,
                        0.75*radiusMultiplier, stalking ? 0.75*radiusMultiplier : 0 );

        GreasedRegion nightVision = new GreasedRegion(fovCmp.nightVision, 0.0).not();
        GreasedRegion notLit = new GreasedRegion(lightingCmp.fgLightLevel, statsCmp.getLightDetectionLvl()-0.001);


        GreasedRegion currentlySeen = new GreasedRegion(fovCmp.fov, 0.0).not();

        currentlySeen.andNot(notLit);
        currentlySeen.or(nightVision);

        fovCmp.seen.or(currentlySeen);
        fovCmp.visible = currentlySeen;
        if(stalking)
                applyStalkingCostMap(entity);

    }

    private double orientationToAngle(Direction orientation)
    {
        switch (orientation)
        {

            case UP: return  270.0;

            case DOWN: return  90.0;

            case LEFT: return 180.0;

            case NONE:
            case RIGHT: return 0.0;

            case UP_LEFT: return  225;

            case UP_RIGHT: return  315.0;

            case DOWN_LEFT: return  135.0;

            case DOWN_RIGHT: return  45.0;

        }
        return  0.0;
    }

    private void applyStalkingCostMap(Entity entity)
    {


        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        StatsCmp stats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
        AICmp aiCmp = CmpMapper.getAIComp(stats.mobType.aiType, entity);

        double visibilityLvl = stats.getVisibleLightLvl();
        GreasedRegion exposed = new GreasedRegion(lightingCmp.fgLightLevel, visibilityLvl).not();

        for(Integer id : aiCmp.visibleEnemies)
        {
            Entity enemy = getGame().getEntity(id);
            if(enemy==null)
            {

                continue;
            }
            FOVCmp enemyFOV = (FOVCmp) CmpMapper.getComp(CmpType.FOV, enemy);
            exposed.or(new GreasedRegion(enemyFOV.nightVision, 0.0).not());


        }


        double[][] costMap = aiCmp.getTerrainCosts(levelCmp.decoDungeon);
        for(Coord coord : exposed)
        {
            costMap[coord.x][coord.y] = 10.0;
        }
        aiCmp.dijkstraMap.initializeCost(costMap);

    }

}
