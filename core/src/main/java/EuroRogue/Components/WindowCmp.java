package EuroRogue.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Stage;

import EuroRogue.LightHandler;
import EuroRogue.MySparseLayers;

public class WindowCmp implements Component
{
    public MySparseLayers display;
    public Stage stage;
    public LightHandler lightingHandler;

    public int[] columnIndexes;

    public WindowCmp (MySparseLayers display, Stage stage , boolean visible)
    {
        this.display = display;
        this.stage = stage;
        display.setVisible(visible);
        this.lightingHandler = null;

    }
    public WindowCmp (MySparseLayers display, Stage stage, LightHandler lightingHandler, boolean visible)
    {
        this.display = display;
        this.stage = stage;
        display.setVisible(visible);
        this.lightingHandler = lightingHandler;

    }
}
