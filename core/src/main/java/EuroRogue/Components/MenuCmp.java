package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.MenuItem;
import squidpony.squidgrid.SpatialMap;

public class MenuCmp implements Component
{
    public SpatialMap<Character, MenuItem> menuMap = new SpatialMap<>();

}
