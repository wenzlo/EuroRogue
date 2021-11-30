package EuroRogue.Systems;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.LevelEvt;
import EuroRogue.EventComponents.StorageEvt;
import EuroRogue.LevelType;
import EuroRogue.MyEntitySystem;
import EuroRogue.Storage;

public class StorageSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public StorageSys() {
        super.priority = 98;
    }


    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(StorageEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        Storage storage = getGame().storage;
        for(Entity entity : entities)
        {
            StorageEvt storageEvt = (StorageEvt) CmpMapper.getComp(CmpType.STORAGE_EVT, entity);
            if(storageEvt.processed) return;
            else storageEvt.processed = true;

            switch (storageEvt.storageEvtType)
            {

                case SAVE_BUILD:
                    storage.storeCharBuild(storageEvt.buildName, getGame());

                    getGame().depth++;
                    InventoryCmp inventoryCmp = ( InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, getGame().getFocus());
                    Entity evtEntity = new Entity();
                    List<LevelType> levelTypes = new ArrayList<>();
                    Collections.addAll(levelTypes, LevelType.values());

                    levelTypes.remove(LevelType.START);
                    //TODO move rng level tye selection to level sys
                    LevelEvt levelEvt = new LevelEvt(getGame().rng.getRandomElement(levelTypes));
                    CampEvt campEvt = new CampEvt(getGame().getFocus().hashCode(), inventoryCmp.getEquippedIDs());
                    evtEntity.add(levelEvt);
                    getGame().getFocus().add(campEvt);
                    getGame().engine.addEntity(evtEntity);

                    break;
                case LOAD_BUILD:
                    storage.loadCharBuild(storageEvt.buildName, getGame());
                    break;
                case DELETE_BUILD:
                    storage.deleteBuild(storageEvt.buildName);
                    break;
            }

        }
    }
}
