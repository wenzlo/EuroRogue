package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import EuroRogue.IColoredString;
import EuroRogue.AOEType;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.DamageType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.LightHandler;
import EuroRogue.MySparseLayers;
import EuroRogue.ScheduledEvt;
import EuroRogue.SortByDistance;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.TargetType;
import squidpony.StringKit;
import squidpony.squidai.BlastAOE;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class Blink extends Ability
{
    private  Coord targetedLocation;

    public Blink()
    {
        super("Blink", new PointAOE(Coord.get(-1,-1),0,10), AOEType.POINT);
        super.skill = Skill.BLINK;
        super.reactsTo = Arrays.asList(Skill.MELEE_ATTACK, Skill.DAGGER_THROW, Skill.CHARGE);
    }

    @Override
    public void perform(Entity targetEntity, ActionEvt action, EuroRogue game)
    {
        //TODO refund mana if targetEntity is dead
        if(targetEntity==null) return;
        inactivate();

        Entity performerEntity = game.getEntity(action.performerID);
        FOVCmp fovCmp = (FOVCmp) CmpMapper.getComp(CmpType.FOV, performerEntity);
        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow);
        interrupt(performerEntity, game, windowCmp);

        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);

        PositionCmp targetPosition = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, targetEntity);
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performerEntity);
        StatsCmp performerStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performerEntity);

        BlastAOE blastAOE = new BlastAOE(positionCmp.coord, performerStats.getIntel(), Radius.CIRCLE);
        blastAOE.setMap(levelCmp.decoDungeon);
        List<Coord> blastArea = new ArrayList<>();
        blastArea.addAll(blastAOE.findArea().keySet());
        Collections.sort(blastArea, new SortByDistance(targetPosition.coord));
        Collections.reverse(blastArea);
        blastArea.removeAll(fovCmp.seen.copy().not());
        Coord destination = null;

        for(Coord coord : blastArea)
        {
            if(!levelCmp.isBlocked(coord))
            {
                char chr = levelCmp.decoDungeon[coord.x][coord.y];
                if(chr!='#' && chr!='~' && chr!='??' && chr!='+')
                {

                    destination = coord;
                    break;
                }
            }
        }
        if(destination==null)
        {
            System.out.println("Blink Dest Bug");
            System.out.println(blastArea);
            destination = positionCmp.coord;
        }

        glyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performerEntity)).glyph;

        AnimateGlyphEvt animateGlyphEvt = genAnimateGlyphEvt(performerEntity, destination, action, windowCmp.display);

        performerEntity.add(animateGlyphEvt);
        levelCmp.actors.move(positionCmp.coord, destination);
        positionCmp.coord = destination;




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
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, performer);
        aoe.setOrigin(positionCmp.coord);
        aoe.setMaxRange(statsCmp.getSpellPower()/4);
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
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler, Entity performer) {

    }
    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {

        TextCellFactory.Glyph glyph = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, performer)).glyph;
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer);
        return new AnimateGlyphEvt(glyph, AnimationsSys.AnimationType.BLINK, positionCmp.coord, targetCoord, eventCmp );
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
    public float getDmgReduction(StatsCmp statsCmp)
    {
        return 1f;
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

    private void interrupt(Entity entity, EuroRogue game, WindowCmp windowCmp)
    {

        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER,game.ticker);
        ParticleEffectsCmp peaCmp = (ParticleEffectsCmp) CmpMapper.getComp(CmpType.PARTICLES, entity);
        ArrayList<ScheduledEvt> schedule = ticker.getScheduledActions(entity);

        for(ScheduledEvt event : schedule)
        {

            try {
                ActionEvt actionEvt = (ActionEvt) event.eventComponent;
                Skill skill = actionEvt.skill;

                Ability ability = CmpMapper.getAbilityComp(skill, entity);
                /*System.out.println("Removing effects for ability "+ability.name);
                System.out.println();*/
                peaCmp.removeEffectsByGlyph(ability.getGlyph(), windowCmp.display);
                windowCmp.lightingHandler.removeLightByGlyph(ability.getGlyph());

            } catch (Exception e) {
                continue;
            }

        }
        game.ticker.getComponent(TickerCmp.class).actionQueue.removeAll(schedule);
    }

    @Override
    public void postToLog(Entity performer, EuroRogue game) {
        super.postToLog(performer, game);

        SColor schoolColor = getSkill().school.color;
        List<String> description = StringKit.wrap(
                "Instantly teleports caster away from attacker. Distance is determined by Spell Power."
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
