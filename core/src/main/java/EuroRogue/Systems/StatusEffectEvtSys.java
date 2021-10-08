package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.IColoredString;
import EuroRogue.MyEntitySystem;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.StatusEffectCmp;
import squidpony.squidgrid.gui.gdx.SColor;


public class StatusEffectEvtSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public StatusEffectEvtSys(){super.priority = 5;}

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(StatusEffectEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        {
            TickerCmp tickerCmp = ((TickerCmp)CmpMapper.getComp(CmpType.TICKER, getGame().ticker));
            for(Entity entity:entities)
            {
                StatusEffectEvt statusEffectEvt = (StatusEffectEvt) CmpMapper.getComp(CmpType.STATUS_EFFECT_EVT, entity);

                if(statusEffectEvt.isProcessed()) return;
                statusEffectEvt.processed = true;
                Entity targetEntity = getGame().getEntity(statusEffectEvt.targetID);
                if(targetEntity==null) return;

                ArrayList<StatusEffect> currentEffects = getGame().getStatusEffects(targetEntity);
                StatusEffect nextIntensity = StatusEffect.getNextIntensity(statusEffectEvt.effect);
                if(currentEffects.contains(nextIntensity)) return;

                boolean cancelFlag = false;
                if(currentEffects.contains(statusEffectEvt.effect) && nextIntensity!=null)
                {
                    statusEffectEvt.effect = nextIntensity;

                } else if(currentEffects.contains(statusEffectEvt.effect) && nextIntensity==null){

                    StatusEffectCmp statusEffectCmp = (StatusEffectCmp)CmpMapper.getStatusEffectComp(statusEffectEvt.effect, targetEntity);
                    statusEffectCmp.lastTick = statusEffectEvt.tick+statusEffectEvt.duration;
                    return;

                }


                for (Class cls : statusEffectEvt.effect.cancels)
                {
                    StatusEffectCmp statusEffectCmp = (StatusEffectCmp) targetEntity.getComponent(cls);
                    if(statusEffectCmp != null)
                    {
                        statusEffectCmp.lastTick = tickerCmp.tick-1;
                        cancelFlag = true;
                    }
                }

                if(cancelFlag) continue;


                for(Class cls:statusEffectEvt.effect.removes)
                {
                    StatusEffectCmp statusEffectCmp = (StatusEffectCmp) targetEntity.getComponent(cls);
                    if(statusEffectCmp != null) statusEffectCmp.lastTick = 0;

                }
                StatsCmp targetStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS,targetEntity);
                targetEntity.add(StatusEffect.newStatusEffectCmp(statusEffectEvt, targetStats));
                ((LogCmp) CmpMapper.getComp(CmpType.LOG, getGame().logWindow)).logEntries.add(generateSEffectLogEvt(statusEffectEvt).entry);


                //if(statusEffectEvt.effect==StatusEffect.FROZEN)clearActionQueue(targetEntity);

            }
        }
    }

    public void clearActionQueue(Entity entity)
    {
        TickerCmp tickerCmp = (TickerCmp)CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        tickerCmp.statusEffectEvtQueue.removeAll(tickerCmp.getScheduledActions(entity));
    }

    private LogEvt generateSEffectLogEvt (StatusEffectEvt statusEffectEvt)
    {
        //Entity performerEntity = getGame().getEntity(statusEffectEvt.performerID);
        Entity targetEntity = getGame().getEntity(statusEffectEvt.targetID);
        SColor targetColor = ((CharCmp)CmpMapper.getComp(CmpType.CHAR, targetEntity)).color;
        IColoredString.Impl<SColor> coloredEvtText = new IColoredString.Impl<>();
        Integer tick = ((TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker)).tick;
        coloredEvtText.append(tick.toString(), SColor.WHITE);
        String targetName = ((NameCmp) CmpMapper.getComp(CmpType.NAME, targetEntity)).name;
        coloredEvtText.append(" "+targetName+": ", targetColor);
        coloredEvtText.append(statusEffectEvt.effect.name+" ", SColor.WHITE);
        if(statusEffectEvt.duration != null)
            coloredEvtText.append(statusEffectEvt.duration.toString(), SColor.WHITE);


        return new LogEvt(tick, coloredEvtText);
    }
}
