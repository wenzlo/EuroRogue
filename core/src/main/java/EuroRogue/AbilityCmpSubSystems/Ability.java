package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.PositionCmp;
import EuroRogue.LightHandler;
import EuroRogue.TargetType;
import EuroRogue.DamageType;
import EuroRogue.MySparseLayers;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.squidai.AOE;
import squidpony.squidai.Technique;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Ability extends Technique implements IAbilityCmpSubSys
{
    public Ability(String name, AOE aoe) { super(name, aoe); }



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
        return false;
    }

    @Override
    public void setScroll(boolean bool) {

    }

    @Override
    public Integer getScrollID() {
        return null;
    }

    @Override
    public void setScrollID(Integer id) {

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
        return false;
    }

    @Override
    public void activate() {

    }

    @Override
    public void inactivate() {

    }

    @Override
    public void updateAOE(Entity performer) { }

    @Override
    public OrderedMap<Coord, ArrayList<Coord>> getIdealLocations(Entity actor, LevelCmp levelCmp) {
        return null;
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

    public static Ability newAbilityCmp(Skill skill)
    {
        System.out.println(skill);
        switch (skill)
        {
            case ENLIGHTEN:
                Enlighten enlighten= new Enlighten();
                return  enlighten;
            case ICE_SHIELD:
                IceShield iceShield = new IceShield();
                return  new IceShield();
            case MAGIC_MISSILE:
                MagicMissile magicMissile = new MagicMissile();
                return magicMissile;
            case ERUPTION:
                Eruption eruption = new Eruption();
                return  eruption;
            case ARCANE_TOUCH:
                ArcaneTouch arcaneTouch = new ArcaneTouch();
                return arcaneTouch;
            case DAGGER_THROW:
                DaggerThrow daggerThrow = new DaggerThrow();
                return daggerThrow;
            case CHILL:
                Chill chill = new Chill();
                return chill;
            case IMMOLATE:
                Immolate immolate = new Immolate();
                return immolate;
            case DODGE:
                Dodge dodge = new Dodge();
                return dodge;
            case ENRAGE:
                Enrage enrage = new Enrage();
                return enrage;
            case MELEE_ATTACK:
                MeleeAttack meleeAttack = new MeleeAttack();
                return meleeAttack;
            //case OPPORTUNITY: return new Opportunity();
        }
        return null;
    }
}
