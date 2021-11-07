package EuroRogue.Systems;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.DayNightCycleEvt;
import EuroRogue.MyEntitySystem;

public class DayNightCycleSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private boolean rising = false;

    public DayNightCycleSys() {
        super.priority = 7;
    }


    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(DayNightCycleEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        if(windowCmp.display.hasActiveAnimations() || windowCmp.display.hasActions() ) return;
        if (entities.size() == 0) return;
        DayNightCycleEvt dayNightCycleEvt = (DayNightCycleEvt) CmpMapper.getComp(CmpType.DAYNIGHTCYCLE_EVT, entities.get(0));
        if(dayNightCycleEvt.processed) return;
        dayNightCycleEvt.processed =  true;

        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        if(rising)lightingCmp.dayNightCycleForward(levelCmp);
        else lightingCmp.dayNightCycleBackward(levelCmp);
        if(lightingCmp.ambientLightLvl == lightingCmp.maxAmbientLight) rising = false;
        else if (lightingCmp.ambientLightLvl == lightingCmp.minAmbientLight) rising = true;



    }
}
