package EuroRogue.AbilityCmpSubSystems;

import com.badlogic.ashley.core.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WeaponCmp;
import EuroRogue.DamageType;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.IEventComponent;
import EuroRogue.MySparseLayers;
import EuroRogue.StatType;
import EuroRogue.StatusEffectCmps.SEParameters;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.TargetType;
import EuroRogue.WeaponType;
import squidpony.squidai.PointAOE;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class QuickStrike extends Ability
{
    private Skill skill = Skill.QUICK_STRIKE;
    public HashMap<StatusEffect, SEParameters> statusEffects = new HashMap<>();
    private TextCellFactory.Glyph glyph;
    public int itemID;
    public char chr;
    private Coord targetedLocation;

    public QuickStrike()
    {
        super("Quick Strike", new PointAOE(Coord.get(-1,-1),1,1));
    }

    public Skill getSkill() {
        return skill;
    }

    public List<Skill> getReactions() {
        return Arrays.asList();
    }

    @Override
    public void setAvailable(Entity performer, EuroRogue game) {
        super.setAvailable(performer, game);
        InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY,performer);
        Entity weaponEntity = game.getEntity(inventoryCmp.getSlotEquippedID(EquipmentSlot.RIGHT_HAND_WEAP));
        WeaponType weaponType = null;
        if(weaponEntity!=null)
        {
            WeaponCmp weaponCmp = (WeaponCmp) CmpMapper.getComp(CmpType.WEAPON, weaponEntity);
            weaponType = weaponCmp.weaponType;
        }

        if(isAvailable() && weaponType!=null)
        {
            itemID = weaponEntity.hashCode();
            chr = weaponType.chr;
            statusEffects = CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, performer).getStatusEffects();
        }
    }

    @Override
    public void updateAOE(Entity performer)
    {
        PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer);
        aoe.setOrigin(positionCmp.coord);
    }

    @Override
    public void setTargetedLocation(Coord targetedLocation) { this.targetedLocation = targetedLocation;}

    @Override
    public Coord getTargetedLocation() { return targetedLocation; }


    @Override
    public AnimateGlyphEvt genAnimateGlyphEvt(Entity performer, Coord targetCoord, IEventComponent eventCmp, MySparseLayers display)
    {

        Coord startPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, performer)).coord;
        TextCellFactory.Glyph glyph = ((GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, performer)).rightGlyph;

        return new AnimateGlyphEvt(glyph, AnimationsSys.AnimationType.BUMP, startPos, targetCoord, eventCmp);
    }

    @Override
    public TextCellFactory.Glyph getGlyph() {
        return glyph;
    }



    @Override
    public HashMap<StatusEffect, SEParameters> getStatusEffects() {

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
        return statsCmp.getAttackPower();
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
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getWeaponDamage()/2;

    }
    @Override
    public DamageType getDmgType(Entity performer)
    {
        Ability meleeAttack = CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, performer);
        if(meleeAttack!=null) return meleeAttack.getDmgType(performer);
        return DamageType.PIERCING;
    }
    @Override
    public int getTTPerform(Entity performer)
    {
        return ((StatsCmp) CmpMapper.getComp(CmpType.STATS,performer)).getTTMelee()/2;
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
}
