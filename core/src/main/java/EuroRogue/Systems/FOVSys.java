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
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.MyEntitySystem;
import squidpony.squidgrid.FOV;
import squidpony.squidmath.GreasedRegion;


public class FOVSys extends MyEntitySystem
{

    private ImmutableArray<Entity> entities;


    public FOVSys ()
    {
        super.priority = 8;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
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
        for (int i = 0; i < entities.size(); ++i)
        {
            updatFOV(entities.get(i));
        }
    }
    public  void debugFov(double[][] map)
    {
       GreasedRegion gr = new GreasedRegion(map,0.0).not();
       System.out.println(gr);
    }
    public void updatFOV(Entity entity)
    {
        FOVCmp fovCmp = (FOVCmp) CmpMapper.getComp(CmpType.FOV, entity);
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);

        FOV.reuseFOV(levelCmp.resistance, fovCmp.los, positionCmp.coord.x, positionCmp.coord.y, 14);
        FOV.reuseFOV(levelCmp.resistance, fovCmp.nightVision, positionCmp.coord.x, positionCmp.coord.y, Math.round(statsCmp.getPerc()));

        GreasedRegion nightVision = new GreasedRegion(fovCmp.nightVision, 0.0).not();
        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        GreasedRegion notLit = new GreasedRegion(lightingCmp.fgLightLevel, 0.0);

        GreasedRegion currentlySeen = new GreasedRegion(fovCmp.los,0.0).not();
        currentlySeen.andNot(notLit);
        currentlySeen.or(nightVision);
        fovCmp.seen.or(currentlySeen);
        fovCmp.visible = currentlySeen;


        //System.out.println(fovCmp.visible);

    }
}
