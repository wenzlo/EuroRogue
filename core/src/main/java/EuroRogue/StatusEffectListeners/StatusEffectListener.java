package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import java.util.ArrayList;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.LightCmpTemp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.EuroRogue;
import EuroRogue.ScheduledEvt;
import EuroRogue.StatType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.StatusEffectCmp;

public class StatusEffectListener implements EntityListener
{
    StatusEffect effect;
    EuroRogue game;

    public StatusEffectListener(EuroRogue game){
        this.game=game;
    }

    @Override
    public void entityAdded(Entity entity)
    {

        ArrayList<StatusEffect> currentStatusEffects = getStatusEffects(entity);
        setStatMultipliers(entity, currentStatusEffects);

    }

    @Override
    public void entityRemoved(Entity entity)
    {

        ArrayList<StatusEffect> currentStatusEffects = getStatusEffects(entity);
        currentStatusEffects.remove(effect);
        setStatMultipliers(entity, currentStatusEffects);

    }

    private void setStatMultipliers(Entity entity, ArrayList<StatusEffect> currentStatusEffects)
    {


        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);

        for (StatType stat : StatType.values() )
        {
            float multiplier = 1;
            List<StatusEffect> statusEffects= new ArrayList<>();
            for (StatusEffect statusEffect : currentStatusEffects) {
                StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, entity);
                if(statusEffectCmp ==null) continue;
                ArrayList<StatType> affectedStatsOther = statusEffectCmp.getAffectedStats();
                if (affectedStatsOther.contains(stat))
                {
                    float otherMultiplier = statusEffectCmp.getStatMultiplier(stat);
                    multiplier = multiplier + (otherMultiplier - 1);
                    statusEffects.add(statusEffect);
                }
            }
            statsCmp.setStatMultiplier(stat, multiplier);

        }
    }
    private  ArrayList<StatusEffect> getStatusEffects(Entity entity)
    {
        ArrayList<StatusEffect> statusEffects = new ArrayList<>();
        for(StatusEffect statusEffect: StatusEffect.values())
        {
            if(CmpMapper.getStatusEffectComp(statusEffect, entity)!=null) statusEffects.add(statusEffect);
        }
        return statusEffects;
    }
    public void addLightCmpTemp(Entity entity, StatusEffectCmp statusEffectCmp)
    {
        LightCmpTemp lightCmpTemp = new LightCmpTemp(statusEffectCmp.lightLevel, statusEffectCmp.lightColor, statusEffectCmp.flicker, statusEffectCmp.strobe);
        entity.add(lightCmpTemp);

    }

    public void removeLightCmpTemp(Entity entity)
    {

        entity.remove(LightCmpTemp.class);
    }

    public void interrupt(Entity entity)
    {
        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, game.ticker);
        ArrayList<ScheduledEvt> scheduledEvents = tickerCmp.getScheduledActions(entity);
        for(ScheduledEvt scheduledEvt : scheduledEvents) tickerCmp.actionQueue.remove(scheduledEvt);
    }


}
