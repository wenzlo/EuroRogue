package EuroRogue.Systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Collections;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.MobType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.AI.AIType;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
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
import squidpony.squidmath.GreasedRegion;

public class DeathSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public DeathSys() {
        super.priority = 7;
    }


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
            int currentTime = (int) System.currentTimeMillis();
            System.out.println(currentTime);
            System.out.println(deathEvt.tod);
            System.out.println(currentTime-deathEvt.tod );
            if((currentTime-deathEvt.tod) > deathEvt.delay)
                deathEvt.setProcessed(true);

            else continue;


            if(getGame().getFocus()==entity)
            {
                Entity eventEntity = new Entity();
                entity.add(new GameStateEvt(GameState.GAME_OVER));
                getEngine().addEntity(eventEntity);
            }
            else kill(entity);
        }
    }

    public void kill(Entity entity)
    {
        DeathEvt deathEvt = (DeathEvt) CmpMapper.getComp(CmpType.DEATH_EVT, entity);
        deathEvt.setProcessed(true);
        EuroRogue game = getGame();
        interupt( entity);


        StatsCmp playerStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, game.player);
        AICmp playerAI = CmpMapper.getAIComp(AIType.DEFAULT_AI, game.player);
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

        dropLocations.removeAll(new GreasedRegion(levelCmp.decoDungeon, '~'));
        dropLocations.removeAll(new GreasedRegion(levelCmp.decoDungeon, '§'));
        dropLocations.removeAll(levelCmp.doors);

        Collections.sort(dropLocations, new SortByDistance(actorPosition));

        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
        if(statsCmp.mobType==MobType.DEFAULT)
        {
            boolean scrollDropped = false;
            for(Skill skill : entityCodexCmp.known)
            {
                if(!playerCodexCmp.known.contains(skill) && Skill.qualify(skill, playerStats, playerCodexCmp))
                {
                    for(Coord pos : dropLocations)
                    {
                        if(!levelCmp.items.positions().contains(pos) && levelCmp.floors.contains(pos))
                        {
                            Entity scrollItem = getGame().generateScroll(pos, skill, levelCmp);
                            getEngine().addEntity(scrollItem);
                            scrollDropped=true;

                            dropLocations.remove(pos);
                            break;
                        }
                    }
                    break;
                }
            }
            for(Skill skill : entityCodexCmp.known)
            {
                if(!scrollDropped && skill!=Skill.MELEE_ATTACK)
                {
                    for(Coord pos : dropLocations)
                    {
                        if(!levelCmp.items.positions().contains(pos) && levelCmp.floors.contains(pos))
                        {
                            Entity scrollItem = getGame().generateScroll(pos, skill, levelCmp);
                            getEngine().addEntity(scrollItem);
                            dropLocations.remove(pos);
                            break;
                        }
                    }
                    break;
                }
            }

            for(Coord pos : dropLocations)
            {

                if(!levelCmp.items.positions().contains(pos) && levelCmp.floors.contains(pos) &! manaPoolCmp.attuned.isEmpty())
                {
                    getEngine().addEntity(getGame().generateManaITem(pos, manaPoolCmp.attuned.get(0)));

                    dropLocations.remove(pos);
                    break;
                }
            }
        }

        removeLights(entity);

        if(statsCmp.mobType == MobType.DEFAULT)
            dropItems(entity, dropLocations);
        removeParticleEffects(entity, display);
        removeGlyphs(entity, display);
        game.currentLevel.getComponent(LevelCmp.class).actors.remove(entity.hashCode());
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
        Coord entityPos = ((PositionCmp)CmpMapper.getComp(CmpType.POSITION, entity)).coord;
        ArrayList<Integer> itemsToDrop = new ArrayList<>();
        itemsToDrop.addAll(inventoryCmp.getItemIDs());
        itemsToDrop.addAll(inventoryCmp.getEquippedIDs());

        for(Integer itemID : itemsToDrop)
        {
            Entity itemEntity = getGame().getEntity(itemID);

            if(itemEntity!=null)
            {
                NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME,itemEntity);
                ItemCmp itemCmp = (ItemCmp)CmpMapper.getComp(CmpType.ITEM, itemEntity);
                EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, itemEntity);
                if(equipmentCmp!=null)
                    equipmentCmp.equipped = false;
                Collections.sort(dropLocations, new SortByDistance(entityPos));

                for(Coord pos : dropLocations)
                {
                    if(!levelCmp.items.positions().contains(pos) && levelCmp.floors.contains(pos))
                    {
                        itemCmp.ownerID = null;
                        itemEntity.remove(PositionCmp.class);
                        itemEntity.add(new PositionCmp(pos));

                        dropLocations.remove(pos);

                        break;
                    }
                }
            }
        }
    }

    private void removeLights(Entity entity)
    {
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        GlyphsCmp glyphsCmp = (GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, entity);

        windowCmp.lightingHandler.removeLight(windowCmp.lightingHandler.getLightByGlyph(glyphsCmp.glyph).hashCode());
        windowCmp.lightingHandler.removeLight(windowCmp.lightingHandler.getLightByGlyph(glyphsCmp.leftGlyph).hashCode());
        windowCmp.lightingHandler.removeLight(windowCmp.lightingHandler.getLightByGlyph(glyphsCmp.rightGlyph).hashCode());
        CodexCmp codexCmp = (CodexCmp)CmpMapper.getComp(CmpType.CODEX, entity);
        for(Skill skill : codexCmp.prepared)
        {

            Ability abilityComp = CmpMapper.getAbilityComp(skill, entity);
            if(abilityComp!=null)
            {
                TextCellFactory.Glyph  glyph = abilityComp.getGlyph();
                if(glyph!=null) windowCmp.lightingHandler.removeLightByGlyph(abilityComp.getGlyph());
            }
        }
        Entity focus = getGame().getFocus();
        codexCmp = (CodexCmp)CmpMapper.getComp(CmpType.CODEX, focus);

        for(Skill skill : codexCmp.prepared)
        {

            Ability abilityComp = CmpMapper.getAbilityComp(skill, focus);

            if(abilityComp!=null)
            {

                TextCellFactory.Glyph  glyph = abilityComp.glyph;

                if(glyph!=null)
                {

                    windowCmp.lightingHandler.removeLightByGlyph(abilityComp.getGlyph());
                }
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
            Ability Ability = (Ability) CmpMapper.getAbilityComp(skill, entity);
            if(Ability !=null)
            {
                TextCellFactory.Glyph  glyph = Ability.getGlyph();
                if(glyph!=null) display.removeGlyph(Ability.getGlyph());
            }
        }
    }
    private void removeParticleEffects(Entity entity, MySparseLayers display)
    {
        ParticleEffectsCmp peCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
        ArrayList<TextCellFactory.Glyph> glyphs = new ArrayList<>();
        glyphs.addAll(peCmp.particleEffectsMap.keySet());
        ArrayList<ParticleEffectsCmp.ParticleEffect> effects = new ArrayList<>();
        for(TextCellFactory.Glyph glyph : glyphs) {
            for(ParticleEffectsCmp.ParticleEffect effect : peCmp.particleEffectsMap.get(glyph).keySet())
            {
               effects.add(effect);
            }

            for (ParticleEffectsCmp.ParticleEffect effect : effects)
            {
                peCmp.removeEffect(glyph, effect, display);
            }

        }
        Entity focus = getGame().getFocus();
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX,focus);
        peCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, focus);
        for(Skill skill : codexCmp.prepared)
        {
            Ability ability = CmpMapper.getAbilityComp(skill, focus);

            if(ability.getGlyph()!=null)
            {

                peCmp.removeEffectsByGlyph(ability.getGlyph(), display);
            }
        }

    }

}
