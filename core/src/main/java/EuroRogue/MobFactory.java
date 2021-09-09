package EuroRogue;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import EuroRogue.AbilityCmpSubSystems.IAbilityCmpSubSys;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FactionCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.NameCmp;
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
    GWTRNG rng;
    public MobFactory(EuroRogue game, int seed)
    {
        this.game=game;
        this.rng = new GWTRNG(seed);
}

    public Entity generateRndPlayer()
    {

        Entity mob = new Entity();
        mob.add(new NameCmp(game.playerName));
        mob.add(new CodexCmp());


        mob.add(new CharCmp('@', SColor.WHITE));
        StatsCmp statsCmp =game.getRandomStats(12);
        mob.add(statsCmp);
        mob.add(new InventoryCmp(new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP, EquipmentSlot.LEFT_HAND_WEAP, EquipmentSlot.CHEST}, statsCmp.getStr()+4));

        mob.add(new FactionCmp(FactionCmp.Faction.PLAYER));
        mob.add(new ManaPoolCmp());
        mob.add(new LightCmp(0, SColor.COSMIC_LATTE.toFloatBits()));

        setRandomSkillSet(mob);
        if(((StatsCmp) CmpMapper.getComp(CmpType.STATS, mob)).getPerc()<3) addTorch(mob);
        return mob;
    }
    public Entity generateSkillessPlayer(Coord loc)
    {
        MySparseLayers display = game.dungeonWindow.getComponent(WindowCmp.class).display;
        LevelCmp level = game.currentLevel.getComponent(LevelCmp.class);
        Entity mob = new Entity();
        mob.add(new NameCmp("Player"));
        mob.add(new CodexCmp());
        mob.add(new PositionCmp(loc));


        mob.add(new CharCmp('@', SColor.WHITE));
        mob.add(new GlyphsCmp(display, '@', SColor.WHITE, loc.x, loc.y));
        StatsCmp statsCmp =game.getRandomStats(10);
        mob.add(statsCmp);
        mob.add(new InventoryCmp(new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP, EquipmentSlot.LEFT_HAND_WEAP, EquipmentSlot.CHEST}, statsCmp.getStr()+4));

        AICmp aiCmp = new AICmp(level.decoDungeon, new ArrayList<>(Arrays.asList(TerrainType.STONE, TerrainType.MOSS, TerrainType.SHALLOW_WATER, TerrainType.BRIDGE)));
        mob.add(aiCmp);
        mob.add(new FOVCmp(level.decoDungeon[0].length,level.decoDungeon.length));
        mob.add(new FactionCmp(FactionCmp.Faction.PLAYER));
        mob.add(new ManaPoolCmp(Arrays.asList(School.PHY)));
        mob.add(new LightCmp(0, SColor.COSMIC_LATTE.toFloatBits()));
        game.engine.addEntity(mob);
        return mob;
    }
    public Entity generateRndMob(Coord loc, String name, int depth)
    {
        MySparseLayers display = game.dungeonWindow.getComponent(WindowCmp.class).display;
        LevelCmp level = game.currentLevel.getComponent(LevelCmp.class);
        Entity mob = new Entity();
        mob.add(new NameCmp(name));
        mob.add(new CodexCmp());
        mob.add(new PositionCmp(loc));
        mob.add(new CharCmp('E', SColor.NAVY_BLUE));
        GlyphsCmp glyphsCmp = new GlyphsCmp(display, 'E', SColor.DARK_CERULEAN, loc.x, loc.y);
        glyphsCmp.leftGlyph = display.glyph('•', SColor.DARK_CERULEAN, loc.x, loc.y);
        glyphsCmp.rightGlyph = display.glyph('•', SColor.DARK_CERULEAN, loc.x, loc.y);
        mob.add(glyphsCmp);
        StatsCmp statsCmp =game.getRandomStats(7+(depth*2));
        mob.add(statsCmp);
        mob.add(new InventoryCmp(new EquipmentSlot[]{EquipmentSlot.RIGHT_HAND_WEAP, EquipmentSlot.LEFT_HAND_WEAP, EquipmentSlot.CHEST}, statsCmp.getStr()+4));

        AICmp aiCmp = new AICmp(level.decoDungeon, new ArrayList(Arrays.asList(TerrainType.STONE, TerrainType.MOSS, TerrainType.SHALLOW_WATER, TerrainType.BRIDGE)));
        mob.add(aiCmp);
        mob.add(new FOVCmp(level.decoDungeon[0].length,level.decoDungeon.length));
        mob.add(new FactionCmp(FactionCmp.Faction.MONSTER));
        mob.add(new ManaPoolCmp());
        mob.add(new LightCmp(0, SColor.COSMIC_LATTE.toFloatBits()));
        game.engine.addEntity(mob);
        setRandomSkillSet(mob);
        if(((StatsCmp) CmpMapper.getComp(CmpType.STATS, mob)).getPerc()<3) addTorch(mob);
        return mob;
    }

    public void setRandomSkillSet(Entity mob)
    {
        StatsCmp stats = (StatsCmp)CmpMapper.getComp(CmpType.STATS, mob);
        ManaPoolCmp manaPool = (ManaPoolCmp)CmpMapper.getComp(CmpType.MANA_POOL, mob);
        CodexCmp codex = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, mob);
        int spentLimit = stats.getIntel() + 3;
        List<Skill> skillPool = new ArrayList<Skill>(Arrays.asList(Skill.values()));

        skillPool.remove(0);

        manaPool.spent.addAll(Arrays.asList(Skill.MELEE_ATTACK.prepCost));
        manaPool.spent.addAll(Arrays.asList(Skill.MELEE_ATTACK.castingCost));
        codex.known.add(Skill.MELEE_ATTACK);
        codex.prepared.add(Skill.MELEE_ATTACK);
        manaPool.spent.add(2, School.PHY);
        for(School mana:Skill.MELEE_ATTACK.prepCost)
        {
            manaPool.attuned.add(mana);
            manaPool.spent.remove(mana);
        }
        mob.add((IAbilityCmpSubSys.newAbilityCmp(Skill.MELEE_ATTACK)));
        spentLimit = spentLimit - Skill.MELEE_ATTACK.prepCost.length;


        while (spentLimit > 0 && skillPool.size()>0)
        {
            Skill skill = rng.getRandomElement(skillPool);

            skillPool.remove(skill);
            if (skill.castingCost.length < spentLimit && Skill.qualify(skill, stats) &! codex.getExcludedSchools().contains(skill.school))
            {
                manaPool.spent.addAll(Arrays.asList(skill.prepCost));
                manaPool.spent.addAll(Arrays.asList(skill.castingCost));
                manaPool.spent.addAll(Arrays.asList(skill.castingCost));
                codex.known.add(skill);
                codex.prepared.add(skill);
                for(School mana:skill.prepCost)
                {
                    manaPool.attuned.add(mana);
                    manaPool.spent.remove(mana);
                }
                mob.add((IAbilityCmpSubSys.newAbilityCmp(skill)));
                spentLimit = spentLimit - skill.prepCost.length;
            }
        }
        /*for (Skill skill : skillPool) {
            if (skill.castingCost.length < spentLimit && Skill.qualify(skill, stats) ) {
                manaPool.spent.addAll(Arrays.asList(skill.prepCost));
                manaPool.spent.addAll(Arrays.asList(skill.castingCost));
                manaPool.spent.addAll(Arrays.asList(skill.castingCost));
                codex.known.add(skill);
                codex.prepared.add(skill);
                for(School mana:skill.prepCost)
                {
                    manaPool.attuned.add(mana);
                    manaPool.spent.remove(mana);
                }
                mob.add((IAbilityCmpSubSys.newAbilityCmp(skill)));
                spentLimit = spentLimit - skill.prepCost.length;
            }
        }*/
        if(codex.prepared.contains(Skill.DAGGER_THROW))
        {
            Entity daggerEntity = game.weaponFactory.newBasicWeapon(WeaponType.DAGGER);
            game.weaponFactory.addOnHitSERnd(daggerEntity, TargetType.ENEMY);
            ItemCmp itemCmp = (ItemCmp) CmpMapper.getComp(CmpType.ITEM, daggerEntity);
            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, mob);
            itemCmp.ownerID= mob.hashCode();
            inventoryCmp.put(daggerEntity.hashCode());
            game.engine.addEntity(daggerEntity);

        }
    }

    public void addTorch(Entity mob)
    {
        Entity torch = new Entity();
        torch.add(new NameCmp("Torch"));
        torch.add(new ItemCmp(ItemType.TORCH));
        torch.add(new EquipmentCmp(new EquipmentSlot[]{EquipmentSlot.LEFT_HAND_WEAP}));
        torch.add(new CharCmp('*', SColor.SAFETY_ORANGE));
        torch.add(new LightCmp(rng.between(3, 6), SColor.COSMIC_LATTE.toFloatBits()));
        InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, mob);
        inventoryCmp.put(torch.hashCode());
        game.engine.addEntity(torch);
    }

}
