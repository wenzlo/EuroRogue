package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import EuroRogue.DamageType;
import EuroRogue.School;
import EuroRogue.SortManaBySchool;
import EuroRogue.StatType;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.GWTRNG;


public class StatsCmp implements Component
{
    private int str = 0;
    private int dex = 0;
    private int con = 0;
    private int perc = 0;
    private int intel = 0;
    private int spirit = 0;
    public int hp;
    private float maxHPMult = 1;
    private float ttMoveMult = 1;
    private float ttMeleeMult = 1;
    private float ttCastMult = 1;
    private float ttRestMult = 1;
    private float attackPwrMult = 1;
    private float spellPwrMult = 1;
    private float arcaneDefMult = 1;
    private float fireDefMult = 1;
    private float iceDefMult = 1;
    private float peirceDefMult = 1;
    private float bludgDefMult = 1;
    private float slashDefMult = 1;
    private float moveSndLvlMult = 1;
    private float meleeSndLvlMult = 1;
    private float spellSndLvlMult = 1;
    private HashMap<StatType, List<School>> statCosts = new HashMap<>();

    private static final List<StatType>
            spiritIncreases = Arrays.asList(StatType.ATTACK_PWR, StatType.SPELL_PWR, StatType.MAX_HP,
                                            StatType.ARC_DEF, StatType.ICE_DEF, StatType.PIERCE_DEF,
                                            StatType.FIRE_DEF, StatType.SLASH_DEF, StatType.BLUDG_DEF);
    private static final List<StatType>
            spiritDecreases = Arrays.asList(StatType.TT_CAST, StatType.TT_MELEE, StatType.TT_MOVE,
                                            StatType.TT_REST);

    public int getStr() { return str; }
    public int getDex() { return dex; }
    public int getCon() { return con; }
    public int getPerc() { return perc; }
    public int getIntel() { return intel; }
    public int getSoundDetectionLvl(){return 9-perc/2;}
    public double getMoveSndLvl()
    {
        return Math.round(10 * moveSndLvlMult);
    }
    public StatsCmp(){}
    public StatsCmp(GWTRNG rng)
    {
        ArrayList<School> pool = new ArrayList<>();
        pool.addAll(Arrays.asList(School.values()));
        pool.addAll(Arrays.asList(School.values()));
        pool.addAll(Arrays.asList(School.values()));
        pool.addAll(Arrays.asList(School.values()));
        pool.addAll(Arrays.asList(School.values()));
        pool.addAll(Arrays.asList(School.values()));
        pool.remove(School.ARC);
        pool.remove(School.FIR);
        pool.remove(School.ICE);
        pool.remove(School.ARC);

        List<School> startingCost = new ArrayList<>();
        for(int i=0; i<3; i++)
        {
            School school = rng.getRandomElement(pool);
            startingCost.add(school);
            pool.remove(school);

        }
        startingCost.sort(new SortManaBySchool());
        statCosts.put(StatType.STR, startingCost);
        List<School> cost = new ArrayList<>(startingCost);
        for(StatType statType : StatType.CORE_STATS)
        {
            while(statCosts.values().contains(cost) || cost.size()<3 || Collections.frequency(cost, cost.get(0))>1)
            {
                pool.addAll(cost);
                cost.clear();
                for(int i=0; i<3; i++)
                {
                    School school = rng.getRandomElement(pool);
                    cost.add(school);
                    pool.remove(school);
                }
                cost.sort(new SortManaBySchool());
            }
            statCosts.put(statType, new ArrayList<>(cost));
            cost.clear();
        }
    }

    public int getMaxHP()
    {
        return Math.round((Arrays.stream(new int[]{str, con, dex, intel, perc}).sum()*4+8* con)*getStatMultiplier(StatType.MAX_HP));
    }

    public int getSpellPower()
    {
        if(getIntel() == 0) return 0;
        return Math.round((1+(intel/2f))*getStatMultiplier(StatType.SPELL_PWR)*4);
    }
    public int getWeaponDamage()
    {
        return  Math.round(getAttackPower()*getStatMultiplier(StatType.ATTACK_PWR));
    }

