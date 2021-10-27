package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.DamageType;
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
import squidpony.squidai.AOE;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;

public class IceShield extends Ability
{
    private Skill skill = Skill.ICE_SHIELD;
    private PointAOE aoe = new PointAOE(Coord.get(-1,-1), 1, 1);
    private Coord targetedLocation;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public TextCellFactory.Glyph glyph;
    private GWTRNG rng = new GWTRNG();

    public IceShield()
    {
        super("Ice Shield", new PointAOE(Coord.get(-1,-1), 0, 2));
        statusEffects.put(StatusEffect.CHILLED, new SEParameters(TargetType.ENEMY, SERemovalType.TIMED));
    }


    @Override
    public Skill getSkill() {
        return skill;
    }
    @Override
    public List<Skill> getReactions()  {
        return Arrays.asList();
    }

    @Override
    public void updateAOE(Entity performer)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer);
        aoe.setOrigin(positionCmp.coord);
    }
    @Override
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation = targetedLocation; }

    @Override
    public Coord getTargetedLocation() { return targetedLocation; }

    private AOE getAOE() {
        return aoe;
    }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {
        inactivate();

        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;

        return new AnimateGlyphEvt(glyph, AnimationsSys.AnimationType.ICE_SHIELD, startPos, targetCoord, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return glyph;
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler, Entity performer)
    {
        ParticleEffectsCmp peCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, performer);
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performer);
        glyph = display.glyph(' ',getSkill().school.color.toFloatBits(), glyphsCmp.glyph.getX(), glyphsCmp.glyph.getY());
        SColor color = skill.school.color;
        peCmp.addEffect(glyphsCmp.glyph, ParticleEffectsCmp.ParticleEffect.ICE_SHIELD, display);

        Light light = new Light(Coord.get(aoe.getOrigin().x*3, aoe.getOrigin().y*3), new Radiance(1, SColor.lerpFloatColors(color.toFloatBits(), SColor.WHITE_FLOAT_BITS, 0.4f)));
        glyph.setName(light.hashCode() + " " + "0" + " temp");
        lightingHandler.addLight(light.hashCode(), light);
    }

    @Override
    public HashMap<StatusEffect, SEParameters> getStatusEffects() {

        return statusEffects;
    }

    @Override
    public void addStatusEffect(StatusEffect statusEffect, SEParameters seParameters)
    {
        statusEffects.put(statusEffect, seParameters);
    }

    @Override
    public void removeStatusEffect(StatusEffect statusEffect) {
        statusEffects.remove(statusEffect);

    }

    @Override
    public Integer getStatusEffectDuration(StatsCmp statsCmp, StatusEffect statusEffect)
    {
        return statsCmp.getSpellPower()*3;
    }

    @Override
    public float getDmgReduction(StatsCmp statsCmp)
    {
        return rng.between(1, 11)<statsCmp.getPerc() ? 1f:.5f;
    }
    @Override
    public TargetType getTargetType()
    {
        return TargetType.ENEMY;
    }
    @Override
    public int getDamage(Entity performer) {
        return 0;
    }

    @Override
    public DamageType getDmgType(Entity performer) {
        return DamageType.ICE;
    }

    @Override
    public int getTTPerform(Entity performer) {
        return 0;
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 10;
    }
}
