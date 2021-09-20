package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FactionCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.NoiseMap;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.Components.GlyphsCmp;

import EuroRogue.Components.ItemCmp;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.FrozenEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.GameState;
import EuroRogue.IColoredString;
import EuroRogue.ItemEvtType;
import EuroRogue.MySparseLayers;

import EuroRogue.SortByDistance;
import EuroRogue.TargetType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.CmpType;

import EuroRogue.Components.InventoryCmp;

import EuroRogue.Components.ScrollCmp;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.ActionEvt;

import EuroRogue.MyEntitySystem;
import EuroRogue.ScheduledEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.EventComponents.RestEvt;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.OrderedMap;

import static EuroRogue.TargetType.AOE;
import static EuroRogue.TargetType.ENEMY;
import static EuroRogue.TargetType.ITEM;
import static EuroRogue.TargetType.SELF;


public class AISys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private final GWTRNG rng = new GWTRNG();
    private int  previousTick = 0;

    public AISys()
    {
        super.priority = 8;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(AICmp.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        EuroRogue game = getGame();
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, game.ticker);
        LevelCmp level = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
        int currentTick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;

        for (Entity entity: entities)
        {
            if(!ticker.getScheduledActions(entity).isEmpty())continue;
            if( CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, entity)!=null && entity!=getGame().getFocus()) continue;
            observe(entity);
            if(currentTick > previousTick)
            {

                getGame().updateAbilities(entity);
            }
            previousTick=currentTick;



            MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).display;
            ArrayList<StatusEffect> focusStatusEffects = getGame().getStatusEffects(getGame().getFocus());
            if(focusStatusEffects.contains(StatusEffect.FROZEN) || focusStatusEffects.contains(StatusEffect.BURNING))
                if(display.hasActiveAnimations()) continue;


            PositionCmp position = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
            AICmp ai = (AICmp) CmpMapper.getComp(CmpType.AI, entity);
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

                while (ai.dijkstraMap.costMap[newX][newY]== DijkstraMap.WALL)
                {
                    rndDir = game.rng.getRandomElement(Direction.CLOCKWISE);
                    newX = position.coord.x + rndDir.deltaX;
                    newY = position.coord.y + rndDir.deltaY;
                }
                scheduleMoveEvt(entity, rndDir, ai.dijkstraMap.costMap[newX][newY]);
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
            if(!availableAbilities.isEmpty() &! ai.visibleEnemies.isEmpty())
            {
                Ability abilityToSchedule = rng.getRandomElement(availableAbilities);
                scheduleActionEvt(entity, abilityToSchedule);

            }
            else if(manaPool.active.size()<manaPool.spent.size() || manaPool.active.size()==0){

               scheduleRestEvt(entity);

            } else if(!ai.visibleEnemies.isEmpty())
            {
                setTarget(entity, getGame().getEntity(ai.visibleEnemies.get(0)));
                Coord targetLoc = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, ai.getTargetEntity(getGame()))).coord;
                ai.pathToFollow = ai.dijkstraMap.findPath(10, level.getPositions(ai.visibleFriendlies), null, position.coord, targetLoc);
                if(ai.pathToFollow.size()>0)
                {
                    Coord step = ai.pathToFollow.remove(0);
                    double terrainCost = ai.dijkstraMap.costMap[step.x][step.y];
                    scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);

                }else{

                    scheduleRestEvt(entity);
                }

            }

            else if(!ai.alerts.isEmpty())
            {
                List<Coord> alerts = Arrays.asList(ai.alerts.toArray(new Coord[]{}));
                Collections.sort(alerts, new SortByDistance(ai.location));
                Coord targetLoc = alerts.get(0);
                ai.pathToFollow = ai.dijkstraMap.findPath(10,null, null, position.coord, targetLoc);
                if(ai.pathToFollow.size()>0)
                {
                    Coord step = ai.pathToFollow.remove(0);
                    double terrainCost = ai.dijkstraMap.costMap[step.x][step.y];
                    scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                }
            }
            else if(!ai.visibleItems.isEmpty())
            {

                Coord targetLoc = ai.getTargetLocations(ITEM, getGame()).get(0);
                if(targetLoc == ai.location)
                {
                    scheduleItemEvt(entity, level.items.getIdentity(targetLoc), ItemEvtType.PICKUP);
                }
                ai.pathToFollow = ai.dijkstraMap.findPath(10, level.getPositions(ai.visibleItems), null, position.coord, targetLoc);
                if(ai.pathToFollow.size()>0)
                {
                    Coord step = ai.pathToFollow.remove(0);
                    double terrainCost = ai.dijkstraMap.costMap[step.x][step.y];
                    scheduleMoveEvt(entity, Direction.toGoTo(position.coord, step), terrainCost);
                }
            }
            else scheduleRestEvt(entity);
        }
    }
    public void observe(Entity entity)
    {
        AICmp ai = (AICmp) CmpMapper.getComp(CmpType.AI, entity);
        ai.location=((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
        FOVCmp selfFOV = (FOVCmp) CmpMapper.getComp(CmpType.FOV, entity);
        ai.visibleEnemies.clear();
        ai.visibleFriendlies.clear();
        ai.visibleItems.clear();
        GreasedRegion goals = new GreasedRegion();
        FactionCmp.Faction myFaction = ((FactionCmp) CmpMapper.getComp(CmpType.FACTION, entity)).faction;
        LevelCmp levelCmp = (LevelCmp)CmpMapper.getComp(CmpType.LEVEL,getGame().currentLevel);

        for(Coord entPos:levelCmp.actors.positions())
        {
            if(entPos==ai.location) continue;
            Integer entID = levelCmp.actors.get(entPos);
            Entity otherEnt = getGame().getEntity(entID);
            FOVCmp otherFOV = (FOVCmp) CmpMapper.getComp(CmpType.FOV,otherEnt);
            FactionCmp.Faction otherFaction = ((FactionCmp) CmpMapper.getComp(CmpType.FACTION, getGame().getEntity(entID))).faction;

            if(selfFOV.visible.contains(entPos))
                if(otherFaction!=myFaction)
                {
                    ai.visibleEnemies.add(entID);
                    goals.add(entPos);

                } else ai.visibleFriendlies.add(entID);
        }
        for(Coord entPos:levelCmp.items.positions())
        {
            Integer entID = levelCmp.items.get(entPos);
            if(selfFOV.visible.contains(entPos))
            {
                ai.visibleItems.add(entID);
                goals.add(entPos);
            }
        }

        if(!ai.visibleEnemies.contains(ai.target))
        {
            clearTarget(entity);

        }
        if(ai.target==null &! ai.visibleEnemies.isEmpty())
        {
            Entity possibleTarget = getGame().getEntity(ai.visibleEnemies.get(0));
            if(possibleTarget!=null) setTarget(entity, possibleTarget);
        }

        List<Coord> alertsToRemove = new ArrayList<>();
        for(Coord coord : ai.alerts)
        {
            if(selfFOV.visible.contains(coord))
            {

                alertsToRemove.add(coord);
            }

        }
        for(Coord coord : alertsToRemove) ai.alerts.remove(coord);
        goals.addAll(ai.alerts);


        ai.dijkstraMap.clearGoals();
        ai.dijkstraMap.setGoals(goals);
        ai.dijkstraMap.scan();



    }
    public ArrayList<Ability> getAvailableActions(Entity entity)
    {
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, entity);
        ArrayList<Ability> availableAbilities = new ArrayList<>();
        for(Skill skill : codexCmp.prepared)
        {
            Ability abilityCmp = (Ability) CmpMapper.getAbilityComp(skill, entity);
            getGame().updateAbility(abilityCmp, entity, null);
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
            getGame().updateAbility(abilityCmp, entity, itemEntity);
            if(abilityCmp.isAvailable() && abilityCmp.getSkill().skillType != Skill.SkillType.REACTION)
            {
                availableAbilities.add(abilityCmp);
            }
        }
        return availableAbilities;
    }
    public int scheduleMoveEvt(Entity entity, Direction direction, double terrainCost)
    {
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        int gameTick = ticker.tick;
        int scheduledTick = gameTick + ((StatsCmp) CmpMapper.getComp(CmpType.STATS, entity)).getTTMove(direction, terrainCost);
        MoveEvt moveEvt = new MoveEvt(entity.hashCode(), direction, 1);
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),moveEvt);
        ticker.actionQueue.add(scheduledEvt);

        return scheduledTick;
    }
    public int scheduleActionEvt (Entity entity, Ability ability)
    {
        TargetType targetType = ability.getTargetType();

        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        int gameTick = ticker.tick;
        int scheduledTick = gameTick + ability.getTTPerform();

        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, entity);
        Integer targetID;
        if(targetType==SELF) targetID=entity.hashCode();
        else targetID = aiCmp.target;
        ArrayList<Integer> targets = new ArrayList<>();
        targets.add(targetID);
        if(ability.getTargetType()==AOE) targets.clear();

        ActionEvt actionEvt = new ActionEvt(entity.hashCode(), ability.getScrollID(), ability.getSkill(), targets, ability.getDamage(), ability.getStatusEffects());
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),actionEvt);
        ticker.actionQueue.add(scheduledEvt);

        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        ability.spawnGlyph(windowCmp.display, windowCmp.lightingHandler);
        if(ability.getTargetType()==ENEMY) rotate(entity, getGame().getEntity(targetID));
        if(ability.getTargetType()==AOE && getGame().gameState!=GameState.AIMING)
        {
            LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
            Coord location = ((PositionCmp)CmpMapper.getComp(CmpType.POSITION, entity)).coord;
            ability.apply(location, ability.getIdealLocations(entity, levelCmp).keySet().first());
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

        int scheduledTick = ticker.tick + 100;

        CampEvt campEvt = new CampEvt(entity.hashCode());
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),campEvt);
        ticker.actionQueue.add(scheduledEvt);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        NoiseMap noiseMap = (NoiseMap) CmpMapper.getComp(CmpType.NOISE_MAP, entity);
        noiseMap.noiseMap.clearSounds();

        noiseMap.noiseMap.setSound(positionCmp.coord, 15);
        noiseMap.noiseMap.scan();
        OrderedMap<Coord, Double> alerted = noiseMap.noiseMap.findAlerted(levelCmp.actors.positions(), new HashMap<>());
        alerted.remove(positionCmp.coord);
        for(Coord position : alerted.keySet())
        {
            Entity alertedEntity = getGame().getEntity(levelCmp.actors.get(position));
            StatsCmp alertedStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, alertedEntity);
            if(alerted.get(position)>=alertedStats.getSoundDetectionLvl())
            {
                Entity alertedActor = getGame().getEntity(levelCmp.actors.get(position));
                AICmp alertedAI = (AICmp) CmpMapper.getComp(CmpType.AI, alertedActor);
                alertedAI.alerts.add(positionCmp.coord);


            }
        }
        getEngine().getSystem(MakeCampSys.class).setProcessing(true);




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
        AICmp aiCmp=(AICmp) CmpMapper.getComp(CmpType.AI, actor);
        if(actor==getGame().getFocus())
        {
            ImmutableArray<Entity> focusTargetInArray = getEngine().getEntitiesFor(Family.all(FocusTargetCmp.class).get());
            if(focusTargetInArray.size()>0)
            {
                FocusTargetCmp focusTargetCmp = (FocusTargetCmp) getGame().getFocusTarget().remove(FocusTargetCmp.class);
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
        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, actor);
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
    private void rotate(Entity actor, Entity target)
    {
        if(target == null || actor == null) return;
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH,actor);
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);
        Coord targetPosition = ((PositionCmp)CmpMapper.getComp(CmpType.POSITION, target)).coord;
        positionCmp.orientation = Direction.toGoTo(positionCmp.coord,targetPosition);
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);

        if(glyphsCmp.leftGlyph!=null)
            windowCmp.display.slide(0f,glyphsCmp.leftGlyph, glyphsCmp.getLeftGlyphPositionX(windowCmp.display, positionCmp), glyphsCmp.getLeftGlyphPositionY(windowCmp.display, positionCmp), 0.18f, null);
        if(glyphsCmp.rightGlyph!=null)
            windowCmp.display.slide(0f,glyphsCmp.rightGlyph, glyphsCmp.getRightGlyphPositionX(windowCmp.display, positionCmp), glyphsCmp.getRightGlyphPositionY(windowCmp.display, positionCmp), 0.18f, null);
    }

}
