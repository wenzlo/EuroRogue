package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.DamageType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.LightHandler;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;

public class Charge extends Ability
{

    private Skill skill = Skill.CHARGE;
    private  Coord targetedLocation;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    private GWTRNG rng = new GWTRNG();

    public Charge()
    {
        super("Charge", new PointAOE(Coord.get(-1,-1),2,2));
        statusEffects.put(StatusEffect.STAGGERED, new SEParameters(TargetType.ENEMY, SERemovalType.TIMED));
    }


    @Override
    public void perform(Entity targetEntity, ActionEvt action, EuroRogue game) {

        Entity performerEntity = game.getEntity(action.performerID);
        PositionCmp performerPositionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performerEntity);

        PositionCmp targetPositionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, targetEntity);

        Direction targetToPerformer = Direction.toGoTo(targetPositionCmp.coord, performerPositionCmp.coord);
        performerPositionCmp.orientation = Direction.toGoTo( performerPositionCmp.coord, targetPositionCmp.coord);
        Coord destination = targetPositionCmp.coord.translate(targetToPerformer);

        MoveEvt moveEvt = new MoveEvt(action.performerID, destination, 0.5f);
        performerEntity.add(moveEvt);

        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, game.ticker);
        tickerCmp.interrupt(targetEntity);
    }

    @Override
    public Skill getSkill() {
        return skill;
    }
    @Override
    public List<Skill> getReactions()  {
        return Arrays.asList(Skill.DODGE, Skill.ICE_SHIELD);
    }

    @Override
    public void updateAOE(Entity performer)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        aoe.setOrigin(positionCmp.coord);
        aoe.setMaxRange(1+statsCmp.getStr()/2);

    }

    @Override
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation = targetedLocation; }

    @Override
    public Coord getTargetedLocation() { return targetedLocation; }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {
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
    public TargetType getTargetType()
    {
        return TargetType.ENEMY;
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
