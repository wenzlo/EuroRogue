package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;

import EuroRogue.AbilityCmpSubSystems.MeleeAttack;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.StringKit;
import squidpony.squidgrid.gui.gdx.SColor;

public class WinSysStats extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public WinSysStats()
    {
        super.priority=10;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(FocusCmp.class, FocusTargetCmp.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        if(!((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().focusStatsWindow)).display.isVisible()) return;

        MySparseLayers display = null;
        Stage stage = null;
        SColor bgColor;
        String caption;
        for(Entity entity:entities)
        {
            EuroRogue game = getGame();

            if(game.getFocus()==entity)
            {
                display = game.focusStatsWindow.getComponent(WindowCmp.class).display;

                bgColor = (SColor) display.defaultBackground;
                stage = game.focusStatsWindow.getComponent(WindowCmp.class).stage;
                caption = ((NameCmp)CmpMapper.getComp(CmpType.NAME, entity)).name+" Stats";
            }
            else
            {
                display = (MySparseLayers)game.targetStatsWindow.getComponent(WindowCmp.class).display;
                bgColor = (SColor) display.defaultBackground;
                stage = game.targetStatsWindow.getComponent(WindowCmp.class).stage;
                Entity focusTarget = game.getFocusTarget();
                caption = " Stats";
                if(focusTarget!=null)
                {
                    caption = ((NameCmp)CmpMapper.getComp(CmpType.NAME, focusTarget)).name+" Stats";
                }

            }

            StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
            display.clear();

            String statsSB = statsCmp.toString();
            int y = 1;
            int x = 1;
            for(String string:StringKit.split(statsSB, "\n"))
            {
                display.put(x,y,string,SColor.LIGHT_YELLOW_DYE);
                y++;
                if(y>16)
                {
                    x=((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().focusStatsWindow)).columnIndexes[1];
                    y=1;
                }
            }
            MeleeAttack meleeAttack = (MeleeAttack) CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, getGame().getFocus());
            double noiseLvl = 0;
            if(meleeAttack!=null) noiseLvl = meleeAttack.getNoiseLvl(getGame().getFocus());
            display.put(20,16,"Noise Lvl="+noiseLvl, SColor.WHITE);

            stage.getViewport().apply(false);
            stage.act();
            stage.draw();
        }
    }
}
