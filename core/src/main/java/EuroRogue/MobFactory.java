package EuroRogue;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FactionCmp;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;

public class MobFactory
{
    EuroRogue game;
    WeaponFactory weaponFactory;
    ArmorFactory armorFactory;
    GWTRNG rng;
    public MobFactory(EuroRogue game, int seed, WeaponFactory weaponFactory, ArmorFactory armorFactory)
    {
        this.game=game;
        this.rng = new GWTRNG(seed);
        this.weaponFactory = weaponFactory;
        this.armorFactory = armorFactory;
    }

    public Entity generateRndPlayer()
    {
        Entity mob = new Entity();
        mob.add(new NameCmp(game.playerName));
        CodexCmp codexCmp = new CodexCmp();
        mob.add(codexCmp);

        mob.add(new CharCmp('@', SColor.LIGHT_YELLOW_DYE));
        StatsCmp statsCmp = getRandomStats(9+ game.depth*2, true);
        mob.add(statsCmp);
        mob.add(new InventoryCmp(new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP, EquipmentSlot.LEFT_HAND_WEAP, EquipmentSlot.CHEST}, statsCmp.getStr()+4));
        AICmp aiCmp = new AICmp(new ArrayList(Arrays.asList(TerrainType.STONE, TerrainType.MOSS, TerrainType.SHALLOW_WATER, TerrainType.BRIDGE)));
        mob.add(aiCmp);
        mob.add(new FactionCmp(FactionCmp.Faction.PLAYER));
        mob.add(new ManaPoolCmp(statsCmp.getNumAttunedSlots()));
        mob.add(new LightCmp(0, SColor.AURORA_BURNT_YELLOW.toFloatBits()));
        mob.add(new FocusCmp());
        mob.add(new ParticleEffectsCmp());

        setRandomSkillSet(mob, true);

