package EuroRogue.EventComponents;

import com.badlogic.gdx.scenes.scene2d.ui.ParticleEffectActor;

import EuroRogue.Systems.AnimationsSys;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class AnimateGlyphEvt implements IEventComponent
{
    public boolean processed = false;
    public TextCellFactory.Glyph glyph;
    public AnimationsSys.AnimationType animationType;
    public Coord startLocation, endLocation;
    public IEventComponent sourceEvent;
    public ParticleEffectActor pea;

    public AnimateGlyphEvt(TextCellFactory.Glyph glyph, AnimationsSys.AnimationType animationType,
                           Coord startLocation, Coord endLocation, IEventComponent sourceEvent)
    {
        this.glyph=glyph;
        this.animationType=animationType;
        this.startLocation=startLocation;
        this.endLocation=endLocation;
        this.sourceEvent=sourceEvent;
    }

    public AnimateGlyphEvt(TextCellFactory.Glyph glyph, AnimationsSys.AnimationType animationType,
                           Coord startLocation, Coord endLocation, IEventComponent sourceEvent, ParticleEffectActor pea)
    {
        this.glyph=glyph;
        this.animationType=animationType;
        this.startLocation=startLocation;
        this.endLocation=endLocation;
        this.sourceEvent=sourceEvent;
        this.pea = pea;
    }


    public AnimateGlyphEvt(TextCellFactory.Glyph glyph, AnimationsSys.AnimationType animationType, IEventComponent sourceEvent)
    {
        this.glyph=glyph;
        this.animationType=animationType;
        this.startLocation=null;
        this.endLocation=null;
        this.sourceEvent=sourceEvent;
    }


    @Override
    public boolean isProcessed() { return processed; }

    @Override
    public void setProcessed(boolean bool) {
        processed = bool;
    }
}
