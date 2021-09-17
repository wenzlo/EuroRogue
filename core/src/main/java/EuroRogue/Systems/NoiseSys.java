package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.Arrays;
import java.util.HashMap;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.NoiseMap;
import EuroRogue.Components.StatsCmp;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.MoveEvt;
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


        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);
        TerrainType terrainType = TerrainType.getTerrainTypeFromChar(levelCmp.decoDungeon[positionCmp.coord.x][positionCmp.coord.y]);
        NoiseMap noiseMap = (NoiseMap) CmpMapper.getComp(CmpType.NOISE_MAP, actor);
        noiseMap.noiseMap.clearSounds();

        noiseMap.noiseMap.setSound(positionCmp.coord, statsCmp.getMoveSndLvl()*terrainType.noiseMult);
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
    }

    private void processItemEvt(ItemEvt itemEvent)
    {
        if(!Arrays.asList(new ItemEvtType[]{ItemEvtType.DROP, ItemEvtType.EQUIP, ItemEvtType.UNEQUIP}).contains(itemEvent.type)) return;
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        Entity actor = getGame().getEntity(itemEvent.actorID);

        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, actor);

        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);
        NoiseMap noiseMap = (NoiseMap) CmpMapper.getComp(CmpType.NOISE_MAP, actor);
        noiseMap.noiseMap.clearSounds();
        noiseMap.noiseMap.setSound(positionCmp.coord, statsCmp.getMoveSndLvl());
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

    }

    private void processActionEvt(ActionEvt actionEvt)
    {
        Entity performerEntity = getGame().getEntity(actionEvt.performerID);

        if(!actionEvt.isProcessed()) return;

        Ability abilityCmp = (Ability) CmpMapper.getAbilityComp(actionEvt.skill, performerEntity);
        if(actionEvt.scrollID!=null)
        {
            Entity scrollEntity = getGame().getEntity(actionEvt.scrollID);
            abilityCmp = (Ability) CmpMapper.getAbilityComp(actionEvt.skill, scrollEntity);
            getEngine().removeEntity(scrollEntity);
        }
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performerEntity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        NoiseMap noiseMap = (NoiseMap) CmpMapper.getComp(CmpType.NOISE_MAP, performerEntity);
        noiseMap.noiseMap.clearSounds();
        double noiseLvl = 0;
        try {
            noiseLvl = abilityCmp.getNoiseLvl(performerEntity);
        } catch (Exception e) {


            e.printStackTrace();
        }

        noiseMap.noiseMap.setSound(positionCmp.coord, noiseLvl);
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

    }





}