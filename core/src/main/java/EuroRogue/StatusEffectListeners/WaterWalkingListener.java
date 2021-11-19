package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EuroRogue;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TerrainType;

public class WaterWalkingListener extends StatusEffectListener
{
    public WaterWalkingListener(EuroRogue game){
        super(game);
        effect= StatusEffect.WATER_WALKING;
    }

    @Override
    public void entityAdded(Entity entity)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);

        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
        AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, entity);
        aiCmp.addTraversable(TerrainType.DEEP_WATER, levelCmp.decoDungeon);
    }

    @Override
    public void entityRemoved(Entity entity)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, entity);
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, entity);
        AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, entity);
        aiCmp.removeTraversable(TerrainType.DEEP_WATER, levelCmp.decoDungeon);
    }
}
