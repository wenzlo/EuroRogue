package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.ItemEvtType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;

public class StalkingListener extends StatusEffectListener
{
    public StalkingListener(EuroRogue game){
        super(game);
        effect= StatusEffect.STALKING;
    }

    @Override
    public void entityAdded(Entity entity) {
        super.entityAdded(entity);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);
        Integer itemID = inventoryCmp.getSlotEquippedID(EquipmentSlot.LEFT_HAND_WEAP);
        if(itemID!=null)
            entity.add(new ItemEvt(itemID, entity.hashCode(), ItemEvtType.UNEQUIP));

    }

    @Override
    public void entityRemoved(Entity entity)
    {
        super.entityRemoved(entity);
        GlyphsCmp glyphsCmp = (GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, entity);
        PositionCmp positionCmp = (PositionCmp)CmpMapper.getComp(CmpType.POSITION, entity);
        AnimateGlyphEvt animationEvt = new AnimateGlyphEvt(glyphsCmp.glyph, AnimationsSys.AnimationType.BURST,positionCmp.coord, positionCmp.coord, null);
        entity.add(animationEvt);
    }
}
