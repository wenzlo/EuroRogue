package EuroRogue.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import EuroRogue.EuroRogue;
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
    public HashMap<Integer,Coord> alerts = new HashMap<>();
    public List<Coord> pathToFollow = new ArrayList();
    public Integer target;
    public DijkstraMap dijkstraMap;
    public ArrayList<TerrainType> traversable =  new ArrayList<>();




    //public AICmp(){}

    public AICmp (char[][] bareDungeon, char[][] decoDungeon, ArrayList<TerrainType> traversable)
    {

        this.dijkstraMap = new DijkstraMap(bareDungeon, Measurement.EUCLIDEAN);
        this.traversable= traversable;
        this.dijkstraMap.initializeCost(getTerrainCosts(decoDungeon));
    }

    public double[][] getTerrainCosts(char[][] map)
    {

        int width = map.length;
        int height = map[0].length;
        double[][] costMap = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                if(!traversable.contains(TerrainType.getTerrainTypeFromChar(map[i][j])))
                {
                    costMap[i][j] = DijkstraMap.WALL;
                    continue;
                }

                switch (map[i][j]) {

                    case '§':
                    case '#':
                        costMap[i][j] = DijkstraMap.WALL;
                        break;

                    case '.':
                    case ':':
                        costMap[i][j] = 1.0;
                        break;
                    case ',':
                        costMap[i][j] = 1.3;
                        break;
                    case '~':
                    case '+':
                        costMap[i][j] = 2.0;
                        break;
                    default:
                        costMap[i][j] = 1.0;
                }
            }


        }

        return costMap;

    }


    public void addTraversable(TerrainType terrainType, char[][] decoDungeon)
    {
        traversable.add(terrainType);
        dijkstraMap.costMap=getTerrainCosts(decoDungeon);
    }
    public void removeTraversable(TerrainType terrainType, char[][] decoDungeon)
    {
        traversable.remove(terrainType);
        dijkstraMap.costMap=getTerrainCosts(decoDungeon);
    }

    public void addTraversables(List<TerrainType> terrainTypes, char[][] decoDungeon)
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
