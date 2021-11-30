package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import EuroRogue.MySparseLayers;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
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
    public GlyphsCmp(MySparseLayers mySparseLayers, char chr, char leftChr, char rightChr, SColor color, int x, int y)
    {
        this.glyph = mySparseLayers.glyph(chr, color, x,y);
        this.leftGlyph = mySparseLayers.glyph(leftChr, color, x,y);
        this.rightGlyph = mySparseLayers.glyph(rightChr, color, x,y);

    }
    public void setVisibility(boolean bool)
    {
        glyph.setVisible(bool);
        if(leftGlyph!=null) leftGlyph.setVisible(bool);
        if(rightGlyph!=null) rightGlyph.setVisible(bool);
    }
    public float getLeftGlyphPositionX(SparseLayers display, PositionCmp positionCmp)
    {
        Direction orientation = positionCmp.orientation;
        float worldX = display.worldX(positionCmp.coord.x);
        switch (orientation)
        {
            case RIGHT:
                if(leftGlyph.shown==',') return worldX+6;
                else return worldX+6;
            case UP:
                if(leftGlyph.shown==',') return worldX-9;
                else return worldX-9;
            case DOWN:
                if(leftGlyph.shown==',') return worldX+6;
                else return worldX+9;
            case LEFT:
                if(leftGlyph.shown==',') return worldX-8;
                else return worldX-6;

            case UP_LEFT:
                if(leftGlyph.shown==',') return worldX-10;
                else return worldX-10;
            case UP_RIGHT:
                if(leftGlyph.shown==',') return worldX-1;
                else return worldX-2;

            case DOWN_LEFT:
                if(leftGlyph.shown==',') return worldX+2;
                else return worldX;
            case DOWN_RIGHT:
                if(leftGlyph.shown==',') return worldX + 7;
                else return worldX + 11;

        }
        return worldX;
    }
    public float getLeftGlyphPositionY(SparseLayers display, PositionCmp positionCmp)
    {
        Direction orientation = positionCmp.orientation;
        float worldY = display.worldY(positionCmp.coord.y);
        switch (orientation)
        {
            case RIGHT:
                if(leftGlyph.shown==',') return worldY+10;
                else return worldY+10;
            case UP:
                if(leftGlyph.shown==',') return worldY+12;
                else return worldY+6;
            case DOWN:
                if(leftGlyph.shown==',') return worldY-1;
                else return worldY-6;
            case LEFT:
                if(leftGlyph.shown==',') return worldY-2;
                else return worldY-9;

            case UP_LEFT:
                if(leftGlyph.shown==',') return worldY+10;
                else return worldY-2;
            case UP_RIGHT:
                if(leftGlyph.shown==',') return worldY+17;
                else return worldY+12;

            case DOWN_LEFT:
                if(leftGlyph.shown==',') return worldY-3;
                else return worldY-11;
            case DOWN_RIGHT:
                if(leftGlyph.shown==',') return worldY+6;
                else return worldY+2;

        }
        return worldY;
    }
    public float getRightGlyphPositionX(SparseLayers display, PositionCmp positionCmp)
    {
        Direction orientation = positionCmp.orientation;
        float worldX = display.worldX(positionCmp.coord.x);
        switch (orientation)
        {
            case RIGHT:
                if(leftGlyph.shown==',') return worldX+6;
                else return worldX+6;
            case UP:
                if(leftGlyph.shown==',') return worldX+6;
                else return worldX+9;
            case DOWN:
                if(leftGlyph.shown==',') return worldX-8;
                else return worldX-9;
            case LEFT:
                if(leftGlyph.shown==',') return worldX-8;
                else return worldX-6;
            case UP_LEFT:
                if(leftGlyph.shown==',') return worldX+1;
                else return worldX+2;
            case UP_RIGHT:
                if(leftGlyph.shown==',') return worldX+4;
                else return worldX+11;
            case DOWN_LEFT:
                if(leftGlyph.shown==',') return worldX-8;
                else return worldX-11;
            case DOWN_RIGHT:
                if(leftGlyph.shown==',') return worldX-1;
                else return worldX-2;

        }
        return worldX;
    }
    public float getRightGlyphPositionY(SparseLayers display, PositionCmp positionCmp)
    {
        Direction orientation = positionCmp.orientation;
        float worldY = display.worldY(positionCmp.coord.y);
        switch (orientation)
        {
            case RIGHT:
                if(leftGlyph.shown==',') return worldY-2;
                else return worldY-9;
            case UP:
                if(leftGlyph.shown==',') return worldY+12;
                else return worldY+6;
            case DOWN:
                if(leftGlyph.shown==',') return worldY-1;
                else return worldY-6;
            case LEFT:
                if(leftGlyph.shown==',') return worldY+15;
                else return worldY+9;

            case UP_LEFT:
                if(leftGlyph.shown==',') return worldY+17;
                else return worldY+12;
            case UP_RIGHT:
                if(leftGlyph.shown==',') return worldY+8;
                else return worldY-2;

            case DOWN_LEFT:
                if(leftGlyph.shown==',') return worldY+6;
                else return worldY;
            case DOWN_RIGHT:
                if(leftGlyph.shown==',') return worldY-2;
                else return worldY-11;

        }
        return worldY;
    }
}
