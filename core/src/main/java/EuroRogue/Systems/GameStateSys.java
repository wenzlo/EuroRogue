package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.GameState;
import EuroRogue.MyEntitySystem;
import EuroRogue.StatusEffectCmps.StatusEffect;

public class GameStateSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

   //public GameStateSys()
    {
        super.priority = 99;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(GameStateEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {

        for(Entity entity:entities)
        {
            GameStateEvt gameStateEvt = (GameStateEvt)CmpMapper.getComp(CmpType.GAMESTATE_EVT, entity);
            gameStateEvt.processed=true;

            GameState newGameState = gameStateEvt.newGameState;
            if(newGameState==GameState.PLAYING && getGame().gameState==GameState.CAMPING && CmpMapper.getStatusEffectComp(StatusEffect.STARVING, getGame().getFocus())==null)
            {
                StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, getGame().getFocus());
                statsCmp.hp=statsCmp.getMaxHP();

            }

            setWindowVisiblity(newGameState);
            setInputProcessor(newGameState);
            ((GameStateEvt)CmpMapper.getComp(CmpType.GAMESTATE_EVT, entity)).setProcessed(true);
            getGame().gameState=newGameState;
        }

    }

    private void setWindowVisiblity(GameState gameState)
    {
        switch (gameState)
        {

            case STARTING:
                for(Entity windowEntity : getGame().allWindows)
                    ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, windowEntity)).display.setVisible(getGame().startWindows.contains(windowEntity));
                break;
            case LOADING:
                for(Entity windowEntity : getGame().allWindows)
                    ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, windowEntity)).display.setVisible(false);
                break;

            case PLAYING:
            case AIMING:
                for(Entity windowEntity : getGame().allWindows)
                {
                    ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, windowEntity)).display.setVisible(getGame().playingWindows.contains(windowEntity));
                }
                break;
            case CAMPING:
                for(Entity windowEntity : getGame().allWindows)
                {
                    ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, windowEntity)).display.setVisible(getGame().campingWindows.contains(windowEntity));
                }
                break;
            case GAME_OVER:
                for(Entity windowEntity : getGame().allWindows)
                {

                    if(getGame().gameOverWindows.contains(windowEntity))
                    {
                        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, windowEntity)).display.setVisible(true);
                    }
                    else  ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, windowEntity)).display.setVisible(false);
                }
                break;
        }
    }
    private void setInputProcessor(GameState gameState)
    {
        switch (gameState)
        {

            case PLAYING:
                getGame().campInput.setIgnoreInput(true);
                getGame().input.setIgnoreInput(false);
                getGame().aimInput.setIgnoreInput(true);
                getGame().startInput.setIgnoreInput(true);
                break;
            case AIMING:
                getGame().campInput.setIgnoreInput(true);
                getGame().input.setIgnoreInput(true);
                getGame().aimInput.setIgnoreInput(false);
                getGame().startInput.setIgnoreInput(true);
                break;
            case CAMPING:
                getGame().campInput.setIgnoreInput(false);
                getGame().input.setIgnoreInput(true);
                getGame().aimInput.setIgnoreInput(true);
                getGame().startInput.setIgnoreInput(true);
                break;
            case STARTING:
                getGame().campInput.setIgnoreInput(true);
                getGame().input.setIgnoreInput(true);
                getGame().aimInput.setIgnoreInput(true);
                getGame().startInput.setIgnoreInput(false);
                break;
            case LOADING:
            case GAME_OVER:
                break;
        }
    }
}
