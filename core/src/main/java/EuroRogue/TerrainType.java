package EuroRogue;

public enum TerrainType
{
    DEEP_WATER('~', 1.5),
    SHALLOW_WATER(',', 1.5),
    BRIDGE(':', 1.25),
    STONE('.', 1),
    MOSS('"', 0.5);

    public char chr;
    public double noiseMult;

    TerrainType (char chr, double noiseMult)

    {
        this.chr = chr;
        this.noiseMult = noiseMult;
    }

    public static TerrainType getTerrainTypeFromChar (char chr)
    {
        for(TerrainType terrainType : TerrainType.values())
        {
            if(terrainType.chr==chr) return terrainType;
        }
        return STONE;
    }


}
