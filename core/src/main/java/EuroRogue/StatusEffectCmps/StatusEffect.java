package EuroRogue.StatusEffectCmps;


import EuroRogue.Components.StatsCmp;
import EuroRogue.DamageType;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.StatType;

public enum StatusEffect
{
    L_ARMOR_EFCT(LeatherArmorEfct.class, "Leather Armor Eq", EffectType.ARMOR, DamageType.NONE, new Class[0], new Class[0], false),
    M_ARMOR_EFCT(MailArmorEfct.class, "Mail Armor Eq", EffectType.ARMOR, DamageType.NONE, new Class[0], new Class[0], false),
    P_ARMOR_EFCT(PlateArmorEfct.class, "Plate Armor Eq", EffectType.ARMOR, DamageType.NONE, new Class[0], new Class[0], false),
    DAGGER_EFCT( DaggerEfct.class, "Dagger Eq", EffectType.WEAPON, DamageType.NONE,     new Class[0],                                new Class[0],false),
    SWORD_EFCT(   SwordEfct.class, "Sword Eq",  EffectType.WEAPON, DamageType.NONE,     new Class[0],                                new Class[0],false),
    QSTAFF_EFCT( QStaffEfct.class, "QStaff Eq", EffectType.WEAPON, DamageType.NONE,     new Class[0],                                new Class[0],false),
    STAFF_EFCT(   StaffEfct.class,  "Staff Eq", EffectType.WEAPON, DamageType.NONE,     new Class[0],                                new Class[0],false),
    BURNING(        Burning.class, "Burning",         EffectType.DEBUFF, DamageType.FIRE,     new Class[]{Chilled.class, Frozen.class},    new Class[]{Calescent.class, Enraged.class},false),
    BLEEDING(      Bleeding.class, "Bleeding",        EffectType.DEBUFF, DamageType.PIERCING, new Class[0],                                new Class[0],false),
    ENRAGED(        Enraged.class, "Enraged",         EffectType.BUFF,   DamageType.NONE,     new Class[]{Enlightened.class},                                new Class[0],                               false),
    ENLIGHTENED(Enlightened.class, "Enlightened",     EffectType.BUFF,   DamageType.NONE,     new Class[]{Enraged.class},                                new Class[0],                               false),
    CALESCENT(    Calescent.class, "Calescent",       EffectType.DEBUFF, DamageType.FIRE,     new Class[]{Chilled.class, Frozen.class},    new Class[0],                                true),
    EXHAUSTED(    Exhausted.class, "Exhausted",       EffectType.DEBUFF, DamageType.NONE,     new Class[0],                                new Class[0],                               false),
    STAGGERED(    Staggered.class, "Staggered",       EffectType.DEBUFF, DamageType.BLUDGEONING ,new Class[0],                                new Class[0],                               false),
    HUNGRY(          Hungry.class, "Hungry",          EffectType.DEBUFF, DamageType.NONE,     new Class[]{WellFed.class},                  new Class[0],                               true),
    STARVING(      Starving.class, "Starving",        EffectType.DEBUFF, DamageType.NONE,    new Class[0],                  new Class[]{Hungry.class},                  false),
    WELL_FED(       WellFed.class, "Well Fed",        EffectType.BUFF,   DamageType.NONE,     new Class[]{Hungry.class, Starving.class},   new Class[0], false),
    FROZEN(          Frozen.class, "Frozen",          EffectType.DEBUFF, DamageType.ICE,     new Class[]{Calescent.class, Burning.class}, new Class[]{Chilled.class, Enraged.class},  false),
    CHILLED(        Chilled.class, "Chilled",         EffectType.DEBUFF, DamageType.ICE,     new Class[]{Calescent.class, Burning.class}, new Class[0],                                true),
    WATER_WALKING(WaterWalking.class, "Water Walking",EffectType.BUFF,   DamageType.NONE,      new Class[0],                                new Class[0],                                false);

    public Class cls;
    public String name;
    public EffectType effectType;
    public DamageType resistance;
    public Class[] cancels;
    public Class[] removes;
    public boolean intensifies;


    StatusEffect(Class cls, String name, EffectType effectType, DamageType resistance,  Class[] cancels, Class[] removes, boolean intensifies)
    {
        this.cls = cls;
        this.name = name;
        this.effectType = effectType;
        this.resistance = resistance;
        this.cancels = cancels;
        this.removes = removes;
        this.intensifies = intensifies;
    }

