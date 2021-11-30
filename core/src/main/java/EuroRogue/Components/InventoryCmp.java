package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.HashMap;


public class InventoryCmp implements Component
{
    public HashMap<EquipmentSlot, Integer> equipmentSlots= new HashMap<>();
    public ArrayList<Integer> inventory = new ArrayList<>();
    private ArrayList<Integer> food = new ArrayList<>();
    private ArrayList<Integer> scrolls = new ArrayList<>();
    private int capacity = 10;

    public InventoryCmp(){}
    public InventoryCmp(EquipmentSlot[] slots)
    {
        for (EquipmentSlot slot : slots) equipmentSlots.put(slot,null);
    }
    public InventoryCmp(EquipmentSlot[] slots, int capacity)
    {
        for (EquipmentSlot slot : slots) equipmentSlots.put(slot,null);
        this.capacity=capacity;
    }
    public ArrayList<Integer> getEquippedIDs()
    {
        ArrayList<Integer> equippedIDs = new ArrayList<>();
        for(EquipmentSlot slot : EquipmentSlot.values())
        {
            Integer equippedID = getSlotEquippedID(slot);
            if(equippedID!=null) equippedIDs.add(equippedID);
        }
        return  equippedIDs;
    }
    public Integer getSlotEquippedID(EquipmentSlot slot)
    { return  equipmentSlots.get(slot); }
    public boolean isEmpty(EquipmentSlot slot)
    { return equipmentSlots.get(slot)==null; }
    public boolean isFull()
    {
        HashMap<Integer, Integer> equippedIDs = new HashMap<>();
        for(Integer id : getEquippedIDs()) equippedIDs.put(id,null);
        return (inventory.size()+equippedIDs.keySet().size())>= capacity;
    }
    public ArrayList<Integer> getItemIDs()
    {
        return inventory;
    }
    public ArrayList<Integer> getAllItemIDs()
    {
        ArrayList<Integer> allItemIDs = new ArrayList<>();
        allItemIDs.addAll(inventory);
        allItemIDs.addAll(scrolls);
        allItemIDs.addAll(getEquippedIDs());

        return allItemIDs;
    }

    public ArrayList<Integer> getFoodIDs()
    {return food;}
    public ArrayList<Integer> getScrollsIDs()
    {return scrolls;}


    public void put(Integer id)
    {
        inventory.add(id);
    }

    public void putFood(Integer id)
    {
        food.add(id);
    }
    public void consumeFood(Integer id)
    {
        food.remove(id);
    }

    public void putScroll(Integer id)
    {
        scrolls.add(id);
    }
    public void remove(Integer id)
    {
        inventory.remove(id);
        food.remove(id);
        scrolls.remove(id);
    }
    public void equip(Integer id, EquipmentSlot[] slots)
    {
        for(EquipmentSlot slot: slots)
        {
            equipmentSlots.replace(slot, id);

        }
        inventory.remove(id);
    }

    public void unequip(Integer id)
    {
        for(EquipmentSlot slot: equipmentSlots.keySet())
        {
            if(equipmentSlots.get(slot) !=null)
                if(equipmentSlots.get(slot).equals(id))
                {
                    equipmentSlots.replace(slot, null);

                }
        }
        put(id);
    }



}
