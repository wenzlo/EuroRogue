package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.DamageType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.LightHandler;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import squidpony.squidai.AOE;
import squidpony.squidai.Technique;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Ability extends Technique implements IAbilityCmpSubSys
{
    public boolean aimable = false;
    public boolean aimed = false;
    private boolean active = true;
    private boolean scroll = false;
    private Integer scrollID = null;
    public Ability(String name, AOE aoe) { super(name, aoe); }


    @Override
    public void perform(Entity targetEntity, ActionEvt action, EuroRogue game)
    {
        MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).display;
        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;
        Entity performerEntity = game.getEntity(action.performerID);
        if(action.skill.skillType== Skill.SkillType.REACTION) spawnGlyph(display, lightHandler);
        AnimateGlyphEvt animateGlyphEvt = genAnimateGlyphEvt(performerEntity, getTargetedLocation(), action, display);

        ItemEvt itemEvt = genItemEvent(performerEntity, targetEntity);
        if (animateGlyphEvt != null) performerEntity.add(animateGlyphEvt);
        if(itemEvt != null) performerEntity.add(itemEvt);
    }

    @Override
    public Skill getSkill() {
        return null;
    }

    @Override
    public List<Skill> getReactions() {
        return null;
    }

    @Override
    public boolean scroll() {
        return scroll;
    }

    @Override
    public void setScroll(boolean bool) {
        scroll = bool;

    }

    @Override
    public Integer getScrollID() {
        return scrollID;
    }

    @Override
    public void setScrollID(Integer id) {
        scrollID=id;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void setAvailable(boolean available) {

    }

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public void activate() {
        active=true;

    }

    @Override
    public void inactivate() {
        active =false;
    }

    @Override
    public void updateAOE(Entity performer) { }


    @Override
    public OrderedMap<Coord, ArrayList<Coord>> getIdealLocations(Entity actor, LevelCmp levelCmp)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);


        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, actor);

        return idealLocations(positionCmp.coord, aiCmp.getEnemyLocations(levelCmp), aiCmp.getFriendLocations(levelCmp));
    }

    @Override
    public HashMap<Integer, Integer> getAOEtargetsDmg(LevelCmp levelCmp, EuroRogue game)
    {
        HashMap<Integer, Integer> targets = new HashMap<>();
        Integer performerID = levelCmp.actors.get(aoe.getOrigin());
        Entity performerEntity = game.getEntity(performerID);
        for(Coord coord : aoe.findArea().keySet())
        {
            if(levelCmp.actors.positions().contains(coord))
            {
                Integer targetID = levelCmp.actors.get(coord);
                Entity aoeTarEnt = game.getEntity(targetID);
                PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, aoeTarEnt);
                int dmg = (int) (aoe.findArea().get(positionCmp.coord)*getDamage(performerEntity));
                targets.put(levelCmp.actors.get(coord), dmg);
            }
        }
        return targets;
    }

    @Override
    public void setTargetedLocation(Coord targetLocation) {

    }

    @Override
    public Coord getTargetedLocation() {
        return null;
    }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display) {
        return null;
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
        return null;
    }

    @Override
    public void addStatusEffect(StatusEffect statusEffect, SEParameters seParameters) {

    }

    @Override
    public void removeStatusEffect(StatusEffect statusEffect) {

    }

    @Override
    public Integer getStatusEffectDuration(StatsCmp statsCmp, StatusEffect statusEffect) {
        return null;
    }

    @Override
    public float getDmgReduction(StatsCmp statsCmp) {
        return 0;
    }

    @Override
    public TargetType getTargetType() {
        return null;
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
    public int getTTPerform(Entity performer) {
        return 0;
    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 0;
    }

    /**
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
    public OrderedMap<Coord, Double> apply(Coord user, Coord aimAt) {
        return super.apply(user, aimAt);
    }

    public static Ability newAbilityCmp(Skill skill, boolean player)
    {
        Ability ability;
        switch (skill)
        {
            case ENLIGHTEN:
                ability = new Enlighten();
                break;
            case ICE_SHIELD:
                ability = new IceShield();
                break;
            case MAGIC_MISSILE:
                ability = new MagicMissile();
                break;
            case ERUPTION:
                ability = new Eruption();
                break;
            case SHATTER:
                ability = new Shatter();
                break;
            case CONE_OF_COLD:
                ability = new ConeOfCold();
                break;
            case ARCANE_TOUCH:
                ability = new ArcaneTouch();
                break;
            case DAGGER_THROW:
                ability = new DaggerThrow();
                break;
            case CHILL:
                ability = new Chill();
                break;
            case IMMOLATE:
                ability = new Immolate();
                break;
            case DODGE:
                ability = new Dodge();
                break;
            case CHARGE:
                ability = new Charge();
                break;
            case ENRAGE:
                ability = new Enrage();
                break;
            case MELEE_ATTACK:
                ability = new MeleeAttack();
                break;
            //case OPPORTUNITY: return new Opportunity();
            default:
                throw new IllegalStateException("Unexpected value: " + skill);
        }
        if(player && ability.aimable) ability.aimed=true;

        return ability;
    }
}
