package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.DamageType;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.LightHandler;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import squidpony.squidai.AOE;
import squidpony.squidai.BlastAOE;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Eruption implements IAbilityCmpSubSys
{
    private Skill skill = Skill.ERUPTION;
    private boolean active = true;
    private  boolean scroll = false;
    private Integer scrollID = null;
    private BlastAOE aoe = new BlastAOE(Coord.get(0,0),1, Radius.CIRCLE, 1, 1);
    private OrderedMap<Coord, ArrayList<Coord>> idealLocations = new OrderedMap<>();
    private Coord targetedLocation;
    private boolean available = false;
    private int damage;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    private int ttPerform;
    public TextCellFactory.Glyph glyph;


    public Eruption()
    {
        statusEffects.put(StatusEffect.CALESCENT, new SEParameters(TargetType.ENEMY, SERemovalType.TIMED, DamageType.FIRE));
    }

    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
        return Arrays.asList();
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
    public boolean isAvailable()
    {

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
    public void setIdealLocations(OrderedMap<Coord, ArrayList<Coord>> targets)
    {
        this.idealLocations = targets;
    }

    @Override
    public OrderedMap<Coord, ArrayList<Coord>> getIdealLocations() {
        return idealLocations;
    }

    @Override
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation = targetedLocation; }

    @Override
    public Coord getTargetedLocation() { return targetedLocation; }

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
        return TargetType.AOE;
    }

    @Override
    public int getDamage() { return damage; }

    @Override
    public void setDamage(Entity performer)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        damage = Math.round(statsCmp.getSpellPower()*0.5f);
    }

    @Override
    public DamageType getDmgType(Entity performer)
    {
        return DamageType.FIRE;
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
        return 10;
    }

    @Override
    public void updateAOE(Entity actor, LevelCmp levelCmp, AOE aoe, Entity scrollEntity)
    {
        BlastAOE blastAOE = (BlastAOE) aoe;
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);
        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, actor);
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, actor);
        if(scroll) statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, scrollEntity);

        blastAOE.setOrigin(positionCmp.coord);
        blastAOE.setMaxRange(statsCmp.getIntel());
        blastAOE.setRadius(statsCmp.getSpellPower()/4);

        ArrayList<Coord> enemyLocations = new ArrayList<>();
        for(Integer enemyID : aiCmp.visibleEnemies) enemyLocations.add(levelCmp.actors.getPosition(enemyID));
        ArrayList<Coord> friendLocations = new ArrayList<>();
        for(Integer friendlyID : aiCmp.visibleFriendlies) enemyLocations.add(levelCmp.actors.getPosition(friendlyID));
        friendLocations.add(positionCmp.coord);
        setIdealLocations(blastAOE.idealLocations(enemyLocations, friendLocations));
        if(!idealLocations.isEmpty()) blastAOE.shift(idealLocations.keySet().first());
        else blastAOE.shift(positionCmp.coord);
    }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) { return null; }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {
        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;

        TextCellFactory.Glyph glyph = getGlyph();

        return new AnimateGlyphEvt(glyph, skill.animationType, startPos, targetCoord, eventCmp);
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
        return statsCmp.getSpellPower()*3;
    }


}
