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
import EuroRogue.StatType;
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
import squidpony.squidmath.OrderedMap;

public class MeleeAttack implements IAbilityCmpSubSys
{
    private Skill skill = Skill.MELEE_ATTACK;
    private boolean active = true;
    private  boolean scroll = false;
    private Integer scrollID = null;
    private PointAOE aoe = new PointAOE(Coord.get(-1,-1), 1, 1);
    public OrderedMap<Coord, ArrayList<Coord>> targets = new OrderedMap();
    private boolean available = false;
    public int damage;
    public DamageType damageType =  DamageType.BLUDGEONING;
    private HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public int ttPerform;
    public char chr = '•';
    public TextCellFactory.Glyph glyph;



    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
        return Arrays.asList(Skill.DODGE, Skill.ICE_SHIELD);
    }

    @Override
    public boolean scroll() {
        return scroll;
    }

    @Override
    public void setScroll(boolean bool) { scroll = bool; }

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
        return damageType;
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
    public double getNoiseLvl(Entity performer)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        double noiseLvl = 0;
        switch (getDmgType(performer))
        {
            case BLUDGEONING:
            case SLASHING:
                noiseLvl=15;
                break;
            case PIERCING:
                noiseLvl=10;
                break;
        }
        return noiseLvl * statsCmp.getStatMultiplier(StatType.MELEE_SND_LVL);
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
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Entity target, IEventComponent eventCmp, MySparseLayers display)
    {

        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;
        Coord endPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, target)).coord;
        TextCellFactory.Glyph glyph = ((GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, performer)).rightGlyph;

        return new AnimateGlyphEvt(glyph, skill.animationType, startPos, endPos, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return glyph;
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler)
    {

    }

    @Override
    public HashMap<StatusEffect, SEParameters> getStatusEffects() { return statusEffects; }

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
        return statsCmp.getAttackPower()*3;
    }



}
