package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Collections;


import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import squidpony.squidai.BlastAOE;
import squidpony.squidai.Technique;
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
                case BLAST:

                    ActionEvt actionEvt = (ActionEvt) animation.sourceEvent;
                    Entity actor = getGame().getEntity(actionEvt.performerID);
                    Technique ability = (Technique) CmpMapper.getAbilityComp(actionEvt.skill,actor );
                    BlastAOE blastAOE = (BlastAOE) ability.aoe;
                    Coord center = blastAOE.getCenter();
                    LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;

                    float delay =  0.0f;
                    ArrayList<Coord> blastZone = new ArrayList(blastAOE.findArea().keySet());
                    Collections.shuffle(blastZone);
                    for(Coord coord : blastZone)
                    {
                        ArrayList<Integer> lightList = display.summonWithLight(delay,center.x, center.y, coord.x, coord.y, '*', SColor.YELLOW.toFloatBits(), SColor.SAFETY_ORANGE.toFloatBits(), 0.25f,  lightHandler, null);
                        delay = (float) (delay+(0.2/blastAOE.findArea().keySet().size()));
                        Runnable postRunnable = new Runnable() {
                            @Override
                            public void run()
                            {
                                for(Integer lightID : lightList)
                                {
                                    lightHandler.removeLight(lightID);
                                }
                            }
                        };
                        if(actionEvt.targetsDmg.isEmpty())
                        {
                            TextCellFactory.Glyph glyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, actor)).leftGlyph;
                            display.tint(0f, glyph, SColor.SAFETY_ORANGE.toFloatBits(),0.75f, postRunnable);
                        }
                        for(Integer targetID : actionEvt.targetsDmg.keySet())
                        {

                            Entity targetActor = getGame().getEntity(targetID);
                            GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, targetActor);
                            display.tint(0f, glyphsCmp.glyph, SColor.SAFETY_ORANGE.toFloatBits(),0.75f, postRunnable);
                        }
                    }
                    Light light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);
                    display.removeGlyph(animation.glyph);
                    windowCmp.lightingHandler.removeLight(light.hashCode());
                    break;

                case PROJ_MAGIC:

                    xo = animation.startLocation.x;
                    xe = animation.endLocation.x;
                    yo = animation.startLocation.y;
                    ye = animation.endLocation.y;
                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);

                    actionEvt = (ActionEvt) animation.sourceEvent;
                    SColor color = actionEvt.skill.school.color;
                    light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);

                    Entity target = getGame().getEntity(actionEvt.targetsDmg.get(0));
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

                    target = getGame().getEntity(actionEvt.targetsDmg.get(0));
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
                    target = getGame().getEntity(actionEvt.targetsDmg.get(0));
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
                    target = getGame().getEntity(actionEvt.targetsDmg.get(0));
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
        BLAST,
        SELF_BUFF,
        TINT,
        PROJ_MAGIC,
        PROJECTILE,
        MELEE_MAGIC,
        MELEE_WEAPON,
        WIGGLE
    }
}
