package EuroRogue.Listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidmath.Coord;

public class ObjectListener implements EntityListener {
    EuroRogue game;
    public ObjectListener(EuroRogue game) {this.game=game;}
    @Override
    public void entityAdded(Entity entity)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL,game.currentLevel);
        Coord position = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
        levelCmp.objects.add(position, entity.hashCode(), entity.hashCode());


        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;
        LightCmp lightCmp = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, entity);
        if(lightCmp!=null)
        {
            Light light = new Light(Coord.get(position.x*3+1, position.y*3+1), new Radiance(lightCmp.level, lightCmp.color, lightCmp.flicker, lightCmp.strobe) );
            lightHandler.addLight(entity.hashCode(), light);
        }

    }

    @Override
    public void entityRemoved(Entity entity)
    {

        LevelCmp level = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
        level.items.remove(entity.hashCode());


        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;
        lightHandler.removeLight(entity.hashCode());

    }
}
