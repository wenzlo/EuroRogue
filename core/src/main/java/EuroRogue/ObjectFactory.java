package EuroRogue;

import com.badlogic.ashley.core.Entity;

import EuroRogue.Components.CharCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.ObjectCmp;
import EuroRogue.Components.ObjectType;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.ShrineCmp;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;

public class ObjectFactory
{
    GWTRNG rng;

    public ObjectFactory(GWTRNG rng) {this.rng=rng;}

    public Entity getRndObject(ObjectType type, Coord coord)
    {
        Entity obj = new Entity();

        switch (type)
        {
            case SHRINE:
                School school = rng.getRandomElement(School.values());
                obj.add(new NameCmp(school.name +" Shrine"));
                obj.add(new ObjectCmp(ObjectType.SHRINE));
                obj.add(new ShrineCmp(school));
                obj.add(new CharCmp(type.chr, school.color));
                obj.add(new PositionCmp(coord));
                obj.add(new LightCmp(3, SColor.lerpFloatColors(school.color.toFloatBits(), SColor.FLOAT_WHITE, 0.2f), 0.3f, 0f));
                break;
        }
        return obj;
    }
    public Entity getShrine(Coord coord, School school)
    {
        Entity obj = new Entity();
        obj.add(new NameCmp(school.name +" Shrine"));
        obj.add(new ObjectCmp(ObjectType.SHRINE));
        obj.add(new ShrineCmp(school));
        obj.add(new CharCmp(ObjectType.SHRINE.chr, school.color));
        obj.add(new PositionCmp(coord));
        obj.add(new LightCmp(3, SColor.lerpFloatColors(school.color.toFloatBits(), SColor.FLOAT_WHITE, 0.2f), 0.3f, 0f));
        return  obj;
    }
}
