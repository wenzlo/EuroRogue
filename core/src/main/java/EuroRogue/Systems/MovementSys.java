package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.DamageType;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.DamageEvent;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.IColoredString;
import EuroRogue.CmpType;

import EuroRogue.EuroRogue;
import EuroRogue.GameState;
import EuroRogue.EventComponents.MoveEvt;
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
            Coord oldPos = Coord.get(positionCmp.coord.x, positionCmp.coord.y);
            Coord newPos = Coord.get(positionCmp.coord.x+moveEvt.direction.deltaX, positionCmp.coord.y+moveEvt.direction.deltaY);
            actorMap.move(oldPos, newPos);
            if(actorMap.get(newPos) == null) return;
            if((Integer) actorMap.get(newPos)!=actor.hashCode()) return;

            positionCmp.coord = newPos;
            positionCmp.orientation = moveEvt.direction;

            GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, actor);
            WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow);

            Bleeding bleeding = (Bleeding) CmpMapper.getStatusEffectComp(StatusEffect.BLEEDING, actor);
            if(bleeding!=null)
            {
                Entity damageEvtEntity = new Entity();
                damageEvtEntity.add(new DamageEvent(actor.hashCode(), bleeding.damagePerMove, DamageType.NONE, StatusEffect.BLEEDING));
                getEngine().addEntity(damageEvtEntity);
            }
           /* for(StatusEffect statusEffect : getGame().getStatusEffects(entity))
            {
                StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, entity);
                if(statusEffectCmp.seRemovalType == SERemovalType.MOVE) entity.remove(statusEffectCmp.getClass());
            }*/

            if(levelCmp.decoDungeon[newPos.x][newPos.y]==',' || levelCmp.decoDungeon[newPos.x][newPos.y]=='~') actor.remove(Burning.class);
            /*if(levelCmp.decoDungeon[newPos.x][newPos.y]==',' || levelCmp.decoDungeon[newPos.x][newPos.y]=='~')
            {
                if(!getGame().getStatusEffects(entity).contains(StatusEffect.CHILLED))
                {
                    Entity eventEntity = new Entity();
                    StatusEffectEvt statusEffectEvt = new StatusEffectEvt(getGame().getGameTick(), StatusEffect.CHILLED, null, null, entity.hashCode(), SERemovalType.MOVE);
                    eventEntity.add(statusEffectEvt);
                    getEngine().addEntity(eventEntity);
                }
            }*/

            windowCmp.display.slide(0f, glyphsCmp.glyph, positionCmp.coord.x, positionCmp.coord.y, 0.18f, null);
            if(glyphsCmp.leftGlyph!=null)
                windowCmp.display.slide(0f,glyphsCmp.leftGlyph, glyphsCmp.getLeftGlyphPositionX(windowCmp.display, positionCmp), glyphsCmp.getLeftGlyphPositionY(windowCmp.display, positionCmp), 0.18f, null);
            if(glyphsCmp.rightGlyph!=null)
                windowCmp.display.slide(0f,glyphsCmp.rightGlyph, glyphsCmp.getRightGlyphPositionX(windowCmp.display, positionCmp), glyphsCmp.getRightGlyphPositionY(windowCmp.display, positionCmp), 0.18f, null);

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