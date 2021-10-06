package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.LightHandler;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import EuroRogue.DamageType;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.CmpType;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.MySparseLayers;
import squidpony.squidai.AOE;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.OrderedMap;

public class Dodge extends Ability
{

    private Skill skill = Skill.DODGE;
    private  Coord targetedLocation;
    private boolean available;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    private GWTRNG rng = new GWTRNG();

    public Dodge()
    {
        super("Dodge", new PointAOE(Coord.get(-1,-1),0,2));
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
    public boolean isAvailable() {
        return available;
    }
    @Override
    public void setAvailable(boolean available)
    {
        this.available=available;
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
        TextCellFactory.Glyph glyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performer)).glyph;
        return new AnimateGlyphEvt(glyph, skill.animationType, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return null;
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler) {

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

    }

    @Override
    public Integer getStatusEffectDuration(StatsCmp statsCmp, StatusEffect statusEffect) {
        return 0;
    }

    @Override
    public float getDmgReduction(StatsCmp statsCmp)
    {
        return rng.between(1, 11)<statsCmp.getDex() ? 1f:.5f;
    }
    @Override
    public TargetType getTargetType()
    {
        return TargetType.SELF;
    }

    @Override
    public int getDamage(Entity performer) {
        return 0;
    }

    @Override
    public DamageType getDmgType(Entity performer) {
        return DamageType.NONE;
    }

    @Override
    public int getTTPerform(Entity performer) {
        return 0;
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 0;
    }
}
