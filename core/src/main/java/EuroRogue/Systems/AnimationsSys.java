package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.ParticleEmittersCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.SortByDistance;
import squidpony.StringKit;
import squidpony.squidai.BlastAOE;
import squidpony.squidai.ConeAOE;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.Radiance;
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
        MySparseLayers display = (MySparseLayers) windowCmp.display;
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        for(Entity entity:entities)
        {
            AnimateGlyphEvt animation = (AnimateGlyphEvt) CmpMapper.getComp(CmpType.ANIM_GLYPH_EVT, entity);
            if(animation.glyph.hasActions())continue;
            animation.processed=true;

            String[] splitName = StringKit.split(animation.glyph.getName(), " ");
            String type = splitName[2];
            Runnable postRunnable = null;
            if (type=="temp")
            {
                postRunnable = () -> display.glyphs.remove(animation.glyph);
            }

            switch (animation.animationType)
            {
                case BUMP:
                    int xo = animation.startLocation.x;
                    int xe = animation.endLocation.x;
                    int yo = animation.startLocation.y;
                    int ye = animation.endLocation.y;
                    Direction direction = Direction.toGoTo(animation.startLocation, animation.endLocation);
                    display.bump(0, animation.glyph, direction, 0.12f, postRunnable);
                    break;

                case SLIDE:
                    xe = animation.endLocation.x;
                    ye = animation.endLocation.y;
                    display.slide(0f, animation.glyph, xe, ye, 0.18f, postRunnable);
                    break;

                case OFFSET_SLIDE:
                    xe = animation.endLocation.x;
                    ye = animation.endLocation.y;
                    display.slide(0f, animation.glyph, (float)xe, (float)ye, 0.18f, postRunnable);
                    break;

                case BURST:

                case TINT:
                    break;
                case BLAST:
                    ActionEvt actionEvt = (ActionEvt) animation.sourceEvent;
                    Entity actor = getGame().getEntity(actionEvt.performerID);
                    Entity scroll = getGame().getEntity(actionEvt.scrollID);
                    Ability ability = CmpMapper.getAbilityComp(actionEvt.skill,actor );
                    ParticleEmittersCmp peCmp = (ParticleEmittersCmp) CmpMapper.getComp(CmpType.PARTICLES, actor);
                    if(scroll!=null) ability = CmpMapper.getAbilityComp(actionEvt.skill,scroll );
                    BlastAOE blastAOE = (BlastAOE) ability.aoe;
                    Coord center = blastAOE.getCenter();
                    LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;

                    float delay =  0.0f;
                    ArrayList<Coord> blastZone = new ArrayList(blastAOE.findArea().keySet());
                    Collections.shuffle(blastZone);
                    HashMap<Integer, TextCellFactory.Glyph> killList = new HashMap<>();
                    for(Coord coord : blastZone)
                    {
                        float intensity = (float) (blastAOE.findArea().get(coord)*1f);


                        TextCellFactory.Glyph glyph = display.glyph('*', SColor.LIGHT_YELLOW_DYE.toFloatBits(), center.x, center.y);
                        Light light = new Light(coord, new Radiance(3*intensity, SColor.SAFETY_ORANGE.toFloatBits()));
                        glyph.setName(light.hashCode() + " 0 " + " temp");
                        peCmp.addEffect(glyph, ParticleEmittersCmp.ParticleEffect.FIRE_P, display);
                        peCmp.particleEffectsMap.get(glyph).setScale(intensity*1.5f);
                        killList.put(light.hashCode(), glyph);
                        display.slide(delay, glyph, coord.x, coord.y, 0.2f, null);
                        delay = (float) (delay+(0.2/blastAOE.findArea().keySet().size()));
                        postRunnable = new Runnable() {
                            @Override
                            public void run()
                            {
                                for(Integer lightID : killList.keySet())
                                {
                                    lightHandler.removeLight(lightID);
                                    peCmp.removeEffect(killList.get(lightID), display);
                                    display.glyphs.remove(killList.get(lightID));


                                }
                            }
                        };
                        if(actionEvt.targetsDmg.isEmpty())
                        {
                            glyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, actor)).leftGlyph;
                            display.tint(0f, glyph, ability.getSkill().school.color.toFloatBits(),0.75f, postRunnable);
                        }
                        for(Integer targetID : actionEvt.targetsDmg.keySet())
                        {

                            Entity targetActor = getGame().getEntity(targetID);
                            GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, targetActor);
                            display.tint(0f, glyphsCmp.glyph, ability.getSkill().school.color.toFloatBits(),0.75f, postRunnable);
                        }
                    }
                    Light light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);
                    display.removeGlyph(animation.glyph);
                    windowCmp.lightingHandler.removeLight(light.hashCode());
                    break;
                case SHATTER:

                    actionEvt = (ActionEvt) animation.sourceEvent;
                    actor = getGame().getEntity(actionEvt.performerID);
                    ability = CmpMapper.getAbilityComp(actionEvt.skill,actor );
                    blastAOE = (BlastAOE) ability.aoe;
                    lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;

                    delay =  0.0f;
                    blastZone = new ArrayList(blastAOE.findArea().keySet());
                    Collections.sort(blastZone, new SortByDistance(blastAOE.getCenter()));
                    for(Coord coord : blastZone)
                    {

                        display.tint(delay,coord.x,coord.y,ability.getSkill().school.color.toFloatBits(), 0.1f, null);
                        delay = (float) (delay+(0.1/blastAOE.findArea().keySet().size()));
                        /*ArrayList<Integer> lightList = display.summonWithLight(delay,coord.x, coord.y, coord.x, coord.y, ' ', SColor.WHITE.toFloatBits(), SColor.BABY_BLUE.toFloatBits(), 0.1f,  lightHandler, null);
                        delay = (float) (delay+(0.1/blastAOE.findArea().keySet().size()));
                        Runnable postRunnable = new Runnable() {
                            @Override
                            public void run()
                            {
                                for(Integer lightID : lightList)
                                {
                                    lightHandler.removeLight(lightID);
                                }
                            }
                        };*/
                        if(actionEvt.targetsDmg.isEmpty())
                        {
                            TextCellFactory.Glyph glyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, actor)).leftGlyph;
                            display.tint(0f, glyph, ability.getSkill().school.color.toFloatBits(),0.75f, postRunnable);
                        }
                        for(Integer targetID : actionEvt.targetsDmg.keySet())
                        {

                            Entity targetActor = getGame().getEntity(targetID);
                            GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, targetActor);
                            display.tint(0f, glyphsCmp.glyph, ability.getSkill().school.color.toFloatBits(),0.75f, postRunnable);
                        }
                    }
                    light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);
                    display.removeGlyph(animation.glyph);
                    windowCmp.lightingHandler.removeLight(light.hashCode());
                    break;
                case CONE_OF_COLD:
                    actionEvt = (ActionEvt) animation.sourceEvent;
                    actor = getGame().getEntity(actionEvt.performerID);
                    scroll = getGame().getEntity(actionEvt.scrollID);
                    ability = CmpMapper.getAbilityComp(actionEvt.skill,actor );
                    peCmp = (ParticleEmittersCmp) CmpMapper.getComp(CmpType.PARTICLES, actor);
                    if(scroll!=null) ability = CmpMapper.getAbilityComp(actionEvt.skill,scroll );
                    ConeAOE coneAOE = (ConeAOE) ability.aoe;
                    center = coneAOE.getOrigin();
                    lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;

                    delay =  0.0f;
                    blastZone = new ArrayList(coneAOE.findArea().keySet());
                    Collections.sort(blastZone, new SortByDistance(center));
                    blastZone.remove(0);
                    for(Coord coord : blastZone)
                    {
                        TextCellFactory.Glyph glyph = display.glyph('*', SColor.WHITE.toFloatBits(), center.x, center.y);
                        light = new Light(coord, new Radiance(3, SColor.BABY_BLUE.toFloatBits()));
                        glyph.setName(light.hashCode() + " 0 " + " temp");
                        peCmp.addEffect(glyph, ParticleEmittersCmp.ParticleEffect.ICE_P, display);
                        peCmp.particleEffectsMap.get(glyph).setScale(1.2f);


                        Light finalLight3 = light;
                        TextCellFactory.Glyph finalGlyph = glyph;
                        postRunnable = new Runnable() {
                            @Override
                            public void run()
                            {

                                    lightHandler.removeLight(finalLight3.hashCode());
                                    peCmp.removeEffect(finalGlyph, display);
                                    display.glyphs.remove(finalGlyph);


                            }
                        };
                        delay = (float) (delay+(0.2/coneAOE.findArea().keySet().size()));
                        display.slide(delay, glyph, coord.x, coord.y, 0.2f, postRunnable);
                    }
                    light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);
                    display.removeGlyph(animation.glyph);
                    windowCmp.lightingHandler.removeLight(light.hashCode());
                    break;

                    /*actionEvt = (ActionEvt) animation.sourceEvent;
                    actor = getGame().getEntity(actionEvt.performerID);
                    ability = CmpMapper.getAbilityComp(actionEvt.skill,actor );
                    ConeAOE coneAOE = (ConeAOE) ability.aoe;
                    center = coneAOE.getOrigin();
                    lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;

                    delay =  0.0f;
                    blastZone = new ArrayList(coneAOE.findArea().keySet());
                    Collections.shuffle(blastZone);
                    for(Coord coord : blastZone)
                    {
                        ArrayList<Integer> lightList = display.summonWithLight(delay,center.x, center.y, coord.x, coord.y, '*', SColor.WHITE.toFloatBits(), SColor.BABY_BLUE.toFloatBits(), 0.2f,  lightHandler, null);
                        delay = (float) (delay+(0.2/coneAOE.findArea().keySet().size()));
                        postRunnable = new Runnable() {
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
                            display.tint(0f, glyph, ability.getSkill().school.color.toFloatBits(),0.75f, postRunnable);
                        }
                        for(Integer targetID : actionEvt.targetsDmg.keySet())
                        {

                            Entity targetActor = getGame().getEntity(targetID);
                            GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, targetActor);
                            display.tint(0f, glyphsCmp.glyph, ability.getSkill().school.color.toFloatBits(),0.75f, postRunnable);
                        }
                    }
                    light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);
                    display.removeGlyph(animation.glyph);
                    windowCmp.lightingHandler.removeLight(light.hashCode());
                    break;*/

                case PROJ_MAGIC:

                    xo = animation.startLocation.x;
                    xe = animation.endLocation.x;
                    yo = animation.startLocation.y;
                    ye = animation.endLocation.y;
                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);
                    actionEvt = (ActionEvt) animation.sourceEvent;


                    actionEvt = (ActionEvt) animation.sourceEvent;
                    SColor color = actionEvt.skill.school.color;
                    light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);

                    Entity target = getGame().getEntity(actionEvt.targetsDmg.get(0));
                    TextCellFactory.Glyph targetGlyph = null;
                    if(target!=null) targetGlyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, target)).glyph;

                    TextCellFactory.Glyph finalTargetGlyph = targetGlyph;
                    actor = getGame().getEntity(actionEvt.performerID);
                    peCmp = (ParticleEmittersCmp)CmpMapper.getComp(CmpType.PARTICLES, actor);

                    Light finalLight = light;
                    Runnable burst = () -> {
                        animation.pea.dispose();
                        display.removeGlyph(animation.glyph);
                        peCmp.removeEffect(animation.glyph, display);
                        windowCmp.lightingHandler.removeLight(finalLight.hashCode());

                        display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.18f, null);

                    };

                    display.slide(0f, animation.glyph, xe, ye, 0.25f, burst);

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
                    if(target==null) target = getGame().getEntity(levelCmp.actors.get(animation.endLocation));
                    targetGlyph = null;
                    if(target!=null) targetGlyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, target)).glyph;
                    light = windowCmp.lightingHandler.getLightByGlyph(animation.glyph);
                    actor = getGame().getEntity(actionEvt.performerID);
                    peCmp = (ParticleEmittersCmp)CmpMapper.getComp(CmpType.PARTICLES, actor);

                    Light finalLight1 = light;
                    burst = () -> {
                        display.removeGlyph(animation.glyph);
                        peCmp.removeEffect(animation.glyph, display);
                        windowCmp.lightingHandler.removeLight(finalLight1.hashCode());
                        display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.14f, null);

                    };

                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);
                    display.bump(0, animation.glyph, direction, 0.12f, burst);


                    break;
                case SELF_BUFF:
                    actionEvt = (ActionEvt) animation.sourceEvent;
                    display.tint(animation.glyph, actionEvt.skill.school.color.toFloatBits(), 0.22f);
                    display.wiggle(0f, animation.glyph, 0.18f, postRunnable);
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

                    Light finalLight2 = light;
                    burst = () -> {
                        display.removeGlyph(animation.glyph);
                        windowCmp.lightingHandler.removeLight(finalLight2.hashCode());
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
        OFFSET_SLIDE,
        CONE_OF_COLD,
        BUMP,
        BURST,
        BLAST,
        SHATTER,
        SELF_BUFF,
        TINT,
        PROJ_MAGIC,
        PROJECTILE,
        MELEE_MAGIC,
        MELEE_WEAPON,
        WIGGLE
    }
}
