package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import EuroRogue.AOEType;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.DamageType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.IColoredString;
import EuroRogue.LightHandler;
import EuroRogue.MySparseLayers;
import EuroRogue.School;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.SERemovalType;
import EuroRogue.StatusEffectCmps.Stalking;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import squidpony.squidai.AOE;
import squidpony.squidai.BlastAOE;
import squidpony.squidai.ConeAOE;
import squidpony.squidai.Technique;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

public class Ability extends Technique implements Component
{
    public AOEType aoeType;
    public boolean aimable = false;
    public boolean aimed = false;
    public boolean available = false;
    private boolean active = true;
    public TextCellFactory.Glyph glyph;
    private boolean scroll = false;
    private Integer scrollID = null;

    public Ability(String name, AOE aoe, AOEType aoeType)
    {
        super(name, aoe);
        this.aoeType = aoeType;
    }

    public void perform(Entity targetEntity, ActionEvt action, EuroRogue game)
    {
        if(getSkill().skillType== Skill.SkillType.REACTION) inactivate();
        MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).display;
        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, game.dungeonWindow)).lightingHandler;
        Entity performerEntity = game.getEntity(action.performerID);
        if(action.skill.skillType== Skill.SkillType.REACTION) spawnGlyph(display, lightHandler, performerEntity);

        AnimateGlyphEvt animateGlyphEvt = genAnimateGlyphEvt(performerEntity, getTargetedLocation(), action, display);
        ItemEvt itemEvt = genItemEvent(performerEntity, targetEntity);
        if (animateGlyphEvt != null) performerEntity.add(animateGlyphEvt);
        if(itemEvt != null) performerEntity.add(itemEvt);

        if(getSkill().school != School.SUB) performerEntity.remove(Stalking.class);
    }

    public Skill getSkill() {
        return null;
    }

    public List<Skill> getReactions() {
        return null;
    }

    public boolean scroll() {
        return scroll;
    }

    public void setScroll(boolean bool) {
        scroll = bool;

    }

    public Integer getScrollID() {
        return scrollID;
    }

    public void setScrollID(Integer id) {
        scrollID=id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(Entity performer, EuroRogue game)
    {
        if(performer==null) return;
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, performer);
        AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType,performer);
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL,performer);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);

        boolean canAfford = manaPoolCmp.canAfford(getSkill());
        if(scroll()) canAfford = true;

        if(getSkill().skillType!=Skill.SkillType.REACTION && getSkill().skillType!=Skill.SkillType.BUFF &! aimed)
        {
            Entity targetEntity = game.getEntity(aiCmp.target);
            if(targetEntity==null)
                this.available = false;
            else
            {
                this.available = ( aiCmp.target!=null && canAfford && getActive() &&
                        getIdealLocations(performer, levelCmp).containsKey(((PositionCmp)CmpMapper.getComp(CmpType.POSITION, targetEntity)).coord));
            }

        }
        else  if(getSkill().skillType==Skill.SkillType.REACTION || getSkill().skillType==Skill.SkillType.BUFF )
        {
            this.available =canAfford && !getIdealLocations(performer, levelCmp).isEmpty() && getActive();
        }
        else if(aimed) this.available =canAfford;


    }

    public boolean getActive() {
        return active;
    }

    public void activate() {
        active=true;

    }

    public void inactivate() {
        active = false;
    }

    public void updateAOE(Entity performer) { }

    public OrderedMap<Coord, ArrayList<Coord>> getIdealLocations(Entity actor, LevelCmp levelCmp)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, actor);

        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, actor);
        AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, actor);

        return idealLocations(positionCmp.coord, aiCmp.getEnemyLocations(levelCmp), aiCmp.getFriendLocations(levelCmp));
    }
    public HashMap<Integer, Integer> getAOEtargetsDmg(Entity performerEntity, LevelCmp levelCmp, EuroRogue game)
    {
        HashMap<Integer, Integer> targets = new HashMap<>();
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
    public void setTargetedLocation(Coord targetLocation) {

    }

    public Coord getTargetedLocation() {
        return null;
    }

    public ItemEvt genItemEvent(Entity performer, Entity target) {
        return null;
    }

    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display) {
        return null;
    }

    public TextCellFactory.Glyph getGlyph() {
        return glyph;
    }

    public void spawnGlyph(MySparseLayers display, LightHandler lightingHandler, Entity performer) {

    }

    public HashMap<StatusEffect, SEParameters> getStatusEffects() {
        return null;
    }

    public void addStatusEffect(StatusEffect statusEffect, SEParameters seParameters) {

    }

    public void removeStatusEffect(StatusEffect statusEffect) {

    }

    public Integer getStatusEffectDuration(StatsCmp statsCmp, StatusEffect statusEffect) {
        return null;
    }

    public float getDmgReduction(StatsCmp statsCmp) {
        return 0;
    }

    public TargetType getTargetType() {
        return null;
    }

    public int getDamage(Entity performer) {
        return 0;
    }

    public DamageType getDmgType(Entity performer) {
        return DamageType.NONE;
    }

    public int getTTPerform(Entity performer) {
        return 0;
    }

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

    public OrderedMap<Coord, Double> apply(Coord user, Coord aimAt) {
        return super.apply(user, aimAt);
    }

    public static Ability newAbilityCmp(Skill skill, boolean player)
    {
        Ability ability;
        switch (skill)
        {
            case BACK_STAB:
                ability =  new BackStab();
                break;
            case QUICK_STRIKE:
                ability =  new QuickStrike();
                break;
            case ENLIGHTEN:
                ability = new Enlighten();
                break;
            case ICE_SHIELD:
                ability = new IceShield();
                break;
            case MAGIC_MISSILE:
                ability = new MagicMissile();
                break;
            case BLINK:
                ability =  new Blink();
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
            case STALK:
                ability = new Stalk();
                break;
            //case OPPORTUNITY: return new Opportunity();
            default:
                throw new IllegalStateException("Unexpected value: " + skill);
        }
        if(player && ability.aimable) ability.aimed=true;

        return ability;
    }
    public void postToLog(Entity performer, EuroRogue game)
    {
        SColor schoolColor = getSkill().school.color;

        IColoredString.Impl<SColor> line0 = new IColoredString.Impl<SColor>();
        line0.append("-----------------------------------------------------", schoolColor);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(line0);

        IColoredString.Impl<SColor> line1 = new IColoredString.Impl<SColor>();
        line1.append("Name: ");
        line1.append(name, schoolColor);
        line1.append("   School: ");
        line1.append(getSkill().school.name, schoolColor);
        line1.append("   ttPerform: ");
        line1.append(((Integer)getTTPerform(performer)).toString(), schoolColor);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(line1);


        IColoredString.Impl<SColor> line2 = new IColoredString.Impl<SColor>();
        line2.append("MaxRange: ");
        line2.append(((Integer)aoe.getMaxRange()).toString(), schoolColor);
        line2.append("   MinRange: ");
        line2.append(((Integer)aoe.getMinRange()).toString(), schoolColor);

        try {
            BlastAOE blastAOE = (BlastAOE) aoe;
            line2.append("   Blast Radius: ");
            line2.append(((Integer)blastAOE.getRadius()).toString(), schoolColor);
        } catch (Exception e) {
            try{
                ConeAOE coneAOE = (ConeAOE) aoe;
                line2.append("   Cone: "+coneAOE.getSpan());
            }catch(Exception f){}
        }
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(line2);
        IColoredString.Impl<SColor> line3 = new IColoredString.Impl<SColor>();
        line3.append("Damage: ");
        line3.append(((Integer)getDamage(performer)).toString()+" ", schoolColor);

        line3.append(getDmgType(performer).name(), schoolColor);
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(line3);

        IColoredString.Impl<SColor> line4 = new IColoredString.Impl<SColor>();
        line4.append("Status Effects Applied: ");
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(line4);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, performer);
        for(StatusEffect statusEffect : getStatusEffects().keySet())
        {
            IColoredString.Impl<SColor> effectLine = new IColoredString.Impl<SColor>("   "+statusEffect.name+"  ", schoolColor);
            SERemovalType seRemovalType = getStatusEffects().get(statusEffect).seRemovalType;
            if(seRemovalType == SERemovalType.TIMED)
                effectLine.append(getStatusEffectDuration(statsCmp, statusEffect)+" ticks", SColor.WHITE);
            else
                effectLine.append(seRemovalType.name());
            ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(effectLine);
        }

        LevelCmp levelCmp = (LevelCmp)CmpMapper.getComp(CmpType.LEVEL, game.currentLevel);
        IColoredString.Impl<SColor> lineTargets = new IColoredString.Impl<SColor>();
        lineTargets.append("Available Targets:");
        ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(lineTargets);
        if(getSkill()==Skill.SHATTER)
        {
            for(Integer id : getAOEtargetsDmg(performer, levelCmp, game).keySet())
            {
                Entity targetEntity = game.getEntity(id);
                if(targetEntity != null)
                {
                    NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, targetEntity);
                    IColoredString.Impl<SColor> targetLine = new IColoredString.Impl<SColor>("   "+nameCmp.name+"  ", SColor.LIGHT_YELLOW_DYE);
                    ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(targetLine);
                }
            }

        } else {

            for(Coord coord : getIdealLocations(performer, levelCmp).keySet())
            {
                Entity targetEntity = game.getEntity(levelCmp.actors.get(coord));
                if(targetEntity != null)
                {
                    NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, targetEntity);
                    IColoredString.Impl<SColor> targetLine = new IColoredString.Impl<SColor>("   "+nameCmp.name+"  ", SColor.LIGHT_YELLOW_DYE);
                    ((LogCmp) CmpMapper.getComp(CmpType.LOG, game.logWindow)).logEntries.add(targetLine);
                }
            }
        }

    }
}
