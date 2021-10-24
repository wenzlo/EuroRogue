package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.GameState;
import EuroRogue.MyEntitySystem;
import EuroRogue.MyFOV;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Measurement;
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
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        MyFOV.reuseFOV(levelCmp.resistance, fovCmp.fov, positionCmp.coord.x, positionCmp.coord.y, 10, Radius.CIRCLE);
        //TODO make nightVision an ability. Prepping it gives status effect, no cast necessary
        float multiplier = 1.01f-(float)lightingCmp.fgLightLevel[positionCmp.coord.x][positionCmp.coord.y];
        int nightVisionDistance = Math.round((1 + statsCmp.getPerc() / 2f) * multiplier);
        MyFOV.reuseFOV(levelCmp.resistance, fovCmp.nightVision, positionCmp.coord.x, positionCmp.coord.y, nightVisionDistance);

        GreasedRegion nightVision = new GreasedRegion(fovCmp.nightVision, 0.0).not();
        GreasedRegion notLit = new GreasedRegion(lightingCmp.fgLightLevel, 0.0);

        GreasedRegion currentlySeen = new GreasedRegion(fovCmp.fov, 0.0).not();
        currentlySeen.andNot(notLit);
        currentlySeen.or(nightVision);

        fovCmp.seen.or(currentlySeen);
        fovCmp.seen.or(nightVision);
        fovCmp.visible = currentlySeen;


        //System.out.println(fovCmp.visible);

    }

}
