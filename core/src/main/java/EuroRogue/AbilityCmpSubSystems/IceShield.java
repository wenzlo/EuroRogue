package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
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
import EuroRogue.TargetType;
import squidpony.squidai.AOE;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.OrderedMap;

public class IceShield implements IAbilityCmpSubSys
{
    private boolean active = true;
    private Skill skill = Skill.ICE_SHIELD;
    private  boolean scroll = false;
    private Integer scrollID = null;
    private PointAOE aoe = new PointAOE(Coord.get(-1,-1), 1, 1);
    public OrderedMap<Coord, ArrayList<Coord>> targets = new OrderedMap();
    private boolean available;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public TextCellFactory.Glyph glyph;
    public GWTRNG rng = new GWTRNG();

    public IceShield()
    {
        statusEffects.put(StatusEffect.CHILLED, new SEParameters(TargetType.ENEMY, SERemovalType.TIMED, DamageType.ICE));
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

        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;
        Coord endPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, target)).coord;


        return new AnimateGlyphEvt(glyph, skill.animationType, startPos, endPos, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return glyph;
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler)
    {
        glyph = display.glyph('*',getSkill().school.color, aoe.getOrigin().x, aoe.getOrigin().y);
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
    public int getDamage() {
        return 0;
    }
    @Override
    public void setDamage(Entity performer) {
    }

    @Override
    public DamageType getDmgType(Entity performer) {
        return DamageType.ICE;
    }

    @Override
    public int getTTPerform() {
        return 0;
    }
    @Override
    public void setTTPerform(Entity performer) {
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 10;
    }
}