    public static StatusEffectCmp newStatusEffectCmp(StatusEffectEvt seEvent, StatsCmp targetStats)
    {
        StatusEffectCmp statusEffectCmp;
        switch (seEvent.effect)
        {
            case EXHAUSTED:
                statusEffectCmp = new Exhausted();
                break;
            case CHILLED:
                statusEffectCmp = new Chilled();
                break;
            case ENRAGED:
                int str = targetStats.getStr();
                statusEffectCmp = new Enraged();
                statusEffectCmp.statMultipliers.put(StatType.ATTACK_PWR, statusEffectCmp.statMultipliers.get(StatType.ATTACK_PWR)+(str*0.05f));
                break;
            case CALESCENT:
                statusEffectCmp = new Calescent();
                break;
            case L_ARMOR_EFCT:
                statusEffectCmp = new LeatherArmorEfct();
                int dex = targetStats.getDex();
                statusEffectCmp.statMultipliers.put(StatType.TT_MOVE, statusEffectCmp.statMultipliers.get(StatType.TT_MOVE)-(dex*0.025f));
                statusEffectCmp.statMultipliers.put(StatType.TT_MELEE, statusEffectCmp.statMultipliers.get(StatType.TT_MELEE)-(dex*0.025f));
                statusEffectCmp.statMultipliers.put(StatType.TT_CAST, statusEffectCmp.statMultipliers.get(StatType.TT_CAST)-(dex*0.025f));
                statusEffectCmp.statMultipliers.put(StatType.TT_REST, statusEffectCmp.statMultipliers.get(StatType.TT_REST)-(dex*0.025f));
                break;
            case M_ARMOR_EFCT:
                int strDexAvg = Math.round((targetStats.getDex()+targetStats.getStr())/2f);
                statusEffectCmp = new MailArmorEfct();
                statusEffectCmp.statMultipliers.put(StatType.TT_MOVE, statusEffectCmp.statMultipliers.get(StatType.TT_MOVE)-(strDexAvg*0.05f));
                statusEffectCmp.statMultipliers.put(StatType.TT_MELEE, statusEffectCmp.statMultipliers.get(StatType.TT_MELEE)-(strDexAvg*0.05f));
                statusEffectCmp.statMultipliers.put(StatType.TT_CAST, statusEffectCmp.statMultipliers.get(StatType.TT_CAST)-(strDexAvg*0.05f));
                statusEffectCmp.statMultipliers.put(StatType.TT_REST, statusEffectCmp.statMultipliers.get(StatType.TT_REST)-(strDexAvg*0.05f));
                break;
            case P_ARMOR_EFCT:
                str = targetStats.getStr();
                statusEffectCmp = new PlateArmorEfct();
                statusEffectCmp.statMultipliers.put(StatType.TT_MOVE, statusEffectCmp.statMultipliers.get(StatType.TT_MOVE)-(str*0.075f));
                statusEffectCmp.statMultipliers.put(StatType.TT_MELEE, statusEffectCmp.statMultipliers.get(StatType.TT_MELEE)-(str*0.075f));
                statusEffectCmp.statMultipliers.put(StatType.TT_CAST, statusEffectCmp.statMultipliers.get(StatType.TT_CAST)-(str*0.075f));
                statusEffectCmp.statMultipliers.put(StatType.TT_REST, statusEffectCmp.statMultipliers.get(StatType.TT_REST)-(str*0.075f));

                break;
            case DAGGER_EFCT:
                statusEffectCmp = new DaggerEfct();
                dex = targetStats.getDex();
                statusEffectCmp.statMultipliers.put(StatType.ATTACK_PWR, statusEffectCmp.statMultipliers.get(StatType.ATTACK_PWR)+(dex*0.06f));
                break;
            case SWORD_EFCT:
                statusEffectCmp = new SwordEfct();
                str = targetStats.getStr();
                statusEffectCmp.statMultipliers.put(StatType.ATTACK_PWR, statusEffectCmp.statMultipliers.get(StatType.ATTACK_PWR)+(str*0.05f));
                break;
            case QSTAFF_EFCT:
                statusEffectCmp = new QStaffEfct();
                break;
            case STAFF_EFCT:
                statusEffectCmp = new StaffEfct();
                break;
            case BURNING:
                statusEffectCmp = new Burning();
                break;
            case FROZEN:
                statusEffectCmp = new Frozen();
                break;
            case BLEEDING:
                statusEffectCmp = new Bleeding();
                break;
            case ENLIGHTENED:
                statusEffectCmp = new Enlightened(SERemovalType.SHORT_REST, targetStats);
                break;
            case STAGGERED:
                statusEffectCmp = new Staggered();
                break;
            case HUNGRY:
                statusEffectCmp = new Hungry();
                break;
            case STARVING:
                statusEffectCmp = new Starving();
                break;
            case WELL_FED:
                statusEffectCmp = new WellFed();
                break;
            case WATER_WALKING:
                statusEffectCmp = new WaterWalking();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + seEvent.effect);
        }
        statusEffectCmp.seRemovalType = seEvent.seRemovalType;
        if(seEvent.seRemovalType==SERemovalType.TIMED)
        {
            statusEffectCmp.lastTick = seEvent.tick+seEvent.duration;
        }

        return  statusEffectCmp;
    }

    public static StatusEffect getNextIntensity(StatusEffect effect)
    {
        switch (effect)
        {
            case CHILLED:
                return FROZEN;

            case CALESCENT:
                return BURNING;

            case HUNGRY:
                return STARVING;
        }
        return effect;
    }

    public enum EffectType
    {
        BUFF,
        DEBUFF,
        WEAPON,
        ARMOR
    }

    public static String getEffectDescriptorPre(StatusEffect statusEffect) {
        switch (statusEffect) {
            case BURNING:
                return "Blazing";

            case ENRAGED:
                return "Enraging";

            case ENLIGHTENED:
                return "Edifying";

            case CALESCENT:
                return "Scorching";

            case EXHAUSTED:
                return "Wearying";

            case STAGGERED:
                return "Faltering";

            case FROZEN:
                return "Freezing";

            case CHILLED:
                return "Chilling";
            case WATER_WALKING:
                return "Buoyant";
        }
        return "";
    }

    public static String getEffectDescriptorPost(StatusEffect statusEffect) {
        switch (statusEffect) {
            case BURNING:
                return "Burning";

            case ENRAGED:
                return "Rage";

            case ENLIGHTENED:
                return "Enlightenment";

            case CALESCENT:
                return "Searing";

            case EXHAUSTED:
                return "Exhaustion";

            case STAGGERED:
                return "Staggering";

            case FROZEN:
                return "Freezing";

            case CHILLED:
                return "Frost";
            case WATER_WALKING:
                return "Buoyancy";
        }
        return "";
    }


}
