package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.Arrays;
import java.util.HashMap;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.FactionCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.NoiseMapCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.GameState;
import EuroRogue.ItemEvtType;
import EuroRogue.MyEntitySystem;
import EuroRogue.TerrainType;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class NoiseSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public NoiseSys()
    {
        super.priority = 6;
    }
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(MoveEvt.class, ActionEvt.class, ItemEvt.class).get());
    }
    @Override
    public void update(float deltaTime)
    {
        if(getGame().gameState!= GameState.PLAYING) return;

        for (Entity entity : entities)
        {
            MoveEvt moveEvt = (MoveEvt) CmpMapper.getComp(CmpType.MOVE_EVT, entity);
            ActionEvt actionEvt = (ActionEvt) CmpMapper.getComp(CmpType.ACTION_EVT, entity);
            ItemEvt itemEvt = (ItemEvt)CmpMapper.getComp(CmpType.ITEM_EVT, entity);
            if(moveEvt!=null) processMoveEvt(moveEvt);
            if(actionEvt!=null) processActionEvt(actionEvt);
            if(itemEvt!=null) processItemEvt(itemEvt);
        }
    }
    private void processMoveEvt(MoveEvt moveEvt)
    {
        if(!moveEvt.processed) return;
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        Entity actor = getGame().getEntity(moveEvt.entityID);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, actor);
        FactionCmp factionCmp = (FactionCmp) CmpMapper.getComp(CmpType.FACTION, actor);



        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);
        TerrainType terrainType = TerrainType.getTerrainTypeFromChar(levelCmp.decoDungeon[positionCmp.coord.x][positionCmp.coord.y]);
        NoiseMapCmp noiseMapCmp = (NoiseMapCmp) CmpMapper.getComp(CmpType.NOISE_MAP, actor);
        noiseMapCmp.noiseMap.clearSounds();

        noiseMapCmp.noiseMap.setSound(positionCmp.coord, statsCmp.getMoveSndLvl()*terrainType.noiseMult);
        noiseMapCmp.noiseMap.scan();
        OrderedMap<Coord, Double> alerted = noiseMapCmp.noiseMap.findAlerted(levelCmp.actors.positions(), new HashMap<>());
        alerted.remove(positionCmp.coord);
        for(Coord position : alerted.keySet())
        {
            Entity alertedEntity = getGame().getEntity(levelCmp.actors.get(position));
            StatsCmp alertedStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, alertedEntity);
            FactionCmp entityFactionCmp = (FactionCmp) CmpMapper.getComp(CmpType.FACTION, alertedEntity);
            if(alerted.get(position)>=alertedStats.getSoundDetectionLvl() && entityFactionCmp.faction!=factionCmp.faction)
            {
                Entity alertedActor = getGame().getEntity(levelCmp.actors.get(position));
                AICmp alertedAI = (AICmp) CmpMapper.getComp(CmpType.AI, alertedActor);
                alertedAI.alerts.put(actor.hashCode(), positionCmp.coord);
            }
        }
    }

    private void processItemEvt(ItemEvt itemEvent)
    {
        if(!Arrays.asList(new ItemEvtType[]{ItemEvtType.DROP, ItemEvtType.EQUIP, ItemEvtType.UNEQUIP}).contains(itemEvent.type)) return;
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        Entity actor = getGame().getEntity(itemEvent.actorID);

        FactionCmp factionCmp = (FactionCmp) CmpMapper.getComp(CmpType.FACTION, actor);

        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, actor);

        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);
        NoiseMapCmp noiseMapCmp = (NoiseMapCmp) CmpMapper.getComp(CmpType.NOISE_MAP, actor);
        noiseMapCmp.noiseMap.clearSounds();
        noiseMapCmp.noiseMap.setSound(positionCmp.coord, statsCmp.getMoveSndLvl());
        noiseMapCmp.noiseMap.scan();
        OrderedMap<Coord, Double> alerted = noiseMapCmp.noiseMap.findAlerted(levelCmp.actors.positions(), new HashMap<>());
        alerted.remove(positionCmp.coord);
        for(Coord position : alerted.keySet())
        {
            Entity alertedEntity = getGame().getEntity(levelCmp.actors.get(position));
            StatsCmp alertedStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, alertedEntity);
            FactionCmp entityFactionCmp = (FactionCmp) CmpMapper.getComp(CmpType.FACTION, alertedEntity);
            if(alerted.get(position)>=alertedStats.getSoundDetectionLvl() && entityFactionCmp.faction!=factionCmp.faction)
            {
                Entity alertedActor = getGame().getEntity(levelCmp.actors.get(position));
                AICmp alertedAI = (AICmp) CmpMapper.getComp(CmpType.AI, alertedActor);
                alertedAI.alerts.put(actor.hashCode(), positionCmp.coord);
            }
        }

    }

    private void processActionEvt(ActionEvt actionEvt)
    {
        Entity performerEntity = getGame().getEntity(actionEvt.performerID);
        FactionCmp factionCmp = (FactionCmp) CmpMapper.getComp(CmpType.FACTION, performerEntity);

        if(!actionEvt.isProcessed()) return;

        Ability abilityCmp = CmpMapper.getAbilityComp(actionEvt.skill, performerEntity);
        if(actionEvt.scrollID!=null)
        {
            Entity scrollEntity = getGame().getEntity(actionEvt.scrollID);
            abilityCmp = CmpMapper.getAbilityComp(actionEvt.skill, scrollEntity);
            getEngine().removeEntity(scrollEntity);
        }
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performerEntity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        NoiseMapCmp noiseMapCmp = (NoiseMapCmp) CmpMapper.getComp(CmpType.NOISE_MAP, performerEntity);
        noiseMapCmp.noiseMap.clearSounds();
        double noiseLvl = 0;
        try {
            noiseLvl = abilityCmp.getNoiseLvl(performerEntity);
        } catch (Exception e) {


            e.printStackTrace();
        }

        noiseMapCmp.noiseMap.setSound(positionCmp.coord, noiseLvl);
        noiseMapCmp.noiseMap.scan();
        OrderedMap<Coord, Double> alerted = noiseMapCmp.noiseMap.findAlerted(levelCmp.actors.positions(), new HashMap<>());
        alerted.remove(positionCmp.coord);
        for(Coord position : alerted.keySet())
        {
            Entity alertedEntity = getGame().getEntity(levelCmp.actors.get(position));
            StatsCmp alertedStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, alertedEntity);
            FactionCmp entityFactionCmp = (FactionCmp) CmpMapper.getComp(CmpType.FACTION, alertedEntity);
            if(alerted.get(position)>=alertedStats.getSoundDetectionLvl() && entityFactionCmp.faction!=factionCmp.faction)
            {
                Entity alertedActor = getGame().getEntity(levelCmp.actors.get(position));
                AICmp alertedAI = (AICmp) CmpMapper.getComp(CmpType.AI, alertedActor);
                alertedAI.alerts.put(performerEntity.hashCode(), positionCmp.coord);


            }
        }

    }





}