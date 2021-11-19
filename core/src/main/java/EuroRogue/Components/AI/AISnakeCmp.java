package EuroRogue.Components.AI;

import java.util.ArrayList;
import java.util.Arrays;

import EuroRogue.TerrainType;

public class AISnakeCmp extends AICmp
{

    public AISnakeCmp()
    {
        super( new ArrayList(Arrays.asList(TerrainType.STONE, TerrainType.MOSS, TerrainType.SHALLOW_WATER, TerrainType.DEEP_WATER, TerrainType.BRIDGE)));

    }
}
