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
import EuroRogue.Components.WindowCmp;
import EuroRogue.MyEntitySystem;
import squidpony.squidgrid.FOV;
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
    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            updateFOV(entities.get(i));
        }
    }

    public void updateFOV(Entity entity) {
        FOVCmp fovCmp = (FOVCmp) CmpMapper.getComp(CmpType.FOV, entity);
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);

        FOV.reuseFOV(levelCmp.resistance, fovCmp.los, positionCmp.coord.x, positionCmp.coord.y, 14, Radius.CIRCLE/*, 90, 270*/);
        //TODO make nightVision an ability. Prepping it gives status effect, no cast necessary
        FOV.reuseFOV(levelCmp.resistance, fovCmp.nightVision, positionCmp.coord.x, positionCmp.coord.y, Math.round(1 + statsCmp.getPerc() / 2f));

        GreasedRegion nightVision = new GreasedRegion(fovCmp.nightVision, 0.0).not();
        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        GreasedRegion notLit = new GreasedRegion(lightingCmp.fgLightLevel, 0.0);

        GreasedRegion currentlySeen = new GreasedRegion(fovCmp.los, 0.0).not();
        currentlySeen.andNot(notLit);
        currentlySeen.or(nightVision);
        fovCmp.seen.or(currentlySeen);
        fovCmp.visible = currentlySeen;


        //System.out.println(fovCmp.visible);

    }
}
