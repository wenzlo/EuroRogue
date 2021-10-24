package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import squidpony.squidai.AimLimit;
import squidpony.squidai.ConeAOE;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class ConeOfCold extends Ability
{
    private Skill skill = Skill.CONE_OF_COLD;
    private Coord targetedLocation;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public TextCellFactory.Glyph glyph;


    public ConeOfCold()
    {

        super("Cone of Cold", new ConeAOE(Coord.get(0,0),1, 0, 90, Radius.CIRCLE));
        aoe.getReach().limit = AimLimit.FREE;
        statusEffects.put(StatusEffect.CHILLED, new SEParameters(TargetType.ENEMY, SERemovalType.TIMED));
        //super.aimable = true;
    }


    /**
     * -Need to override this for any Ability with a non-Point AOE to set targetedLocation-ER
     *
     * This does one last validation of the location aimAt (checking that it is within the valid range for this
     * Technique) before getting the area affected by the AOE targeting that cell. It considers the origin of the AOE
     * to be the Coord parameter user, for purposes of directional limitations and for AOE implementations that need
     * the user's location, such as ConeAOE and LineAOE.
     * <p>
     * YOU MUST CALL setMap() with the current map status at some point before using this method, and call it again if
     * the map changes. Failure to do so can cause serious bugs, from logic errors where monsters consider a door
     * closed when it is open or vice versa, to an ArrayIndexOutOfBoundsException being thrown if the player moved to a
     * differently-sized map and the Technique tries to use the previous map with coordinates from the new one.
     *
     * @param user  The position of the Technique's user, x first, y second.
     * @param aimAt A target Coord typically obtained from idealLocations that determines how to position the AOE.
     * @return a HashMap of Coord keys to Double values from 1.0 (fully affected) to 0.0 (unaffected).
     */
    @Override
    public OrderedMap<Coord, Double> apply(Coord user, Coord aimAt)
    {
        setTargetedLocation(aimAt);
        ConeAOE coneAOE = (ConeAOE) aoe;
        coneAOE.setEndCenter(aimAt);

        return coneAOE.findArea();

    }

    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
        return Arrays.asList();
    }

    @Override
    public void updateAOE(Entity performer)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer);

        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        ConeAOE coneAOE = (ConeAOE) aoe;

        coneAOE.setRadius(1+statsCmp.getIntel()/2);
        coneAOE.setOrigin(positionCmp.coord);
        coneAOE.setMaxRange(1+statsCmp.getIntel()/2);
    }

    @Override
    public OrderedMap<Coord, ArrayList<Coord>> getIdealLocations(Entity actor, LevelCmp levelCmp)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);
        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, actor);
        HashSet<Coord> priorityTargets = new HashSet<>();
        HashSet<Coord> otherTargets = new HashSet<>();
        otherTargets.addAll(aiCmp.getEnemyLocations(levelCmp));
        HashSet<Coord> friendlyTargets = new HashSet<>();
        friendlyTargets.addAll(aiCmp.getFriendLocations(levelCmp));

        Integer priorityTargetID = aiCmp.target;
        Coord priorityTargetPos = levelCmp.actors.getPosition(priorityTargetID);
        if(priorityTargetPos!=null)
        {
            priorityTargets.add(priorityTargetPos);
            otherTargets.remove(priorityTargetPos);
        }

        return idealLocations(positionCmp.coord, aiCmp.getEnemyLocations(levelCmp), aiCmp.getFriendLocations(levelCmp));
    }

    @Override
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation = targetedLocation; }

    @Override
    public Coord getTargetedLocation() { return targetedLocation; }


    @Override
    public float getDmgReduction(StatsCmp statsCmp) {
        return 0;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.AOE;
    }

    @Override
    public int getDamage(Entity performer)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        return 0;
    }

    @Override
    public DamageType getDmgType(Entity performer)
    {
        return DamageType.ICE;
    }

    @Override
    public int getTTPerform(Entity performer)
    {
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTCast();
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 10;
    }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) { return null; }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {
        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;

        return new AnimateGlyphEvt(glyph, skill.animationType, startPos, targetCoord, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return glyph;
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler, Entity performer)
    {
        glyph = display.glyph('•',getSkill().school.color, aoe.getOrigin().x, aoe.getOrigin().y);
        SColor color = skill.school.color;

        Light light = new Light(Coord.get(aoe.getOrigin().x*3, aoe.getOrigin().y*3), new Radiance(2, SColor.lerpFloatColors(color.toFloatBits(), SColor.WHITE_FLOAT_BITS, 0.3f)));
        glyph.setName(light.hashCode() + " " + "0" + " temp");
        lightingHandler.addLight(light.hashCode(), light);
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
        return statsCmp.getSpellPower()*2;
    }


}
