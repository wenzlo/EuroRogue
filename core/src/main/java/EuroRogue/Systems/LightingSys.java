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
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.GlyphsCmp;
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
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.StringKit;
import squidpony.squidai.BlastAOE;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.Noise;
import squidpony.squidmath.WhirlingNoise;

public class LightingSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private GWTRNG rng;



    public LightingSys()
    {
        super.priority = 100;
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
        if(getGame().gameState== GameState.STARTING) return;
        if(getGame().gameState== GameState.CAMPING) return;
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        MySparseLayers display = windowCmp.display;
        LightHandler lightingHandler = windowCmp.lightingHandler;
        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, getGame().currentLevel);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
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

                if(owner!=null)
                {
                    if(owner==getGame().getFocus())
                    {
                        lightingHandler.calculateFOV(lightMapPos.x, lightMapPos.y);
                        FOV.reuseFOV(lightingCmp.resistance3x3, lightingCmp.focusNightVision3x3, lightMapPos.x, lightMapPos.y, focusStatsCmp.getPerc()*3);
                    }
                    if("actor".equals(type))
                    {
                        LightCmpTemp lightCmpTemp = (LightCmpTemp) CmpMapper.getComp(CmpType.LIGHT_TEMP, owner);
                        if(lightCmpTemp!=null)
                        {
                            lightingHandler.lightList.get(lightID).radiance.range=lightCmpTemp.level;
                            lightingHandler.lightList.get(lightID).radiance.color = lightCmpTemp.color;
                            lightingHandler.lightList.get(lightID).radiance.flicker = lightCmpTemp.flicker;
                            lightingHandler.lightList.get(lightID).radiance.strobe = lightCmpTemp.strobe;
                        }

                        else
                        {
                            LightCmp lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, owner);
                            lightingHandler.lightList.get(lightID).radiance.range=lightCmp.level;
                            lightingHandler.lightList.get(lightID).radiance.color = lightCmp.color;
                            lightingHandler.lightList.get(lightID).radiance.flicker = lightCmp.flicker;
                            lightingHandler.lightList.get(lightID).radiance.strobe = lightCmp.strobe;
                        }
                        glyph.setColor(getGlyphColor(owner));
                    }
                }
                Light light = lightingHandler.lightList.get(lightID);
                if(light!=null)
                    light.position=lightMapPos;


            }
        }

        for(Integer id : levelCmp.objects.identities())
        {
            Entity owner= getGame().getEntity(id);
            LightCmp lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, owner);
            lightingHandler.lightList.get(id).radiance.range=lightCmp.level;
            lightingHandler.lightList.get(id).radiance.color = lightCmp.color;
            lightingHandler.lightList.get(id).radiance.flicker = lightCmp.flicker;
            lightingHandler.lightList.get(id).radiance.strobe = lightCmp.strobe;
        }

        lightingCmp.bgLighting=new float[(levelCmp.decoDungeon[0].length)*3][(levelCmp.decoDungeon.length)*3];
        lightingCmp.fgLightLevel =new double[(levelCmp.decoDungeon[0].length)][levelCmp.decoDungeon.length];
        lightingCmp.fgLighting=new float[(levelCmp.decoDungeon[0].length)][levelCmp.decoDungeon.length];

        double[][] tempFov=new double[levelCmp.decoDungeon[0].length][levelCmp.decoDungeon.length];
        for(Light light : lightingHandler.lightList.values())
        {
            Radiance radiance = light.radiance;
            Coord location = light.position;
            FOV.addFOVsInto(lightingCmp.fgLightLevel, FOV.reuseFOV(levelCmp.resistance, tempFov, location.x/3, location.y/3, radiance.range));
        }
        lightingHandler.update();
        lightingCmp.focusSeen3x3.or(new GreasedRegion(lightingHandler.fovResult,0.0).not());
        lightingCmp.focusSeen3x3.or(new GreasedRegion(lightingCmp.focusNightVision3x3, 0.0).not());


        for(int x=0;x<lightingCmp.bgLighting[0].length;x++){
            for(int y=0;y<lightingCmp.bgLighting.length;y++)
            {
                if(lightingHandler.fovResult[x][y]>0)
                {
                    char chr = levelCmp.decoDungeon[Math.round(x/3)][Math.round(y/3)];
                    Noise.Noise3D flicker = new WhirlingNoise();
                    if(chr =='~' || chr ==',')
                    {

                        //bgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), bgColors[Math.round(x/3)][Math.round(y/3)], lightingHandler.colorLighting[0][x][y]);
                        lightingCmp.bgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.bgColors[Math.round(x/3)][Math.round(y/3)], (0xAAp-9f + (0xC8p-9f * lightingHandler.colorLighting[0][x][y] *
                                (1f + 0.25f * (float) flicker.getNoise(x * 0.3, y * 0.3, (System.currentTimeMillis() & 0xffffffL) * 0.00125)))));
                    }
                    else
                    {
                        lightingCmp.bgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.bgColors[Math.round(x/3)][Math.round(y/3)], (0xAAp-9f + (0xC8p-9f * lightingHandler.colorLighting[0][x][y] *
                                (1f + 0.13f * (float) flicker.getNoise(x * 0.3, y * 0.3, (System.currentTimeMillis() & 0xffffffL) * 0.00125)))));
                    }

                }
                else if(lightingCmp.focusSeen3x3.contains(x, y) && lightingCmp.focusNightVision3x3[x][y]==0 && levelCmp.floors.contains(x/3,y/3)) lightingCmp.bgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.bgColors[x/3][y/3], 0.2f);
                else if(lightingCmp.focusNightVision3x3[x][y]>0 && levelCmp.floors.contains(x/3,y/3)) lightingCmp.bgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.bgColors[x/3][y/3], 0.2f);
                else lightingCmp.bgLighting[x][y] = SColor.BLACK.toFloatBits();


            }
        }
        if(getGame().gameState==GameState.AIMING)
        {
            AimingCmp aimingCmp = (AimingCmp) CmpMapper.getComp(CmpType.AIMING, getGame().getFocus());
            Ability aimAbility = (Ability) CmpMapper.getAbilityComp(aimingCmp.skill, getGame().getFocus());
            PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, getGame().getFocus());
            for(Coord coord : aimAbility.possibleTargets(positionCmp.coord, levelCmp.resistance) )
            {
                if(aimAbility.aoe.findArea().keySet().contains(coord) || !levelCmp.floors.contains(coord)) continue;
                for(int x=-0; x<3; x++){
                    for(int y=0; y<3; y++)
                    {

                        lightingCmp.bgLighting[(coord.x*3)+x][(coord.y*3)+y] = SColor.lerpFloatColors(lightingCmp.bgLighting[(coord.x*3)+x][(coord.y*3)+y], SColor.GREEN_BAMBOO.toFloatBits(), 0.15f);
                    }
                }
            }
            for(Coord coord : aimAbility.aoe.findArea().keySet())
            {
                //if(!levelCmp.floors.contains(coord)) continue;
                for(int x=0; x<3; x++){
                    for(int y=0; y<3; y++)
                    {

                        lightingCmp.bgLighting[(coord.x*3)+x][(coord.y*3)+y] = SColor.lerpFloatColors(lightingCmp.bgLighting[(coord.x*3)+x][(coord.y*3)+y], SColor.SAFETY_ORANGE.toFloatBits(), 0.15f);
                    }
                }
            }
        }

        FOVCmp fovCmp = (FOVCmp)CmpMapper.getComp(CmpType.FOV, getGame().getFocus());
        for(int x = 0; x< levelCmp.colors[0].length; x++){
            for(int y = 0; y< levelCmp.colors.length; y++)
            {
                if(fovCmp.los[x][y] > 0.0 && lightingCmp.fgLightLevel[x][y]>0 )
                {
                    char chr = levelCmp.decoDungeon[x][y];
                    if(chr=='~' || chr==',')
                    {
                        Noise.Noise3D flicker = new WhirlingNoise();

                        //bgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), bgColors[Math.round(x/3)][Math.round(y/3)], lightingHandler.colorLighting[0][x][y]);
                        lightingCmp.fgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], (0xAAp-9f + (0xC8p-9f * (float) (lightingCmp.fgLightLevel[x][y]*1.25) *
                                (1f + 0.35f * (float) flicker.getNoise(x * 0.3, y * 0.3, (System.currentTimeMillis() & 0xffffffL) * 0.00125)))));
                    }
                    else lightingCmp.fgLighting[x][y] = SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], MathUtils.clamp((float)(lightingCmp.fgLightLevel[x][y]),0.3f, 0.7f));

                } //else fgLighting[x][y] = SColor.BLACK.toFloatBits();



            }
        }



        lightingHandler.draw(lightingCmp.bgLighting);

        glyphs = display.glyphs.iterator();

        while (glyphs.hasNext()) {
            TextCellFactory.Glyph glyph = glyphs.next();

            if (glyph.getName() != null) {
                String[] splitName = StringKit.split(glyph.getName(), " ");

                Integer ownerID = Integer.parseInt(splitName[1]);
                Entity owner = getGame().getEntity(ownerID);
                if (owner != null) {
                    PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, owner);
                    GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, owner);
                    if (fovCmp.visible.contains(positionCmp.coord)) glyphsCmp.setVisibility(true);
                    else glyphsCmp.setVisibility(false);
                }
            }
        }
    }

    public SColor getGlyphColor(Entity entity)
    {
        CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, entity);

        if(CmpMapper.getStatusEffectComp(StatusEffect.BURNING, entity)!=null) return rng.getRandomElement(new SColor[]{SColor.SAFETY_ORANGE, SColor.BRIGHT_GOLDEN_YELLOW,SColor.CW_DARK_YELLOW});
        if(CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, entity)!=null) return SColor.BABY_BLUE;


        return charCmp.color;
    }


}