        return mob;
    }
    public Entity generateSkillessPlayer()
    {
        Entity mob = new Entity();
        mob.add(new NameCmp(game.playerName));
        mob.add(new CodexCmp());

        mob.add(new CharCmp('@', SColor.LIGHT_YELLOW_DYE));
        StatsCmp statsCmp =getRandomStats(12, true);
        mob.add(statsCmp);
        mob.add(new InventoryCmp(new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP, EquipmentSlot.LEFT_HAND_WEAP, EquipmentSlot.CHEST}, statsCmp.getStr()+4));

        mob.add(new FactionCmp(FactionCmp.Faction.PLAYER));
        mob.add(new ManaPoolCmp(statsCmp.getNumAttunedSlots()));
        mob.add(new LightCmp(0, SColor.COSMIC_LATTE.toFloatBits()));
        return mob;
    }
    public Entity generateRndMob(Coord loc, String name, int depth)
    {

        Entity mob = new Entity();
        mob.add(new NameCmp(name));
        CodexCmp codexCmp = new CodexCmp();
        mob.add(codexCmp);
        mob.add(new PositionCmp(loc));
        mob.add(new CharCmp('ß', SColor.RED_BIRCH));
        StatsCmp statsCmp = getRandomStats(5+(depth*2), false);
        mob.add(statsCmp);
        mob.add(new InventoryCmp(new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP, EquipmentSlot.LEFT_HAND_WEAP, EquipmentSlot.CHEST}, statsCmp.getStr()+4));
        AICmp aiCmp = new AICmp(new ArrayList(Arrays.asList(TerrainType.STONE, TerrainType.MOSS, TerrainType.SHALLOW_WATER, TerrainType.BRIDGE)));
        mob.add(aiCmp);
        mob.add(new FactionCmp(FactionCmp.Faction.MONSTER));
        mob.add(new ManaPoolCmp(statsCmp.getNumAttunedSlots()));
        mob.add(new LightCmp(0, SColor.COSMIC_LATTE.toFloatBits()));
        setRandomSkillSet(mob, false);

        mob.add(new ParticleEffectsCmp());


        return mob;
    }

    public Entity generateMob(MobType mobType, Coord loc, LevelCmp levelCmp, int depth)
    {
        switch (mobType)
        {
            case RAT:
                return generateRat(loc, levelCmp, depth);

        }
        return null;

    }

    public void setRandomSkillSet(Entity mob, boolean player)
    {
        StatsCmp stats = (StatsCmp)CmpMapper.getComp(CmpType.STATS, mob);
        ManaPoolCmp manaPool = (ManaPoolCmp)CmpMapper.getComp(CmpType.MANA_POOL, mob);
        CodexCmp codex = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, mob);
        int spentLimit =  Math.min(4+ game.depth, manaPool.numAttunedSlots);
        List<Skill> skillPool = new ArrayList<>(Arrays.asList(Skill.values()));
        skillPool.remove(Skill.MELEE_ATTACK);

        manaPool.spent.addAll(Arrays.asList(Skill.MELEE_ATTACK.prepCost));
        manaPool.spent.addAll(Arrays.asList(Skill.MELEE_ATTACK.castingCost));
        codex.known.add(Skill.MELEE_ATTACK);
        codex.prepared.add(Skill.MELEE_ATTACK);
        manaPool.spent.add(School.PHY);
        for(School mana:Skill.MELEE_ATTACK.prepCost)
        {
            manaPool.attuned.add(mana);
            manaPool.spent.remove(mana);
        }
        Ability newAbility = Ability.newAbilityCmp(Skill.MELEE_ATTACK, player);
        mob.add(newAbility);
        spentLimit = spentLimit - Skill.MELEE_ATTACK.prepCost.length;


        while (spentLimit > 0 && skillPool.size()>0 && codex.prepared.size() < 1 + game.depth+1)
        {
            Skill skill = rng.getRandomElement(skillPool);

            skillPool.remove(skill);
            if(spentLimit - skill.prepCost.length-1 < 0) continue;
            if (skill.castingCost.length < spentLimit && Skill.qualify(skill, stats) &! codex.getExcludedSchools().contains(skill.school))
            {
                manaPool.spent.addAll(Arrays.asList(skill.prepCost));
                manaPool.spent.addAll(Arrays.asList(skill.castingCost));
                //if(skill.skillType != Skill.SkillType.REACTION || skill.skillType != Skill.SkillType.BUFF) manaPool.spent.addAll(Arrays.asList(skill.castingCost));
                codex.known.add(skill);
                codex.prepared.add(skill);
                for(School mana:skill.prepCost)
                {
                    manaPool.attuned.add(mana);
                    manaPool.spent.remove(mana);
                }
                mob.add(Ability.newAbilityCmp(skill, player));
                spentLimit = spentLimit - skill.prepCost.length;
            }
        }

        stats.setSpirit(manaPool.unattunedMana().size());
        stats.hp= stats.getMaxHP();
    }



    public StatsCmp getRandomStats(int total, boolean player)
    {
        HashMap<StatType, Integer> stats = new HashMap<>();
        stats.put(StatType.STR, 1);
        stats.put(StatType.DEX, 1);
        stats.put(StatType.CON, 1);
        stats.put(StatType.INTEL, 1);
        stats.put(StatType.PERC, 1);
        ArrayList<StatType> statPool = new ArrayList<>();
        statPool.addAll(stats.keySet());

        for (int i = 0; i < total-5; i++)
        {
            StatType stat = rng.getRandomElement(statPool);

            statPool.add(stat);
            stats.put(stat, stats.get(stat)+1);
        }
        StatsCmp statsCmp;
        if(!player) statsCmp = new StatsCmp();
        else statsCmp = new StatsCmp(rng);
        statsCmp.setStr(stats.get(StatType.STR));
        statsCmp.setDex(stats.get(StatType.DEX));
        statsCmp.setCon(stats.get(StatType.CON));
        statsCmp.setPerc(stats.get(StatType.PERC));
        statsCmp.setIntel(stats.get(StatType.INTEL));
        statsCmp.hp=statsCmp.getMaxHP();

        return statsCmp;

    }

    private Entity generateRat(Coord loc, LevelCmp levelCmp, int depth)
    {
        Entity mob = new Entity();
        mob.add(new NameCmp("Rat"));
        CodexCmp codexCmp = new CodexCmp();
        mob.add(codexCmp);
        mob.add(new PositionCmp(loc));
        mob.add(new CharCmp('r', ',', ',', SColor.BROWN_RAT_GREY));
        StatsCmp statsCmp = new StatsCmp(0, 2, 0, 3, 0);
        mob.add(statsCmp);
        mob.add(new InventoryCmp(new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP, EquipmentSlot.LEFT_HAND_WEAP, EquipmentSlot.CHEST}, statsCmp.getStr()+4));
        AICmp aiCmp = new AICmp(new ArrayList(Arrays.asList(TerrainType.STONE, TerrainType.MOSS, TerrainType.SHALLOW_WATER, TerrainType.BRIDGE)));
        mob.add(aiCmp);
        mob.add(new FactionCmp(FactionCmp.Faction.MONSTER));
        mob.add(new ManaPoolCmp(statsCmp.getNumAttunedSlots()));
        mob.add(new LightCmp(0, SColor.COSMIC_LATTE.toFloatBits()));
        setRandomSkillSet(mob, false);

        mob.add(new ParticleEffectsCmp());



        return mob;
    }

}
