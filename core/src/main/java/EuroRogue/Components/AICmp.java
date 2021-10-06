package EuroRogue.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.EuroRogue;
import EuroRogue.SortByDistance;
import EuroRogue.TargetType;
import EuroRogue.TerrainType;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Measurement;
import squidpony.squidmath.Coord;

public class AICmp implements Component
{
    public List<Integer> visibleEnemies = new ArrayList();
    public List<Integer> visibleFriendlies = new ArrayList();
    public List<Integer> visibleItems = new ArrayList();
    public Coord location = Coord.get(-1,-1);
    public Set<Coord> alerts = new HashSet<>();
    public List<Coord> pathToFollow = new ArrayList();
    public Integer target;

    public DijkstraMap dijkstraMap;
    public char[][] decoDungeon;
    public ArrayList<TerrainType> traversable =  new ArrayList<>();




    public AICmp(){}
    public AICmp (char[][] map)
    {
        this.dijkstraMap = new DijkstraMap(map, Measurement.EUCLIDEAN);
        this.dijkstraMap.setBlockingRequirement(0);
        this.dijkstraMap.initializeCost(getTerrainCosts(map));
        this.decoDungeon = map;
    }

    public AICmp (char[][] map, ArrayList<TerrainType> traversable)
    {
        this.dijkstraMap = new DijkstraMap(map, Measurement.EUCLIDEAN);
        this.dijkstraMap.setBlockingRequirement(0);
        this.traversable= traversable;
        this.dijkstraMap.initializeCost(getTerrainCosts(map));
        this.decoDungeon = map;
    }

    public double[][] getTerrainCosts(char[][] decoDungeon)
    {
        double[][] terrainCosts = new double[decoDungeon.length][decoDungeon[0].length];
        for(int x=0; x<decoDungeon.length; x++){
            for(int y=0; y<decoDungeon[0].length; y++)
            {

                if(!traversable.contains(TerrainType.getTerrainTypeFromChar(decoDungeon[x][y])))
                {
                    terrainCosts[x][y] = DijkstraMap.WALL;
                    continue;
                }
                switch(decoDungeon[x][y])
                {
                    case '.':
                    case '"':
                    case ':':
                    case '<':
                    case '>':
                        terrainCosts[x][y]=1.0;
                        break;
                    case '#':
                    case '§':
                        terrainCosts[x][y]= DijkstraMap.WALL;
                        break;
                    case ',':
                        terrainCosts[x][y]=1.3;
                        break;
                    case '~':
                        terrainCosts[x][y]=2.0;
                        break;

                }
            }
        }
        return terrainCosts;
    }

    public void addTraversable(TerrainType terrainType)
    {
        traversable.add(terrainType);
        dijkstraMap.costMap=getTerrainCosts(decoDungeon);
    }
    public void removeTraversable(TerrainType terrainType)
    {
        traversable.remove(terrainType);
        dijkstraMap.costMap=getTerrainCosts(decoDungeon);
    }

    public void addTraversables(List<TerrainType> terrainTypes)
    {
        traversable.addAll(terrainTypes);
        dijkstraMap.costMap=getTerrainCosts(decoDungeon);
    }
    public Entity getTargetEntity(EuroRogue game)
    {
        return game.getEntity(target);
    }

    public void removeTarget(Integer targetID)
    {
        if(target.equals(targetID)) target = null;
        int index=0;
        for(Integer id:visibleEnemies)
        {
            if(id.equals(targetID)) break;
            index++;

        }
        if(index<visibleEnemies.size()) visibleEnemies.remove(index);
    }
    public ArrayList<Coord> getEnemyLocations(LevelCmp levelCmp)
    {
        ArrayList<Coord> enemyLocations = new ArrayList<>();
        for(Integer enemyID : visibleEnemies)
        {
            Coord coord = levelCmp.actors.getPosition(enemyID);
            if(enemyLocations!=null) enemyLocations.add(coord);
        }
        return enemyLocations;
    }
    public ArrayList<Coord> getFriendLocations(LevelCmp levelCmp)
    {
        ArrayList<Coord> friendLocations = new ArrayList<>();
        for(Integer friendID : visibleFriendlies)
        {
            Coord coord = levelCmp.actors.getPosition(friendID);
            if(friendLocations!=null) friendLocations.add(coord);
        }
        return friendLocations;
    }
    public ArrayList<Coord> getItemLocations(LevelCmp levelCmp)
    {
        ArrayList<Coord> itemLocations = new ArrayList<>();
        for(Integer friendID : visibleItems)
        {
            Coord coord = levelCmp.items.getPosition(friendID);
            if(itemLocations!=null) itemLocations.add(coord);
        }
        return itemLocations;
    }
}
