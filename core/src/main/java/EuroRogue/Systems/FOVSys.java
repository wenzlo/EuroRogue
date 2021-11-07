package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.GameState;
import EuroRogue.MyEntitySystem;
import EuroRogue.MyFOV;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.Radius;
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
        entities = engine.getEntitiesFor(Family.one(AICmp.class).get());
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
        for (int i = 0; i < entities.size(); ++i) {
            updateFOV(entities.get(i));
        }
    }

    public void updateFOV(Entity entity) {
        FOVCmp fovCmp = (FOVCmp) CmpMapper.getComp(CmpType.FOV, entity);
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
        LightingCmp lightingCmp = (LightingCmp)CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        MyFOV.reuseFOV(levelCmp.resistance, fovCmp.fov, positionCmp.coord.x, positionCmp.coord.y, 10, Radius.CIRCLE);
        //float multiplier = 1.01f-(float)lightingCmp.fgLightLevel[positionCmp.coord.x][positionCmp.coord.y];
        int nightVisionDistance = Math.round((1 + statsCmp.getPerc() / 2f) /* * multiplier*/);
        MyFOV.reuseFOV(levelCmp.resistance, fovCmp.nightVision, positionCmp.coord.x, positionCmp.coord.y, nightVisionDistance, Radius.CIRCLE,
                        orientationToAngle(positionCmp.orientation), 1.0, 1.0, 0.75, 0.5, 0 );

        GreasedRegion nightVision = new GreasedRegion(fovCmp.nightVision, 0.0).not();
        GreasedRegion notLit = new GreasedRegion(lightingCmp.fgLightLevel, statsCmp.getLightDetectionLvl());


        GreasedRegion currentlySeen = new GreasedRegion(fovCmp.fov, 0.0).not();

        currentlySeen.andNot(notLit);
        currentlySeen.or(nightVision);

        fovCmp.seen.or(currentlySeen);
        fovCmp.visible = currentlySeen;


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

}
