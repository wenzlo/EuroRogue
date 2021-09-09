package EuroRogue.EventComponents;

import EuroRogue.GameState;

public class GameStateEvt implements IEventComponent{


    public boolean processed = false;
    public GameState newGameState;

    public GameStateEvt(){}

    public GameStateEvt(GameState newGameState) { this.newGameState = newGameState; }


    @Override
    public boolean isProcessed()
    {
        return processed;
    }

    @Override
    public void setProcessed(boolean bool) {
        processed = bool;
    }

}
