package EuroRogue.Systems.AI;


import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.AI.AIType;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.GameState;
import EuroRogue.MySparseLayers;
import EuroRogue.SortByDistance;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;


public class AISysSnake extends AISys
{
    public AISysSnake()
    {
        super();
        super.aiType = AIType.SNAKE_AI;

    }
    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        if(getGame().gameState!=GameState.PLAYING) return;
        EuroRogue game = getGame();
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, game.ticker);
        LevelCmp level = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);

        for (Entity entity: entities)
        {
            if(!ticker.getScheduledActions(entity).isEmpty())continue;
            if( CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, entity)!=null && entity!=getGame().getFocus()) continue;
            observe(entity);
            getGame().updateAbilities(entity);


            MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).display;
            ArrayList<StatusEffect> focusStatusEffects = getGame().getStatusEffects(getGame().getFocus());
            if(focusStatusEffects.contains(StatusEffect.FROZEN) || focusStatusEffects.contains(StatusEffect.BURNING))
                if(display.hasActiveAnimations()) continue;

            PositionCmp position = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
            StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
            AICmp aiComp = CmpMapper.getAIComp(statsCmp.mobType.aiType, entity);
            ManaPoolCmp manaPool = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, entity);
            CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, entity);
            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);


            if(CmpMapper.getStatusEffectComp(StatusEffect.BURNING, entity)!=null) {

                Direction rndDir = game.rng.getRandomElement(Direction.CLOCKWISE);
                int newX = position.coord.x + rndDir.deltaX;
                int newY = position.coord.y + rndDir.deltaY;

                while (aiComp.dijkstraMap.costMap[newX][newY]== DijkstraMap.WALL)
                {
                    rndDir = game.rng.getRandomElement(Direction.CLOCKWISE);
                    newX = position.coord.x + rndDir.deltaX;
                    newY = position.coord.y + rndDir.deltaY;
                }
                scheduleMoveEvt(entity, rndDir, aiComp.movementCosts[newX][newY]);
                continue;
            }

            if(manaPool.inert(codexCmp) && getGame().gameState == GameState.PLAYING)
            {
                scheduleRestEvt(entity);
                continue;
            }

            if(game.getFocus()==entity)
            {
                if(CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, entity)!=null) scheduleFrozenEvt(entity);

                getGame().getInput();

                continue;
            }

            List<Coord> grass = Arrays.asList(new GreasedRegion(level.decoDungeon, '"').asCoords());
            Collections.sort(grass, new SortByDistance(position.coord));

            boolean stalking = CmpMapper.getStatusEffectComp(StatusEffect.STALKING,entity) != null;
            ArrayList<Ability> availableAbilities = getAvailableActions(entity);
            if(aiComp.visibleEnemies.isEmpty())
            {
                if(statsCmp.getRestLvl()>10)
                    scheduleRestEvt(entity);

                else scheduleCampEvt(entity);

                continue;
            }
            else if(aiComp.visibleEnemies.isEmpty()  && aiComp.pathToFollow.isEmpty())
            {
                ArrayList<Coord> impassible = new ArrayList<>();
                impassible.addAll(level.getPositions(aiComp.visibleFriendlies));


                for(Coord coord : grass)
                {
                    aiComp.pathToFollow = aiComp.dijkstraMap.findPath(15,  impassible, null, position.coord, coord);
                    if(!aiComp.pathToFollow.isEmpty()) break;

                }
            }
            else if(!availableAbilities.isEmpty())
            {
                for(Ability ability : availableAbilities)
                {
                    if(!ability.getIdealLocations(entity, level).isEmpty())
                    {
                        scheduleActionEvt(entity, ability);
                        continue;
                    }

                }
            }

            else if( manaPool.active.size()==0)
            {
                if(aiComp.visibleEnemies.isEmpty())
                    if(aiComp.pathToFollow.isEmpty())
                        scheduleRestEvt(entity);

                    else {
                        Coord step = aiComp.pathToFollow.remove(0);
                        double terrainCost = aiComp.movementCosts[step.x][step.y];
                        scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                        continue;
                    }


                else
                {
                    List<Coord> fs = aiComp.getEnemyLocations(level);
                    Coord[] fearSources = new Coord[]{fs.get(0), fs.get(fs.size()-1)};

                    aiComp.pathToFollow = aiComp.dijkstraMap.findFleePath(4, 1.2, level.getPositions(aiComp.visibleFriendlies), null, position.coord,  fearSources);
                    if(aiComp.pathToFollow.size()==0)
                        aiComp.pathToFollow = aiComp.dijkstraMap.findAttackPath(2, 7, null, level.getPositions(aiComp.visibleFriendlies), null, position.coord, fearSources);

                    if(aiComp.pathToFollow.size()==0)
                        scheduleRestEvt(entity);
                    continue;

                }

            }
            else if(!aiComp.visibleEnemies.isEmpty() )
            {
                if(aiComp.target!=null)
                    setTarget(entity, getGame().getEntity(aiComp.visibleEnemies.get(0)));

                Coord targetLoc = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, aiComp.getTargetEntity(getGame()))).coord;

                ArrayList<Coord> impassible = new ArrayList<>();
                impassible.addAll(level.getPositions(aiComp.visibleFriendlies));
                if(CmpMapper.getStatusEffectComp(StatusEffect.STALKING, entity)!=null)
                    aiComp.pathToFollow = aiComp.dijkstraMap.findPath(15,  impassible, null, position.coord, targetLoc);
                else
                {
                    List<Coord> fs = aiComp.getEnemyLocations(level);
                    Coord[] fearSources = new Coord[]{fs.get(0), fs.get(fs.size()-1)};

                    aiComp.pathToFollow = aiComp.dijkstraMap.findFleePath(4, 1.2, level.getPositions(aiComp.visibleFriendlies), null, position.coord,  fearSources);
                    if(aiComp.pathToFollow.size()==0)
                        aiComp.pathToFollow = aiComp.dijkstraMap.findPath(2, level.getPositions(aiComp.visibleFriendlies), null, position.coord, targetLoc);

                }

                if(aiComp.pathToFollow.size()>0)
                {
                    Coord step = aiComp.pathToFollow.remove(0);
                    double terrainCost = aiComp.movementCosts[step.x][step.y];
                    GreasedRegion debugDikj = new GreasedRegion(aiComp.dijkstraMap.costMap, 2.0);
                    //debugDikj[position.coord.x][position.coord.y] ='@';
                    //put.println(debugDikj);
                    scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                    continue;

                }

            }

            if(aiComp.pathToFollow.size()>0 && ticker.getScheduledActions(entity).isEmpty())
            {
                Coord step = aiComp.pathToFollow.remove(0);
                double terrainCost = aiComp.movementCosts[step.x][step.y];
                scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                continue;
            }

            /*else if(!aiComp.visibleItems.isEmpty() &! inventoryCmp.isFull() && inventoryCmp.getEquippedIDs().size()<3)
            {

                Coord targetLoc = aiComp.getItemLocations(level).get(0);
                if(targetLoc == aiComp.location)
                {
                    scheduleItemEvt(entity, level.items.getIdentity(targetLoc), ItemEvtType.PICKUP);
                }
                aiComp.pathToFollow = aiComp.dijkstraMap.findPath(10, level.getPositions(aiComp.visibleItems), null, position.coord, targetLoc);
                if(aiComp.pathToFollow.size()>0)
                {
                    Coord step = aiComp.pathToFollow.remove(0);
                    double terrainCost = aiComp.dijkstraMap.costMap[step.x][step.y];
                    scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                    continue;
                }
            }*/

            else if(!manaPool.spent.isEmpty())scheduleRestEvt(entity);
        }
    }


}
