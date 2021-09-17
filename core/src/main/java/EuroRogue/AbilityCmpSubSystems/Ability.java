package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class Ability extends Technique implements IAbilityCmpSubSys {
    public Ability(String name, AOE aoe) {
    }

    public static Ability newAbilityCmp(Skill skill, char[][] map)
    {
        switch (skill)
        {
            case ENLIGHTEN:
                Enlighten enlighten= new Enlighten();
                enlighten.setMap(map);
                return  enlighten;
            case ICE_SHIELD:
                IceShield iceShield = new IceShield();
                iceShield.setMap(map);
                return  new IceShield();
            case MAGIC_MISSILE:
                MagicMissile magicMissile = new MagicMissile();
                magicMissile.setMap(map);
                return magicMissile;
            case ERUPTION:
                Eruption eruption= new Eruption();
                eruption.setMap(map);
                return  eruption;
            case ARCANE_TOUCH:
                ArcaneTouch arcaneTouch = new ArcaneTouch();
                arcaneTouch.setMap(map);
                return arcaneTouch;
            case DAGGER_THROW:
                DaggerThrow daggerThrow = new DaggerThrow();
                daggerThrow.setMap(map);
                return daggerThrow;
            case CHILL:
                Chill chill = new Chill();
                chill.setMap(map);
                return chill;
            case IMMOLATE:
                Immolate immolate = new Immolate();
                immolate.setMap(map);
                return immolate;
            case DODGE:
                Dodge dodge = new Dodge();
                dodge.setMap(map);
                return dodge;
            case ENRAGE:
                Enrage enrage = new Enrage();
                enrage.setMap(map);
                return enrage;
            case MELEE_ATTACK:
                MeleeAttack meleeAttack = new MeleeAttack();
                meleeAttack.setMap(map);
                return meleeAttack;
            //case OPPORTUNITY: return new Opportunity();
        }
        return null;
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
    public int getDamage() {
        return 0;
    }

    @Override
    public void setDamage(Entity performer) {

    }

    @Override
    public DamageType getDmgType(Entity performer) {
        return null;
    }


    @Override
    public int getTTPerform() {
        return 0;
    }

    @Override
    public void setTTPerform(Entity performer) {

    }

    @Override
    public double getNoiseLvl(Entity performer) {
        return 0;
    }
}