    public int getAttackPower()
    {
        if(getStr() ==0) return 0;
        return Math.round((1+(str/2f)))*4;
    }
    public int getTTMoveBase()
    {
        if(getDex() ==0) return 10;
        return Math.round((10-(getDex()/2f+1))*getStatMultiplier(StatType.TT_MOVE));
    }
    public int getTTMove(Direction direction, double terrainCost)
    {
        int ttMove = Math.round(getTTMoveBase() * (float)terrainCost);
        if(Arrays.asList(Direction.DIAGONALS).contains(direction)) ttMove = Math.round(ttMove*1.41f);
        return ttMove;
    }
    public int getTTMelee()
    {
        if(getDex() ==0) return 10;
        return Math.round((10-(getDex() /2f+1))*getStatMultiplier(StatType.TT_MELEE));
    }
    public int getTTCast()
    {
        if(getPerc() ==0) return 10;
        return Math.round((10-(getPerc() /2f+1))*getStatMultiplier(StatType.TT_CAST));
    }
    public int getTTRest()
    {
        if(getCon() ==0) return 10;
        return Math.round((10-(getCon() /2f+1))*getStatMultiplier(StatType.TT_REST));
    }
    public void setStr(int str) { this.str = str; }
    public void setDex(int dex) { this.dex = dex; }
    public void setCon(int con) { this.con = con; }
    public void setPerc(int perc) { this.perc = perc; }
    public void setIntel(int intel) { this.intel = intel; }
    public void setSpirit(int spirit) { this.spirit = spirit; }

    public void setStatMultiplier(StatType stat, float multiplier)
    {
        switch (stat)
        {

            case TT_MOVE:
                ttMoveMult = multiplier;
                break;
            case TT_MELEE:
                ttMeleeMult = multiplier;
                break;
            case TT_CAST:
                ttCastMult = multiplier;
                break;
            case TT_REST:
                ttRestMult = multiplier;
                break;
            case ATTACK_PWR:
                attackPwrMult = multiplier;
                break;
            case SPELL_PWR:
                spellPwrMult = multiplier;
                break;
            case ARC_DEF:
                arcaneDefMult = multiplier;
                break;
            case FIRE_DEF:
                fireDefMult = multiplier;
                break;
            case ICE_DEF:
                iceDefMult = multiplier;
                break;
            case BLUDG_DEF:
                bludgDefMult = multiplier;
                break;
            case SLASH_DEF:
                slashDefMult = multiplier;
                break;
            case PIERCE_DEF:
                peirceDefMult = multiplier;
                break;

            case MAX_HP:
                maxHPMult = multiplier;
                break;

            case MOVE_SND_LVL:
                moveSndLvlMult = multiplier;
                break;

            case MELEE_SND_LVL:
                meleeSndLvlMult = multiplier;
                break;

            case SPELL_SND_LVL:
                spellSndLvlMult = multiplier;
                break;
        }
    }
    public void setStat(StatType stat, int value)
    {
        switch (stat)
        {
            case STR:
                str  = value;
                break;
            case DEX:
                dex = value;
                break;
            case CON:
                con = value;
                break;
            case INTEL:
                intel = value;
                break;
            case PERC:
                perc = value;
                break;

        }
    }
    public float getStatMultiplier(StatType stat)
    {
        float statMult = 1f;
        switch (stat)
        {
            case TT_MOVE:
                statMult = ttMoveMult;
                break;
            case TT_MELEE:
                statMult = ttMeleeMult;
                break;
            case TT_CAST:
                statMult = ttCastMult;
                break;
            case TT_REST:
                statMult = ttRestMult;
                break;
            case MOVE_SND_LVL:
                statMult = moveSndLvlMult;
                break;
            case MELEE_SND_LVL:
                statMult = meleeSndLvlMult;
                break;
            case SPELL_SND_LVL:
                statMult = spellSndLvlMult;
                break;
            case ATTACK_PWR:
                statMult = attackPwrMult;
                break;
            case SPELL_PWR:
                statMult = spellPwrMult;
                break;
            case ARC_DEF:
                statMult = arcaneDefMult;
                break;
            case FIRE_DEF:
                statMult = fireDefMult;
                break;
            case ICE_DEF:
                statMult = iceDefMult;
                break;
            case BLUDG_DEF:
                statMult = bludgDefMult;
                break;
            case SLASH_DEF:
                statMult = slashDefMult;
                break;
            case PIERCE_DEF:
                statMult = peirceDefMult;
                break;
            case MAX_HP:
                statMult = maxHPMult;
                break;
        }
        if(spiritIncreases.contains(stat)) return statMult+(spirit/100f);
        else return statMult-(spirit/100f);

    }
    public int getStat(StatType statType)
    {
        switch (statType)
        {
            case STR: return str;
            case DEX: return dex;
            case CON: return con;
            case INTEL: return intel;
            case PERC: return perc;
        }
        return 0;
    }
    public float getResistMultiplier(DamageType damageType)
    {
        float resistanceMult = 1;
        switch (damageType)
        {

            case PIERCING:
                resistanceMult = peirceDefMult;
                break;
            case BLUDGEONING:
                resistanceMult =  bludgDefMult;
                break;
            case SLASHING:
                resistanceMult = slashDefMult;
                break;
            case ARCANE:
                resistanceMult = arcaneDefMult;
                break;
            case FIRE:
                resistanceMult = fireDefMult;
                break;
            case ICE:
                resistanceMult = iceDefMult;
                break;
        }
        return resistanceMult+(spirit/100f);
    }
    public int getNumAttunedSlots()
    {
        return Math.round(3+(intel/2f));
    }

