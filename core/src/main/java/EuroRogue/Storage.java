package EuroRogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.Components.ArmorCmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WeaponCmp;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.StorageSys;
import squidpony.SquidStorage;
import squidpony.StringKit;

public class Storage extends SquidStorage
{
    public ArrayList<String> buildKeys = new ArrayList<>();
    public Storage()
    {
        super("EuroRogue");
        try {
            System.out.println("Found Build Keys");
            this.buildKeys = this.get("EuroRogue", "buildKeys", buildKeys.getClass());
            System.out.println(this.buildKeys);

        } catch (Exception e) {
            this.buildKeys =  new ArrayList<>();
        }
    }

    public void storeCharBuild(String buildName, EuroRogue game)
    {
        System.out.println("Storing Build");
        Entity character = game.getFocus();
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, character);

        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, character);
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, character);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, character);
        LightCmp lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, character);

        put(buildName + CmpType.STATS.toString(), statsCmp );
        put(buildName + CmpType.CODEX.toString(), codexCmp );
        put(buildName + CmpType.MANA_POOL.toString(), manaPoolCmp );
        put(buildName + CmpType.INVENTORY.toString(), inventoryCmp );
        put(buildName + CmpType.LIGHT.toString(), lightCmp );

        buildKeys.add(buildName);
        put("buildKeys" , this.buildKeys );



        for(Integer id : inventoryCmp.getItemIDs())
        {
            Entity item = game.getEntity(id);
            ItemCmp itemCmp = (ItemCmp) CmpMapper.getComp(CmpType.ITEM, item);
            put(buildName + id + CmpType.ITEM.toString(), itemCmp);

            WeaponCmp weaponCmp = (WeaponCmp) CmpMapper.getComp(CmpType.WEAPON, item);
            put(buildName + id + CmpType.WEAPON.toString(), weaponCmp);

            ArmorCmp armorCmp = (ArmorCmp)CmpMapper.getComp(CmpType.ARMOR, item);
            put(buildName + id + CmpType.ARMOR.toString(), armorCmp);

            EquipmentCmp equipmentCmp = (EquipmentCmp)CmpMapper.getComp(CmpType.EQUIPMENT, item);
            put(buildName + id + CmpType.EQUIPMENT.toString(), equipmentCmp);

            NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, item);
            put(buildName + id + CmpType.NAME.toString(), nameCmp);

            CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, item);
            put(buildName + id + CmpType.CHAR.toString(), charCmp);

            lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, item);
            put(buildName + id + CmpType.LIGHT.toString(), lightCmp);


        }


        store("EuroRogue");

    }

    public void loadCharBuild(String buildName, EuroRogue game)
    {
        System.out.println("Loading Build");
        Entity character = game.getFocus();

        ManaPoolCmp manaPoolCmp = get("EuroRogue", buildName + CmpType.MANA_POOL.toString(), ManaPoolCmp.class);

        CodexCmp codexCmp = get("EuroRogue", buildName + CmpType.CODEX.toString(), CodexCmp.class);
        for(Skill skill : codexCmp.prepared)
        {
            Ability ability = Ability.newAbilityCmp(skill, true);
            character.add(ability);
        }

        StatsCmp statsCmp = get("EuroRogue", buildName + CmpType.STATS.toString(), StatsCmp.class);
        HashMap<StatType, List<School>> newStatCosts = new HashMap<>();
        for(StatType statType : StatType.CORE_STATS)
        {
            newStatCosts.put(statType, statsCmp.statCosts.get(statType.toString()));
        }
        statsCmp.statCosts = newStatCosts;

        InventoryCmp inventoryCmp = get("EuroRogue", buildName + CmpType.INVENTORY.toString(), InventoryCmp.class);
        HashMap<EquipmentSlot, Integer> equipmentSlots= new HashMap<>();
        for(Object slotKey : inventoryCmp.equipmentSlots.keySet())
        {
            EquipmentSlot slot = EquipmentSlot.valueOf(slotKey.toString());
            equipmentSlots.put(slot, null);
        }
        inventoryCmp.equipmentSlots=equipmentSlots;

        ArrayList<Integer> inventory = new ArrayList<>();
        for(Integer id : inventoryCmp.getItemIDs())
        {
            Entity itemEntity = new Entity();
            game.engine.addEntity(itemEntity);
            inventory.add(itemEntity.hashCode());

            ItemCmp itemCmp = get("EuroRogue", buildName + id + CmpType.ITEM.toString(), ItemCmp.class);
            itemCmp.ownerID = character.hashCode();
            itemEntity.add(itemCmp);

            NameCmp nameCmp =  get("EuroRogue", buildName + id + CmpType.NAME.toString(), NameCmp.class);
            itemEntity.add(nameCmp);

            CharCmp charCmp = get("EuroRogue", buildName + id + CmpType.CHAR.toString(), CharCmp.class);
            itemEntity.add(charCmp);

            LightCmp lightCmp = get("EuroRogue", buildName + id+CmpType.LIGHT.toString(), LightCmp.class);
            itemEntity.add(lightCmp);


            WeaponCmp weaponCmp =  get("EuroRogue", buildName + id + CmpType.WEAPON.toString(), WeaponCmp.class);
            if(weaponCmp!=null)
            {
                HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
                for(Object seString : weaponCmp.statusEffects.keySet())
                {
                    StatusEffect statusEffect = StatusEffect.valueOf(seString.toString());
                    statusEffects.put(statusEffect, weaponCmp.statusEffects.get(seString.toString()));
                }
                weaponCmp.statusEffects=statusEffects;
                itemEntity.add(weaponCmp);
            }


            ArmorCmp armorCmp =  get("EuroRogue", buildName + id + CmpType.ARMOR.toString(), ArmorCmp.class);
            if(armorCmp!=null)
                itemEntity.add(armorCmp);

            EquipmentCmp equipmentCmp =  get("EuroRogue", buildName + id + CmpType.EQUIPMENT.toString(), EquipmentCmp.class);
            if(equipmentCmp!=null)
            {
                HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
                for(Object seString : equipmentCmp.statusEffects.keySet())
                {
                    StatusEffect statusEffect = StatusEffect.valueOf(seString.toString());
                    statusEffects.put(statusEffect, equipmentCmp.statusEffects.get(seString.toString()));
                }
                equipmentCmp.statusEffects=statusEffects;
                itemEntity.add(equipmentCmp);
            }


        }
        inventoryCmp.inventory = inventory;

        LightCmp lightCmp = get("EuroRogue", buildName + CmpType.LIGHT.toString(), LightCmp.class);



        character.remove(StatsCmp.class);
        character.remove(CodexCmp.class);
        character.remove(ManaPoolCmp.class);
        character.remove(InventoryCmp.class);
        character.remove(LightCmp.class);

        character.add(statsCmp);
        character.add(codexCmp);
        character.add(manaPoolCmp);
        character.add(inventoryCmp);
        character.add(lightCmp);

    }

    public void deleteBuild(String buildName)
    {
        System.out.println("Deleting Build");
        remove(buildName);
        buildKeys.remove(buildName);
        put("buildKeys" , this.buildKeys );
        store("EuroRogue");



    }

}
