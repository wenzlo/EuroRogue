package EuroRogue.Systems.AI;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.ItemEvtType;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

public interface iAISys
{
    void                observe(Entity entity);
    ArrayList<Ability>  getAvailableActions(Entity entity);
    int                 scheduleMoveEvt(Entity entity, Direction direction, double terrainCost);
    int                 scheduleActionEvt (Entity entity, Ability ability);
    int                 scheduleRestEvt (Entity entity);
    int                 scheduleFrozenEvt (Entity entity);
    int                 scheduleCampEvt (Entity entity);
    int                 scheduleItemEvt (Entity entity, Integer itemID, ItemEvtType itemEvtType);
    void                setTarget(Entity actor, Entity target);
    void                clearTarget(Entity actor);
    void                rotate(Entity actor, Entity target);
    void                rotate(Entity actor, Coord target);

}
