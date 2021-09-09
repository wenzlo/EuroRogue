package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.Light;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class AnimationsSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public AnimationsSys()
    {
        super.priority=2;
    }

    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(AnimateGlyphEvt.class).get());
    }

    @Override
    public void update(float deltaTime)
    {
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        MySparseLayers display = (windowCmp).display;

        for(Entity entity:entities)
        {
            AnimateGlyphEvt animation = (AnimateGlyphEvt) CmpMapper.getComp(CmpType.ANIM_GLYPH_EVT, entity);
            Coord entityLoc = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
            animation.processed=true;

            /*String[] splitName = StringKit.split(animation.glyph.getName(), " ");
            String type = splitName[2];
            if (type=="temp")
            {
                postRunnable = () -> display.glyphs.remove(animation.glyph);
            }*/

            switch (animation.animationType)
            {
                case BUMP:
                    int xo = animation.startLocation.x;
                    int xe = animation.endLocation.x;
                    int yo = animation.startLocation.y;
                    int ye = animation.endLocation.y;
                    Direction direction = Direction.toGoTo(animation.startLocation, animation.endLocation);
                    display.bump(0, animation.glyph, direction, 0.12f, null);
                    break;

                case SLIDE:
                    xe = animation.endLocation.x;
                    ye = animation.endLocation.y;
                    display.slide(0f, animation.glyph, xe, ye, 0.18f, null);
                    break;

                case BURST:
                case TINT:
                    break;

                case PROJ_MAGIC:

                    xo = animation.startLocation.x;
                    xe = animation.endLocation.x;
                    yo = animation.startLocation.y;
                    ye = animation.endLocation.y;
                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);

                    ActionEvt actionEvt = (ActionEvt) animation.sourceEvent;
                    SColor color = actionEvt.skill.school.color;
                    Light light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);

                    Entity target = getGame().getEntity(actionEvt.targetID);
                    TextCellFactory.Glyph targetGlyph = null;
                    if(target!=null) targetGlyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, target)).glyph;

                    TextCellFactory.Glyph finalTargetGlyph = targetGlyph;

                    Runnable burst = () -> {
                        display.removeGlyph(animation.glyph);
                        windowCmp.lightingHandler.removeLight(light.hashCode());
                        display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.18f, null);

                        if(finalTargetGlyph !=null)
                        {
                            display.tint(0,finalTargetGlyph, color.toFloatBits(), 0.10f, null );
                        }


                    };

                    display.slide(0f, animation.glyph, xe, ye, 0.18f, burst);

                    break;

                case PROJECTILE:

                    xo = animation.startLocation.x;
                    xe = animation.endLocation.x;
                    yo = animation.startLocation.y;
                    ye = animation.endLocation.y;
                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);

                    actionEvt = (ActionEvt) animation.sourceEvent;
                    color = actionEvt.skill.school.color;

                    target = getGame().getEntity(actionEvt.targetID);
                    targetGlyph = null;
                    if(target!=null) targetGlyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, target)).glyph;

                    finalTargetGlyph = targetGlyph;

                    burst = () -> {
                        display.removeGlyph(animation.glyph);
                        display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.18f, null);

                        if(finalTargetGlyph !=null)
                        {
                            display.tint(0,finalTargetGlyph, color.toFloatBits(), 0.10f, null );
                        }


                    };

                    display.slide(0f, animation.glyph, xe, ye, 0.18f, burst);

                    break;

                case MELEE_MAGIC:

                    xe = animation.endLocation.x;

                    ye = animation.endLocation.y;
                    actionEvt = (ActionEvt) animation.sourceEvent;
                    color = actionEvt.skill.school.color;
                    target = getGame().getEntity(actionEvt.targetID);
                    targetGlyph = null;
                    if(target!=null) targetGlyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, target)).glyph;
                    light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);

                    finalTargetGlyph = targetGlyph;
                    burst = () -> {
                        display.removeGlyph(animation.glyph);
                        windowCmp.lightingHandler.removeLight(light.hashCode());
                        display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.14f, null);

                        if(finalTargetGlyph !=null)
                        {
                            display.tint(0,finalTargetGlyph, color.toFloatBits(), 0.10f, null );
                        }

                    };

                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);
                    display.bump(0, animation.glyph, direction, 0.12f, burst);
                    break;
                case SELF_BUFF:
                    actionEvt = (ActionEvt) animation.sourceEvent;
                    display.tint(animation.glyph, actionEvt.skill.school.color.toFloatBits(), 0.22f);
                    display.wiggle(animation.glyph, 0.18f);
                    break;
                case MELEE_WEAPON:
                    xe = animation.endLocation.x;
                    ye = animation.endLocation.y;
                    actionEvt = (ActionEvt) animation.sourceEvent;
                    color = actionEvt.skill.school.color;
                    target = getGame().getEntity(actionEvt.targetID);
                    targetGlyph = null;
                    if(target!=null) targetGlyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, target)).glyph;
                    light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);

                    finalTargetGlyph = targetGlyph;

                    burst = () -> {
                        display.removeGlyph(animation.glyph);
                        windowCmp.lightingHandler.removeLight(light.hashCode());
                        //display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.14f, null);

                        if(finalTargetGlyph !=null)
                        {
                            display.tint(0,finalTargetGlyph, color.toFloatBits(), 0.10f, null );
                        }

                    };

                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);
                    display.bump(0, animation.glyph, direction, 0.12f, burst);
                    break;
                case WIGGLE:
                    display.wiggle(animation.glyph, 0.12f);
                    break;
            }
        }
    }

    public enum AnimationType
    {
        SLIDE,
        BUMP,
        BURST,
        SELF_BUFF,
        TINT,
        PROJ_MAGIC,
        PROJECTILE,
        MELEE_MAGIC,
        MELEE_WEAPON,
        WIGGLE
    }
}
