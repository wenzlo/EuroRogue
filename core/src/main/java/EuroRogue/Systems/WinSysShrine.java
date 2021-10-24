package EuroRogue.Systems;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Arrays;
import java.util.Collections;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.ShrineCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WeaponCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.CodexEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.ShrineEvt;
import EuroRogue.GameState;
import EuroRogue.IColoredString;
import EuroRogue.ItemEvtType;
import EuroRogue.LightHandler;
import EuroRogue.MenuItem;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.School;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.TargetType;
import EuroRogue.WeaponType;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;

public class WinSysShrine extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private Integer shrineID;
    private MySparseLayers display;
    private LightHandler lightHandler;



    public WinSysShrine(MySparseLayers display)
    {
        super.priority = 10;
        this.display = display;
        lightHandler = new LightHandler();
    }


    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(ShrineEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        if(entities.size()>0)
        {
            ShrineEvt shrineEvt = (ShrineEvt) CmpMapper.getComp(CmpType.SHRINE_EVT, entities.get(0));
            if(shrineEvt!=null) shrineID = shrineEvt.shrineID;
        }


        if(getGame().gameState==GameState.SHRINE)
        {
           getGame().globalMenuIndex = 0;
           getGame().keyLookup.clear();

           Entity shrineEntity = getGame().getEntity(shrineID);
           ShrineCmp shrineCmp = ((ShrineCmp) CmpMapper.getComp(CmpType.SHRINE, shrineEntity));

           WindowCmp window = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().shrineWindow));
           MySparseLayers display = (MySparseLayers) window.display;
           if(display.isVisible()==false) return;

           updateShrineMenu(getGame().shrineWindow, shrineEntity);
           //getGame().globalMenuIndex = 1; //key=1;
           MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, getGame().shrineWindow);

           Stage stage = window.stage;

           display.clear();
           //display.fillBackground(SColor.DARK_GRAY);
           for(Coord coord : menuCmp.menuMap.positions())
           {
               display.put(window.columnIndexes[coord.x], coord.y+1, menuCmp.menuMap.get(coord).label);
           }
           getGame().getInput();
           stage.getViewport().apply(false);
           stage.act();
           stage.draw();
       }
    }

    private void updateShrineMenu(Entity windowEntity, Entity shrineEntity)
    {
        ShrineCmp shrineCmp = (ShrineCmp) CmpMapper.getComp(CmpType.SHRINE, shrineEntity);
        if(shrineCmp.charges==0)
        {
            Entity focusEntity = getGame().getFocus();
            CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focusEntity);
            Entity gameStateEvtEnt = new Entity();
            gameStateEvtEnt.add(new GameStateEvt(GameState.PLAYING));
            getEngine().addEntity(gameStateEvtEnt);
            LightCmp lightCmp = (LightCmp) CmpMapper.getComp(CmpType.LIGHT, shrineEntity);
            lightCmp.level=0;
            lightCmp.strobe=0f;
            lightCmp.flicker=0f;
            shrineEntity.remove(ShrineEvt.class);

            for(Skill skill : codexCmp.prepared)
            {
                Ability ability = CmpMapper.getAbilityComp(skill, focusEntity);
                ability.setMap(((LevelCmp)CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel)).decoDungeon);
            }
            return;
        }
        School school = shrineCmp.school;
        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        Entity focusEntity = getGame().getFocus();
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, windowEntity);
        WindowCmp window = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, windowEntity);
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focusEntity);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, focusEntity);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, focusEntity);

        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, focusEntity);

        menuCmp.menuMap.put(Coord.get(0,1), null, new MenuItem(new IColoredString.Impl("Charges = "+shrineCmp.charges, SColor.WHITE )));
        int x = 0;
        int y = 3;
        for (Integer itemID : inventoryCmp.getScrollsIDs())
        {

            Entity scrollEntity = getGame().getEntity(itemID);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, scrollEntity);
            if(scrollCmp.skill.school==school)
            {

                School scrollSchool = scrollCmp.skill.school;
                Runnable primaryAction = new Runnable() {
                    @Override
                    public void run() {
                        Entity eventEntity = new Entity();
                        ItemEvt itemEvt = new ItemEvt(itemID, focusEntity.hashCode(), ItemEvtType.CONSUME);
                        eventEntity.add(itemEvt);
                        getEngine().addEntity(eventEntity);

                        manaPoolCmp.addMana(new School[]{scrollSchool}, statsCmp);
                        shrineCmp.charges--;

                    }
                };

                char chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                MenuItem menuItem = new MenuItem(getShrineScrollLabel(scrollCmp.skill, chr));
                menuItem.addPrimaryAction(primaryAction);

                menuCmp.menuMap.put(Coord.get(x, y), chr, menuItem);
                getGame().keyLookup.put(chr, menuCmp);
                getGame().globalMenuIndex++;
                y++;
            }
        }

        /*for(School scl : School.values())
        {
            if(scl==school)continue;
            if(manaPoolCmp.spent.contains(school))
            {
                Runnable primaryAction = new Runnable() {
                    @Override
                    public void run() {

                        manaPoolCmp.addMana(new School[]{scl});
                        manaPoolCmp.removeMana(new School[]{school});

                    }
                };

                char chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                MenuItem menuItem = new MenuItem(getManaLabel(school, scl, chr));
                menuItem.addPrimaryAction(primaryAction);

                menuCmp.menuMap.put(Coord.get(x, y), chr, menuItem);
                getGame().keyLookup.put(chr, menuCmp);
                getGame().globalMenuIndex++;
                y++;
            }
        }*/
        for(School scl : School.values())
        {
            if(scl==school)continue;
            if(manaPoolCmp.spent.contains(scl))
            {
                Runnable primaryAction = new Runnable() {
                    @Override
                    public void run() {

                        manaPoolCmp.addMana(new School[]{school}, statsCmp);
                        manaPoolCmp.removeMana(new School[]{scl}, statsCmp);
                        shrineCmp.charges = shrineCmp.charges-1;

                    }
                };

                char chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                MenuItem menuItem = new MenuItem(getManaLabel(scl, school, chr));
                menuItem.addPrimaryAction(primaryAction);

                menuCmp.menuMap.put(Coord.get(x, y), chr, menuItem);
                getGame().keyLookup.put(chr, menuCmp);
                getGame().globalMenuIndex++;
                y++;
            }
        }

        for(Skill skill : shrineCmp.skillOffer)
        {
            Runnable primaryAction = new Runnable() {
                @Override
                public void run() {
                    Entity eventEntity = new Entity();
                    CodexEvt codexEvt = new CodexEvt(focusEntity.hashCode(), Arrays.asList(skill), null, null);
                    eventEntity.add(codexEvt);
                    getEngine().addEntity(eventEntity);
                    shrineCmp.skillOffer.remove(skill);
                    shrineCmp.charges = shrineCmp.charges-1;

                }
            };

            char chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
            MenuItem menuItem = new MenuItem(getSkillLabel(skill, chr));
            menuItem.addPrimaryAction(primaryAction);

            menuCmp.menuMap.put(Coord.get(x, y), chr, menuItem);
            getGame().keyLookup.put(chr, menuCmp);
            getGame().globalMenuIndex++;
            y++;
        }

        Integer weaponID = inventoryCmp.getSlotEquippedID(EquipmentSlot.RIGHT_HAND_WEAP);
        if(weaponID!=null && Collections.frequency(manaPoolCmp.unattunedMana(), school)>=3)
        {
            Entity weaponEntity = getGame().getEntity(weaponID);
            Runnable primaryAction = null;
            NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, weaponEntity);
            StatusEffect statusEffect = null;
            switch (school)
            {
                case ARC:

                    break;
                case FIR:
                    statusEffect = StatusEffect.CALESCENT;
                    StatusEffect finalStatusEffect = statusEffect;
                    primaryAction = new Runnable() {
                        @Override
                        public void run() {
                            getGame().weaponFactory.addOnHitSE(weaponEntity, finalStatusEffect, TargetType.ENEMY);

                            manaPoolCmp.removeMana(new School[]{school, school, school}, statsCmp);
                            shrineCmp.charges--;
                        }
                    };
                    break;
                case ICE:
                    statusEffect = StatusEffect.CHILLED;
                    finalStatusEffect = statusEffect;
                    primaryAction = new Runnable() {
                        @Override
                        public void run() {
                            getGame().weaponFactory.addOnHitSE(weaponEntity, finalStatusEffect, TargetType.ENEMY);

                            manaPoolCmp.removeMana(new School[]{school, school, school}, statsCmp);
                            shrineCmp.charges--;
                        }
                    };
                    break;
            }
            if(statusEffect!=null)
            {
                char chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                MenuItem menuItem = new MenuItem(getEnchantLabel(weaponEntity, statusEffect, shrineCmp.school, chr));
                menuItem.addPrimaryAction(primaryAction);

                menuCmp.menuMap.put(Coord.get(x, y), chr, menuItem);
                getGame().keyLookup.put(chr, menuCmp);
                getGame().globalMenuIndex++;
                y++;
            }
        }
    }

    private IColoredString.Impl getShrineScrollLabel(Skill skill, Character selectionKey)
    {
        float abilityColor = skill.school.color.toFloatBits();

        IColoredString.Impl coloredString = new IColoredString.Impl();

        coloredString.append(selectionKey.toString() + ") ", SColor.WHITE);
        coloredString.append(skill.name + " ", SColor.colorFromFloat(abilityColor));

        coloredString.append('%', skill.school.color);
        coloredString.append(" »» ", SColor.WHITE);
        coloredString.append("■ ", skill.school.color);

        return coloredString;
    }

    private IColoredString.Impl getManaLabel(School spent, School gained, Character selectionKey)
    {

        IColoredString.Impl coloredString = new IColoredString.Impl();

        coloredString.append(selectionKey.toString() + ") ", SColor.WHITE);
        coloredString.append("■", spent.color);
        coloredString.append(" »» ", SColor.WHITE);
        coloredString.append("■", gained.color);


        return coloredString;
    }
    private IColoredString.Impl getSkillLabel(Skill skill, Character selectionKey)
    {

        IColoredString.Impl coloredString = new IColoredString.Impl();

        coloredString.append(selectionKey.toString() + ") ", SColor.WHITE);

        coloredString.append(skill.name, skill.school.color);




        return coloredString;
    }
    private IColoredString.Impl getEnchantLabel(Entity weaponEntity, StatusEffect statusEffect, School school, Character selectionKey)
    {
        WeaponCmp weaponCmp = (WeaponCmp)CmpMapper.getComp(CmpType.WEAPON, weaponEntity);
        NameCmp nameCmp = (NameCmp)CmpMapper.getComp(CmpType.NAME, weaponEntity);


        IColoredString.Impl coloredString = new IColoredString.Impl();

        coloredString.append(selectionKey.toString() + ") ", SColor.WHITE);
        coloredString.append(weaponCmp.weaponType.chr+"+", SColor.LIGHT_GRAY);
        coloredString.append(statusEffect.name, school.color);
        return coloredString;
    }




}
