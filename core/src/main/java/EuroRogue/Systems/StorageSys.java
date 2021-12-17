package EuroRogue.Systems;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.EventComponents.StorageEvt;
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
        Storage storage = getGame().buildStorage;
        for(Entity entity : entities)
        {
            StorageEvt storageEvt = (StorageEvt) CmpMapper.getComp(CmpType.STORAGE_EVT, entity);
            if(storageEvt.processed) return;
            else storageEvt.processed = true;

            switch (storageEvt.storageEvtType)
            {

                case SAVE_BUILD:
                    System.out.println(storageEvt);
                    System.out.println(storageEvt.buildName);
                    storage.storeCharBuild(storageEvt.buildName, getGame());

                    break;
                case LOAD_BUILD:
                    storage.loadCharBuild(storageEvt.buildName,getGame());
                    break;
                case DELETE_BUILD:
                    storage.deleteBuild(storageEvt.buildName);
                    break;
            }

        }
    }
}
