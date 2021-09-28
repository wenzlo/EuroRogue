package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.LightHandler;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatType;
import EuroRogue.TargetType;
import EuroRogue.DamageType;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.CmpType;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.MySparseLayers;
import squidpony.squidai.AOE;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class MeleeAttack extends Ability
{
    private Skill skill = Skill.MELEE_ATTACK;
    private boolean active = true;
    private  boolean scroll = false;
    private Integer scrollID = null;
    private Coord targetedLocation;
    private boolean available = false;
    public DamageType damageType =  DamageType.BLUDGEONING;
    private HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    public char chr = '•';
    public TextCellFactory.Glyph glyph;

    public MeleeAttack()
    {
        super("Melee Attack", new PointAOE(Coord.get(-1,-1), 1, 1));
    }


    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
        return Arrays.asList(Skill.DODGE, Skill.ICE_SHIELD);
    }

    @Override
    public boolean scroll() {
        return scroll;
    }

    @Override
    public void setScroll(boolean bool) { scroll = bool; }

    @Override
    public Integer getScrollID() { return scrollID; }

    @Override
    public void setScrollID(Integer id) { scrollID = id; }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void setAvailable(boolean available)
    {
        this.available=available;
    }

    @Override
    public boolean getActive()
    {
        return active;
    }
    @Override
    public void activate()
    {
        active=true;
    }
    @Override
    public void inactivate()
    {
        active=false;
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
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);

        AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, actor);
        ArrayList<Coord> enemyLocations = new ArrayList<>();
        for(Integer enemyID : aiCmp.visibleEnemies)
        {
            Coord position = levelCmp.actors.getPosition(enemyID);
            if(position!=null) enemyLocations.add(position);
        }

        ArrayList<Coord> friendLocations = new ArrayList<>();
        for(Integer friendlyID : aiCmp.visibleFriendlies)
        {
            Coord position = levelCmp.actors.getPosition(friendlyID);
            if(position!=null) enemyLocations.add(position);
        }
        friendLocations.add(positionCmp.coord);

        return idealLocations(positionCmp.coord, enemyLocations, friendLocations);
    }

    @Override
    public void setTargetedLocation(Coord targetedLocation) {this.targetedLocation = targetedLocation; }

    @Override
    public Coord getTargetedLocation() {
        return targetedLocation;
    }

    public AOE getAOE() {
        return aoe;
    }

    @Override
    public float getDmgReduction(StatsCmp statsCmp) {
        return 0;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.ENEMY;
    }

    @Override
    public int getDamage(Entity performer)
    {
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS, performer)).getWeaponDamage();
    }

    @Override
    public DamageType getDmgType(Entity performer)
    {
        return damageType;
    }

    @Override
    public int getTTPerform(Entity performer)
    {
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTMelee();
    }

    @Override
    public double getNoiseLvl(Entity performer)
    {
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        double noiseLvl = 0;
        switch (getDmgType(performer))
        {
            case BLUDGEONING:
            case SLASHING:
                noiseLvl=15;
                break;
            case PIERCING:
                noiseLvl=10;
                break;
        }
        return noiseLvl * statsCmp.getStatMultiplier(StatType.MELEE_SND_LVL);
    }

    @Override
    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }

    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {

        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;
        TextCellFactory.Glyph glyph = ((GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, performer)).rightGlyph;

        return new AnimateGlyphEvt(glyph, skill.animationType, startPos, targetCoord, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return glyph;
    }

    @Override
    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler)
    {

    }

    @Override
    public HashMap<StatusEffect, SEParameters> getStatusEffects() { return statusEffects; }

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
        return statsCmp.getAttackPower()*3;
    }



}
