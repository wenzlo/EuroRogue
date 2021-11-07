package EuroRogue.AbilityCmpSubSystems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.School;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.WeaponType;

public enum Skill
{
    MELEE_ATTACK(         "Melee Attack", MeleeAttack.class,  SkillType.ACTION,  School.WAR, new School[]{School.WAR},      new School[]{School.WAR}, null,       null,null,  0, 0, 0, 0, 0),
    CHARGE(                     "Charge", Charge.class,        SkillType.ACTION,  School.WAR, new School[]{School.WAR},      new School[]{School.WAR, School.WAR}, null, null,     StatusEffect.STAGGERED,  4, 0, 0, 0, 0),
    ENRAGE(                     "Enrage", Enrage.class,            SkillType.BUFF,  School.WAR, new School[]{School.WAR},      new School[]{School.WAR}, null,    null,          StatusEffect.ENRAGED,  5, 0, 0, 0, 0),

    DAGGER_THROW(       "Dagger Throw", DaggerThrow.class,   SkillType.ACTION,  School.SUB, new School[]{School.SUB}, new School[]{School.SUB}, WeaponType.DAGGER, null,null,   0, 4, 0, 0, 0),
    DODGE(                       "Dodge", Dodge.class,            SkillType.REACTION,  School.SUB, new School[]{School.SUB},      new School[]{School.SUB}, null,   null,   null,  0, 3, 0, 0, 0),
    STALK(               "Stalk", Stalk.class,         SkillType.BUFF,  School.SUB, new School[]{School.SUB, School.SUB},      new School[]{School.SUB, School.SUB}, null,    null,      StatusEffect.STALKING,  0, 2, 0, 2, 0),
    BACK_STAB(       "Back Stab", BackStab.class,   SkillType.ACTION,  School.SUB, new School[]{School.SUB}, new School[]{School.SUB}, WeaponType.DAGGER, STALK, null,  0, 2, 0, 2, 0),

    ENLIGHTEN(               "Enlighten", Enlighten.class,         SkillType.BUFF,  School.ARC, new School[]{School.ARC},      new School[]{School.ARC}, null,   null,       StatusEffect.ENLIGHTENED,  0, 0, 0, 0, 5),
    MAGIC_MISSILE(       "Magic Missile", MagicMissile.class,    SkillType.ACTION,  School.ARC, new School[]{School.ARC, School.ARC}, new School[]{School.ARC, School.ARC}, null, null, null,  0, 0, 0, 0, 4),
    ARCANE_TOUCH(         "Arcane Touch", ArcaneTouch.class,      SkillType.ACTION,  School.ARC, new School[]{School.ARC},      new School[]{School.ARC}, null, null,    null,  0, 0, 0, 0, 2),

    ICE_SHIELD(               "Ice Shield", IceShield.class,         SkillType.REACTION,  School.ICE, new School[]{School.ICE, School.WAR},      new School[]{School.ICE}, null,   null,        StatusEffect.CHILLED,  0, 0, 1, 2, 1),
    CHILL(            "Chill", Chill.class,  SkillType.ACTION,  School.ICE, new School[]{School.ICE},      new School[]{School.ICE}, null,     null,         StatusEffect.CHILLED,  0, 0, 0, 0, 2),
    CONE_OF_COLD(        "Cone Of Cold", ConeOfCold.class,       SkillType.ACTION,  School.ICE, new School[]{School.ICE},      new School[]{School.ICE}, null,  null,null,  0, 0, 0, 2, 2),
    SHATTER(                 "Shatter", Shatter.class,       SkillType.ACTION,  School.ICE, new School[]{School.ICE, School.ICE, School.ICE},      new School[]{School.ICE, School.ICE, School.ICE}, null,  null,null,  0, 0, 0, 0, 3),

    IMMOLATE(                 "Immolate", Immolate.class,        SkillType.ACTION,  School.FIR, new School[]{School.FIR},      new School[]{School.FIR}, null, null,        StatusEffect.CALESCENT,  0, 0, 0, 0, 2),
    ERUPTION(                 "Eruption", Eruption.class,      SkillType.ACTION,  School.FIR, new School[]{School.FIR, School.FIR, School.FIR},      new School[]{School.FIR, School.FIR, School.FIR}, null,  null,StatusEffect.CALESCENT,  0, 0, 0, 2, 3),
    //OPPORTUNITY(    "Opportunity Attack", Opportunity.class,      SkillType.REACTION,  PHY, new School[]{PHY},      new School[]{PHY},      null,  0, 0, 0, 3, 0)
    ;

    public final String name;
    public final Class cls;
    public final SkillType skillType;
    public final School school;
    public final School[] prepCost, castingCost;
    public final WeaponType weaponReq;
    public final Skill skillReq;
    public final StatusEffect statusEffect;
    public final int strReq, dexReq, constReq, percReq, intReq;


    Skill(String name, Class cls, SkillType skillType, School school, School[] prepCost, School[] castingCost, WeaponType weaponReq, Skill skillReq, StatusEffect statusEffect,
          int strReq, int dexReq, int constReq, int percReq, int intReq)
    {
        this.name = name;
        this.cls = cls;
        this.skillType=skillType;
        this.school=school;
        this.prepCost=prepCost;
        this.castingCost=castingCost;
        this.weaponReq = weaponReq;
        this.skillReq = skillReq;
        this.statusEffect = statusEffect;
        this.strReq=strReq;
        this.dexReq=dexReq;
        this.constReq=constReq;
        this.percReq=percReq;
        this.intReq=intReq;
    }


    public enum SkillType { ACTION, REACTION, BUFF }

    public static boolean qualify(Skill skill, StatsCmp stats, CodexCmp codexCmp)
    {
        if(stats.getStr()<skill.strReq) return false;
        if(stats.getDex()<skill.dexReq) return false;
        if(stats.getCon()<skill.constReq) return false;
        if(stats.getIntel()<skill.intReq) return false;
        if(skill.skillReq!=null)
            if(!codexCmp.prepared.contains(skill.skillReq))return false;
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
