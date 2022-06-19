package EuroRogue;

import com.badlogic.ashley.core.Entity;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.FoodCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.StatsCmp;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class ScrollFactory
{

    public ScrollFactory(){}

    public Entity generateScrollITem(Skill skill, Coord loc)
    {
        Entity scrollItem = new Entity();
        if(loc!=null)
            scrollItem.add(new PositionCmp(loc));
        scrollItem.add(new ItemCmp(ItemType.SCROLL));
        scrollItem.add(new CharCmp('%', skill.school.color));
        scrollItem.add(new LightCmp());
        scrollItem.add(new NameCmp(skill.name+" Scroll"));
        scrollItem.add(new ScrollCmp(skill));
        Ability ability = Ability.newAbilityCmp(skill, false);
        ability.setScrollID(scrollItem.hashCode());
        ability.setScroll(true);
        scrollItem.add(ability);

        return scrollItem;
    }

}
