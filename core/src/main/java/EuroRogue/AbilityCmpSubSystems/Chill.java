package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.IColoredString;
import EuroRogue.AOEType;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.DamageType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.TargetType;
import squidpony.StringKit;
import squidpony.squidai.AOE;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class Chill extends Ability
{
    private Coord targetedLocation;

    public Chill()
    {
        super("Chilled", new PointAOE(Coord.get(-1,-1), 1, 1), AOEType.POINT);
        statusEffects.put(StatusEffect.CHILLED, new SEParameters(TargetType.ENEMY, SERemovalType.TIMED));
        super.skill = Skill.CHILL;
    }


    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
        return Arrays.asList();
    }


    @Override
    public void updateAOE(Entity performer)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer);
        aoe.setOrigin(positionCmp.coord);
    }


    @Override
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation = targetedLocation;}

    @Override
    public Coord getTargetedLocation() {
        return targetedLocation;
    }

    private AOE getAOE() {
        return aoe;
    }

    @Override
    public float getDmgReduction(StatsCmp statsCmp) {
        return 0;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.ENEMY;
    }

    @Override
    public int getDamage(Entity performer)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        return Math.round(statsCmp.getSpellPower()*0.5f);
    }

    @Override
    public DamageType getDmgType(Entity performer) {
        return DamageType.ICE;
    }

    @Override
    public int getTTPerform(Entity performer)
    {
        return  ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTCast();
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 10;
    }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {
        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;



        return new AnimateGlyphEvt(glyph, AnimationsSys.AnimationType.MELEE_ICE, startPos, targetCoord, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return glyph;
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler, Entity performer)
    {
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performer);
        glyph = display.glyph(' ',getSkill().school.color.toFloatBits(), glyphsCmp.rightGlyph.getX(), glyphsCmp.rightGlyph.getY());
        SColor color = skill.school.color;

        Light light = new Light(Coord.get(aoe.getOrigin().x*3, aoe.getOrigin().y*3), new Radiance(2, SColor.lerpFloatColors(color.toFloatBits(), SColor.WHITE_FLOAT_BITS, 0.4f)));
        glyph.setName(light.hashCode() + " " + "0" + " temp");
        lightingHandler.addLight(light.hashCode(), light);
        ParticleEffectsCmp peCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, performer);
        peCmp.addEffect(glyph, ParticleEffectsCmp.ParticleEffect.ICE_P, display);

    }

    @Override
    public HashMap<StatusEffect, SEParameters> getStatusEffects(Entity performer)
    {

        return statusEffects;
    }

    @Override
    public void addStatusEffect(StatusEffect statusEffect, SEParameters seParameters)
    {
        statusEffects.put(statusEffect, seParameters);
    }

    @Override
    public void removeStatusEffect(StatusEffect statusEffect)
    {
        statusEffects.remove(statusEffect);
    }

    @Override
    public Integer getStatusEffectDuration(StatsCmp statsCmp, StatusEffect statusEffect)
    {
        return statsCmp.getSpellPower();
    }
    @Override
    public void postToLog(Entity performer, EuroRogue game) {
        super.postToLog(performer, game);
        SColor schoolColor = getSkill().school.color;
        List<String> description = StringKit.wrap(
                "A melee range spell attack dealing " + getDmgType(performer) + " damage equal to Spell Power. Applies Frozen status effect if the target is already Chilled."
                , 40);

        IColoredString.Impl<SColor> desc = new IColoredString.Impl<SColor>();
        desc.append("Description:", SColor.WHITE);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(desc);

        for(String line : description)
        {
            IColoredString.Impl<SColor> lineText = new IColoredString.Impl<SColor>();
            lineText.append("   "+line, SColor.LIGHT_YELLOW_DYE);
            ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineText);

        }


        IColoredString.Impl<SColor> lineLast = new IColoredString.Impl<SColor>();
        lineLast.append("-----------------------------------------------------", schoolColor);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineLast);
    }


}
