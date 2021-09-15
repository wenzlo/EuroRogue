package EuroRogue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;
import java.util.Objects;

import squidpony.StringKit;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.LightingHandler;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

//import squidpony.squidgrid.FOVCache;

public class LightHandler extends LightingHandler
{
    public HashMap<Integer, Light> lightList;
    MySparseLayers display;

    public LightHandler(){}

    public LightHandler(double[][] resistance)
    {
        super(resistance);
        lightList= new HashMap<>();
    }

    public LightHandler(double[][] resistance, Color backgroundColor, Radius radiusStrategy, double viewerVisionRange, MySparseLayers display) {
        super(resistance, backgroundColor, radiusStrategy, viewerVisionRange);

        lightList= new HashMap<>();
        this.display = display;

    }

    public void removeLight(Integer id) {
        lightList.remove(id);
    }


    public LightingHandler addLight(Integer id, Light light)
    {
        lightList.put(id, light);
        return this;
    }


    public int lightMapX(TextCellFactory.Glyph glyph)
    {
        int lightMapX = Math.round(((glyph.getX() - display.getX()) / display.font.actualCellWidth)*3)+1;
        //lightMapX = display.gridX(glyph.getX())*3;
        return lightMapX;
    }


    public int lightMapY(TextCellFactory.Glyph glyph)
    {

        int lightMapY = Math.round(((display.getY() - glyph.getY()) / display.font.actualCellHeight + display.gridHeight)*3)+1;

        return lightMapY;

    }

    @Override
    public void update() {
        Radiance radiance;
        SColor.eraseColoredLighting(colorLighting);
        Coord pos;
        for (Light light : lightList.values()) {
            pos = Coord.get(light.position.x, light.position.y);
            if(!noticeable.contains(pos))
            continue;
            radiance = light.radiance;
            FOV.reuseFOV(resistances, tempFOV, pos.x, pos.y, radiance.currentRange()*3);
            //SColor.colorLightingInto(tempColorLighting, tempFOV, radiance.color);
            mixColoredLighting(radiance.flare, radiance.color);
        }
    }

    @Override
    public void updateAll() {
        Radiance radiance;
        SColor.eraseColoredLighting(colorLighting);
        Coord pos;
        for (Light light : lightList.values()) {
            pos = Coord.get(light.position.x, light.position.y);

            radiance = light.radiance;
            FOV.reuseFOV(resistances, tempFOV, pos.x+1, pos.y+1, radiance.currentRange()*3);
            mixColoredLighting(radiance.flare, radiance.color);
        }
    }

    public double[][] calculateFOV(TextCellFactory.Glyph viewer) {
        return calculateFOV(lightMapX(viewer), lightMapY(viewer));
    }

    @Override
    public double[][] calculateFOV(Coord viewer)
    {
        return calculateFOV(viewer.x*3, viewer.y*3);
    }

    @Override
    public double[][] calculateFOV(int viewerX, int viewerY) {
        return calculateFOV(viewerX, viewerY, 0, 0, width, height);
    }

    @Override
    public double[][] calculateFOV(int viewerX, int viewerY, int minX, int minY, int maxX, int maxY)
    {

        Radiance radiance;
        minX = MathUtils.clamp(minX, 0, width);
        maxX = MathUtils.clamp(maxX, 0, width);
        minY = MathUtils.clamp(minY, 0, height);
        maxY = MathUtils.clamp(maxY, 0, height);
        FOV.reuseFOV(resistances, fovResult, viewerX, viewerY, viewerRange, radiusStrategy);
        SColor.eraseColoredLighting(colorLighting);
        float maxRange = 0, range;
        Coord pos;
        for (Light light : lightList.values())
        {
            pos = Coord.get(light.position.x, light.position.y);
            range = light.radiance.range*3;
            if(range > maxRange &&
                    pos.x + range >= minX && pos.x - range < maxX && pos.y + range >= minY && pos.y - range < maxY)
                maxRange = range;
        }
        FOV.reuseLOS(resistances, losResult, viewerX, viewerY, minX, minY, maxX, maxY);
        noticeable.refill(losResult, 0.0001, Double.POSITIVE_INFINITY).expand8way((int) Math.ceil(maxRange));
        for (Light light : lightList.values())
        {
            pos = Coord.get(light.position.x, light.position.y);
            if(!noticeable.contains(pos))
                continue;
            radiance = light.radiance;
            FOV.reuseFOV(resistances, tempFOV, pos.x, pos.y, radiance.range*3);
            //SColor.colorLightingInto(tempColorLighting, tempFOV, radiance.color);
            mixColoredLighting(radiance.flare, radiance.color);
        }
        for (int x = Math.max(0, minX); x < maxX && x < width; x++) {
            for (int y = Math.max(0, minY); y < maxY && y < height; y++) {
                if (losResult[x][y] > 0.0) {
                    fovResult[x][y] = MathUtils.clamp(fovResult[x][y] + colorLighting[0][x][y], 0, 1);
                }
            }
        }
        return fovResult;
    }

   /* public Light getLightByActor(Actor actor)
    {
        if(actor.glyph==null) return null;
       return getLightByGlyph(actor.glyph);
    }*/
    public Light getLightByGlyph(TextCellFactory.Glyph glyph)
    {

        int id = Integer.parseInt(StringKit.split(glyph.getName()," ")[0]);
        return lightList.get(id);
    }

    public Light removeLightByGlyph(TextCellFactory.Glyph glyph)
    {
        if(glyph.getName()==null) return null;
        int id = Integer.parseInt(StringKit.split(glyph.getName()," ")[0]);
        return lightList.remove(id);
    }



}
