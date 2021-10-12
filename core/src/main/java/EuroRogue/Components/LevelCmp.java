package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import EuroRogue.MyDungeonUtility;
import EuroRogue.MyFOV;
import EuroRogue.MyMapUtility;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.SpatialMap;
import squidpony.squidgrid.mapping.LineKit;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;

public class LevelCmp implements Component
{
    public char[][] decoDungeon, bareDungeon, lineDungeon, prunedDungeon;
    public int[][] environment;
    public float[][] colors, bgColors;
    public double[][] resistance;
    public GreasedRegion floors;
    public GreasedRegion doors;
    public SpatialMap<Integer, Integer> actors = new SpatialMap();
    public SpatialMap<Integer, Integer> items = new SpatialMap();
    public SpatialMap<Integer, Integer> objects = new SpatialMap();


   /* public LevelCmp(DungeonGenerator dungeonGenerator)
    {
        this.decoDungeon=dungeonGenerator.getDungeon();
        this.bareDungeon=dungeonGenerator.getBareDungeon();
        this.lineDungeon = DungeonUtility.hashesToLines(decoDungeon);
        this.prunedDungeon = new char[decoDungeon[0].length][decoDungeon.length];
        LineKit.pruneLines(this.lineDungeon, new GreasedRegion(), prunedDungeon);
        this.environment = dungeonGenerator.e;
        this.resistance = DungeonUtility.generateResistances(decoDungeon);
        this.bgColors = MyMapUtility.generateDefaultBGColorsFloat(decoDungeon);
        this.colors = MyMapUtility.generateDefaultColorsFloat(decoDungeon);
        this.floors = new GreasedRegion(bareDungeon, '.');
    }*/
    public LevelCmp (char[][] decoDungeon, char[][] bareDungeon, int[][] environment)
    {
        this.decoDungeon=decoDungeon;
        this.bareDungeon=bareDungeon;
        this.lineDungeon = MyDungeonUtility.hashesToLines(decoDungeon);
        this.prunedDungeon = new char[decoDungeon[0].length][decoDungeon.length];
        LineKit.pruneLines(this.lineDungeon, new GreasedRegion(), prunedDungeon);
        this.environment = environment;
        this.resistance = MyDungeonUtility.generateSimpleResistances(decoDungeon);
        System.out.println("Resistance at level creation");
        System.out.println(new GreasedRegion(this.resistance, 0.0));
        this.bgColors = MyMapUtility.generateDefaultBGColorsFloat(decoDungeon);
        this.colors = MyMapUtility.generateDefaultColorsFloat(decoDungeon);
        this.floors = new GreasedRegion(bareDungeon, '.');
    }

    public boolean isOccupied(Coord coord)
    {
        return actors.get(coord)!=null;
    }

    public List<Coord> getPositions(List<Integer> actorIDs)
    {
        List<Coord> positions = new ArrayList();
        for(Integer id:actorIDs)
        {
            positions.add(actors.getPosition(id));
        }
        return positions;
    }
    public List<Coord> getAdjCells(Coord coord)
    {
        List<Coord> adjCells = new ArrayList<>();
        for(Direction direction: Direction.CLOCKWISE)
        {
            int deltaX = direction.deltaX;
            int deltaY = direction.deltaY;
            Coord coord1 = Coord.get(coord.x + deltaX, coord.y + deltaY);
            if(floors.contains(coord1)) adjCells.add(coord1);

        }
        return adjCells;
    }
    public Set<Integer> getAdjActorIDs(Coord coord)
    {
        Set<Integer> actorIDs = new HashSet<>();
        for(Coord cell:getAdjCells(coord))
        {
            Integer actorID = actors.get(cell);
            if(actorID!=null) actorIDs.add(actorID);

        }
        return  actorIDs;
    }

}
