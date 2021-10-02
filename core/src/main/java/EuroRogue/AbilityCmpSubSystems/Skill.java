package EuroRogue.AbilityCmpSubSystems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.School;
import EuroRogue.WeaponType;

public enum Skill
{
    MELEE_ATTACK(         "Melee Attack", MeleeAttack.class,   AnimationsSys.AnimationType.BUMP,         SkillType.ACTION,  School.PHY, new School[0],      new School[]{School.PHY}, null,       null,  0, 0, 0, 0, 0),
    ENLIGHTEN(               "Enlighten", Enlighten.class,     AnimationsSys.AnimationType.SELF_BUFF,      SkillType.BUFF,  School.ARC, new School[]{School.ARC},      new School[]{School.ARC}, null,          StatusEffect.ENLIGHTENED,  0, 0, 0, 0, 3),
    ICE_SHIELD(               "Ice Shield", IceShield.class,     AnimationsSys.AnimationType.MELEE_MAGIC,      SkillType.REACTION,  School.ICE, new School[]{School.ICE, School.PHY},      new School[]{School.ICE}, null,           StatusEffect.CHILLED,  0, 0, 1, 2, 1),
    MAGIC_MISSILE(       "Magic Missile", MagicMissile.class,  AnimationsSys.AnimationType.PROJ_MAGIC,   SkillType.ACTION,  School.ARC, new School[]{School.ARC, School.ARC}, new School[]{School.ARC, School.ARC}, null,  null,  0, 0, 0, 0, 3),
    DAGGER_THROW(       "Dagger Throw", DaggerThrow.class,  AnimationsSys.AnimationType.PROJECTILE,   SkillType.ACTION,  School.PHY, new School[0], new School[]{School.PHY}, WeaponType.DAGGER, null,   0, 3, 0, 0, 0),
    CHILL(     "Chill", Chill.class, AnimationsSys.AnimationType.MELEE_MAGIC,  SkillType.ACTION,  School.ICE, new School[]{School.ICE},      new School[]{School.ICE}, null,              StatusEffect.CHILLED,  0, 0, 0, 0, 2),
    IMMOLATE(                 "Immolate", Immolate.class,      AnimationsSys.AnimationType.MELEE_MAGIC,  SkillType.ACTION,  School.FIR, new School[]{School.FIR},      new School[]{School.FIR}, null,             StatusEffect.CALESCENT,  0, 0, 0, 0, 2),
    ERUPTION(                 "Eruption", Eruption.class,      AnimationsSys.AnimationType.BLAST,  SkillType.ACTION,  School.FIR, new School[]{School.FIR, School.FIR, School.FIR},      new School[]{School.FIR, School.FIR, School.FIR}, null,  StatusEffect.CALESCENT,  0, 0, 0, 0, 3),
    ARCANE_TOUCH(         "Arcane Touch", ArcaneTouch.class,      AnimationsSys.AnimationType.MELEE_MAGIC,  SkillType.ACTION,  School.ARC, new School[]{School.ARC},      new School[]{School.ARC}, null,     null,  0, 0, 0, 0, 2),
    DODGE(                       "Dodge", Dodge.class,         AnimationsSys.AnimationType.WIGGLE,     SkillType.REACTION,  School.PHY, new School[]{School.PHY},      new School[]{School.PHY}, null,      null,  0, 3, 0, 0, 0),
    ENRAGE(                     "Enrage", Enrage.class,        AnimationsSys.AnimationType.SELF_BUFF,      SkillType.BUFF,  School.PHY, new School[]{School.PHY},      new School[]{School.PHY}, null,              StatusEffect.ENRAGED,  3, 0, 0, 0, 0),
    //OPPORTUNITY(    "Opportunity Attack", Opportunity.class,   AnimationsSys.AnimationType.BUMP,       SkillType.REACTION,  PHY, new School[]{PHY},      new School[]{PHY},      null,  0, 0, 0, 3, 0)
    ;

    public final String name;
    public final Class cls;
    public final AnimationsSys.AnimationType animationType;
    public final SkillType skillType;
    public final School school;
    public final School[] prepCost, castingCost;
    public final WeaponType weaponReq;
    public final StatusEffect statusEffect;
    public final int strReq, dexReq, constReq, percReq, intReq;


    Skill(String name, Class cls, AnimationsSys.AnimationType animationType, SkillType skillType, School school, School[] prepCost, School[] castingCost, WeaponType weaponReq, StatusEffect statusEffect,
          int strReq, int dexReq, int constReq, int percReq, int intReq)
    {
        this.name = name;
        this.cls = cls;
        this.animationType=animationType;
        this.skillType=skillType;
        this.school=school;
        this.prepCost=prepCost;
        this.castingCost=castingCost;
        this.weaponReq = weaponReq;
        this.statusEffect = statusEffect;
        this.strReq=strReq;
        this.dexReq=dexReq;
        this.constReq=constReq;
        this.percReq=percReq;
        this.intReq=intReq;
    }



    public enum SkillType {ACTION, REACTION, BUFF }

    public static boolean qualify(Skill skill, StatsCmp stats)
    {
        if(stats.getStr()<skill.strReq) return false;
        if(stats.getDex()<skill.dexReq) return false;
        if(stats.getCon()<skill.constReq) return false;
        if(stats.getIntel()<skill.intReq) return false;
        return stats.getPerc() >= skill.percReq;
    }
    public static boolean affordToPrep(Skill skill, ManaPoolCmp manaPoolCmp)
    {
        for(School school : skill.prepCost)
        {
            if(Collections.frequency(manaPoolCmp.spent, school)< Collections.frequency(Arrays.asList(skill.prepCost),school)) return false;
        }
        if(manaPoolCmp.numAttunedSlots<(skill.prepCost.length+ manaPoolCmp.attuned.size())) return false;
        return true;
    }
    public static ArrayList<Skill> getSkillsBySchool(School school)
    {
        ArrayList<Skill> skills = new ArrayList<>();
        for(Skill skill : Skill.values())
        {
            if(skill.school==school) skills.add(skill);
        }
        return skills;
    }

}
