package EuroRogue.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import EuroRogue.EuroRogue;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration configuration = new GwtApplicationConfiguration(180 * 9, 114 * 9);
        return configuration;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new EuroRogue();
    }
}