    public void mergeWith(StatsCmp statsCmp)
    {
        str =Math.max(str, statsCmp.getStr());
        dex =Math.max(dex, statsCmp.getDex());
        con =Math.max(con, statsCmp.getCon());
        intel =Math.max(intel, statsCmp.getIntel());
        perc =Math.max(perc, statsCmp.getPerc());
    }
    public boolean afford(StatType statType, ManaPoolCmp manaPoolCmp)
    {
        for(School mana : statCosts.get(statType))
        {
            if(Collections.frequency(statCosts.get(statType), mana)> Collections.frequency(manaPoolCmp.spent, mana))
            {
                return false;
            }
        }
        return  true;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CORE STATS" +"\n");
        sb.append("HP = "+hp+"/"+getMaxHP() +"\n");
        sb.append("strength     = "+ str +"\n");
        sb.append("dexterity    = "+ dex +"\n");
        sb.append("constitution = "+ con +"\n");
        sb.append("perception   = "+ perc +"\n");
        sb.append("intelligence = "+ intel +"\n");
        sb.append("Spirit = "+ spirit +"\n"+"\n");
        sb.append("SPEED STATS" +"\n");
        sb.append("ttMove  = "+getTTMoveBase()+"\n");
        sb.append("ttMelee = "+getTTMelee()+"\n");
        sb.append("ttCast  = "+getTTCast()+"\n");
        sb.append("ttRest  = "+getTTRest()+"\n"+"\n");
        sb.append("NoiseLvl= "+ getMoveSndLvl()+"\n");

        sb.append("RESISTS"+"\n"+"\n");
        sb.append("Bludgeon = "+(Math.round((-(1-getResistMultiplier(DamageType.BLUDGEONING)))*100))+"%"+"\n");
        sb.append("Piercing = "+(Math.round((-(1-getResistMultiplier(DamageType.PIERCING)))*100))+"%"+"\n");
        sb.append("Slashing = "+(Math.round((-(1-getResistMultiplier(DamageType.SLASHING)))*100))+"%"+"\n");
        sb.append("Fire     = "+(Math.round((-(1-getResistMultiplier(DamageType.FIRE)))*100))+"%"+"\n");
        sb.append("Ice      = "+(Math.round((-(1-getResistMultiplier(DamageType.ICE)))*100))+"%"+"\n");
        sb.append("Arcane   = "+(Math.round((-(1-getResistMultiplier(DamageType.ARCANE)))*100))+"%"+"\n"+"\n");
        sb.append("DAMAGE STATS"+"\n");
        sb.append("Weapon Damage = "+ getWeaponDamage()+"\n");
        sb.append("Attack Power  = "+ getAttackPower()+"\n");
        sb.append("Spell Power   = "+getSpellPower()+"\n");
        float dpt = ((float) getWeaponDamage()/(float)getTTMelee());
        sb.append("Melee DPT = "+dpt+"\n");
        dpt = ((float)getSpellPower()/(float)getTTCast());
        sb.append("Spell DPT = "+dpt+"\n");

        return sb.toString();
    }
    public List<School> getStatCost(StatType statType)
    {
        return statCosts.get(statType);
    }
}
