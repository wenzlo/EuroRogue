package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.DaggerThrow;
import EuroRogue.AbilityCmpSubSystems.MeleeAttack;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.WeaponCmp;
import EuroRogue.MyEntitySystem;
import EuroRogue.WeaponType;
import squidpony.squidai.AOE;
import squidpony.squidai.BlastAOE;
import squidpony.squidai.Technique;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;


public class AimSys extends MyEntitySystem
{

    private ImmutableArray<Entity> entities;


    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(AimingCmp.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        if(entities.size()==0) return;
        Entity actor = getGame().getFocus();
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);
        AimingCmp aimingCmp = (AimingCmp) CmpMapper.getComp(CmpType.AIMING, actor);
        Ability ability = (Ability) CmpMapper.getAbilityComp(aimingCmp.skill, actor);
        if(ability.canTarget(positionCmp.coord, aimingCmp.aimCoord)) ability.apply(positionCmp.coord, aimingCmp.aimCoord);
        else ability.apply(positionCmp.coord, positionCmp.coord.translate(Direction.UP));
        System.out.println("updating idealLocations - AimSys method");
        ability.apply(positionCmp.coord, aimingCmp.aimCoord);
        //updateAbility((Ability) ability, actor);
    }

 /*   private void updateAbility(Ability abilityCmp, Entity entity)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        Skill skill = abilityCmp.getSkill();
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);
        Entity weaponEntity = getGame().getEntity(inventoryCmp.getSlotEquippedID(EquipmentSlot.RIGHT_HAND_WEAP));
        WeaponType weaponType = null;
        if(weaponEntity!=null && skill.weaponReq!=null)
        {
            WeaponCmp weaponCmp = (WeaponCmp) CmpMapper.getComp(CmpType.WEAPON, weaponEntity);
            weaponType = weaponCmp.weaponType;
        }
        ArrayList<Coord> targets = new ArrayList<>();
        for(Coord coord : abilityCmp.aoe.findArea().keySet())
        {
            if(levelCmp.actors.containsPosition(coord))
            {

                targets.add(coord);
            }
        }


        if(skill == Skill.DAGGER_THROW && abilityCmp.isAvailable())
        {
            ((DaggerThrow)abilityCmp).itemID = weaponEntity.hashCode();
            ((DaggerThrow)abilityCmp).chr = weaponType.chr;
            ((DaggerThrow)abilityCmp).statusEffects = ((MeleeAttack) CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, entity)).getStatusEffects();

        }

        abilityCmp.setDamage(entity);
        abilityCmp.setTTPerform(entity);
        abilityCmp.setAvailable(true);
    }*/

}
