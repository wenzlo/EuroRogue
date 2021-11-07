package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import EuroRogue.MyDungeonUtility;
import EuroRogue.MyMapUtility;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.SpatialMap;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.mapping.LineKit;
import squidpony.squidgrid.mapping.SectionDungeonGenerator;
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
    public GreasedRegion caves;
    public GreasedRegion caveWalls;
    public GreasedRegion rooms;
    public SpatialMap<Integer, Integer> actors = new SpatialMap();
    public SpatialMap<Integer, Integer> items = new SpatialMap();
    public SpatialMap<Integer, Integer> objects = new SpatialMap();
    public float fowColor;

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
        this.bgColors = MyMapUtility.generateDefaultBGColorsFloat(decoDungeon);
        this.colors = MyMapUtility.generateDefaultColorsFloat(decoDungeon);
        this.floors = new GreasedRegion(bareDungeon, '.');

    }
    public LevelCmp (SectionDungeonGenerator generator)
    {
        this.decoDungeon=generator.getDungeon();
        this.bareDungeon=generator.getBareDungeon();
        this.lineDungeon = MyDungeonUtility.hashesToLines(decoDungeon);
        this.floors = generator.finder.allFloors.copy();
        this.caves =generator.placement.finder.allCaves.copy();
        this.caveWalls = generator.placement.finder.allCaves.copy().fringe8way();
        this.rooms = generator.finder.allRooms.copy();
        this.bgColors = MyMapUtility.generateDefaultBGColorsFloat(decoDungeon);
        this.colors = MyMapUtility.generateDefaultColorsFloat(decoDungeon);
        this.floors = new GreasedRegion(bareDungeon, '.');
        this.prunedDungeon = new char[decoDungeon[0].length][decoDungeon.length];
        this.doors = new GreasedRegion(decoDungeon, '+').or(new GreasedRegion(decoDungeon, '/'));
        LineKit.pruneLines(this.lineDungeon, new GreasedRegion(), prunedDungeon);
        for(int x=0; x<this.lineDungeon.length; x++){
            for(int y=0; y<this.lineDungeon[0].length; y++){
                if(!floors.contains(Coord.get(x,y))&&caveWalls.contains(Coord.get(x,y)))
                {
                    lineDungeon[x][y]='#';

                } else if(!caves.contains(x,y) && !caveWalls.contains(x,y))
                {
                    this.bgColors[x][y] = SColor.SLATE_GRAY.toFloatBits();
                    if(!doors.contains(x,y))
                        this.colors[x][y] = SColor.SLATE_GRAY.toFloatBits();
                }
                if(lineDungeon[x][y]==' ' &&
                        generator.placement.finder.allCaves.copy().fringe8way(2).contains(x,y)) lineDungeon[x][y] = '#';
            }
        }

        this.environment = generator.finder.environment;
        this.resistance = MyDungeonUtility.generateSimpleResistances(decoDungeon);

    }

    public boolean isBlocked(Coord coord)
    {
        if (actors.positions().contains(coord)) return  true;
        if (objects.positions().contains(coord)) return true;
        return !new GreasedRegion(bareDungeon, '.').contains(coord);
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
