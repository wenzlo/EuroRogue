package EuroRogue.Components.AI;

import java.util.ArrayList;
import java.util.Arrays;

import EuroRogue.TerrainType;

public class AIRatCmp extends AICmp
{

    public AIRatCmp()
    {
        super( new ArrayList(Arrays.asList(TerrainType.STONE, TerrainType.MOSS, TerrainType.SHALLOW_WATER, TerrainType.BRIDGE)));
    }
}
