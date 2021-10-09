package EuroRogue;

import com.badlogic.ashley.core.Entity;

import EuroRogue.Components.CharCmp;
import EuroRogue.Components.FoodCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.PositionCmp;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class FoodFactory
{

    public FoodFactory(){}

    public Entity generateFoodITem(Coord loc)
    {
        Entity foodItem = new Entity();
        if(loc!=null)
            foodItem.add(new PositionCmp(loc));
        foodItem.add(new ItemCmp(ItemType.FOOD));
        foodItem.add(new CharCmp('ƒ', SColor.BRIGHT_GOLD_BROWN));
        foodItem.add(new FoodCmp());

        return foodItem;
    }

}
