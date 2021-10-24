package EuroRogue;

import com.badlogic.ashley.core.Entity;

import EuroRogue.Components.CharCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.WeaponCmp;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;

public class WeaponFactory
{
    private GWTRNG rng;

    public WeaponFactory (int rngSeed)
    {
        this.rng = new GWTRNG(rngSeed);
    }


    public static StatusEffect[] onHitEffectsPool = new StatusEffect[]
            {StatusEffect.CHILLED, StatusEffect.CALESCENT, StatusEffect.EXHAUSTED, StatusEffect.ENLIGHTENED, StatusEffect.ENRAGED};
    public static StatusEffect[] onEquipEffectsPool = new StatusEffect[]
            { StatusEffect.ENLIGHTENED, StatusEffect.ENRAGED, StatusEffect.EXHAUSTED};

    public Entity newBasicWeapon(WeaponType weaponType, Coord loc)
    {
        Entity weapon = new Entity();
        weapon.add(new NameCmp(weaponType.name));
        weapon.add(new ItemCmp(ItemType.WEAPON));
        weapon.add(new CharCmp(weaponType.chr, SColor.LIGHT_GRAY));
        weapon.add(new LightCmp());
        EquipmentCmp equipmentCmp = new EquipmentCmp(weaponType.slots);
        equipmentCmp.statusEffects.put(weaponType.grantedEffect, new SEParameters(TargetType.SELF, SERemovalType.OTHER));
        weapon.add(equipmentCmp);
        WeaponCmp weaponCmp = new WeaponCmp(weaponType);
        if(weaponType==WeaponType.DAGGER)weaponCmp.throwable=true;
        weapon.add(weaponCmp);
        if(loc!=null) weapon.add(new PositionCmp(loc));

        return weapon;
    }
    public Entity newRndWeapon() {return newRndWeapon(null);}

    public Entity newRndWeapon(Coord loc)
    {
        WeaponType weaponType = rng.getRandomElement(WeaponType.values());
        Entity weapon = newBasicWeapon(weaponType, loc);
        /*if(rng.nextInt()%20==0) addOnHitSERnd(weapon, TargetType.ENEMY);
        if(rng.nextInt()%20==0) addOnEquipSERnd(weapon);*/

        return weapon;
    }

    public  Entity newBasicWeapon(WeaponType weaponType) {return newBasicWeapon(weaponType, null);}
    public void addOnHitSERnd (Entity weaponEntity,  TargetType targetType)
    {
        addOnHitSE(weaponEntity, rng.getRandomElement(onHitEffectsPool), targetType);
    }
    public static void addOnHitSE (Entity weaponEntity, StatusEffect statusEffect, TargetType targetType)
    {
        WeaponCmp weaponCmp = (WeaponCmp) CmpMapper.getComp(CmpType.WEAPON, weaponEntity);
        weaponCmp.statusEffects.put(statusEffect, new SEParameters(targetType, SERemovalType.TIMED));
        NameCmp nameCmp = (NameCmp) CmpMapper.getComp(CmpType.NAME, weaponEntity);
        nameCmp.name = nameCmp.name+" of "+StatusEffect.getEffectDescriptorPost(statusEffect);

    }
    public void addOnEquipSERnd (Entity weaponEntity)
    {
        addOnEquipSE(weaponEntity, rng.getRandomElement(onEquipEffectsPool));
    }
    public static void addOnEquipSE (Entity weaponEntity, StatusEffect statusEffect)
    {
        EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, weaponEntity);
        equipmentCmp.statusEffects.put(statusEffect, new SEParameters(TargetType.SELF, SERemovalType.OTHER));
        NameCmp nameCmp = (NameCmp) CmpMapper.getComp(CmpType.NAME, weaponEntity);
        nameCmp.name = StatusEffect.getEffectDescriptorPre(statusEffect)+" "+nameCmp.name;
    }
    public Entity newTorch(Coord loc)
    {
        Entity torch = new Entity();
        torch.add(new NameCmp("Torch"));
        torch.add(new ItemCmp(ItemType.TORCH));
        torch.add(new CharCmp('*', SColor.SAFETY_ORANGE));
        EquipmentCmp equipmentCmp = new EquipmentCmp(new EquipmentSlot[]{EquipmentSlot.LEFT_HAND_WEAP});
        torch.add(equipmentCmp);
        torch.add(new LightCmp(5, SColor.COSMIC_LATTE.toFloatBits()));
        if(loc!=null) torch.add(new PositionCmp(loc));

        return torch;
    }



}
