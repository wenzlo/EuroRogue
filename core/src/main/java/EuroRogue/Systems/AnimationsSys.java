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
import EuroRogue.Components.AICmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.ParticleEffectsCmp;
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
                    ParticleEffectsCmp performerPeaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, actor);
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


                        TextCellFactory.Glyph glyph = display.glyph(' ', SColor.LIGHT_YELLOW_DYE.toFloatBits(), center.x, center.y);
                        Light light = new Light(coord, new Radiance(3*intensity, SColor.SAFETY_ORANGE.toFloatBits()));
                        glyph.setName(light.hashCode() + " 0 " + " temp");
                        performerPeaCmp.addEffect(glyph, ParticleEffectsCmp.ParticleEffect.FIRE_P, display);
                        performerPeaCmp.particleEffectsMap.get(glyph).get(ParticleEffectsCmp.ParticleEffect.FIRE_P).setScale(intensity*1.5f);
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
                                    performerPeaCmp.removeEffect(killList.get(lightID), ParticleEffectsCmp.ParticleEffect.FIRE_P, display);
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
                    performerPeaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, actor);
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
                        performerPeaCmp.addEffect(glyph, ParticleEffectsCmp.ParticleEffect.ICE_P, display);
                        performerPeaCmp.particleEffectsMap.get(glyph).get(ParticleEffectsCmp.ParticleEffect.ICE_P).setScale(1.2f);


                        Light finalLight3 = light;
                        TextCellFactory.Glyph finalGlyph = glyph;
                        postRunnable = new Runnable() {
                            @Override
                            public void run()
                            {

                                    lightHandler.removeLight(finalLight3.hashCode());
                                    performerPeaCmp.removeEffect(finalGlyph, ParticleEffectsCmp.ParticleEffect.ICE_P, display);
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
                    actor = getGame().getEntity(actionEvt.performerID);
                    AICmp aiCmp = (AICmp)CmpMapper.getComp(CmpType.AI, actor);
                    Entity target = getGame().getEntity(aiCmp.target);
                    TextCellFactory.Glyph targetGlyph = null;
                    if(target!=null) targetGlyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, target)).glyph;

                    final TextCellFactory.Glyph finalTargetGlyph = targetGlyph;

                    performerPeaCmp = (ParticleEffectsCmp)CmpMapper.getComp(CmpType.PARTICLES, actor);
                    ParticleEffectsCmp targetPeaCmp = (ParticleEffectsCmp)CmpMapper.getComp(CmpType.PARTICLES, target);

                    Light finalLight = light;

                    Runnable burst = () -> {
                        performerPeaCmp.removeEffect(animation.glyph, ParticleEffectsCmp.ParticleEffect.ARCANE_P, display);
                        windowCmp.lightingHandler.removeLight(finalLight.hashCode());
                        targetPeaCmp.addEffect(finalTargetGlyph, ParticleEffectsCmp.ParticleEffect.ARCANE_DMG, display);
                        display.removeGlyph(animation.glyph);

                        //display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.18f, null);

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

                    //finalTargetGlyph = targetGlyph;

                    burst = () -> {
                        display.removeGlyph(animation.glyph);
                        display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.18f, null);


                    };

                    display.slide(0f, animation.glyph, xe, ye, 0.18f, burst);

                    break;
                case ICE_SHIELD:

                    xe = animation.endLocation.x;

                    ye = animation.endLocation.y;
                    actionEvt = (ActionEvt) animation.sourceEvent;
                    target = getGame().getEntity(levelCmp.actors.get((Integer) ((ActionEvt) animation.sourceEvent).targetsDmg.keySet().toArray()[0]));
                    if(target==null) break;
                    targetPeaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, target);
                    GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, target);

                    postRunnable = new Runnable() {
                        @Override
                        public void run() {
                            targetPeaCmp.addEffect(glyphsCmp.glyph, ParticleEffectsCmp.ParticleEffect.ICE_DMG, display);

                            windowCmp.lightingHandler.removeLightByGlyph(animation.glyph);


                            display.removeGlyph(animation.glyph);
                        }
                    };

                    display.slide(0.3f, animation.glyph, xe, ye,0.03f, postRunnable);


                    break;

                case MELEE_ARCANE:

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
                    performerPeaCmp = (ParticleEffectsCmp)CmpMapper.getComp(CmpType.PARTICLES, actor);
                    targetPeaCmp = (ParticleEffectsCmp)CmpMapper.getComp(CmpType.PARTICLES, target);
                    TextCellFactory.Glyph finalTargetGlyph2 = targetGlyph;

                    Light finalLight1 = light;
                    burst = () -> {

                        performerPeaCmp.removeEffect(animation.glyph, ParticleEffectsCmp.ParticleEffect.ARCANE_P, display);
                        windowCmp.lightingHandler.removeLight(finalLight1.hashCode());
                        targetPeaCmp.addEffect(finalTargetGlyph2, ParticleEffectsCmp.ParticleEffect.ARCANE_DMG, display);
                        display.removeGlyph(animation.glyph);

                        //display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.14f, null);

                    };

                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);
                    display.slide(0f, animation.glyph, xe, ye, 0.12f, burst);


                    break;
                case MELEE_ICE:

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
                    performerPeaCmp = (ParticleEffectsCmp)CmpMapper.getComp(CmpType.PARTICLES, actor);
                    targetPeaCmp = (ParticleEffectsCmp)CmpMapper.getComp(CmpType.PARTICLES, target);
                    TextCellFactory.Glyph finalTargetGlyph3 = targetGlyph;

                    finalLight1 = light;
                    burst = () -> {
                        performerPeaCmp.removeEffect(animation.glyph, ParticleEffectsCmp.ParticleEffect.ICE_P, display);
                        windowCmp.lightingHandler.removeLight(finalLight1.hashCode());
                        targetPeaCmp.addEffect(finalTargetGlyph3, ParticleEffectsCmp.ParticleEffect.ICE_DMG, display);
                        display.removeGlyph(animation.glyph);
                        //display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.14f, null);

                    };

                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);
                    display.slide(0f, animation.glyph, xe, ye, 0.12f, burst);


                    break;
                case MELEE_FIRE:

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
                    performerPeaCmp = (ParticleEffectsCmp)CmpMapper.getComp(CmpType.PARTICLES, actor);
                    targetPeaCmp = (ParticleEffectsCmp)CmpMapper.getComp(CmpType.PARTICLES, target);
                    TextCellFactory.Glyph finalTargetGlyph4 = targetGlyph;

                    finalLight1 = light;
                    burst = () -> {
                        performerPeaCmp.removeEffect(animation.glyph, ParticleEffectsCmp.ParticleEffect.FIRE_P, display);
                        windowCmp.lightingHandler.removeLight(finalLight1.hashCode());
                        targetPeaCmp.addEffect(finalTargetGlyph4, ParticleEffectsCmp.ParticleEffect.FIRE_DMG, display);
                        display.removeGlyph(animation.glyph);
                    };

                    direction = Direction.toGoTo(animation.startLocation, animation.endLocation);
                    display.slide(0f, animation.glyph, xe, ye, 0.12f, burst);


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

                    TextCellFactory.Glyph finalTargetGlyph5 = targetGlyph;

                    Light finalLight2 = light;
                    burst = () -> {
                        display.removeGlyph(animation.glyph);
                        windowCmp.lightingHandler.removeLight(finalLight2.hashCode());
                        //display.burst(0,xe,ye,1, Radius.CIRCLE, '.', color.toFloatBits(), color.toFloatBits(),0.14f, null);

                        if(finalTargetGlyph5 !=null)
                        {
                            display.tint(0,finalTargetGlyph5, color.toFloatBits(), 0.10f, null );
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
        MELEE_ARCANE,
        MELEE_FIRE,
        MELEE_ICE,
        ICE_SHIELD,
        MELEE_WEAPON,
        WIGGLE
    }
}
