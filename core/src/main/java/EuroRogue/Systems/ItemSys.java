package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import java.util.Arrays;

import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.ManaCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WeaponCmp;
import EuroRogue.AbilityCmpSubSystems.MeleeAttack;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.DamageType;
import EuroRogue.EventComponents.CodexEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.CmpType;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.MyEntitySystem;
import squidpony.squidai.BlastAOE;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class ItemSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public ItemSys () {super.priority=5;}

    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(ItemEvt.class).get());
    }
    @Override
    public void update(float deltaTime)
    {
        for(Entity entity:entities)
        {
            ItemEvt itemEvt = (ItemEvt) CmpMapper.getComp(CmpType.ITEM_EVT, entity);
            itemEvt.processed=true;
            Entity itemEntity = getGame().getEntity(itemEvt.itemID);
            if(itemEntity==null)
            {
                System.out.println("Item entity = null error");
                continue;
            }
            Entity actorEntity = getGame().getEntity(itemEvt.actorID);
            if(actorEntity==null)
            {
                System.out.println("Actor entity = null error "+itemEvt.actorID);
                System.out.println(getGame().getFocus().hashCode());
                continue;
            }


            Entity otherActorEntity = getGame().getEntity(itemEvt.otherActorID);
            ItemCmp itemCmp = (ItemCmp) CmpMapper.getComp(CmpType.ITEM, itemEntity);
            EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, itemEntity);
            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, actorEntity);
            switch (itemEvt.type)
            {
                case EQUIP:
                    for(EquipmentSlot slot : equipmentCmp.slotsOccupied)
                    {
                        Entity itemEntityToRemove = getGame().getEntity(inventoryCmp.getSlotEquippedID(slot));
                        if(itemEntityToRemove!=null) unequip(itemEntityToRemove, actorEntity);
                    }
                    equip(itemEntity, actorEntity);
                    LightCmp lightCmp = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, itemEntity);
                    if(lightCmp!=null)
                    {
                        LightCmp actorLight = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, actorEntity);
                        actorLight.color = lightCmp.color;
                        actorLight.level = lightCmp.level;
                    }

                    break;
                case PICKUP:
                    pickup(itemEntity, actorEntity);
                    break;

                case DROP:
                    if(equipmentCmp!=null)
                        if(equipmentCmp.equipped)
                            unequip(itemEntity, actorEntity);
                    drop(itemEntity, actorEntity);
                    break;
                case TRANSFER:
                    transfer(itemEntity, actorEntity, otherActorEntity);
                    break;
                case UNEQUIP:
                    unequip(itemEntity, actorEntity);
                    break;
                case USE:
                    break;
                case CONSUME:
                    itemCmp.ownerID= null;
                    inventoryCmp.remove(itemEntity.hashCode());
                    //getEngine().removeEntity(itemEntity);
                    break;
            }
            updateGlyphsCmp(actorEntity);
        }
    }
    private void equip(Entity itemEntity, Entity actorEntity)
    {
        EquipmentCmp equipmentCmp = (EquipmentCmp)CmpMapper.getComp(CmpType.EQUIPMENT, itemEntity);
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, actorEntity);
        if(!equipmentCmp.canEquip(statsCmp)) return;

        InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, actorEntity);

        WeaponCmp weaponCmp = (WeaponCmp)CmpMapper.getComp(CmpType.WEAPON, itemEntity);
        inventoryCmp.equip(itemEntity.hashCode(), equipmentCmp.slotsOccupied);


        equipmentCmp.equipped=true;
        for(StatusEffect statusEffect : equipmentCmp.statusEffects.keySet())
        {
            Entity eventEntity = new Entity();
            StatusEffectEvt statusEffectEvt = new StatusEffectEvt(getGame().getGameTick(), null, statusEffect, null, null, actorEntity.hashCode(), SERemovalType.OTHER );
            eventEntity.add(statusEffectEvt);
            getEngine().addEntity(eventEntity);
        }
        if(weaponCmp!=null)
        {
            MeleeAttack meleeAttack = (MeleeAttack) CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, actorEntity);
            meleeAttack.chr = weaponCmp.weaponType.chr;
            for(StatusEffect statusEffect : weaponCmp.statusEffects.keySet())
            {
                SEParameters seParameters = weaponCmp.statusEffects.get(statusEffect);
                meleeAttack.addStatusEffect(statusEffect, weaponCmp.statusEffects.get(statusEffect));
                meleeAttack.damageType = weaponCmp.weaponType.damageType;

            }
        }
    }
    private void unequip(Entity itemEntity, Entity actorEntity)
    {
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, actorEntity);
        EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, itemEntity);
        WeaponCmp weaponCmp = (WeaponCmp) CmpMapper.getComp(CmpType.WEAPON, itemEntity);
        inventoryCmp.unequip(itemEntity.hashCode());

        equipmentCmp.equipped = false;
        for (StatusEffect statusEffect : equipmentCmp.statusEffects.keySet()) {
            actorEntity.remove(statusEffect.cls);
        }
        if (weaponCmp != null) {

            for (StatusEffect statusEffect : weaponCmp.statusEffects.keySet()) {
                MeleeAttack meleeAttack = (MeleeAttack) CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, actorEntity);
                if(meleeAttack==null) continue;
                meleeAttack.removeStatusEffect(statusEffect);
                meleeAttack.chr = '•';
                meleeAttack.damageType = DamageType.BLUDGEONING;
            }
        }
        LightCmp lightCmp = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, itemEntity);
        if(lightCmp!=null)
        {
            LightCmp actorLight = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, actorEntity);
            actorLight.level=0;
            actorLight.color= SColor.COSMIC_LATTE.toFloatBits();

        }
    }
    private void drop(Entity itemEntity, Entity actorEntity)
    {
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, actorEntity);
        EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, itemEntity);
        WeaponCmp weaponCmp = (WeaponCmp) CmpMapper.getComp(CmpType.WEAPON, itemEntity);
        ItemCmp itemCmp = (ItemCmp) CmpMapper.getComp(CmpType.ITEM, itemEntity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        Coord actorPosition = levelCmp.actors.getPosition(actorEntity.hashCode());
        if(equipmentCmp!=null && inventoryCmp!=null )
            if(equipmentCmp.equipped)
            {
                itemCmp.ownerID= null;
                equipmentCmp.equipped=false;
                for(EquipmentSlot slot : equipmentCmp.slotsOccupied)
                {
                    inventoryCmp.equipmentSlots.replace(slot, null);
                }
                for(StatusEffect statusEffect : equipmentCmp.statusEffects.keySet())
                {
                    actorEntity.remove(statusEffect.cls);
                }
                LightCmp lightCmp = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, itemEntity);
                if(lightCmp!=null)
                {
                    LightCmp actorLight = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, actorEntity);
                    actorLight.level=0;
                    actorLight.color= SColor.COSMIC_LATTE.toFloatBits();

                }

            }

        if(weaponCmp!=null)
        {
            for(StatusEffect statusEffect : weaponCmp.statusEffects.keySet())
            {
                MeleeAttack meleeAttack = (MeleeAttack) CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, actorEntity);
                meleeAttack.removeStatusEffect(statusEffect);
            }
        }


        BlastAOE aoe = new BlastAOE(actorPosition, 2, Radius.CIRCLE);
        aoe.setMap(levelCmp.bareDungeon);
        if(levelCmp.items.positions().contains(actorPosition))
        {
            for(Coord pos : aoe.findArea().keySet())
            {
                if(!levelCmp.items.positions().contains(pos) && levelCmp.floors.contains(pos))
                {
                    actorPosition = Coord.get(pos.x, pos.y);
                    break;
                }
            }
        }

        itemCmp.ownerID = null;

        itemEntity.add(new PositionCmp(actorPosition));
        inventoryCmp.remove(itemEntity.hashCode());
    }
    private void pickup(Entity itemEntity, Entity actorEntity)
    {
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, actorEntity);
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, actorEntity);
        ItemCmp itemCmp = (ItemCmp) CmpMapper.getComp(CmpType.ITEM, itemEntity);
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp)CmpMapper.getComp(CmpType.MANA_POOL, actorEntity);
        switch (itemCmp.type)
        {
            case FOOD:
                itemCmp.ownerID= actorEntity.hashCode();
                itemEntity.remove(PositionCmp.class);
                inventoryCmp.putFood(itemEntity.hashCode());
                return;
            case MANA:
                manaPoolCmp.spent.add(((ManaCmp)CmpMapper.getComp(CmpType.MANA, itemEntity)).school);
                getEngine().removeEntity(itemEntity);
                return;

            case SCROLL:
                ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, itemEntity);
                StatsCmp ownerStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, actorEntity);
                if(!codexCmp.known.contains(scrollCmp.skill) )
                {
                    Entity eventEntity = new Entity();
                    eventEntity.add(new CodexEvt(actorEntity.hashCode(), Arrays.asList(scrollCmp.skill), null, null));
                    getGame().engine.addEntity(eventEntity);
                    getEngine().removeEntity(itemEntity);
                    return;

                }
                StatsCmp scrollStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, itemEntity);
                scrollStats.mergeWith(ownerStats);
                itemCmp.ownerID= actorEntity.hashCode();
                itemEntity.remove(PositionCmp.class);
                inventoryCmp.putScroll(itemEntity.hashCode());
                return;

            case TORCH:
            case WEAPON:
            case ARMOR:
                if(!inventoryCmp.isFull())
                {
                    itemCmp.ownerID= actorEntity.hashCode();
                    itemEntity.remove(PositionCmp.class);
                    inventoryCmp.put(itemEntity.hashCode());
                }
        }
    }
    private void transfer(Entity itemEntity, Entity ownerEntity, Entity receiverEntity)
    {
        EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, itemEntity);
        InventoryCmp ownerInv = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, ownerEntity);
        InventoryCmp receiverInv = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, receiverEntity);

        if(equipmentCmp.equipped) unequip(itemEntity, ownerEntity);
        ownerInv.remove(itemEntity.hashCode());
        receiverInv.put(itemEntity.hashCode());


    }
    private void updateGlyphsCmp(Entity actorEntity)
    {

        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, actorEntity);

        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, actorEntity);

        Integer idR = inventoryCmp.getSlotEquippedID(EquipmentSlot.RIGHT_HAND_WEAP);
        Integer idL = inventoryCmp.getSlotEquippedID(EquipmentSlot.LEFT_HAND_WEAP);
        if(idR==null)
        {
            glyphsCmp.rightGlyph.shown = '•';
            glyphsCmp.rightGlyph.setColor(glyphsCmp.glyph.getColor());
        }
        if(idL==null)
        {
            glyphsCmp.leftGlyph.shown = '•';
            glyphsCmp.rightGlyph.setColor(glyphsCmp.glyph.getColor());
        }

        if(idL!=null)
        {
            Entity itemEntity = getGame().getEntity(idL);
            CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, itemEntity);

            glyphsCmp.leftGlyph.shown = charCmp.chr;
            glyphsCmp.leftGlyph.setColor(charCmp.color);
        }
        if(idR!=null)
        {
            Entity itemEntity = getGame().getEntity(idR);
            CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, itemEntity);

            glyphsCmp.rightGlyph.shown = charCmp.chr;
            glyphsCmp.rightGlyph.setColor(charCmp.color);
        }
        if(idR==idL && idR != null)
        {
            Entity itemEntity = getGame().getEntity(idR);
            CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, itemEntity);
            glyphsCmp.leftGlyph.shown = ' ';
            glyphsCmp.rightGlyph.shown = charCmp.chr;
            glyphsCmp.rightGlyph.setColor(charCmp.color);
        }

    }

}
