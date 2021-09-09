package EuroRogue;

import com.badlogic.ashley.core.EntitySystem;


public class MyEntitySystem extends EntitySystem
{
    @Override
    public MyEngine getEngine()
    {
        return (MyEngine)super.getEngine();
    }

    public EuroRogue getGame()
    {
        return getEngine().game;
    }
}
