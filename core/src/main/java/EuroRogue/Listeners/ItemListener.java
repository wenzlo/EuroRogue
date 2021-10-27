package EuroRogue.Listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import squidpony.squidgrid.gui.gdx.Radiance;

public class ItemListener implements EntityListener {
    EuroRogue game;
    public ItemListener (EuroRogue game) {this.game=game;}
    @Override
    public void entityAdded(Entity entity)
    {
        NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, entity);

        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL,game.currentLevel);
        if(levelCmp==null) return;
        System.out.println(nameCmp.name+" added");
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity);
        System.out.println(positionCmp.coord);
        levelCmp.items.put(positionCmp.coord, entity.hashCode(), entity.hashCode());
        System.out.println(entity.hashCode() == levelCmp.items.get(positionCmp.coord));
        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;
        LightCmp lightCmp = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, entity);
        if(lightCmp!=null )
        {
            Light light = new Light(positionCmp.coord, new Radiance(lightCmp.level, lightCmp.color, lightCmp.flicker, lightCmp.strobe) );
            lightHandler.addLight(light.hashCode(), light);
        }

    }

    @Override
    public void entityRemoved(Entity entity)
    {
        NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, entity);
        System.out.println(nameCmp.name+" removed");

        LevelCmp level = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
        level.items.remove(entity.hashCode());

        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;
        lightHandler.removeLight(entity.hashCode());

    }
}
