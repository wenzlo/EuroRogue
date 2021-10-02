package EuroRogue.Systems;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.ShrineCmp;
import EuroRogue.EventComponents.CodexEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.IColoredString;
import EuroRogue.School;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.MenuItem;
import EuroRogue.Components.CodexCmp;

import EuroRogue.Components.InventoryCmp;

import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.MenuCmp;

import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.ItemEvtType;

import EuroRogue.EventComponents.ItemEvt;

import EuroRogue.EventComponents.ShrineEvt;
import EuroRogue.GameState;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;

import squidpony.squidgrid.gui.gdx.SColor;

import squidpony.squidmath.Coord;

public class ShrineSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public ShrineSys() {
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
        entities = engine.getEntitiesFor(Family.all(ShrineEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
       if(entities.size()==0 ) return;
        Entity shrineEntity = entities.get(0);
        ShrineEvt shrineEvt = (ShrineEvt) CmpMapper.getComp(CmpType.SHRINE_EVT, shrineEntity);
        ShrineCmp shrineCmp = (ShrineCmp)CmpMapper.getComp(CmpType.SHRINE, shrineEntity);

        if(shrineCmp.charges==0)
        {
            Entity gameStateEvtEnt = new Entity();
            gameStateEvtEnt.add(new GameStateEvt(GameState.PLAYING));
            getEngine().addEntity(gameStateEvtEnt);
            LightCmp lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, shrineEntity);
            lightCmp.level=0;
            lightCmp.strobe=0f;
            lightCmp.flicker=0f;
            shrineEntity.remove(ShrineEvt.class);
            return;
        }
        if(shrineEvt.processed) return;
        shrineEvt.processed = true;

        Entity focus = getGame().getFocus();

        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focus);
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, focus);

        int charges = Collections.frequency(manaPoolCmp.attuned, shrineCmp.school);
        if(charges==0)
        {
            shrineEntity.remove(ShrineEvt.class);
            return;
        }
        if(shrineCmp.charges==-1)shrineCmp.charges = charges;


        Entity gameStateEvtEnt = new Entity();
        gameStateEvtEnt.add(new GameStateEvt(GameState.SHRINE));
        getEngine().addEntity(gameStateEvtEnt);

        ArrayList<Skill> skills = Skill.getSkillsBySchool(shrineCmp.school);
        skills.removeAll(codexCmp.known);
        Collections.shuffle(skills);
        for(int i=0; i<Math.min(skills.size(), 2); i++)
        {
            Skill skill = skills.get(i);
            shrineCmp.skillOffer.add(skill);
        }

        manaPoolCmp.spent.addAll(manaPoolCmp.active);
        manaPoolCmp.active.clear();

    }
}
