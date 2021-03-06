package EuroRogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import squidpony.squidgrid.Direction;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.FilterBatch;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SubcellLayers;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class MySparseLayers extends SubcellLayers
{

    public MySparseLayers(int gridWidth, int gridHeight, float cellWidth, float cellHeight, TextCellFactory font, float xOffset, float yOffset) {
        super(gridWidth, gridHeight, cellWidth, cellHeight, font, xOffset, yOffset);
    }
    public MySparseLayers(int gridWidth, int gridHeight, float cellWidth, float cellHeight, TextCellFactory font) {
        super(gridWidth, gridHeight, cellWidth, cellHeight, font, 0f, 0f);
    }

    public TextCellFactory.Glyph glyph(char shown, float color, float x, float y) {
        TextCellFactory.Glyph g = this.font.glyph(shown, color, x, y);
        super.glyphs.add(g);
        return g;
    }

    public int getBigWidth()
    {
        return (int) (getStage().getViewport().getWorldWidth()/cellWidth());
    }
    public int getBigHeight()
    {
        return (int) (getStage().getViewport().getWorldHeight()/cellHeight());
    }
    public void put(int xOffset, int yOffset, IColoredString<? extends Color> cs) {
        int x = xOffset;
        for (IColoredString.Bucket<? extends Color> fragment : cs) {
            final String s = fragment.getText();
            final Color color = fragment.getColor();
            put(x, yOffset, s, color == null ? getDefaultForegroundColor() : scc.filter(color), null);
            x += s.length();
        }
    }

    public void slide(float delay, TextCellFactory.Glyph glyph, final int newX,
                      final int newY, float duration, /* @Nullable */ Runnable postRunnable) {

        duration = Math.max(0.015f, duration);
        final int nbActions = 1 + (0 < delay ? 1 : 0) + (postRunnable == null ? 0 : 1);
        int index = 0;
        final Action[] sequence = new Action[nbActions];
        if (0 < delay)
            sequence[index++] = Actions.delay(delay);
        final float nextX = worldX(newX);
        final float nextY = worldY(newY);
        sequence[index++] = Actions.moveToAligned(nextX, nextY, Align.bottomLeft, duration);
        if(postRunnable != null)
        {
            sequence[index] = Actions.run(postRunnable);
        }

        glyph.addAction(Actions.sequence(sequence));
    }
    public void slide(float delay, TextCellFactory.Glyph glyph, final float worldX,
                      final float worldY, float duration, /* @Nullable */ Runnable postRunnable) {

        duration = Math.max(0.015f, duration);
        final int nbActions = 1 + (0 < delay ? 1 : 0) + (postRunnable == null ? 0 : 1);
        int index = 0;
        final Action[] sequence = new Action[nbActions];
        if (0 < delay)
            sequence[index++] = Actions.delay(delay);
        final float nextX = worldX;
        final float nextY = worldY;
        sequence[index++] = Actions.moveToAligned(nextX, nextY, Align.bottomLeft, duration);
        if(postRunnable != null)
        {
            sequence[index] = Actions.run(postRunnable);
        }

        glyph.addAction(Actions.sequence(sequence));
    }

    public void slide(float delay, TextCellFactory.Glyph glyph, int startX, int startY, final int newX,
                      final int newY, float duration, /* @Nullable */ Runnable postRunnable) {


        duration = Math.max(0.015f, duration);
        final int nbActions = 1 + (0 < delay ? 1 : 0) + (postRunnable == null ? 0 : 1);
        int index = 0;
        final Action[] sequence = new Action[nbActions];
        if (0 < delay)
            sequence[index++] = Actions.delay(delay);
        final float nextX = worldX(newX);
        final float nextY = worldY(newY);
        sequence[index++] = Actions.moveToAligned(nextX, nextY, Align.bottomLeft, duration);
        if(postRunnable != null)
        {
            sequence[index] = Actions.run(postRunnable);
        }

        glyph.addAction(Actions.sequence(sequence));
    }

    public void reverseBurst(float delay, int x, int y, int distance, Radius measurement, char shown,
                      float startColor, float endColor, float duration, /* @Nullable */ Runnable postRunnable)
    {
        Direction d;
        switch (measurement)
        {
            case SQUARE:
            case CUBE:
                for (int i = 0; i < 7; i++) {
                    d = Direction.CLOCKWISE[i];
                    summon(delay,  x - d.deltaX * distance, y + d.deltaY * distance, x, y,
                            shown, startColor, endColor, duration, null);
                }
                d = Direction.CLOCKWISE[7];
                summon(delay, x - d.deltaX * distance, y + d.deltaY * distance, x, y,
                        shown, startColor, endColor, duration, postRunnable);
                break;
            case CIRCLE:
            case SPHERE:
                float xf = worldX(x), yf = worldY(y);
                for (int i = 0; i < 4; i++) {
                    d = Direction.DIAGONALS[i];
                    summon(delay,  xf - d.deltaX * font.actualCellWidth * distance * 0.7071067811865475f, // the constant is 1.0 / Math.sqrt(2.0)
                            yf + d.deltaY * font.actualCellHeight * distance * 0.7071067811865475f, xf, yf,
                            shown, startColor, endColor, duration, null);
                }
                // break intentionally absent
            default:
                for (int i = 0; i < 3; i++) {
                    d = Direction.CARDINALS_CLOCKWISE[i];
                    summon(delay,  x - d.deltaX * distance, y + d.deltaY * distance, x, y,
                            shown, startColor, endColor, duration, null);
                }
                d = Direction.CARDINALS_CLOCKWISE[3];
                summon(delay, x - d.deltaX * distance, y + d.deltaY * distance, x, y,
                        shown, startColor, endColor, duration, postRunnable);
                break;
        }
    }


    /**
     * Draws the SubcellLayers and all glyphs it tracks. {@link Batch#begin()} must have already been called on the
     * batch, and {@link Batch#end()} should be called after this returns and before the rendering code finishes for the
     * frame.
     * <br>
     * This will set the shader of {@code batch} if using a distance field or MSDF font and the shader is currently not
     * configured for such a font; it does not reset the shader to the default so that multiple Actors can all use the
     * same shader and so specific extra glyphs or other items can be rendered after calling draw(). If you need to draw
     * both a distance field font and full-color art, you should set the shader on the Batch to null when you want to
     * draw full-color art, and end the Batch between drawing this object and the other art.
     *
     * @param batch a Batch such as a {@link FilterBatch} that must be between a begin() and end() call; usually done by Stage
     * @param parentAlpha currently ignored
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        float xo = getX(), yo = getY(), yOff = (yo + 1f + gridHeight * font.actualCellHeight)-2, gxo, gyo;
        font.draw(batch, backgrounds, xo, yo, 3, 3);
        int len = layers.size();
        Frustum frustum = null;
        Stage stage = getStage();
        if(stage != null) {
            Viewport viewport = stage.getViewport();
            if(viewport != null)
            {
                Camera camera = viewport.getCamera();
                if(camera != null)
                {
                    if(
                            camera.frustum != null &&
                                    (!camera.frustum.boundsInFrustum(xo, yOff - font.actualCellHeight - 1f, 0f, font.actualCellWidth, font.actualCellHeight, 0f) ||
                                            !camera.frustum.boundsInFrustum(xo-1 + font.actualCellWidth * (gridWidth-1), yo, 0f, font.actualCellWidth, font.actualCellHeight, 0f))
                    )
                        frustum = camera.frustum;
                }
            }
        }
        font.configureShader(batch);
        if(frustum == null) {
            for (int i = 0; i < len; i++) {
                layers.get(i).draw(batch, font, xo+1, yOff);
            }
        }
        else
        {
            for (int i = 0; i < len; i++) {
                layers.get(i).draw(batch, font, frustum, xo+1, yOff);
            }
        }

        int x, y;
        for (int i = 0; i < glyphs.size(); i++) {
            TextCellFactory.Glyph glyph = glyphs.get(i);
            if(glyph == null)
                continue;
            glyph.act(Gdx.graphics.getDeltaTime());
            if(!glyph.isVisible() ||
                    (x = Math.round((gxo = glyph.getX() - xo) / font.actualCellWidth)) < 0 || x >= gridWidth ||
                    (y = Math.round((gyo = glyph.getY() - yo)  / -font.actualCellHeight + gridHeight)) < 0 || y >= gridHeight ||
                    backgrounds[x * 3 + 1][y * 3 + 1] == 0f || (frustum != null && !frustum.boundsInFrustum(gxo, gyo, 0f, font.actualCellWidth, font.actualCellHeight, 0f)))
                continue;
            glyph.draw(batch, 1f);
        }
    }

    @Override
    public void bump(final float delay, final TextCellFactory.Glyph glyph, Direction direction, float duration, final /* @Nullable */ Runnable postRunnable)
    {
        glyph.clearActions();
        final float x = glyph.getX(),
                y = glyph.getY();
        duration = Math.max(0.015f, duration);
        final int nbActions = 2 + (0 < delay ? 1 : 0) + (postRunnable == null ? 0 : 1);
        int index = 0;
        final Action[] sequence = new Action[nbActions];
        if (0 < delay)
            sequence[index++] = Actions.delay(delay);
        sequence[index++] = Actions.moveToAligned(x + direction.deltaX * 0.25f * font.actualCellWidth,
                y - direction.deltaY * 0.25f * font.actualCellHeight,
                Align.bottomLeft, duration * 0.25F);
        sequence[index++] = Actions.moveToAligned(x, y, Align.bottomLeft, duration * 0.65F);
        if(postRunnable != null)
        {
            sequence[index] = Actions.run(postRunnable);
        }
        glyph.addAction(Actions.sequence(sequence));

    }
    @Override
    /**
     * Tints the background at position x,y (in cells) so it becomes the given encodedColor, waiting for {@code delay}
     * (in seconds) before performing it, then after the tint is complete it returns the cell to its original color,
     * taking duration seconds. Additionally, enqueue {@code postRunnable} for running after the created action ends.
     * All subcells in the tinted cell will reach the same color during this animation, but the subcells can start with
     * different colors, and they will return to those starting colors after this animation finishes.
     * <br>
     * This will only behave correctly if you call {@link Stage#act()} before you call {@link Stage#draw()}, but after
     * any changes to the contents of this SparseLayers. If you change the contents, then draw, and then act, that will
     * draw the contents without the tint this applies, then apply the tint when you call act(), then quickly overwrite
     * the tint in the next frame. That visually appears as nothing happening other than a delay.
     * @param delay how long to wait in seconds before starting the effect
     * @param x the x-coordinate of the cell to tint
     * @param y the y-coordinate of the cell to tint
     * @param encodedColor what to transition the cell's color towards, and then transition back from, as a packed float
     * @param duration how long the total "round-trip" transition should take in seconds
     * @param postRunnable a Runnable to execute after the tint completes; may be null to do nothing.
     */
    public void tint(final float delay, final int x, final int y, final float encodedColor, float duration,
            /* @Nullable */ Runnable postRunnable) {
        if(x < 0 || x >= gridWidth || y < 0 || y >= gridHeight)
            return;
        duration = Math.max(0.015f, duration);
        final int xx = x * 3, yy = y * 3;
        final float
                x0y0 = backgrounds[xx][yy], x1y0 = backgrounds[xx+1][yy], x2y0 = backgrounds[xx+2][yy],
                x0y1 = backgrounds[xx][yy+1], x1y1 = backgrounds[xx+1][yy+1], x2y1 = backgrounds[xx+2][yy+1],
                x0y2 = backgrounds[xx][yy+2], x1y2 = backgrounds[xx+1][yy+2], x2y2 = backgrounds[xx+2][yy+2];
        final int nbActions = 3 + (0 < delay ? 1 : 0) + (postRunnable == null ? 0 : 1);
        final Action[] sequence = new Action[nbActions];
        int index = 0;
        if (0 < delay)
            sequence[index++] = Actions.delay(delay);
        sequence[index++] = new TemporalAction(duration * 0.3f) {
            @Override
            protected void update(float percent) {
                backgrounds[xx  ][yy  ] = SColor.lerpFloatColors(x0y0, encodedColor, percent);
                backgrounds[xx  ][yy+1] = SColor.lerpFloatColors(x0y1, encodedColor, percent);
                backgrounds[xx  ][yy+2] = SColor.lerpFloatColors(x0y2, encodedColor, percent);
                backgrounds[xx+1][yy  ] = SColor.lerpFloatColors(x1y0, encodedColor, percent);
                backgrounds[xx+1][yy+1] = SColor.lerpFloatColors(x1y1, encodedColor, percent);
                backgrounds[xx+1][yy+2] = SColor.lerpFloatColors(x1y2, encodedColor, percent);
                backgrounds[xx+2][yy  ] = SColor.lerpFloatColors(x2y0, encodedColor, percent);
                backgrounds[xx+2][yy+1] = SColor.lerpFloatColors(x2y1, encodedColor, percent);
                backgrounds[xx+2][yy+2] = SColor.lerpFloatColors(x2y2, encodedColor, percent);
            }
        };
        sequence[index++] = new TemporalAction(duration * 0.7f) {
            @Override
            protected void update(float percent) {
                backgrounds[xx  ][yy  ] = SColor.lerpFloatColors(encodedColor, x0y0, percent);
                backgrounds[xx  ][yy+1] = SColor.lerpFloatColors(encodedColor, x0y1, percent);
                backgrounds[xx  ][yy+2] = SColor.lerpFloatColors(encodedColor, x0y2, percent);
                backgrounds[xx+1][yy  ] = SColor.lerpFloatColors(encodedColor, x1y0, percent);
                backgrounds[xx+1][yy+1] = SColor.lerpFloatColors(encodedColor, x1y1, percent);
                backgrounds[xx+1][yy+2] = SColor.lerpFloatColors(encodedColor, x1y2, percent);
                backgrounds[xx+2][yy  ] = SColor.lerpFloatColors(encodedColor, x2y0, percent);
                backgrounds[xx+2][yy+1] = SColor.lerpFloatColors(encodedColor, x2y1, percent);
                backgrounds[xx+2][yy+2] = SColor.lerpFloatColors(encodedColor, x2y2, percent);
            }
        };
        if(postRunnable != null)
        {
            sequence[index++] = Actions.run(postRunnable);
        }
        sequence[index] = Actions.delay(duration, Actions.run(new Runnable() {
            @Override
            public void run() {
                backgrounds[xx  ][yy  ] = x0y0;
                backgrounds[xx  ][yy+1] = x0y1;
                backgrounds[xx  ][yy+2] = x0y2;
                backgrounds[xx+1][yy  ] = x1y0;
                backgrounds[xx+1][yy+1] = x1y1;
                backgrounds[xx+1][yy+2] = x1y2;
                backgrounds[xx+2][yy  ] = x2y0;
                backgrounds[xx+2][yy+1] = x2y1;
                backgrounds[xx+2][yy+2] = x2y2;
            }
        }));

        addAction(Actions.sequence(sequence));
    }

    public ArrayList<Integer> summonWithLight(float delay, int startX, int startY, int endX, int endY, char shown,
                       final float startColor, final float endColor, float duration, LightHandler lightHandler,
            /* @Nullable */ Runnable postRunnable)
    {
        duration = Math.max(0.015f, duration);
        final int nbActions = 2 + (0 < delay ? 1 : 0) + (postRunnable == null ? 0 : 1);
        int index = 0;
        final Action[] sequence = new Action[nbActions];
        if (0 < delay)
            sequence[index++] = Actions.delay(delay);
        final TextCellFactory.Glyph glyph = glyph(shown, startColor, startX, startY);
        ArrayList<Integer> lightIDs = new ArrayList<>();
        sequence[index++] = Actions.parallel(
                new TemporalAction(duration) {
                    @Override
                    protected void update(float percent) {
                        glyph.setPackedColor(SColor.lerpFloatColors(startColor, endColor, percent * 0.95f));
                        int radius = (int) (Coord.get(startX, startY).distance(Coord.get(endX,endY))+1);
                        Light light = new Light(Coord.get(startX*3, startY*3), new Radiance(radius, SColor.lerpFloatColors(glyph.getColor().toFloatBits(), SColor.WHITE_FLOAT_BITS, 0.3f),0.8f));
                        glyph.setName(light.hashCode() + " " + "0" + " temp");
                        lightHandler.addLight(light.hashCode(), light);
                        lightIDs.add(light.hashCode());

                    }
                },
                Actions.moveTo(worldX(endX), worldY(endY), duration));
        if(postRunnable != null)
        {
            sequence[index++] = Actions.run(postRunnable);
        }
        /* Do this one last, so that hasActiveAnimations() returns true during 'postRunnables' */
        sequence[index] = Actions.run(new Runnable() {
            @Override
            public void run() {
                glyphs.remove(glyph);

            }
        });
        glyph.addAction(Actions.sequence(sequence));
        return lightIDs;
    }

    /*public HashMap<Integer, TextCellFactory.Glyph> summonWithParticlesAndLight(float delay, int startX, int startY, int endX, int endY, char shown,
                                                                               final float startColor, final float endColor, float duration, float intensity, LightHandler lightHandler,
                                                                               Entity performer, ParticleEffectsCmp.ParticleEffect particleType, *//* @Nullable *//* Runnable postRunnable)
    {
        duration = Math.max(0.015f, duration);
        ParticleEffectsCmp peCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, performer);
        final int nbActions = 2 + (0 < delay ? 1 : 0) + (postRunnable == null ? 0 : 1);
        int index = 0;
        final Action[] sequence = new Action[nbActions];
        if (0 < delay)
            sequence[index++] = Actions.delay(delay);
        final TextCellFactory.Glyph glyph = glyph(shown, startColor, startX, startY);
        ArrayList<Integer> lightIDs = new ArrayList<>();
        MySparseLayers display = this;
        HashMap<Integer, TextCellFactory.Glyph> killList = new HashMap<>();
        sequence[index++] = Actions.parallel(
                new TemporalAction(duration) {
                    @Override
                    protected void update(float percent) {
                        glyph.setPackedColor(SColor.lerpFloatColors(startColor, endColor, percent * 0.95f));
                        int radius = (int) (Coord.get(startX, startY).distance(Coord.get(endX,endY))+1);
                        Light light = new Light(Coord.get(startX*3, startY*3), new Radiance(Math.round(radius*intensity), SColor.lerpFloatColors(glyph.getColor().toFloatBits(), SColor.WHITE_FLOAT_BITS, 0.3f),0.8f));
                        glyph.setName(light.hashCode() + " " + "0" + " temp");
                        lightHandler.addLight(light.hashCode(), light);
                        killList.put(light.hashCode(), glyph);
                        glyphs.add(glyph);
                        peCmp.addEffect(glyph, particleType, display);
                        peCmp.particleEffectsMap.get(glyph).setScale(intensity*2.5f);



                    }
                },
                Actions.moveTo(worldX(endX), worldY(endY), duration));
        if(postRunnable != null)
        {
            sequence[index++] = Actions.run(postRunnable);
        }
        *//* Do this one last, so that hasActiveAnimations() returns true during 'postRunnables' *//*
        sequence[index] = Actions.run(new Runnable() {
            @Override
            public void run() {
                glyphs.remove(glyph);

            }
        });
        glyph.addAction(Actions.sequence(sequence));
        return killList;
    }*/
}
