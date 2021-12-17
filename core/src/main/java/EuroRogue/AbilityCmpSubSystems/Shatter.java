package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.IColoredString;
import EuroRogue.AOEType;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.DamageType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.TargetType;
import squidpony.StringKit;
import squidpony.squidai.BlastAOE;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Shatter extends Ability
{
    private Coord targetedLocation;

    public Shatter()
    {
        super("Shatter", new BlastAOE(Coord.get(0,0),1, Radius.CIRCLE, 0, 0), AOEType.BLAST);
        super.skill = Skill.SHATTER;
    }
    /**
     * -Need to override this for any Ability with a non-Point AOE for setTargetedLocation(aimAt)-ER
     *
     * This does one last validation of the location aimAt (checking that it is within the valid range for this
     * Technique) before getting the area affected by the AOE targeting that cell. It considers the origin of the AOE
     * to be the Coord parameter user, for purposes of directional limitations and for AOE implementations that need
     * the user's location, such as ConeAOE and LineAOE.
     * <p>
     * YOU MUST CALL setMap() with the current map status at some point before using this method, and call it again if
     * the map changes. Failure to do so can cause serious bugs, from logic errors where monsters consider a door
     * closed when it is open or vice versa, to an ArrayIndexOutOfBoundsException being thrown if the player moved to a
     * differently-sized map and the Technique tries to use the previous map with coordinates from the new one.
     *
     * @param user  The position of the Technique's user, x first, y second.
     * @param aimAt A target Coord typically obtained from idealLocations that determines how to position the AOE.
     * @return a HashMap of Coord keys to Double values from 1.0 (fully affected) to 0.0 (unaffected).
     */
    @Override
    public OrderedMap<Coord, Double> apply(Coord user, Coord aimAt)
    {
        setTargetedLocation(aimAt);

        return super.apply(user, aimAt);

    }

    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
        return Arrays.asList();
    }

    @Override
    public void setAvailable(Entity performer, EuroRogue game)
    {
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, performer);
        AICmp aiCmp = (AICmp) CmpMapper.getAIComp(statsCmp.mobType.aiType, performer);
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL,performer);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);

        boolean canAfford = manaPoolCmp.canAfford(getSkill());
        if(scroll()) canAfford = true;
        this.available = ( aiCmp.target!=null && canAfford && getActive() &! getAOEtargetsDmg(performer, levelCmp, game).isEmpty());
    }

    @Override
    public void updateAOE(Entity performer)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer);

        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        BlastAOE blastAOE = (BlastAOE) aoe;
        blastAOE.setRadius(statsCmp.getIntel());
        blastAOE.setOrigin(positionCmp.coord);
        blastAOE.setCenter(positionCmp.coord);
    }

    @Override
    public HashMap<Integer, Integer> getAOEtargetsDmg(Entity performerEntity, LevelCmp levelCmp, EuroRogue game)
    {
        HashMap<Integer, Integer> targets = new HashMap<>();
        Integer performerID = levelCmp.actors.get(aoe.getOrigin());
        System.out.println("Shatter getDmg bug");
        for(Coord coord : aoe.findArea().keySet())
        {
            if(levelCmp.actors.positions().contains(coord))
            {
                Integer targetID = levelCmp.actors.get(coord);
                Entity aoeTarEnt = game.getEntity(targetID);
                int dmg = getDamage(performerEntity);
                if(CmpMapper.getStatusEffectComp(StatusEffect.FROZEN, aoeTarEnt)!=null)
                    targets.put(levelCmp.actors.get(coord), dmg);
                else if(CmpMapper.getStatusEffectComp(StatusEffect.CHILLED, aoeTarEnt)!=null)
                    targets.put(levelCmp.actors.get(coord), Math.round(dmg/2f));
            }
        }
        System.out.println(targets);
        return targets;

    }

    @Override
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation = targetedLocation; }

    @Override
    public Coord getTargetedLocation() { return targetedLocation; }

    @Override
    public float getDmgReduction(StatsCmp statsCmp) {
        return 0;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.AOE;
    }

    @Override
    public int getDamage(Entity performer)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        return Math.round(statsCmp.getSpellPower()*1f);
    }

    @Override
    public DamageType getDmgType(Entity performer)
    {
        return DamageType.BLUDGEONING;
    }

    @Override
    public int getTTPerform(Entity performer)
    {
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTCast();
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 10;
    }


    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {
        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;

        return new AnimateGlyphEvt(glyph, AnimationsSys.AnimationType.SHATTER, startPos, targetCoord, eventCmp);
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler, Entity performer)
    {
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performer);
        glyph = display.glyph(' ',getSkill().school.color.toFloatBits(), glyphsCmp.rightGlyph.getX(), glyphsCmp.rightGlyph.getY());
        SColor color = skill.school.color;

        Light light = new Light(Coord.get(aoe.getOrigin().x*3, aoe.getOrigin().y*3), new Radiance(2, SColor.lerpFloatColors(color.toFloatBits(), SColor.WHITE_FLOAT_BITS, 0.3f)));
        glyph.setName(light.hashCode() + " " + "0" + " temp");
        lightingHandler.addLight(light.hashCode(), light);
    }


    @Override
    public Integer getStatusEffectDuration(StatsCmp statsCmp, StatusEffect statusEffect)
    {
        return statsCmp.getSpellPower()*3;
    }

    @Override
    public void postToLog(Entity performer, EuroRogue game) {
        super.postToLog(performer, game);
        SColor schoolColor = getSkill().school.color;
        List<String> description = StringKit.wrap(
                "A Blast AOE spell, centered on the caster, that deals "+getDmgType(performer)+" damage equal to Spell Power to Frozen targets and 1/2 Spell Power to Chilled targets."
                , 40);

        IColoredString.Impl<SColor> desc = new IColoredString.Impl<SColor>();
        desc.append("Description:", SColor.WHITE);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(desc);

        for(String line : description)
        {
            IColoredString.Impl<SColor> lineText = new IColoredString.Impl<SColor>();
            lineText.append("   "+line, SColor.LIGHT_YELLOW_DYE);
            ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineText);
            System.out.println(lineText.present());
        }


        IColoredString.Impl<SColor> lineLast = new IColoredString.Impl<SColor>();
        lineLast.append("-----------------------------------------------------", schoolColor);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineLast);
    }
}
