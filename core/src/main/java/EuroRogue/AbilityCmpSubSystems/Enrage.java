package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.Light;
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
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Enrage extends Ability
{
    private Skill skill = Skill.ENRAGE;
    private Coord targetedLocation;
    private boolean available;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public TextCellFactory.Glyph glyph;


    public Enrage()
    {
        super("Enrage", new PointAOE(Coord.get(-1,-1),0,0));
        statusEffects.put(StatusEffect.ENRAGED, new SEParameters(TargetType.SELF, SERemovalType.SHORT_REST));
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
    public OrderedMap<Coord, ArrayList<Coord>> getIdealLocations(Entity actor, LevelCmp levelCmp)
    {
        Coord location = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION,actor)).coord;
        ArrayList<Coord> self = new ArrayList<>();
        self.add(location);
        return new OrderedMap(self,self);
    }


    @Override
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation = targetedLocation; }

    @Override
    public Coord getTargetedLocation() {
        return targetedLocation;
    }

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
        return new AnimateGlyphEvt(glyph, AnimationsSys.AnimationType.SELF_BUFF, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return null;
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler)
    {

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
    public int getDamage(Entity performer) {
        return 0;
    }

    @Override
    public DamageType getDmgType(Entity performer) {
        return null;
    }

    @Override
    public int getTTPerform(Entity performer)
    {
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTMelee();
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 10;
    }
}
