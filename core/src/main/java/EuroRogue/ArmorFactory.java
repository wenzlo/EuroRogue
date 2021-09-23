package EuroRogue;

import com.badlogic.ashley.core.Entity;

import EuroRogue.Components.ArmorCmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;

public class ArmorFactory
{
    private GWTRNG rng;

    public ArmorFactory(int rngSeed)
    {
        this.rng = new GWTRNG(rngSeed);
    }


    public static StatusEffect[] onHitEffectsPool = new StatusEffect[]
            {StatusEffect.CHILLED, StatusEffect.CALESCENT, StatusEffect.EXHAUSTED};
    public static StatusEffect[] onEquipEffectsPool = new StatusEffect[]
            { StatusEffect.ENLIGHTENED, StatusEffect.ENRAGED, StatusEffect.EXHAUSTED, StatusEffect.WATER_WALKING};


    public Entity newBasicArmor(ArmorType armorType, Coord loc)
    {
        Entity armor = new Entity();
        armor.add(new NameCmp(armorType.name));
        armor.add(new ItemCmp(ItemType.ARMOR));
        armor.add(new CharCmp('Ω', armorType.color));
        armor.add(new ArmorCmp(armorType));
        EquipmentCmp equipmentCmp = new EquipmentCmp(new EquipmentSlot[]{EquipmentSlot.CHEST});
        equipmentCmp.statusEffects.put(armorType.grantedEffect, new SEParameters(TargetType.SELF, SERemovalType.OTHER));
        armor.add(equipmentCmp);
        if(loc!=null) armor.add(new PositionCmp(loc));

        return armor;
    }
    public Entity newRndArmor() {return newRndArmor(null);}

    public Entity newRndArmor(Coord loc)
    {
        ArmorType armorType = rng.getRandomElement(ArmorType.values());
        Entity armor = newBasicArmor(armorType, loc);
        //if(rng.nextInt()%5==0) addOnHitSERnd(armor, TargetType.ENEMY);
        if(rng.nextInt()%20==0) addOnEquipSERnd(armor);

        return armor;
    }

   /* public void addOnHitSERnd (Entity armorEntity,  TargetType targetType)
    {
        addOnHitSE(armorEntity, rng.getRandomElement(onHitEffectsPool), targetType);
    }
    public static void addOnHitSE (Entity armorEntity, StatusEffect statusEffect, TargetType targetType)
    {
        WeaponCmp weaponCmp = (WeaponCmp) CmpMapper.getComp(CmpType.WEAPON, armorEntity);
        weaponCmp.statusEffects.put(statusEffect, new SEParameters(targetType, SERemovalType.TIMED, statusEffect.resistance));
        NameCmp nameCmp = (NameCmp) CmpMapper.getComp(CmpType.NAME, armorEntity);
        nameCmp.name = nameCmp.name+" of "+statusEffect.name;

    }*/
    public void addOnEquipSERnd (Entity armorEntity)
    {
        addOnEquipSE(armorEntity, rng.getRandomElement(onEquipEffectsPool));
    }
    public static void addOnEquipSE (Entity weaponEntity, StatusEffect statusEffect)
    {
        EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, weaponEntity);
        equipmentCmp.statusEffects.put(statusEffect, new SEParameters(TargetType.SELF, SERemovalType.OTHER));
        NameCmp nameCmp = (NameCmp) CmpMapper.getComp(CmpType.NAME, weaponEntity);
        nameCmp.name = statusEffect.name+" "+nameCmp.name;

    }
}
