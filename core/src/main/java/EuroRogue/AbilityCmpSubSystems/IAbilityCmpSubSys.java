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
    void        updateAOE(Entity performer);
    OrderedMap<Coord, ArrayList<Coord>>
                getIdealLocations(Entity actor, LevelCmp levelCmp);
    void        setTargetedLocation(Coord targetLocation);
    Coord       getTargetedLocation();

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
    int         getDamage(Entity performer);
    DamageType  getDmgType(Entity performer);
    int         getTTPerform(Entity performer);
    double      getNoiseLvl(Entity performer);


}

