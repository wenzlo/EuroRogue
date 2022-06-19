package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.IColoredString;
import EuroRogue.AOEType;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
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
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.TargetType;
import squidpony.StringKit;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Bresenham;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Charge extends Ability
{
    private  Coord targetedLocation;


    public Charge()
    {
        super("Charge", new PointAOE(Coord.get(-1,-1),2,2), AOEType.POINT);
        statusEffects.put(StatusEffect.STAGGERED, new SEParameters(TargetType.ENEMY, SERemovalType.TIMED));
        super.skill = Skill.CHARGE;
    }
    @Override
    public OrderedMap<Coord, Double> apply(Coord user, Coord aimAt)
    {
        setTargetedLocation(aimAt);

        return super.apply(user, aimAt);

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
        return Arrays.asList(Skill.DODGE, Skill.ICE_SHIELD, Skill.BLINK);
    }

    @Override
    public void setAvailable(Entity performer, EuroRogue game) {
        super.setAvailable(performer, game);
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, performer);
        AICmp aiCmp = (AICmp) CmpMapper.getAIComp(statsCmp.mobType.aiType, performer);
        LevelCmp levelCmp = (LevelCmp)CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
        setTargetedLocation(levelCmp.actors.getPosition(aiCmp.target));
        if(targetedLocation!=null && available)
        {
            Coord performerLoc = ((PositionCmp)CmpMapper.getComp(CmpType.POSITION,performer)).coord;
            List<Coord> line = Arrays.asList(Bresenham.line2D_(performerLoc, getTargetedLocation()));

            for(Coord coord : line.subList(1, line.size()-1))
            {
                if(levelCmp.isBlocked(coord))
                {
                    available = false;
                    break;
                }
            }
        }
    }

    @Override
    public void updateAOE(Entity performer)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        aoe.setOrigin(positionCmp.coord);
        int str = Math.max(statsCmp.getStr(), skill.strReq);
        aoe.setMaxRange(1+str/2);

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
        return new AnimateGlyphEvt(glyph, AnimationsSys.AnimationType.CHARGE, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return null;
    }


    @Override
    public HashMap<StatusEffect, SEParameters> getStatusEffects(Entity performer) {

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
        return 15;
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
        return 15;
    }

    @Override
    public void postToLog(Entity performer, EuroRogue game) {
        super.postToLog(performer, game);

        SColor schoolColor = getSkill().school.color;
        List<String> description = StringKit.wrap(
                "Charge at the target. Interrupts the targets current action."
                , 40);

        IColoredString.Impl<SColor> desc = new IColoredString.Impl<SColor>();
        desc.append("Description:", SColor.WHITE);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(desc);

        for(String line : description)
        {
            IColoredString.Impl<SColor> lineText = new IColoredString.Impl<SColor>();
            lineText.append("    "+line, SColor.LIGHT_GRAY);
            ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineText);

        }


        IColoredString.Impl<SColor> lineLast = new IColoredString.Impl<SColor>();
        lineLast.append("-----------------------------------------------------", schoolColor);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineLast);
    }
}
