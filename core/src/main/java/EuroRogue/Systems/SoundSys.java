package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.HashMap;

import EuroRogue.AbilityCmpSubSystems.IAbilityCmpSubSys;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.SoundMapCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.MyEntitySystem;
import EuroRogue.TerrainType;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class SoundSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public SoundSys()
    {
        super.priority = 6;
    }
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(MoveEvt.class, ActionEvt.class).get());
    }
    @Override
    public void update(float deltaTime)
    {

        for (Entity entity : entities)
        {
            MoveEvt moveEvt = (MoveEvt) CmpMapper.getComp(CmpType.MOVE_EVT, entity);
            ActionEvt actionEvt = (ActionEvt) CmpMapper.getComp(CmpType.ACTION_EVT, entity);
            if(moveEvt!=null) processMoveEvt(moveEvt);
            if(actionEvt!=null) processActionEvt(actionEvt);
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
        SoundMapCmp soundMapCmp = (SoundMapCmp) CmpMapper.getComp(CmpType.SOUND_MAP, actor);
        soundMapCmp.soundMap.clearSounds();
        double noiseLvl = statsCmp.getMoveSndLvl()*terrainType.noiseMult;

        soundMapCmp.soundMap.setSound(positionCmp.coord, statsCmp.getMoveSndLvl()*terrainType.noiseMult);
        soundMapCmp.soundMap.scan();
        OrderedMap<Coord, Double> alerted = soundMapCmp.soundMap.findAlerted(levelCmp.actors.positions(), new HashMap<>());
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

        IAbilityCmpSubSys abilityCmp = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(actionEvt.skill, performerEntity);
        if(actionEvt.scrollID!=null)
        {
            Entity scrollEntity = getGame().getEntity(actionEvt.scrollID);
            abilityCmp = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(actionEvt.skill, scrollEntity);
            getEngine().removeEntity(scrollEntity);
        }
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performerEntity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        SoundMapCmp soundMapCmp = (SoundMapCmp) CmpMapper.getComp(CmpType.SOUND_MAP, performerEntity);soundMapCmp.soundMap.clearSounds();
        double noiseLvl = 0;
        try {
            noiseLvl = abilityCmp.getNoiseLvl(performerEntity);
        } catch (Exception e) {


            e.printStackTrace();
        }

        soundMapCmp.soundMap.setSound(positionCmp.coord, noiseLvl);
        soundMapCmp.soundMap.scan();
        OrderedMap<Coord, Double> alerted = soundMapCmp.soundMap.findAlerted(levelCmp.actors.positions(), new HashMap<>());
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