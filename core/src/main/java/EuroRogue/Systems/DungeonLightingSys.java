package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;

import java.util.Iterator;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.LightCmpTemp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.GameState;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.MyEntitySystem;
import EuroRogue.MyFOV;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.StringKit;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.Noise;
import squidpony.squidmath.WhirlingNoise;

public class DungeonLightingSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private GWTRNG rng;



    public DungeonLightingSys()
    {
        super.priority = 9;
        this.rng = new GWTRNG();
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(LightingCmp.class, LevelCmp.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        updateLighting();
    }

    public void updateLighting()
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        if(levelCmp==null) return;
        if(getGame().gameState== GameState.STARTING) return;
        if(getGame().gameState== GameState.CAMPING) return;
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        MySparseLayers display = windowCmp.display;
        LightHandler lightingHandler = windowCmp.lightingHandler;
        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);

        StatsCmp focusStatsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, getGame().getFocus());

        Iterator<TextCellFactory.Glyph> glyphs = display.glyphs.iterator();
        while (glyphs.hasNext())
        {
            TextCellFactory.Glyph glyph = glyphs.next();

            if (glyph.getName() != null)
            {
                String[] splitName = StringKit.split(glyph.getName(), " ");
                Integer lightID =  Integer.parseInt(splitName[0]);
                Integer ownerID =  Integer.parseInt(splitName[1]);
                String type = splitName[2];
                Coord lightMapPos = Coord.get(lightingHandler.lightMapX(glyph), lightingHandler.lightMapY(glyph));
                Entity owner= getGame().getEntity(ownerID);
                Light light = lightingHandler.lightList.get(lightID);

                if(owner!=null)
                {
                    InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, owner);
                    if(owner==getGame().getFocus())
                    {
                        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, getGame().getFocus());
                        Coord fovPos = Coord.get(lightingHandler.lightMapX(glyphsCmp.glyph), lightingHandler.lightMapY(glyphsCmp.glyph));
                        lightingHandler.calculateFOV(fovPos.x, fovPos.y);
                        MyFOV.reuseFOV(lightingCmp.resistance3x3, lightingCmp.focusNightVision3x3, fovPos.x, fovPos.y, focusStatsCmp.getPerc()*3);
                    }
                    if("actor".equals(type) && light!=null)
                    {
                        LightCmpTemp lightCmpTemp = (LightCmpTemp) CmpMapper.getComp(CmpType.LIGHT_TEMP, owner);
                        LightCmp lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, owner);
                        if(lightCmpTemp!=null && light!=null)
                        {
                            light.radiance.range=lightCmpTemp.level;
                            light.radiance.color = lightCmpTemp.color;
                            light.radiance.flicker = lightCmpTemp.flicker;
                            light.radiance.strobe = lightCmpTemp.strobe;
                        }

                        else if(lightCmp!=null && light!=null)
                        {
                            light.radiance.range=lightCmp.level;
                            light.radiance.color = lightCmp.color;
                            light.radiance.flicker = lightCmp.flicker;
                            light.radiance.strobe = lightCmp.strobe;
                        }
                        glyph.setColor(SColor.colorFromFloat(getGlyphColor(owner)));
                        CharCmp charCmp =(CharCmp) CmpMapper.getComp(CmpType.CHAR, owner);
                        if(charCmp.armorChr!=null) glyph.shown = charCmp.armorChr;
                        else glyph.shown = charCmp.chr;
                    }
                    else if ("actorLeft".equals(type))
                    {
                        Integer itemId = inventoryCmp.getSlotEquippedID(EquipmentSlot.LEFT_HAND_WEAP);
                        if(itemId!=null && light!=null)
                        {

                            Entity itemEntity = getGame().getEntity(itemId);
                            LightCmp lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, itemEntity);
                            light.radiance.range=lightCmp.level;
                            light.radiance.color = lightCmp.color;
                            light.radiance.flicker = lightCmp.flicker;
                            light.radiance.strobe = lightCmp.strobe;

                        } else lightingHandler.lightList.get(lightID).radiance.range = 0;

                    }
                    else if ("actorRight".equals(type))
                    {
                        Integer itemId = inventoryCmp.getSlotEquippedID(EquipmentSlot.RIGHT_HAND_WEAP);
                        if(itemId!=null)
                        {
                            Entity itemEntity = getGame().getEntity(itemId);
                            LightCmp lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, itemEntity);
                            light.radiance.range=lightCmp.level;
                            light.radiance.color = lightCmp.color;
                            light.radiance.flicker = lightCmp.flicker;
                            light.radiance.strobe = lightCmp.strobe;

                        } else lightingHandler.lightList.get(lightID).radiance.range = 0;

                    }
                }
                if(light!=null)
                    light.position=lightMapPos;


            }
        }
        for(Integer id : levelCmp.objects.identities())
        {
            Entity owner= getGame().getEntity(id);
            LightCmp lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, owner);
            Light light = lightingHandler.lightList.get(id);
            light.radiance.range=lightCmp.level;
            light.radiance.color = lightCmp.color;
            light.radiance.flicker = lightCmp.flicker;
            light.radiance.strobe = lightCmp.strobe;
        }
        lightingCmp.bgLighting = new float[(lightingCmp.ambientBgLighting.length)][lightingCmp.ambientBgLighting[0].length];
        for (int i = 0; i < lightingCmp.ambientBgLighting.length; i++)
        {
            System.arraycopy(lightingCmp.ambientBgLighting[i], 0, lightingCmp.bgLighting[i], 0, lightingCmp.bgLighting[i].length);
        }

        lightingCmp.fgLightLevel =new double[(levelCmp.decoDungeon.length)][levelCmp.decoDungeon[0].length];
        for (int i = 0; i < lightingCmp.fgLightLevel.length; i++)
        {
            System.arraycopy(lightingCmp.ambientFgLightLvls[i], 0, lightingCmp.fgLightLevel[i], 0, lightingCmp.fgLightLevel[i].length);
        }
        lightingCmp.fgLighting=new float[(levelCmp.decoDungeon.length)][levelCmp.decoDungeon[0].length];

        double[][] tempFov = new double[lightingCmp.bgLighting[0].length][lightingCmp.bgLighting.length];
        double[][] tempFgLightLvl3x3 = new double[lightingCmp.bgLighting[0].length][lightingCmp.bgLighting.length];

        for(Light light : lightingHandler.lightList.values())
        {
            Radiance radiance = light.radiance;
            Coord location = light.position;
            MyFOV.addFOVsInto(tempFgLightLvl3x3, MyFOV.reuseFOV(lightingCmp.resistance3x3, tempFov, location.x, location.y, radiance.currentRange()*3));
        }
        MyFOV.addFOVsInto(tempFgLightLvl3x3, lightingCmp.ambientBgLightLvls);

        for(int x=0; x<lightingCmp.fgLightLevel.length; x++) {
            for (int y = 0; y < lightingCmp.fgLightLevel[0].length; y++)
            {
                lightingCmp.fgLightLevel[x][y] = tempFgLightLvl3x3[1+x*3][1+y*3];

            }
        }

        lightingHandler.update();

        GreasedRegion fs3x3 = new GreasedRegion(lightingHandler.losResult,0.0).not();
        GreasedRegion ambientLit = new GreasedRegion(lightingCmp.ambientBgLightLvls, 0.0).not();
        GreasedRegion lightingLit= new GreasedRegion(lightingHandler.fovResult, 0.0).not();
        GreasedRegion lit = ambientLit.or(lightingLit);

        lightingCmp.focusSeen3x3.or(fs3x3.and(lit));

        lightingCmp.focusSeen3x3.or(new GreasedRegion(lightingCmp.focusNightVision3x3, 0.0).not());
        Noise.Noise3D flicker = new WhirlingNoise();

        for(int x=0; x<lightingCmp.bgLighting.length; x++){
            for(int y=0; y<lightingCmp.bgLighting[0].length; y++)
            {
                if(lightingHandler.colorLighting[0][x][y]>0 || lightingHandler.losResult[x][y]>0 && lightingCmp.ambientBgLightLvls[x][y] >0)
                {
                    char chr = levelCmp.decoDungeon[Math.round(x/3)][Math.round(y/3)];


                    if(chr =='~' || chr ==',')
                    {
                        //bgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), bgColors[Math.round(x/3)][Math.round(y/3)], lightingHandler.colorLighting[0][x][y]);
                        lightingCmp.bgLighting[x][y] = SColor.lerpFloatColors(lightingCmp.ambientBgLighting[x][y], SColor.CW_DARK_AZURE.toFloatBits(), (float) (0xAAp-9f + (0xC8p-9f * 0.65 *
                                                        (1f + 0.45f * (float) flicker.getNoise(x * 0.3, y * 0.3, (System.currentTimeMillis() & 0xffffffL) * 0.00070)))));
                    }
                    else
                    {
                        lightingCmp.bgLighting[x][y] = SColor.lerpFloatColors(lightingCmp.ambientBgLighting[x][y], levelCmp.bgColors[Math.round(x/3)][Math.round(y/3)], (0xAAp-9f + (0xC8p-9f * (lightingHandler.colorLighting[0][x][y]) *
                                (1f + 0.25f * (float) flicker.getNoise(x * 0.3, y * 0.3, (System.currentTimeMillis() & 0xffffffL) * 0.00125)))-0.15f));
                    }
                }
            }
        }

        if(getGame().gameState==GameState.AIMING)
        {
            AimingCmp aimingCmp = (AimingCmp) CmpMapper.getComp(CmpType.AIMING, getGame().getFocus());
            if (aimingCmp != null) {

                Ability aimAbility = CmpMapper.getAbilityComp(aimingCmp.skill, getGame().getFocus());
                System.out.println(aimAbility);
                if(aimingCmp.scroll) aimAbility = CmpMapper.getAbilityComp(aimingCmp.skill, getGame().getScrollForSkill(aimingCmp.skill, getGame().getFocus()));
                System.out.println(aimAbility);
                PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, getGame().getFocus());
                aimAbility.setMap(levelCmp.decoDungeon);
                for(Coord coord : aimAbility.possibleTargets(positionCmp.coord, levelCmp.resistance) )
                {
                    if(aimAbility.aoe.findArea().keySet().contains(coord) || !levelCmp.floors.contains(coord)) continue;
                    for(int x=-0; x<3; x++){
                        for(int y=0; y<3; y++)
                            lightingCmp.bgLighting[(coord.x*3)+x][(coord.y*3)+y] = SColor.lerpFloatColors(lightingCmp.bgLighting[(coord.x*3)+x][(coord.y*3)+y], SColor.GREEN_BAMBOO.toFloatBits(), 0.15f);
                    }
                }
                for(Coord coord : aimAbility.aoe.findArea().keySet())
                {
                    for(int x=0; x<3; x++){
                        for(int y=0; y<3; y++)
                        {
                            lightingCmp.bgLighting[(coord.x*3)+x][(coord.y*3)+y] = SColor.lerpFloatColors(lightingCmp.bgLighting[(coord.x*3)+x][(coord.y*3)+y], aimingCmp.skill.school.color.toFloatBits(), (float) (aimAbility.aoe.findArea().get(coord)*1f));
                        }
                    }
                }
            }
        }

        FOVCmp fovCmp = (FOVCmp)CmpMapper.getComp(CmpType.FOV, getGame().getFocus());

        for(int x = 0; x< levelCmp.colors[0].length; x++){
            for(int y = 0; y< levelCmp.colors.length; y++)
            {
                if(fovCmp.fov[x][y] > 0.0 && lightingCmp.fgLightLevel[x][y]>0 )
                {
                    char chr = levelCmp.decoDungeon[x][y];
                    if(chr=='~' || chr==',')
                    {
                        lightingCmp.fgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], (0xAAp-9f + (0xC8p-9f * 0.8f *
                                (1f + 0.8f * (float) flicker.getNoise(x * 0.3, y * 0.3, (System.currentTimeMillis() & 0xffffffL) * 0.00070)))));
                    }
                    else lightingCmp.fgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], MathUtils.clamp((float)(lightingCmp.fgLightLevel[x][y]),0.3f, 0.7f));

                } else {

                    lightingCmp.fgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], MathUtils.clamp((float)(lightingCmp.ambientFgLightLvls[x][y]),0.0f, 1f));
                }

            }
        }

        lightingHandler.draw(lightingCmp.bgLighting);

        applyOutOfLOSfilter(lightingCmp, lightingHandler, levelCmp);

        glyphs = display.glyphs.iterator();
        AICmp aiCmp = CmpMapper.getAIComp(focusStatsCmp.mobType.aiType, getGame().getFocus());
        while (glyphs.hasNext()) {
            TextCellFactory.Glyph glyph = glyphs.next();

            if (glyph.getName() != null) {
                String[] splitName = StringKit.split(glyph.getName(), " ");

                Integer ownerID = Integer.parseInt(splitName[1]);
                Entity owner = getGame().getEntity(ownerID);

                if (owner != null) {

                    PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, owner);
                    if(positionCmp==null) continue;
                    GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, owner);
                    if(aiCmp.visibleEnemies.contains(ownerID) || aiCmp.visibleFriendlies.contains(ownerID)
                        || aiCmp.visibleNeutrals.contains(ownerID) || owner==getGame().getFocus())
                    {
                        glyphsCmp.setVisibility(true);
                    }

                    else glyphsCmp.setVisibility(false);

                }
            }
        }

    }

    public float getGlyphColor(Entity entity)
    {
        CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, entity);
        float color = charCmp.color.toFloatBits();
        if(charCmp.armorColor!=null) color = charCmp.armorColor.toFloatBits();

        if(CmpMapper.getStatusEffectComp(StatusEffect.BURNING, entity)!=null) return rng.getRandomElement(new SColor[]{SColor.SAFETY_ORANGE, SColor.BRIGHT_GOLDEN_YELLOW,SColor.CW_DARK_YELLOW}).toFloatBits();
        if(CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, entity)!=null) return SColor.BABY_BLUE.toFloatBits();
        if(CmpMapper.getStatusEffectComp(StatusEffect.CHILLED, entity)!=null) return SColor.lerpFloatColors(color, SColor.BABY_BLUE.toFloatBits(), 0.3f);
        if(CmpMapper.getStatusEffectComp(StatusEffect.CALESCENT, entity)!=null) return SColor.lerpFloatColors(color, SColor.BRIGHT_GOLDEN_YELLOW.toFloatBits(), 0.3f);
        if(CmpMapper.getStatusEffectComp(StatusEffect.STALKING, entity)!=null) return SColor.lerpFloatColors(color, SColor.GREEN_BAMBOO.toFloatBits(), 0.7f);

        return color;
    }

    private void applyOutOfLOSfilter(LightingCmp lightingCmp, LightHandler lightingHandler, LevelCmp levelCmp)
    {
        for(int x=0; x<lightingCmp.bgLighting.length; x++)
            for(int y=0; y<lightingCmp.bgLighting[0].length; y++)
            {
                if(!lightingCmp.focusSeen3x3.contains(x, y) && lightingHandler.fovResult[x][y]==0)
                    lightingCmp.bgLighting[x][y] = lightingCmp.fow[x][y];
                if(lightingCmp.focusSeen3x3.contains(x, y) && lightingHandler.losResult[x][y]==0
                        || lightingCmp.focusSeen3x3.contains(x, y) && lightingHandler.colorLighting[0][x][y]==0 && lightingCmp.ambientBgLightLvls[x][y] == 0)
                {
                   /* if(lightingCmp.ambientBgLightLvls[x][y]>0)
                    {
                        lightingCmp.bgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), lightingCmp.ambientBgLighting[x][y], (float) lightingCmp.ambientBgLightLvls[x][y] );

                    } else {*/

                    lightingCmp.bgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.bgColors[x/3][y/3], MathUtils.clamp((float) (lightingCmp.ambientBgLightLvls[x][y]+0.2 ),0.2f,1.0f));

                    //}

                }
            }
    }

}
