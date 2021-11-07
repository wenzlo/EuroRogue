package EuroRogue.StatusEffectListeners;

import com.badlogic.ashley.core.Entity;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.ItemEvtType;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class StalkingListener extends StatusEffectListener
{
    public StalkingListener(EuroRogue game){
        super(game);
        effect= StatusEffect.STALKING;
    }

    @Override
    public void entityAdded(Entity entity) {
        super.entityAdded(entity);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);
        Integer itemID = inventoryCmp.getSlotEquippedID(EquipmentSlot.LEFT_HAND_WEAP);
        if(itemID!=null)
            entity.add(new ItemEvt(itemID, entity.hashCode(), ItemEvtType.UNEQUIP));
    }
}
