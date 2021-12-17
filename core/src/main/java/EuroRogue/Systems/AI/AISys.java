package EuroRogue.Systems.AI;

import static EuroRogue.TargetType.AOE;
import static EuroRogue.TargetType.ENEMY;
import static EuroRogue.TargetType.SELF;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.html.HTMLDocument;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.AI.AIType;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.DetectedCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FactionCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.NoiseMapCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.FrozenEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.MakeCampEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.EventComponents.RestEvt;
import EuroRogue.GameState;
import EuroRogue.IColoredString;
import EuroRogue.ItemEvtType;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.ScheduledEvt;
import EuroRogue.SortByDistance;
import EuroRogue.StatusEffectCmps.Stalking;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.MakeCampSys;
import EuroRogue.TargetType;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.OrderedMap;


public class AISys extends MyEntitySystem
{
    public ImmutableArray<Entity> entities;
    public AIType aiType;

    public AISys()
    {
        super.priority = 8;
        this.aiType = AIType.DEFAULT_AI;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(aiType.cls).get());
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
            NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, entity);

            observe(entity);
            getGame().updateAbilities(entity);


            MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).display;
            ArrayList<StatusEffect> focusStatusEffects = getGame().getStatusEffects(getGame().getFocus());
            if(focusStatusEffects.contains(StatusEffect.FROZEN) || focusStatusEffects.contains(StatusEffect.BURNING))
                if(display.hasActiveAnimations()) continue;

            PositionCmp position = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
            StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
            AICmp aiComp = (AICmp) CmpMapper.getAIComp(statsCmp.mobType.aiType, entity);
            ManaPoolCmp manaPool = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, entity);
            CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, entity);
            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);

            /*if(CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, entity)!=null)
            {
                scheduleFrozenEvt( entity);
                return;
            }*/
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


           /* String name = ((NameCmp) CmpMapper.getComp(CmpType.NAME, entity)).name;
            IColoredString.Impl<SColor> coloredString = new IColoredString.Impl<>(ticker.tick+" "+name+" takes turn", SColor.WHITE);
            entity.add(new LogEvt(ticker.tick, coloredString));*/
            boolean itemEventScheduled = false;
            if(CmpMapper.getStatusEffectComp(StatusEffect.EXHAUSTED, entity) != null
                && aiComp.visibleEnemies.isEmpty())
            {
                scheduleCampEvt(entity);
                continue;
            }
            for(EquipmentSlot slot : EquipmentSlot.values())
            {
                if(inventoryCmp.getSlotEquippedID(slot)==null)
                {

                    for(Integer itemID : inventoryCmp.getItemIDs())
                    {

                        Entity itemEntity = getGame().getEntity(itemID);

                        EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, itemEntity);
                        if(equipmentCmp!=null)
                        {
                            if(equipmentCmp.slotsOccupied[0]==slot)
                            {

                                scheduleItemEvt(entity, itemID, ItemEvtType.EQUIP);
                                itemEventScheduled=true;
                                break;
                            }
                        }

                    }

                }
            }
            if(itemEventScheduled) continue;

            ArrayList<Ability> availableAbilities = getAvailableActions(entity);
            if(!availableAbilities.isEmpty() &! aiComp.visibleEnemies.isEmpty())
            {
                Collections.shuffle(availableAbilities);
                for(Ability ability : availableAbilities)
                {
                    if(!ability.getIdealLocations(entity, level).isEmpty())
                    {
                        scheduleActionEvt(entity, ability);
                        break;
                    }

                }
                if(!ticker.getScheduledActions(entity).isEmpty()) continue;



            }

            else if(manaPool.active.size()<manaPool.spent.size() || manaPool.active.size()==0)
            {

               scheduleRestEvt(entity);

            }
            else if(!aiComp.visibleEnemies.isEmpty())
            {
                if(CmpMapper.getStatusEffectComp(StatusEffect.EXHAUSTED, entity) == null)
                {
                    setTarget(entity, getGame().getEntity(aiComp.visibleEnemies.get(0)));
                    Coord targetLoc = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, aiComp.getTargetEntity(getGame()))).coord;
                    aiComp.pathToFollow = aiComp.dijkstraMap.findPath(1, null, null, aiComp.location, targetLoc);


                } else {

                    List<Coord> fs = aiComp.getEnemyLocations(level);
                    Coord[] fearSources = new Coord[]{fs.get(0), fs.get(fs.size()-1)};

                    aiComp.pathToFollow = aiComp.dijkstraMap.findFleePath(1, 1.2, level.getPositions(aiComp.visibleFriendlies), null, position.coord,  fearSources);
                    if(aiComp.pathToFollow.size()==0)
                    {
                        Coord targetLoc = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, aiComp.getTargetEntity(getGame()))).coord;
                        aiComp.pathToFollow = aiComp.dijkstraMap.findPath(1, null, null, aiComp.location, targetLoc);
                    }


                }
                if(aiComp.pathToFollow.size()>0)
                {
                    Coord step = aiComp.pathToFollow.remove(0);
                    double terrainCost = aiComp.movementCosts[step.x][step.y];

                    scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                    continue;

                }


            }

            else if(!aiComp.alerts.isEmpty())
            {
                ArrayList<Coord> alerts = new ArrayList<>(aiComp.alerts.values());
                Collections.sort(alerts, new SortByDistance(aiComp.location));
                Coord targetLoc = alerts.get(0);
                aiComp.pathToFollow = aiComp.dijkstraMap.findPath(1, null, null, aiComp.location, targetLoc);
                if(aiComp.pathToFollow.size()>0)
                {
                    Coord step = aiComp.pathToFollow.remove(0);
                    double terrainCost = aiComp.movementCosts[step.x][step.y];
                    scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                    continue;
                }
            }
            else if(aiComp.pathToFollow.size()>0)
            {
                Coord step = aiComp.pathToFollow.remove(0);
                double terrainCost = aiComp.movementCosts[step.x][step.y];
                scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                continue;
            }
            else if(aiComp.visibleFriendlies.size()>1)
            {
                ArrayList<Coord> buffer = new ArrayList<>();
                aiComp.pathToFollow = aiComp.dijkstraMap.findFleePath(buffer,20, 20, (double) 1.2,buffer, buffer, position.coord, aiComp.getFriendLocations(level).get(0), aiComp.getFriendLocations(level).get(1));

                if(aiComp.pathToFollow.size()>0)
                {
                    Coord step = aiComp.pathToFollow.remove(0);
                    double terrainCost = aiComp.movementCosts[step.x][step.y];
                    scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                    continue;
                }
            }

            else /*if(!manaPool.spent.isEmpty())*/scheduleRestEvt(entity);
        }
    }


    public void observe(Entity entity)
    {
        StatsCmp observerStats = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
        AICmp aiComp = CmpMapper.getAIComp(observerStats.mobType.aiType, entity);

        aiComp.location=((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
        FOVCmp selfFOV = (FOVCmp) CmpMapper.getComp(CmpType.FOV, entity);

        aiComp.visibleEnemies.clear();
        aiComp.visibleFriendlies.clear();
        aiComp.visibleNeutrals.clear();
        aiComp.visibleItems.clear();
        GreasedRegion goals = new GreasedRegion();
        FactionCmp myFaction = (FactionCmp) CmpMapper.getComp(CmpType.FACTION, entity);
        LevelCmp levelCmp = (LevelCmp)CmpMapper.getComp(CmpType.LEVEL,getGame().currentLevel);
        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);

        if(!detected(entity)) entity.remove(DetectedCmp.class);
        Iterator<Coord> positions = levelCmp.actors.positionIterator();
        while(positions.hasNext())
        {
            Coord entPos = positions.next();
            if(entPos==aiComp.location)
            {
                continue;
            }

            Integer entID = levelCmp.actors.get(entPos);
            Entity actor = getGame().getEntity(entID);

            FactionCmp.Faction otherFaction = ((FactionCmp) CmpMapper.getComp(CmpType.FACTION, actor)).faction;
            StatsCmp enemyStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, actor);
            if(selfFOV.visible.contains(entPos) && lightingCmp.fgLightLevel[entPos.x][entPos.y] >= enemyStats.getVisibleLightLvl()
                        ||  selfFOV.nightVision[entPos.x][entPos.y] > 0)
            {
                if(myFaction.enemy.contains(otherFaction))
                {
                    aiComp.visibleEnemies.add(entID);
                    actor.remove(Stalking.class);
                    actor.add(new DetectedCmp());
                    goals.add(entPos);

                } else if(myFaction.allied.contains(otherFaction))
                    aiComp.visibleFriendlies.add(entID);
                else
                    aiComp.visibleNeutrals.add(entID);
            }

        }

        for(Coord entPos:levelCmp.items.positions())
        {
            Integer entID = levelCmp.items.get(entPos);
            if(selfFOV.visible.contains(entPos))
            {
                aiComp.visibleItems.add(entID);
                //goals.add(entPos);
            }
        }

        if(aiComp.target!=null)
        {
            if(!aiComp.visibleEnemies.contains(aiComp.target) && !aiComp.visibleNeutrals.contains(aiComp.target) && !aiComp.visibleFriendlies.contains(aiComp.target))
            {
                clearTarget(entity);
            }
        }


        if(aiComp.target==null &! aiComp.visibleEnemies.isEmpty())
        {
            Entity possibleTarget = getGame().getEntity(aiComp.visibleEnemies.get(0));
            if(possibleTarget!=null) setTarget(entity, possibleTarget);
        }

        List<Integer> alertsToRemove = new ArrayList<>();
        for(Integer id : aiComp.alerts.keySet())
        {
            Coord alertLoc = aiComp.alerts.get(id);
            if(selfFOV.nightVision[alertLoc.x][alertLoc.y]>0)
            {

                alertsToRemove.add(id);
            }

        }
        for(Integer id : alertsToRemove) aiComp.alerts.remove(id);
        goals.addAll(aiComp.alerts.values());

        /*if(goals.size()>0)
        {
            aiComp.dijkstraMap.clearGoals();
            aiComp.dijkstraMap.setGoals(goals);
            aiComp.dijkstraMap.partialScan(15);
            //System.out.println(aiComp.visibleEnemies);
        }*/




    }

    public ArrayList<Ability> getAvailableActions(Entity entity)
    {
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, entity);
        ArrayList<Ability> availableAbilities = new ArrayList<>();
        for(Skill skill : codexCmp.prepared)
        {
            Ability abilityCmp = CmpMapper.getAbilityComp(skill, entity);
            getGame().updateAbility(abilityCmp, entity);
            //if(entity== getGame().getFocusTarget()) System.out.println(skill+" "+abilityComp.isAvailable());
            if(skill.skillType== Skill.SkillType.REACTION) continue;

            if(abilityCmp.isAvailable()) availableAbilities.add(abilityCmp);
        }
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);
        for(Integer itemID:inventoryCmp.getScrollsIDs())
        {
            Entity itemEntity = getGame().getEntity(itemID);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, itemEntity);
            if(scrollCmp.consumed) continue;
            Ability abilityCmp = (Ability) CmpMapper.getAbilityComp(scrollCmp.skill, itemEntity);
            getGame().updateAbility(abilityCmp, entity);
            if(abilityCmp.isAvailable() && abilityCmp.getSkill().skillType != Skill.SkillType.REACTION)
            {
                availableAbilities.add(abilityCmp);
            }
        }
        return availableAbilities;
    }

    public int scheduleMoveEvt(Entity entity, Direction direction, double terrainCost)
    {
        if(CmpMapper.getStatusEffectComp(StatusEffect.EXHAUSTED, entity)!=null)
        {
            genExhaustedLogEvent(entity);
            return 0;
        }

        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        PositionCmp positionCmp = (PositionCmp)CmpMapper.getComp(CmpType.POSITION, entity);
        int gameTick = ticker.tick;
        int scheduledTick = gameTick + ((StatsCmp) CmpMapper.getComp(CmpType.STATS, entity)).getTTMove(direction, terrainCost);

        MoveEvt moveEvt = new MoveEvt(entity.hashCode(), positionCmp.coord.translate(direction));
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),moveEvt);
        ticker.actionQueue.add(scheduledEvt);

        return scheduledTick;
    }

    public int scheduleActionEvt (Entity entity, Ability ability)
    {

        TargetType targetType = ability.getTargetType();
        NameCmp nameCmp = (NameCmp) CmpMapper.getComp(CmpType.NAME, entity);

        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        int gameTick = ticker.tick;
        int scheduledTick = gameTick + ability.getTTPerform(entity);

        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
        AICmp aiCmp = (AICmp) CmpMapper.getAIComp(statsCmp.mobType.aiType, entity);
        Integer targetID;
        if(targetType==SELF) targetID=entity.hashCode();
        else targetID = aiCmp.target;

        HashMap<Integer, Integer> targets = new HashMap<>();
        targets.put(targetID, ability.getDamage(entity));
        if(ability.getTargetType()==AOE) targets.clear();

        ActionEvt actionEvt = new ActionEvt(entity.hashCode(), ability.getScrollID(), ability.getSkill(), targets, ability.getStatusEffects(entity));
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),actionEvt);
        ticker.actionQueue.add(scheduledEvt);

        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        ability.spawnGlyph(windowCmp.display, windowCmp.lightingHandler, entity);
        if(ability.getTargetType()==ENEMY && ability.getSkill()!=Skill.CHARGE)
            rotate(entity, getGame().getEntity(targetID));
        if(ability.getTargetType()==AOE && getGame().gameState!=GameState.AIMING)
        {
            LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

            Coord location = ((PositionCmp)CmpMapper.getComp(CmpType.POSITION, entity)).coord;
            if(levelCmp.actors.getPosition(aiCmp.target)!=null)
                ability.apply(location, levelCmp.actors.getPosition(aiCmp.target));
        }

        return scheduledTick;
    }

    public int scheduleRestEvt (Entity entity)
    {
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS,entity);

        int scheduledTick = ticker.tick + statsCmp.getTTRest();

        RestEvt restEvent = new RestEvt(entity.hashCode());
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),restEvent);
        ticker.actionQueue.add(scheduledEvt);
        String name = ((NameCmp)CmpMapper.getComp(CmpType.NAME, entity)).name;

        return scheduledTick;
    }

    public int scheduleFrozenEvt (Entity entity)
    {
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);

        int scheduledTick = ticker.tick + 1;

        FrozenEvt frozenEvt = new FrozenEvt(entity.hashCode());
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),frozenEvt);
        ticker.actionQueue.add(scheduledEvt);



        return scheduledTick;
    }

    public int scheduleCampEvt (Entity entity)
    {
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);


        int scheduledTick = ticker.tick + 50;

        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);
        CampEvt campEvt = new CampEvt(entity.hashCode(), inventoryCmp.getEquippedIDs());
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),campEvt);
        ticker.actionQueue.add(scheduledEvt);

        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        NoiseMapCmp noiseMapCmp = (NoiseMapCmp) CmpMapper.getComp(CmpType.NOISE_MAP, entity);
        noiseMapCmp.noiseMap.clearSounds();

        noiseMapCmp.noiseMap.setSound(positionCmp.coord, 15);
        noiseMapCmp.noiseMap.scan();
        OrderedMap<Coord, Double> alerted = noiseMapCmp.noiseMap.findAlerted(levelCmp.actors.positions(), new HashMap<>());
        alerted.remove(positionCmp.coord);
        for(Coord position : alerted.keySet())
        {
            Entity alertedEntity = getGame().getEntity(levelCmp.actors.get(position));
            StatsCmp alertedStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, alertedEntity);
            if(alerted.get(position)>=alertedStats.getSoundDetectionLvl())
            {
                Entity alertedActor = getGame().getEntity(levelCmp.actors.get(position));
                StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, alertedActor);
                AICmp alertedAI = CmpMapper.getAIComp(statsCmp.mobType.aiType, alertedActor);
                alertedAI.alerts.put(entity.hashCode(),positionCmp.coord);


            }
        }
        entity.add(new MakeCampEvt(entity.hashCode(), scheduledTick));




        return scheduledTick;
    }

    public int scheduleItemEvt (Entity entity, Integer itemID, ItemEvtType itemEvtType)
    {
        Entity itemEntity = getGame().getEntity(itemID);
        ItemCmp itemCmp = (ItemCmp) CmpMapper.getComp(CmpType.ITEM, itemEntity);
        if(itemEvtType==ItemEvtType.PICKUP)
        {
            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);
            if(inventoryCmp.isFull() ) {
                if (itemCmp.type != ItemType.FOOD && itemCmp.type != ItemType.SCROLL && itemCmp.type != ItemType.MANA) {
                    Integer tick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
                    LogCmp log = (LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow);

                    IColoredString.Impl string = new IColoredString.Impl();
                    string.append(tick.toString(), SColor.WHITE);
                    string.append(" Inventory Is Full!", SColor.LIGHT_YELLOW_DYE);
                    LogEvt logEvt = new LogEvt(tick, string);

                    log.logEntries.add(logEvt.entry);
                    return tick + 1;
                }
            }
        }
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);

        int scheduledTick = ticker.tick + statsCmp.getTTMove(Direction.UP,1);

        ItemEvt itemEvt = new ItemEvt(itemID, entity.hashCode(), itemEvtType);
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),itemEvt);
        ticker.actionQueue.add(scheduledEvt);
        String name = ((NameCmp)CmpMapper.getComp(CmpType.NAME, entity)).name;


        return scheduledTick;
    }

    public void setTarget(Entity actor, Entity target)
    {
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, actor);
        AICmp aiCmp = (AICmp) CmpMapper.getAIComp(statsCmp.mobType.aiType, actor);
        if(actor==getGame().getFocus())
        {
            ImmutableArray<Entity> focusTargetInArray = getEngine().getEntitiesFor(Family.all(FocusTargetCmp.class).get());
            if(focusTargetInArray.size()>0)
            {
                FocusTargetCmp focusTargetCmp = getGame().getFocusTarget().remove(FocusTargetCmp.class);
                if(focusTargetCmp!=null)
                {
                    target.add(focusTargetCmp);

                }else {

                    target.add(new FocusTargetCmp());
                }


            }else target.add(new FocusTargetCmp());

        }
        aiCmp.target = target.hashCode();
    }

    public void clearTarget(Entity actor)
    {

        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, actor);
        AICmp aiCmp = (AICmp) CmpMapper.getAIComp(statsCmp.mobType.aiType, actor);
        if(aiCmp.target!=null)
        {
            Entity targetEntity = getGame().getEntity(aiCmp.target);
            if(targetEntity!=null)
            {
                FocusTargetCmp focusTargetCmp = (FocusTargetCmp) targetEntity.remove(FocusTargetCmp.class);
                if(focusTargetCmp!=null)
                {
                    ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).display.glyphs.remove(focusTargetCmp.indicatorGlyph2);
                }
            }
        }
        aiCmp.target=null;
    }

    public void rotate(Entity actor, Entity target)
    {
        if(target == null) return;
        Coord targetPosition = ((PositionCmp)CmpMapper.getComp(CmpType.POSITION, target)).coord;
        rotate(actor, targetPosition);
        }


    public void rotate(Entity actor, Coord target)
    {
        if(actor == null) return;
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH,actor);
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);

        positionCmp.orientation = Direction.toGoTo(positionCmp.coord,target);
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);

        if(glyphsCmp.leftGlyph!=null)
            (windowCmp.display).slide(0f,glyphsCmp.leftGlyph, glyphsCmp.getLeftGlyphPositionX( windowCmp.display, positionCmp), glyphsCmp.getLeftGlyphPositionY(windowCmp.display, positionCmp), 0.1f, null);
        if(glyphsCmp.rightGlyph!=null)
            (windowCmp.display).slide(0f,glyphsCmp.rightGlyph, glyphsCmp.getRightGlyphPositionX( windowCmp.display, positionCmp), glyphsCmp.getRightGlyphPositionY(windowCmp.display, positionCmp), 0.1f, null);
    }

    public boolean detected(Entity actor)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        for(Integer id : levelCmp.actors.identities())
        {
            Entity entity = getGame().getEntity(id);
            StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
            AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, entity);
            if(aiCmp.visibleEnemies.contains(levelCmp.actors.get(actor.hashCode())))
                return true;
        }
        return false;
    }

    private void genExhaustedLogEvent(Entity entity)
    {
        int tick = ((TickerCmp)CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        String name = ((NameCmp)CmpMapper.getComp(CmpType.NAME, entity)).name;
        IColoredString.Impl  string = new IColoredString.Impl();
        string.append(tick+" ", SColor.WHITE);
        string.append(name, SColor.LIGHT_YELLOW_DYE);
        string.append(" is to Exhausted to Move.");
        string.append(" Make Camp and rest!", SColor.RED);
        LogEvt logEvt = new LogEvt(tick, string);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(logEvt.entry);
    }

}
