package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.DamageType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.DamageEvent;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.IColoredString;
import EuroRogue.MyEntitySystem;
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

            actorMap.move(positionCmp.coord,  moveEvt.destination);
            if(actorMap.get(moveEvt.destination) == null) return;
            if((Integer) actorMap.get(moveEvt.destination)!=actor.hashCode()) return;

            System.out.println(positionCmp.coord+" "+moveEvt.destination);
            System.out.println(positionCmp.coord.toGoTo(moveEvt.destination).deltaX+" "+positionCmp.coord.toGoTo(moveEvt.destination).deltaY+" "+"orientation "+positionCmp.orientation);

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

            float duration = 0.18f * moveEvt.animSpeed;
            windowCmp.display.slide(0f, glyphsCmp.glyph, positionCmp.coord.x, positionCmp.coord.y, duration, null);
            if(glyphsCmp.leftGlyph!=null)
                windowCmp.display.slide(0f,glyphsCmp.leftGlyph, glyphsCmp.getLeftGlyphPositionX(windowCmp.display, positionCmp), glyphsCmp.getLeftGlyphPositionY(windowCmp.display, positionCmp), duration, null);
            if(glyphsCmp.rightGlyph!=null)
                windowCmp.display.slide(0f,glyphsCmp.rightGlyph, glyphsCmp.getRightGlyphPositionX(windowCmp.display, positionCmp), glyphsCmp.getRightGlyphPositionY(windowCmp.display, positionCmp), duration, null);

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

}