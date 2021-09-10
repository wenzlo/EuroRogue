package EuroRogue.Systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Collections;

import EuroRogue.AbilityCmpSubSystems.IAbilityCmpSubSys;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.DeathEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.GameState;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.SortByDistance;
import squidpony.squidai.BlastAOE;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class DeathSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public DeathSys() {}


    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(DeathEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        for(Entity entity : entities)
        {
            DeathEvt deathEvt = (DeathEvt) CmpMapper.getComp(CmpType.DEATH_EVT, entity);
            deathEvt.setProcessed(true);
            if(getGame().getFocus()==entity)
            {
                Entity eventEntity = new Entity();
                entity.add(new GameStateEvt(GameState.GAME_OVER));
                getEngine().addEntity(eventEntity);
            }else kill(entity);
        }
    }

    public void kill(Entity entity)
    {
        DeathEvt deathEvt = (DeathEvt) CmpMapper.getComp(CmpType.DEATH_EVT, entity);
        deathEvt.setProcessed(true);
        EuroRogue game = getGame();
        interupt( entity);
        game.currentLevel.getComponent(LevelCmp.class).actors.remove(entity.hashCode());

        AICmp playerAI = ((AICmp) CmpMapper.getComp(CmpType.AI, game.player));
        MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW,getGame().dungeonWindow)).display;
        if(playerAI.target!=null)
        {
            if(playerAI.target.equals(entity.hashCode()))
            {
                playerAI.target = null;
                FocusTargetCmp ftc = (FocusTargetCmp) CmpMapper.getComp(CmpType.FOCUS_TARGET, entity);
                if(ftc!=null) display.glyphs.remove(ftc.indicatorGlyph2);
            }
        }
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, entity);

        Collections.shuffle(manaPoolCmp.attuned);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);



        CodexCmp playerCodexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, getGame().getFocus());
        CodexCmp entityCodexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, entity);

        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        Coord actorPosition = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
        BlastAOE aoe = new BlastAOE(actorPosition, 2, Radius.CIRCLE);
        aoe.setMap(levelCmp.bareDungeon);
        ArrayList<Coord> dropLocations = new ArrayList<>();
        dropLocations.addAll(aoe.findArea().keySet());
        Collections.sort(dropLocations, new SortByDistance(actorPosition));



        for(Skill skill : entityCodexCmp.known)
        {
            if(!playerCodexCmp.known.contains(skill))
            {



                for(Coord pos : dropLocations)
                {
                    if(!levelCmp.items.positions().contains(pos) && levelCmp.floors.contains(pos))
                    {
                        Entity scrollItem = getGame().generateScroll(pos, skill);
                        getEngine().addEntity(scrollItem);

                        dropLocations.remove(pos);

                        System.out.println("Droping scroll");
                        break;
                    }

                }
                break;

            }
        }

        for(Coord pos : dropLocations)
        {

            if(!levelCmp.items.positions().contains(pos) && levelCmp.floors.contains(pos))
            {
                getEngine().addEntity(getGame().generateManaITem(pos, manaPoolCmp.attuned.get(0)));

                dropLocations.remove(pos);
                break;
            }
        }

        dropItems(entity, dropLocations);
        removelights(entity);
        removeGlyphs(entity, display);
        getEngine().removeEntity(entity);

    }

    private void interupt(Entity entity)
    {
        EuroRogue game = getGame();
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER,game.ticker);
        game.ticker.getComponent(TickerCmp.class).actionQueue.removeAll(ticker.getScheduledActions(entity));

        for(Component component:entity.getComponents())
        {
            if(component.getClass()== MoveEvt.class )
            {
                entity.remove(component.getClass());
            }
            ImmutableArray<Entity> EvtEntities = getEngine().getEntitiesFor(Family.one(ActionEvt.class).get());
            for(Entity eventEnt:EvtEntities)
            {
                ActionEvt actionEvt = (ActionEvt) CmpMapper.getComp(CmpType.ACTION_EVT, eventEnt);
                if(actionEvt.performerID ==entity.hashCode()) getEngine().removeEntity(eventEnt);
            }
        }
    }

    private void dropItems(Entity entity, ArrayList<Coord> dropLocations)
    {
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        ArrayList<Integer> itemsToDrop = new ArrayList<>();
        itemsToDrop.addAll(inventoryCmp.getItemIDs());
        itemsToDrop.addAll(inventoryCmp.getEquippedIDs());

        for(Integer itemID : itemsToDrop)
        {
            Entity itemEntity = getGame().getEntity(itemID);

            if(itemEntity!=null)
            {
                ItemCmp itemCmp = (ItemCmp)CmpMapper.getComp(CmpType.ITEM, itemEntity);
                EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, itemEntity);
                if(equipmentCmp!=null)
                    equipmentCmp.equipped = false;
                Coord location;
                for(Coord pos : dropLocations)
                {
                    if(!levelCmp.items.positions().contains(pos) && levelCmp.floors.contains(pos))
                    {
                        location = Coord.get(pos.x, pos.y);
                        itemCmp.ownerID = null;

                        itemEntity.add(new PositionCmp(location));
                        dropLocations.remove(location);

                        break;
                    }
                }
            }
        }
    }

    private void removelights(Entity entity)
    {
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        windowCmp.lightingHandler.removeLight(windowCmp.lightingHandler.getLightByGlyph(entity.getComponent(GlyphsCmp.class).glyph).hashCode());
        CodexCmp codexCmp = (CodexCmp)CmpMapper.getComp(CmpType.CODEX, entity);
        for(Skill skill : codexCmp.prepared)
        {
            IAbilityCmpSubSys iAbilityCmpSubSys = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(skill, entity);
            if(iAbilityCmpSubSys!=null)
            {
                TextCellFactory.Glyph  glyph = iAbilityCmpSubSys.getGlyph();
                if(glyph!=null) windowCmp.lightingHandler.removeLightByGlyph(iAbilityCmpSubSys.getGlyph());
            }

        }
    }
    private void removeGlyphs(Entity entity, MySparseLayers display)
    {
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH,entity);
        display.removeGlyph(glyphsCmp.glyph);
        display.removeGlyph(glyphsCmp.rightGlyph);
        display.removeGlyph(glyphsCmp.leftGlyph);
        CodexCmp codexCmp = (CodexCmp)CmpMapper.getComp(CmpType.CODEX, entity);
        for(Skill skill : codexCmp.prepared)
        {
            IAbilityCmpSubSys iAbilityCmpSubSys = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(skill, entity);
            if(iAbilityCmpSubSys!=null)
            {
                TextCellFactory.Glyph  glyph = iAbilityCmpSubSys.getGlyph();
                if(glyph!=null) display.removeGlyph(iAbilityCmpSubSys.getGlyph());
            }

        }
    }


}
