package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import EuroRogue.MyDungeonUtility;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.DamageType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.DamageEvent;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.IColoredString;
import EuroRogue.LightHandler;
import EuroRogue.MyEntitySystem;
import EuroRogue.MyFOV;
import EuroRogue.StatusEffectCmps.Bleeding;
import EuroRogue.StatusEffectCmps.Burning;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.SpatialMap;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class MovementSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public MovementSys()
    {
        super.priority = 4;
    }
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(MoveEvt.class).get());
    }
    @Override
    public void update(float deltaTime)
    {
        for (Entity entity : entities)
        {

            EuroRogue game = getGame();
            MoveEvt moveEvt = (MoveEvt) CmpMapper.getComp(CmpType.MOVE_EVT,entity);

            Entity actor = game.getEntity(moveEvt.entityID);

            moveEvt.processed=true;
            LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
            SpatialMap actorMap = levelCmp.actors;
            PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);


            if(levelCmp.doors.contains(moveEvt.destination) && levelCmp.decoDungeon[moveEvt.destination.x][moveEvt.destination.y] =='+')
            {

                LightingCmp lightingCmp = (LightingCmp)CmpMapper.getComp(CmpType.LIGHTING,getGame().currentLevel);
                openDoor(moveEvt.destination, levelCmp, lightingCmp);
                continue;
            }
            else if (levelCmp.doors.contains(moveEvt.destination) && levelCmp.decoDungeon[moveEvt.destination.x][moveEvt.destination.y] =='/'
                    && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
            {

                LightingCmp lightingCmp = (LightingCmp)CmpMapper.getComp(CmpType.LIGHTING,getGame().currentLevel);
                closeDoor(moveEvt.destination, levelCmp, lightingCmp);
                continue;
            }

            actorMap.move(positionCmp.coord,  moveEvt.destination);
            if(actorMap.get(moveEvt.destination) == null) return;
            if((Integer) actorMap.get(moveEvt.destination)!=actor.hashCode()) return;

            positionCmp.orientation = Direction.getRoughDirection(positionCmp.coord.toGoTo(moveEvt.destination).deltaX, positionCmp.coord.toGoTo(moveEvt.destination).deltaY);
            positionCmp.coord = moveEvt.destination;


            GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, actor);
            WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow);

            Bleeding bleeding = (Bleeding) CmpMapper.getStatusEffectComp(StatusEffect.BLEEDING, actor);
            if(bleeding!=null)
            {
                Entity damageEvtEntity = new Entity();
                damageEvtEntity.add(new DamageEvent(actor.hashCode(), bleeding.damagePerMove, DamageType.NONE, StatusEffect.BLEEDING));
                getEngine().addEntity(damageEvtEntity);
            }

            if(levelCmp.decoDungeon[moveEvt.destination.x][moveEvt.destination.y]==',' || levelCmp.decoDungeon[moveEvt.destination.x][moveEvt.destination.y]=='~') actor.remove(Burning.class);

            Entity eventEntity1 = new Entity();
            AnimateGlyphEvt glyphSlide = new AnimateGlyphEvt(glyphsCmp.glyph, AnimationsSys.AnimationType.SLIDE, null, positionCmp.coord, moveEvt );
            eventEntity1.add(glyphSlide);

            Entity eventEntity2 = new Entity();
            Coord lEnd = Coord.get((int)glyphsCmp.getLeftGlyphPositionX(windowCmp.display, positionCmp), (int)glyphsCmp.getLeftGlyphPositionY(windowCmp.display, positionCmp));
            AnimateGlyphEvt lGlyphSlide = new AnimateGlyphEvt(glyphsCmp.leftGlyph, AnimationsSys.AnimationType.OFFSET_SLIDE, null, lEnd, moveEvt );
            eventEntity2.add(lGlyphSlide);

            Entity eventEntity3 = new Entity();
            Coord rEnd = Coord.get((int)glyphsCmp.getRightGlyphPositionX(windowCmp.display, positionCmp), (int)glyphsCmp.getRightGlyphPositionY(windowCmp.display, positionCmp));
            AnimateGlyphEvt rGlyphSlide = new AnimateGlyphEvt(glyphsCmp.rightGlyph, AnimationsSys.AnimationType.OFFSET_SLIDE, null, rEnd, moveEvt );
            eventEntity3.add(rGlyphSlide);

            getEngine().addEntity(eventEntity1);
            getEngine().addEntity(eventEntity2);
            getEngine().addEntity(eventEntity3);
        }
    }
    private void genLogEvent(Entity entity, Coord oldPos, Coord newPos)
    {
        int tick = ((TickerCmp)CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        String name = ((NameCmp)CmpMapper.getComp(CmpType.NAME, entity)).name;
        IColoredString.Impl  string = new IColoredString.Impl();
        string.append(tick+" ", SColor.WHITE);
        string.append(name, SColor.LIGHT_YELLOW_DYE);
        string.append(" moves " + Direction.toGoTo(oldPos, newPos));
        LogEvt logEvt = new LogEvt(tick, string);
        Entity eventEntity = new Entity();
        getEngine().addEntity(eventEntity);
        eventEntity.add(logEvt);
    }

    public void openDoor(Coord door, LevelCmp levelCmp, LightingCmp lightingCmp)
    {
        if(levelCmp.doors.contains(door))
        {
            levelCmp.decoDungeon[door.x][door.y] = '/';
            levelCmp.resistance = MyFOV.generateSimpleResistances(levelCmp.decoDungeon);
            levelCmp.lineDungeon =  MyDungeonUtility.hashesToLines(levelCmp.decoDungeon);
            for(int x=0; x<levelCmp.lineDungeon.length; x++){
                for(int y=0; y<levelCmp.lineDungeon[0].length; y++){
                    if(!levelCmp.floors.contains(Coord.get(x,y))&&levelCmp.caveWalls.contains(Coord.get(x,y))) levelCmp.lineDungeon[x][y]='#';
                }
            }
            lightingCmp.resistance3x3 = MyFOV.generateSimpleResistances3x3(levelCmp.lineDungeon);

            LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;
            lightHandler.resistances = lightingCmp.resistance3x3;

            for(Integer id : levelCmp.actors.identities())
            {
                Entity actor = getGame().getEntity(id);
                AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, actor);
                aiCmp.dijkstraMap.initialize(levelCmp.bareDungeon);
                aiCmp.dijkstraMap.initializeCost(aiCmp.getTerrainCosts(levelCmp.decoDungeon));

                CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, actor);
                for(Skill skill : codexCmp.prepared)
                {
                    CmpMapper.getAbilityComp(skill, actor).setMap(levelCmp.decoDungeon);
                }
            }
        }
    }

    public void closeDoor(Coord door, LevelCmp levelCmp, LightingCmp lightingCmp)
    {
        if(levelCmp.doors.contains(door)) {
            levelCmp.decoDungeon[door.x][door.y] = '+';
            levelCmp.resistance = MyFOV.generateSimpleResistances(levelCmp.decoDungeon);
            levelCmp.lineDungeon = MyDungeonUtility.hashesToLines(levelCmp.decoDungeon);
            for (int x = 0; x < levelCmp.lineDungeon.length; x++) {
                for (int y = 0; y < levelCmp.lineDungeon[0].length; y++) {
                    if (!levelCmp.floors.contains(Coord.get(x, y)) && levelCmp.caveWalls.contains(Coord.get(x, y)))
                        levelCmp.lineDungeon[x][y] = '#';
                }
            }
            lightingCmp.resistance3x3 = MyFOV.generateSimpleResistances3x3(levelCmp.lineDungeon);

            LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;
            lightHandler.resistances = lightingCmp.resistance3x3;

            for (Integer id : levelCmp.actors.identities()) {
                Entity actor = getGame().getEntity(id);
                AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, actor);
                aiCmp.dijkstraMap.initialize(levelCmp.bareDungeon);
                aiCmp.dijkstraMap.initializeCost(aiCmp.getTerrainCosts(levelCmp.decoDungeon));

                CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, actor);
                for (Skill skill : codexCmp.prepared) {
                    CmpMapper.getAbilityComp(skill, actor).setMap(levelCmp.decoDungeon);
                }
            }
        }

    }

}