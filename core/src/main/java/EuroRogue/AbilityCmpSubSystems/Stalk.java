package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import EuroRogue.IColoredString;
import EuroRogue.AOEType;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.IColoredString;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.DamageType;
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
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Stalk extends Ability
{

    private Coord targetedLocation;

    public Stalk()
    {
        super("Stalk", new PointAOE(Coord.get(-1,-1),0,0), AOEType.POINT);
        statusEffects.put(StatusEffect.STALKING, new SEParameters(TargetType.SELF, SERemovalType.SHORT_REST));
        super.skill = Skill.STALK;
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
    public void setAvailable(Entity performer, EuroRogue game)
    {
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL,performer);
        boolean canAfford = manaPoolCmp.canAfford(getSkill());
        if(scroll()) canAfford = true;

        boolean detected = CmpMapper.detected(performer);

        this.available = canAfford && !detected && CmpMapper.getStatusEffectComp(StatusEffect.STALKING, performer) == null;

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
    public TargetType getTargetType()
    {
        return TargetType.SELF;
    }


    @Override
    public DamageType getDmgType(Entity performer) {
        return DamageType.NONE;
    }

    @Override
    public int getTTPerform(Entity performer)
    {
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTCast();
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 0;
    }

    @Override
    public void postToLog(Entity performer, EuroRogue game) {
        super.postToLog(performer, game);

        SColor schoolColor = getSkill().school.color;
        List<String> description = StringKit.wrap(
                "Applies Stalking status effect to the caster. Stalking is maintained until the next Short Rest or until the caster is detected." +
                        " Increases Night Vision Radius and Light Level needed to detect you. Decreases Casters Sound Level when moving and Minimum light " +
                        "Detection Level. Enables UI overlay that highlights which tiles enemies can detect you in."
                , 55);

        IColoredString.Impl<SColor> desc = new IColoredString.Impl<SColor>();
        desc.append("Description:", SColor.WHITE);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(desc);

        for (String line : description) {
            IColoredString.Impl<SColor> lineText = new IColoredString.Impl<SColor>();
            lineText.append(" " + line, SColor.LIGHT_GRAY);
            ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineText);

        }


        IColoredString.Impl<SColor> lineLast = new IColoredString.Impl<SColor>();
        lineLast.append("-----------------------------------------------------", schoolColor);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineLast);
    }


}
