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
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.TargetType;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class ArcaneTouch extends Ability
{
    private Skill skill = Skill.ARCANE_TOUCH;
    private Coord targetedLocation;

    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    private TextCellFactory.Glyph glyph;

    public ArcaneTouch()
    {
        super("Arcane Touch", new PointAOE(Coord.get(-1,-1), 1, 1));
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
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation=targetedLocation;}
    @Override
    public Coord getTargetedLocation() { return targetedLocation; }
    @Override
    public TargetType getTargetType() {
        return TargetType.ENEMY;
    }
    @Override
    public int getDamage(Entity performer)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        return statsCmp.getSpellPower();
    }
    @Override
    public DamageType getDmgType(Entity performer)
    {
        return DamageType.ARCANE;
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
    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }
    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {
        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;

        return new AnimateGlyphEvt(glyph, AnimationsSys.AnimationType.MELEE_ARCANE, startPos, targetCoord, eventCmp);
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

        Light light = new Light(Coord.get(aoe.getOrigin().x*3, aoe.getOrigin().y*3), new Radiance(2, SColor.lerpFloatColors(color.toFloatBits(), SColor.WHITE_FLOAT_BITS, 0.3f)));
        glyph.setName(light.hashCode() + " " + "0" + " temp");
        lightingHandler.addLight(light.hashCode(), light);
        ParticleEffectsCmp peCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, performer);
        peCmp.addEffect(glyph, ParticleEffectsCmp.ParticleEffect.ARCANE_P, display);


    }
    @Override
    public HashMap<StatusEffect, SEParameters> getStatusEffects()
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
        return statsCmp.getSpellPower()*15;
    }


}
