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
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;
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

public class Enrage implements IAbilityCmpSubSys
{
    private boolean active = true;
    private Skill skill = Skill.ENRAGE;
    private  boolean scroll = false;
    private Integer scrollID = null;
    private int ttPerform;
    private AOE aoe = new PointAOE(Coord.get(-1,-1),0,0);
    public OrderedMap<Coord, ArrayList<Coord>> targets = new OrderedMap();
    private boolean available;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();


    public Enrage()
    {
        statusEffects.put(StatusEffect.ENRAGED, new SEParameters(TargetType.SELF, SERemovalType.SHORT_REST, DamageType.NONE));
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
    public boolean scroll()
    {
        return scroll;
    }
    @Override
    public void setScroll(boolean bool)
    {
        scroll = bool;
    }

    @Override
    public Integer getScrollID() { return scrollID; }

    @Override
    public void setScrollID(Integer id) { scrollID = id; }

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
    public void updateAOE(Entity actor, AOE aoe)
    {
        Coord location = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION,actor)).coord;
        aoe.setOrigin(location);
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
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Entity target, IEventComponent eventCmp, MySparseLayers display)
    {
        inactivate();
        TextCellFactory.Glyph glyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performer)).glyph;
        return new AnimateGlyphEvt(glyph, AnimationsSys.AnimationType.SELF_BUFF, eventCmp);
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
        return null;
    }


    @Override
    public float getDmgReduction(StatsCmp statsCmp) {
        return 0f;
    }
    @Override
    public TargetType getTargetType()
    {
        return TargetType.SELF;
    }
    @Override
    public int getDamage() {
        return 0;
    }
    @Override
    public void setDamage(Entity performer) {
    }

    @Override
    public DamageType getDmgType(Entity performer) {
        return null;
    }

    @Override
    public int getTTPerform() {
        return ttPerform;
    }
    @Override
    public void setTTPerform(Entity performer)
    {
        ttPerform = ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTMelee();
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 10;
    }
}
