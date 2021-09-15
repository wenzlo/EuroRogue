package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.Arrays;

import EuroRogue.AbilityCmpSubSystems.MeleeAttack;
import EuroRogue.DamageType;
import EuroRogue.StatType;
import squidpony.squidgrid.Direction;


public class StatsCmp implements Component
{

    private int str = 0;
    private int dex = 0;
    private int con = 0;
    private int perc = 0;
    private int intel = 0;
    public int hp;
    public float maxHPMult = 1;
    public float ttMoveMult = 1;
    public float ttMeleeMult = 1;
    public float ttCastMult = 1;
    public float ttRestMult = 1;
    public float attackPwrMult = 1;
    public float spellPwrMult = 1;
    public float arcaneDefMult = 1;
    public float fireDefMult = 1;
    public float iceDefMult = 1;
    public float peirceDefMult = 1;
    public float bludgDefMult = 1;
    public float slashDefMult = 1;
    public float moveSndLvlMult = 1;
    public float meleeSndLvlMult = 1;
    public float spellSndLvlMult = 1;

    public int getStr() { return str; }
    public int getDex() { return dex; }
    public int getCon() { return con; }
    public int getPerc() { return perc; }
    public int getIntel() { return intel; }
    public int getSoundDetectionLvl(){return 11-perc;}
    public double getMoveSndLvl()
    {
        return Math.round(10* moveSndLvlMult);
    }



    public int getMaxHP()
    {
        return Math.round((Arrays.stream(new int[]{str, con, dex, intel, perc}).sum()*4+8* con)*maxHPMult);
    }

    public int getSpellPower()
    {
        if(getIntel() == 0) return 0;
        return Math.round((1+(intel/2f))*spellPwrMult*4);
    }
    public int getWeaponDamage()
    {
        return  Math.round(getAttackPower()*attackPwrMult);
    }

    public int getAttackPower()
    {
        if(getStr() ==0) return 0;
        return Math.round((1+(str/2f)))*4;
    }
    public int getTTMoveBase()
    {
        if(getDex() ==0) return 10;
        return Math.round((10-(getDex() /2f+1))*ttMoveMult);
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
        return Math.round((10-(getDex() /2f+1))*ttMeleeMult);
    }
    public int getTTCast()
    {
        if(getPerc() ==0) return 10;
        return Math.round((10-(getPerc() /2f+1))*ttCastMult);
    }
    public int getTTRest()
    {
        if(getCon() ==0) return 10;
        return Math.round((10-(getCon() /2f+1))*ttRestMult);
    }
    public void setStr(int str) { this.str = str; }
    public void setDex(int dex) { this.dex = dex; }
    public void setCon(int con) { this.con = con; }
    public void setPerc(int perc) { this.perc = perc; }
    public void setIntel(int intel) { this.intel = intel; }

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
        switch (stat)
        {

            case TT_MOVE: return ttMoveMult;

            case TT_MELEE: return ttMeleeMult;

            case TT_CAST: return ttCastMult;

            case TT_REST: return ttRestMult;

            case ATTACK_PWR: return attackPwrMult;

            case SPELL_PWR: return spellPwrMult;

            case ARC_DEF: return arcaneDefMult;

            case FIRE_DEF: return fireDefMult;

            case ICE_DEF: return iceDefMult;

            case BLUDG_DEF: return bludgDefMult;

            case SLASH_DEF: return slashDefMult;

            case PIERCE_DEF: return peirceDefMult;

            case MELEE_SND_LVL: return meleeSndLvlMult;

            case SPELL_SND_LVL: return spellSndLvlMult;

        }
        return 1f;
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
        switch (damageType)
        {

            case PIERCING:
                return peirceDefMult;
            case BLUDGEONING:
                return  bludgDefMult;
            case SLASHING:
                return slashDefMult;
            case ARCANE:
                return arcaneDefMult;
            case FIRE:
                return fireDefMult;
            case ICE:
                return iceDefMult;
        }
        return 1f;
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
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CORE STATS" +"\n"+"\n");
        sb.append("HP = "+hp+"/"+getMaxHP() +"\n");
        sb.append("strength     = "+ str +"\n");
        sb.append("dexterity    = "+ dex +"\n");
        sb.append("constitution = "+ con +"\n");
        sb.append("perception   = "+ perc +"\n");
        sb.append("intelligence = "+ intel +"\n"+"\n");
        sb.append("SPEED STATS" +"\n");
        sb.append("ttMove  = "+getTTMoveBase()+"\n");
        sb.append("ttMelee = "+getTTMelee()+"\n");
        sb.append("ttCast  = "+getTTCast()+"\n");
        sb.append("ttRest  = "+getTTRest()+"\n"+"\n");
        sb.append("NoiseLvl= "+ getMoveSndLvl()+"\n");

        sb.append("RESISTS"+"\n"+"\n");
        sb.append("Bludgeon = "+(Math.round((-(1-bludgDefMult))*100))+"%"+"\n");
        sb.append("Piercing = "+(Math.round((-(1-peirceDefMult))*100))+"%"+"\n");
        sb.append("Slashing = "+(Math.round((-(1-slashDefMult))*100))+"%"+"\n");
        sb.append("Fire     = "+(Math.round((-(1-fireDefMult))*100))+"%"+"\n");
        sb.append("Ice      = "+(Math.round((-(1-iceDefMult))*100))+"%"+"\n");
        sb.append("Arcane   = "+(Math.round((-(1-arcaneDefMult))*100))+"%"+"\n"+"\n");
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
}
