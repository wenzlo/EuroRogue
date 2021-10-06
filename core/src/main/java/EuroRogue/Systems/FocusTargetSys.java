package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class FocusTargetSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public FocusTargetSys()
    {
        super.priority=0;
    }


    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(FocusTargetCmp.class).get());
    }

    @Override
    public void update(float deltaTime)
    {
        if(entities.size()==0) return;
        MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW,getGame().dungeonWindow)).display;

        Entity focusTarget = entities.get(0);
        GlyphsCmp ftGlyph = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH,focusTarget);
        FocusTargetCmp ftc = (FocusTargetCmp) CmpMapper.getComp(CmpType.FOCUS_TARGET,focusTarget);
        Coord location = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, focusTarget)).coord;
        if(ftc.indicatorGlyph2==null) ftc.indicatorGlyph2=display.glyph('└', SColor.AURORA_LIGHT_SKIN_5,location.x, location.y);
        else ftc.indicatorGlyph2.setPosition(ftGlyph.glyph.getX()-14, ftGlyph.glyph.getY()-14);

        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, getGame().getFocus());
        aiCmp.target = focusTarget.hashCode();
    }

}
