package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.LightHandler;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.TargetType;
import EuroRogue.DamageType;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.MySparseLayers;
import squidpony.squidai.AOE;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public interface IAbilityCmpSubSys extends Component
{
    Skill       getSkill();
    List<Skill> getReactions();
    boolean     scroll();
    void        setScroll(boolean bool);
    Integer     getScrollID();
    void        setScrollID(Integer id);
    boolean     isAvailable();
    void        setAvailable(boolean available);
    boolean     getActive();
    void        activate();
    void        inactivate();
    void        setIdealLocations(OrderedMap<Coord, ArrayList<Coord>> targets);
    OrderedMap<Coord, ArrayList<Coord>>
                getIdealLocations();
    void        setTargetedLocation(Coord targetLocation);
    Coord       getTargetedLocation();
    AOE         getAOE();
    void        updateAOE(Entity actor, LevelCmp levelCmp, AOE aoe, Entity scrollEntity);
    ItemEvt     genItemEvent(Entity performer, Entity target);
    AnimateGlyphEvt
                genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display);
    TextCellFactory.Glyph getGlyph();
    void        spawnGlyph(MySparseLayers display, LightHandler lightingHandler);
    HashMap<StatusEffect, SEParameters>
                getStatusEffects();
    void        addStatusEffect(StatusEffect statusEffect, SEParameters seParameters);
    void        removeStatusEffect(StatusEffect statusEffect);
    Integer     getStatusEffectDuration(StatsCmp statsCmp, StatusEffect statusEffect);
    float       getDmgReduction(StatsCmp statsCmp);
    TargetType
                getTargetType();
    int         getDamage();
    void        setDamage(Entity performer);
    DamageType  getDmgType(Entity performer);
    int         getTTPerform();
    void        setTTPerform(Entity performer);
    double      getNoiseLvl(Entity performer);

    static IAbilityCmpSubSys newAbilityCmp(Skill skill, char[][] map)
    {
        switch (skill)
        {
            case ENLIGHTEN: return new Enlighten();
            case ICE_SHIELD: return  new IceShield();
            case MAGIC_MISSILE: return new MagicMissile();
            case ERUPTION:
                Eruption eruption= new Eruption();
                eruption.getAOE().setMap(map);
                return  eruption;
            case ARCANE_TOUCH: return new ArcaneTouch();
            case DAGGER_THROW: return new DaggerThrow();
            case CHILL: return new Chill();
            case IMMOLATE: return new Immolate();
            case DODGE: return new Dodge();
            case ENRAGE: return  new Enrage();
            case MELEE_ATTACK: return new MeleeAttack();
            //case OPPORTUNITY: return new Opportunity();
        }
        return null;
    }


}

