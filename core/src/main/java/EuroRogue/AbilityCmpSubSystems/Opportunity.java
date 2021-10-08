package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.GlyphsCmp;
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
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import squidpony.squidai.AOE;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Opportunity extends Ability
{
    //private Skill skill = Skill.OPPORTUNITY;
    private Skill skill = null;
    private PointAOE aoe = new PointAOE(Coord.get(-1,-1), 1, 1);
    private Coord targetedLocation;
    private boolean available = false;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();

    public Opportunity()
    {
        super("Opportunity", new PointAOE(Coord.get(-1,-1), 1, 1));
    }

    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
        return Arrays.asList(Skill.DODGE);
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

    public OrderedMap<Coord, ArrayList<Coord>> getIdealLocations(Entity actor, LevelCmp levelCmp)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);

        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, actor);
        ArrayList<Coord> enemyLocations = new ArrayList<>();
        for(Integer enemyID : aiCmp.visibleEnemies) enemyLocations.add(levelCmp.actors.getPosition(enemyID));
        ArrayList<Coord> friendLocations = new ArrayList<>();
        for(Integer friendlyID : aiCmp.visibleFriendlies) enemyLocations.add(levelCmp.actors.getPosition(friendlyID));
        friendLocations.add(positionCmp.coord);
        return idealLocations(positionCmp.coord, enemyLocations, friendLocations);
    }

    @Override
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation = targetedLocation; }

    @Override
    public Coord getTargetedLocation() { return targetedLocation; }

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
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS, performer)).getWeaponDamage();
    }

    @Override
    public DamageType getDmgType(Entity performer)
    {
        return DamageType.BLUDGEONING;
    }

    @Override
    public int getTTPerform(Entity performer) {
        return 0;
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 0;
    }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent actionEvt, MySparseLayers display)
    {
        TextCellFactory.Glyph glyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performer)).glyph;
        Coord startLoc = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;

        return new AnimateGlyphEvt(glyph, skill.animationType, startLoc, targetCoord, actionEvt);
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
