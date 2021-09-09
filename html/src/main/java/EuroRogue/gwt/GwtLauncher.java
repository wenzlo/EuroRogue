package EuroRogue.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import EuroRogue.EuroRogue;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration configuration = new GwtApplicationConfiguration(90 * 10, (25 + 7) * 20);
        return configuration;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new EuroRogue();
    }
}