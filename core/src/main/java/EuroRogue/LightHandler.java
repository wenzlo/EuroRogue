package EuroRogue;

import static squidpony.squidgrid.gui.gdx.SColor.FLOAT_WHITE;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;

import squidpony.StringKit;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.LightingHandler;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.NumberTools;

public class LightHandler extends LightingHandler
{
    public HashMap<Integer, Light> lightList;
    MySparseLayers display;

    public LightHandler()
    {
        super();
    }

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
        //System.out.println(new GreasedRegion(losResult, 0.0).not());
        for (Light light : lightList.values()) {
            pos = Coord.get(light.position.x, light.position.y);
            if(!noticeable.contains(pos))
            continue;
            radiance = light.radiance;
            FOV.reuseFOV(resistances, tempFOV, pos.x, pos.y, radiance.currentRange()*3);
            //SColor.colorLightingInto(tempColorLighting, tempFOV, radiance.color);
            mixColoredLighting(radiance.flare, radiance.color);

        }
        //for(float[] line : colorLighting[0])
            //System.out.println(line.toString());
    }

    @Override
    public void updateAll()
    {
        Radiance radiance;
        for (int x = 0; x < width; x++) {
            PER_CELL:
            for (int y = 0; y < height; y++) {
                for (int xx = Math.max(0, x - 1), xi = 0; xi < 3 && xx < width; xi++, xx++) {
                    for (int yy = Math.max(0, y - 1), yi = 0; yi < 3 && yy < height; yi++, yy++) {
                        if(resistances[xx][yy] < 1.0){
                            losResult[x][y] = 1.0;
                            continue PER_CELL;
                        }
                    }
                }
            }
        }
        SColor.eraseColoredLighting(colorLighting);
        final int sz = lightList.size();
        Coord pos;
        for (int i = 0; i < sz; i++) {
            Light light = lightList.get(lightList.keySet().toArray()[i]);
            pos = light.position;
            radiance = light.radiance;
            FOV.reuseFOV(resistances, tempFOV, pos.x, pos.y, radiance.currentRange());
            mixColoredLighting(radiance.flare, radiance.color);
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (losResult[x][y] > 0.0) {
                    fovResult[x][y] = MathUtils.clamp(losResult[x][y] + colorLighting[0][x][y], 0, 1);
                }
            }
        }
    }
    /*{
        Radiance radiance;

        System.out.println(new GreasedRegion(losResult, 0.9));

        SColor.eraseColoredLighting(colorLighting);
        Coord pos;
        for (Light light : lightList.values()) {
            pos = Coord.get(light.position.x, light.position.y);

            radiance = light.radiance;
            MyFOV.reuseFOV(resistances, tempFOV, pos.x+1, pos.y+1, radiance.currentRange()*3);
            mixColoredLighting(radiance.flare, radiance.color);
        }
    }*/

    /**
     * Edits {@link #colorLighting} by adding in and mixing the given color where the light strength in {@link #tempFOV}
     * is greater than 0, with that strength boosted by flare (which can be any finite float greater than -1f, but is
     * usually from 0f to 1f when increasing strength).
     * Primarily used internally, but exposed so outside code can do the same things this class can.
     * @param flare boosts the effective strength of lighting in {@link #tempColorLighting}; usually from 0 to 1
     */
    @Override
    public void mixColoredLighting(float flare, float color)
    {
        final float[][][] basis = colorLighting;
        final double[][] otherStrength = tempFOV;
        flare += 1f;
        float b0, b1, o0, o1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (losResult[x][y] > 0) {
                    if (resistances[x][y] >= 1) {
                        o0 = 0f;
                        if (y > 0) {
                            if ((losResult[x][y - 1] > 0 && otherStrength[x][y - 1] > 0 && resistances[x][y - 1] < 1)
                                    || (x > 0 && losResult[x - 1][y - 1] > 0 && otherStrength[x - 1][y - 1] > 0 && resistances[x - 1][y - 1] < 1)
                                    || (x < width - 1 && losResult[x + 1][y - 1] > 0 && otherStrength[x + 1][y - 1] > 0 && resistances[x + 1][y - 1] < 1)) {
                                o0 = (float) otherStrength[x][y];
                            }
                        }
                        if (y < height - 1) {
                            if ((losResult[x][y + 1] > 0 && otherStrength[x][y + 1] > 0 && resistances[x][y + 1] < 1)
                                    || (x > 0 && losResult[x - 1][y + 1] > 0 && otherStrength[x - 1][y + 1] > 0 && resistances[x - 1][y + 1] < 1)
                                    || (x < width - 1 && losResult[x + 1][y + 1] > 0 && otherStrength[x + 1][y + 1] > 0 && resistances[x + 1][y + 1] < 1)) {
                                o0 = (float) otherStrength[x][y];
                            }
                        }
                        if (x > 0 && losResult[x - 1][y] > 0 && otherStrength[x - 1][y] > 0 && resistances[x - 1][y] < 1) {
                            o0 = (float) otherStrength[x][y];
                        }
                        if (x < width - 1 && losResult[x + 1][y] > 0 && otherStrength[x + 1][y] > 0 && resistances[x + 1][y] < 1) {
                            o0 = (float) otherStrength[x][y];
                        }
                        if(o0 > 0f) o1 = color;
                        else continue;
                    } else {
                        if((o0 = (float) otherStrength[x][y]) != 0) o1 = color;
                        else continue;
                    }
                    b0 = basis[0][x][y];
                    b1 = basis[1][x][y];
                    if (b1 == FLOAT_WHITE) {
                        basis[1][x][y] = o1;
                        basis[0][x][y] = Math.min(1.0f, b0 + o0 * flare);
                    } else {
                        if (o1 != FLOAT_WHITE) {
                            float change = (o0 - b0) * 0.5f + 0.5f;
                            final int s = NumberTools.floatToIntBits(b1), e = NumberTools.floatToIntBits(o1),
                                    rs = (s & 0xFF), gs = (s >>> 8) & 0xFF, bs = (s >>> 16) & 0xFF, as = s & 0xFE000000,
                                    re = (e & 0xFF), ge = (e >>> 8) & 0xFF, be = (e >>> 16) & 0xFF, ae = (e >>> 25);
                            change *= ae * 0.007874016f;
                            basis[1][x][y] = NumberTools.intBitsToFloat(((int) (rs + change * (re - rs)) & 0xFF)
                                    | ((int) (gs + change * (ge - gs)) & 0xFF) << 8
                                    | (((int) (bs + change * (be - bs)) & 0xFF) << 16)
                                    | as);
                            basis[0][x][y] = Math.min(1.0f, b0 + o0 * change * flare);
                        } else {
                            basis[0][x][y] = Math.min(1.0f, b0 + o0 * flare);
                        }
                    }
                }
            }
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
        MyFOV.reuseFOV(resistances, fovResult, viewerX, viewerY, viewerRange, radiusStrategy);
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
        MyFOV.reuseLOS(resistances, losResult, viewerX, viewerY, minX, minY, maxX, maxY);
        noticeable.refill(losResult, 0.0001, Double.POSITIVE_INFINITY).expand8way((int) Math.ceil(maxRange));
        for (Light light : lightList.values())
        {
            pos = Coord.get(light.position.x, light.position.y);
            if(!noticeable.contains(pos))
                continue;
            radiance = light.radiance;
            MyFOV.reuseFOV(resistances, tempFOV, pos.x, pos.y, radiance.range*3);
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
