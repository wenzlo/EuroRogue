package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.Light;
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
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class MagicMissile implements IAbilityCmpSubSys
{
    private Skill skill = Skill.MAGIC_MISSILE;
    private AOE aoe = new PointAOE(Coord.get(-1,-1),1,1);
    private boolean active = true;
    private  boolean scroll = false;
    private Integer scrollID = null;
    public int damage;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public int ttPerform;
    public TextCellFactory.Glyph glyph;

    public OrderedMap<Coord, ArrayList<Coord>> targets = new OrderedMap();
    private boolean available = false;

    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
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
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, actor);
        Coord location = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor)).coord;
        aoe.setOrigin(location);
        aoe.setMaxRange(statsCmp.getIntel());
    }
    @Override
    public void updateScrollAOE(Entity scroll, AOE aoe, Coord location)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, scroll);
        aoe.setOrigin(location);
        aoe.setMaxRange(statsCmp.getIntel());
    }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Entity target, IEventComponent eventCmp, MySparseLayers display)
    {
        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;
        Coord endPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, target)).coord;
        //TextCellFactory.Glyph glyph = display.glyph('°',getSkill().school.color, startPos.x, startPos.y);

        return new AnimateGlyphEvt(glyph, skill.animationType, startPos, endPos, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return glyph;
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler)
    {
        glyph = display.glyph('°',getSkill().school.color, aoe.getOrigin().x, aoe.getOrigin().y);
        SColor color = skill.school.color;

        Light light = new Light(Coord.get(aoe.getOrigin().x*3, aoe.getOrigin().y*3), new Radiance(2, SColor.lerpFloatColors(color.toFloatBits(), SColor.WHITE_FLOAT_BITS, 0.4f)));
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
    public void removeStatusEffect(StatusEffect statusEffect)
    {
        statusEffects.remove(statusEffect);
    }

    @Override
    public Integer getStatusEffectDuration(StatsCmp statsCmp, StatusEffect statusEffect)
    {
        return null;
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
    public  void setDamage(Entity performer)
    {
        damage = ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getSpellPower();

    }

    @Override
    public DamageType getDmgType(Entity performer)
    {
        return DamageType.ARCANE;
    }

    @Override
    public int getTTPerform() {
        return ttPerform;
    }

    @Override
    public void setTTPerform(Entity performer)
    {
        ttPerform = ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTCast();
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 15;
    }
}
