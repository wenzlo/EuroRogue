package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.FrozenEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.RestEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.GameState;
import EuroRogue.IColoredString;
import EuroRogue.ItemEvtType;
import EuroRogue.MyEntitySystem;
import EuroRogue.School;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.StatusEffectCmp;
import squidpony.squidgrid.gui.gdx.SColor;


public class RestIdleCampSys extends MyEntitySystem
{
    private ImmutableArray<Entity> restingEnts;
    private ImmutableArray<Entity> frozenEnts;
    private ImmutableArray<Entity> campingEnts;


    //public RestSys(){}

    public RestIdleCampSys()
    {
        super.priority = 3;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        restingEnts = engine.getEntitiesFor(Family.all(RestEvt.class).get());
        frozenEnts = engine.getEntitiesFor(Family.all(FrozenEvt.class).get());
        campingEnts = engine.getEntitiesFor(Family.all(CampEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        if(getGame().gameState!=GameState.PLAYING)return;
        for(Entity entity: restingEnts)
        {
            RestEvt restEvt = (RestEvt) CmpMapper.getComp(CmpType.REST_EVT, entity);
            Entity actor = getGame().getEntity(restEvt.actorID);
            restEvt.processed=true;
            if(actor==null) continue;
            if(CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, actor)!=null) return;

            ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, actor);
            manaPoolCmp.recoverMana();

            activateAbilities( actor);
            for(StatusEffect statusEffect : getGame().getStatusEffects(actor))
            {
                StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, actor);

                if(statusEffectCmp.seRemovalType == SERemovalType.SHORT_REST)
                    actor.remove(statusEffect.cls);

            }
            StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, getGame().getFocus());
            if((CmpMapper.getAIComp(statsCmp.mobType.aiType, getGame().getFocus())).visibleEnemies.contains(actor.hashCode()))
            {
                ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(genLogEvent(actor).entry);
            }
        }
        for(Entity entity: frozenEnts)
        {
            FrozenEvt frozenEvt = (FrozenEvt) CmpMapper.getComp(CmpType.FROZEN_EVT, entity);
            frozenEvt.processed = true;
        }
        for(Entity entity: campingEnts)
        {
            CampEvt campEvt = (CampEvt) CmpMapper.getComp(CmpType.CAMP_EVT, entity);

            Entity actorEntity = getGame().getEntity(campEvt.actorID);
            ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, actorEntity);
            List<School> cost = new ArrayList<>(manaPoolCmp.active);
            manaPoolCmp.spendMana(cost);

            Entity statusEffectEntity = new Entity();
            StatusEffectEvt statusEffectEvt = new StatusEffectEvt(getGame().getGameTick(), null, StatusEffect.HUNGRY, null, actorEntity.hashCode(), actorEntity.hashCode(), SERemovalType.OTHER );
            statusEffectEntity.add(statusEffectEvt);
            getEngine().addEntity(statusEffectEntity);

            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, actorEntity);
            for(Integer equipmentID : inventoryCmp.getEquippedIDs())
            {
                Entity eventEntity = new Entity();
                ItemEvt itemEvt = new ItemEvt(equipmentID, actorEntity.hashCode(), ItemEvtType.UNEQUIP);
                eventEntity.add(itemEvt);
                getEngine().addEntity(eventEntity);
            }

            Entity eventEntity = new Entity();


            if(CmpMapper.getStatusEffectComp(StatusEffect.STARVING, actorEntity)==null)
            {
                StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, actorEntity);
                statsCmp.rl = statsCmp.getMaxRestLvl();
            }
            for(StatusEffect statusEffect : StatusEffect.values())
            {
                StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, actorEntity);
                if(statusEffectCmp!=null)
                {
                    if(statusEffectCmp.seRemovalType == SERemovalType.LONG_REST) entity.remove(statusEffect.cls);
                }
            }


            campEvt.processed = true;

            if(entity == getGame().getFocus())
            {
                GameStateEvt gameStateEvt = new GameStateEvt(GameState.CAMPING);
                eventEntity.add(gameStateEvt);
                getEngine().addEntity(eventEntity);
            }


        }
    }
    private void activateAbilities(Entity entity)
    {
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX,entity);
        for(Skill skill : codexCmp.prepared)
        {
            Ability abilityCmpSubSys = CmpMapper.getAbilityComp(skill,entity);
            abilityCmpSubSys.activate();
        }

    }
    public LogEvt genLogEvent(Entity entity)
    {
        String name = ((NameCmp)CmpMapper.getComp(CmpType.NAME, entity)).name;
        SColor nameColor = ((CharCmp)CmpMapper.getComp(CmpType.CHAR, entity)).color;
        Integer tick = ((TickerCmp)CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        LogCmp log = (LogCmp)CmpMapper.getComp(CmpType.LOG, getGame().logWindow);

        IColoredString.Impl string = new IColoredString.Impl();
        string.append(tick.toString(), SColor.WHITE);
        string.append(" "+name, nameColor);
        string.append(" looks Rested", SColor.WHITE);
        return new LogEvt(tick, string);
    }


}
