package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.LightHandler;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import EuroRogue.DamageType;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.MySparseLayers;
import EuroRogue.CmpType;
import squidpony.squidai.AOE;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Opportunity implements IAbilityCmpSubSys
{
    //private Skill skill = Skill.OPPORTUNITY;
    private Skill skill = null;
    private boolean active = true;
    private  boolean scroll = false;
    private Integer scrollID = null;
    private PointAOE aoe = new PointAOE(Coord.get(-1,-1), 1, 1);
    public OrderedMap<Coord, ArrayList<Coord>> targets = new OrderedMap();
    private boolean available = false;
    public int damage;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public int ttPerform;

    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
        return Arrays.asList(Skill.DODGE);
    }

    @Override
    public boolean scroll() {
        return scroll;
    }

    @Override
    public void setScroll(boolean bool) {scroll = bool; }

    @Override
    public Integer getScrollID() { return scrollID; }

    @Override
    public void setScrollID(Integer id) { scrollID = id;}

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
    public boolean getActive()
    {
        return active;
    }
    @Override
    public void activate()
    {
        active=true;
    }
    @Override
    public void inactivate()
    {
        active=false;
    }

    @Override
    public void setTargets(OrderedMap<Coord, ArrayList<Coord>> targets)
    {
        this.targets = targets;
    }

    @Override
    public OrderedMap<Coord, ArrayList<Coord>> getTargets() {
        return targets;
    }

    @Override
    public AOE getAOE() {
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
    public int getDamage() {
        return damage;
    }

    @Override
    public void setDamage(Entity performer)
    {
        damage = ((StatsCmp) CmpMapper.getComp(CmpType.STATS, performer)).getWeaponDamage();
    }

    @Override
    public DamageType getDmgType(Entity performer)
    {
        return DamageType.BLUDGEONING;
    }

    @Override
    public int getTTPerform() {
        return ttPerform;
    }

    @Override
    public void setTTPerform(Entity performer)
    {
        ttPerform = 0;
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 0;
    }

    @Override
    public void updateAOE(Entity actor, AOE aoe)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);
        aoe.setOrigin(positionCmp.coord);
    }

    @Override
    public void updateScrollAOE(Entity scroll, AOE aoe, Coord location)
    {
        aoe.setOrigin(location);
    }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Entity target, IEventComponent actionEvt, MySparseLayers display)
    {
        TextCellFactory.Glyph glyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performer)).glyph;
        Coord startLoc = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;
        Coord endLoc = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, target)).coord;
        return new AnimateGlyphEvt(glyph, skill.animationType, startLoc, endLoc, actionEvt);
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
    public void removeStatusEffect(StatusEffect statusEffect)
    {
        statusEffects.remove(statusEffect);
    }

    @Override
    public Integer getStatusEffectDuration(StatsCmp statsCmp, StatusEffect statusEffect) {
        return 0;
    }

}
