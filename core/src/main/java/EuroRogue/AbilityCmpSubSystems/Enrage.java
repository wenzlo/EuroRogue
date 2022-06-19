package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.IColoredString;
import EuroRogue.AOEType;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.TargetType;
import squidpony.StringKit;
import squidpony.squidai.AOE;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Enrage extends Ability
{
    private Coord targetedLocation;

    public Enrage()
    {
        super("Enrage", new PointAOE(Coord.get(-1,-1),0,0), AOEType.POINT);
        statusEffects.put(StatusEffect.ENRAGED, new SEParameters(TargetType.SELF, SERemovalType.SHORT_REST));
        super.skill = Skill.ENRAGE;
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
        GlyphsCmp glyphsCmp = (GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performer);

        return new AnimateGlyphEvt(glyphsCmp.glyph, AnimationsSys.AnimationType.SELF_BUFF, eventCmp);
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
    public int getTTPerform(Entity performer)
    {
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTMelee();
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 10;
    }

    @Override
    public void postToLog(Entity performer, EuroRogue game) {
        super.postToLog(performer, game);

        SColor schoolColor = getSkill().school.color;
        List<String> description = StringKit.wrap(
                "Applies Enraged status effect to the caster. Enrage is maintained until the next Short Rest." +
                        " Increases Attack Power (10% * Strength), ttCast(2x), and ttRest(2x)"
                , 40);

        IColoredString.Impl<SColor> desc = new IColoredString.Impl<SColor>();
        desc.append("Description:", SColor.WHITE);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(desc);

        for (String line : description) {
            IColoredString.Impl<SColor> lineText = new IColoredString.Impl<SColor>();
            lineText.append("    " + line, SColor.LIGHT_GRAY);
            ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineText);

        }


        IColoredString.Impl<SColor> lineLast = new IColoredString.Impl<SColor>();
        lineLast.append("-----------------------------------------------------", schoolColor);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineLast);
    }
}
