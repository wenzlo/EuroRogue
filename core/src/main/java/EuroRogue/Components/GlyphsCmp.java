package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.MySparseLayers;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;

public class GlyphsCmp implements Component
{
    public TextCellFactory.Glyph glyph;
    public TextCellFactory.Glyph leftGlyph;
    public TextCellFactory.Glyph rightGlyph;


    public GlyphsCmp(){}
    public GlyphsCmp(MySparseLayers mySparseLayers, char chr, SColor color, int x, int y)
    {
        this.glyph = mySparseLayers.glyph(chr, color, x,y);
    }
    public void setVisibility(boolean bool)
    {
        glyph.setVisible(bool);
        if(leftGlyph!=null) leftGlyph.setVisible(bool);
        if(rightGlyph!=null) rightGlyph.setVisible(bool);
    }
    public float getLeftGlyphPositionX(MySparseLayers display, PositionCmp positionCmp)
    {
        Direction orientation = positionCmp.orientation;
        float worldX = display.worldX(positionCmp.coord.x);
        switch (orientation)
        {
            case RIGHT:
                return worldX+5;
            case UP:
                return worldX-8;
            case DOWN:
                return worldX+8;
            case LEFT:
                return worldX-5;

            case UP_LEFT:
                return worldX-9;
            case UP_RIGHT:
                return worldX-1;

            case DOWN_LEFT:
                return worldX;
            case DOWN_RIGHT:
                return worldX+10;

        }
        return 0;
    }
    public float getLeftGlyphPositionY(MySparseLayers display, PositionCmp positionCmp)
    {
        Direction orientation = positionCmp.orientation;
        float worldY = display.worldY(positionCmp.coord.y);
        switch (orientation)
        {
            case RIGHT:
                return worldY+9;
            case UP:
                return worldY+5;
            case DOWN:
                return worldY-5;
            case LEFT:
                return worldY-8;

            case UP_LEFT:
                return worldY-1;
            case UP_RIGHT:
                return worldY+11;

            case DOWN_LEFT:
                return worldY-10;
            case DOWN_RIGHT:
                return worldY+1;

        }
        return 0;
    }
    public float getRightGlyphPositionX(MySparseLayers display, PositionCmp positionCmp)
    {
        Direction orientation = positionCmp.orientation;
        float worldX = display.worldX(positionCmp.coord.x);
        switch (orientation)
        {
            case RIGHT:
                return worldX+5;
            case UP:
                return worldX+8;
            case DOWN:
                return worldX-8;
            case LEFT:
                return worldX-5;

            case UP_LEFT:
                return worldX+1;
            case UP_RIGHT:
                return worldX+10;

            case DOWN_LEFT:
                return worldX-10;
            case DOWN_RIGHT:
                return worldX-1;

        }
        return 0;
    }
    public float getRightGlyphPositionY(MySparseLayers display, PositionCmp positionCmp)
    {
        Direction orientation = positionCmp.orientation;
        float worldY = display.worldY(positionCmp.coord.y);
        switch (orientation)
        {
            case RIGHT:
                return worldY-8;
            case UP:
                return worldY+5;
            case DOWN:
                return worldY-5;
            case LEFT:
                return worldY+8;

            case UP_LEFT:
                return worldY+11;
            case UP_RIGHT:
                return worldY-1;

            case DOWN_LEFT:
                return worldY;
            case DOWN_RIGHT:
                return worldY-10;

        }
        return 0;
    }
}
