package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

public class ItemCmp implements Component
{
    public Integer ownerID;
    public ItemType type;

    public ItemCmp (){}
    public ItemCmp (ItemType type)
    {
        this.type = type;
    }

}
