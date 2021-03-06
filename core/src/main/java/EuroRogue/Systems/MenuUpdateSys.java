package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.CodexEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.StatEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.EventComponents.StorageEvt;
import EuroRogue.EventComponents.StorageEvtType;
import EuroRogue.GameState;
import EuroRogue.IColoredString;
import EuroRogue.ItemEvtType;
import EuroRogue.MenuItem;
import EuroRogue.MyEntitySystem;
import EuroRogue.ScheduledEvt;
import EuroRogue.School;
import EuroRogue.SortAbilityBySkillType;
import EuroRogue.StatType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.StatusEffectCmp;
import EuroRogue.Storage;
import EuroRogue.Systems.AI.AISys;
import squidpony.squidai.Technique;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
//TODO move to individual WinSystems
public class MenuUpdateSys extends MyEntitySystem {
    private ImmutableArray<Entity> entities;


    public MenuUpdateSys()
    {
        super.priority = 12;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) { entities = engine.getEntitiesFor(Family.one(MenuCmp.class).get()); }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {

        if(getGame().gameState == GameState.STARTING)
        {
            getGame().globalMenuIndex = 0;
            getGame().keyLookup.clear();
        }
        for (Entity entity : entities)
        {
            if(!((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, entity)).display.isVisible()) continue;
            if(entity == getGame().dungeonOverlayWindow) continue;
            MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, entity);

            menuCmp.menuMap.clear();
            //if (entity == getGame().focusHotBar) updateFocusHotBar(entity);
            //if (entity == getGame().targetHotBar)
                //if(getGame().getFocusTarget()!=null) updateTargetHotBar(entity);
            if(entity == getGame().inventoryWindow && getGame().gameState!= GameState.STARTING) updateInventory(entity);
            if(entity == getGame().campWindow) updateCampMenu(entity);
            if(entity == getGame().startWindow)
            {
                Entity focus = getGame().getFocus();
                StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, focus);
                CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focus);
                ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, focus);
                if(manaPoolCmp!=null && statsCmp != null && codexCmp != null)
                    updateStartMenu(entity);
            }
        }
    }
    private void updateFocusHotBar(Entity entity)
    {
        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        Entity focusEntity = getGame().getFocus();
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, entity);
        WindowCmp window = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, entity);
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focusEntity);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, focusEntity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        List<Ability> preparedAbilities = new ArrayList<>();
        for (Skill skill : codexCmp.getPreparedActions())
        {
            Ability ability = CmpMapper.getAbilityComp(skill, focusEntity);

            if (ability != null)
                preparedAbilities.add(CmpMapper.getAbilityComp(skill, focusEntity));
        }
        int finalLength = window.columnIndexes[2] - window.columnIndexes[1] - 5;
        int x = 0;
        int y = 0;
        for (Ability abilityCmp : preparedAbilities)
        {
            Runnable postDescription = new Runnable() {
                @Override
                public void run() {
                    abilityCmp.postToLog(focusEntity, getGame());
                }
            };
            Coord coord = Coord.get(x, y);
            Character chr = null;

            IColoredString.Impl abilityLabel;
            if(getGame().gameState == GameState.PLAYING)
            {
                chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                getGame().globalMenuIndex++;
            }
            abilityLabel = getActionLabel(focusEntity, abilityCmp, chr, finalLength);

            MenuItem menuItem = new MenuItem(abilityLabel);
            Runnable primaryAction = new Runnable()
            {
                @Override
                public void run()
                {
                    if(abilityCmp.isAvailable())
                        getEngine().getSystem(AISys.class).scheduleActionEvt(focusEntity, abilityCmp);
                }
            };
            if(abilityCmp.aimable )
            {

                PositionCmp positionCmp = (PositionCmp)CmpMapper.getComp(CmpType.POSITION, focusEntity);
                primaryAction = new Runnable() {
                    @Override
                    public void run()
                    {
                        if(abilityCmp.isAvailable())
                        {
                            abilityCmp.apply(positionCmp.coord, positionCmp.coord);

                            getGame().getFocus().add(new AimingCmp(abilityCmp.getSkill(), abilityCmp.scroll()));
                            Entity eventEntity = new Entity();
                            GameStateEvt gameStateEvt = new GameStateEvt(GameState.AIMING);
                            eventEntity.add(gameStateEvt);
                            getEngine().addEntity(eventEntity);

                        }
                    }
                };
            }
            menuItem.addPrimaryAction(primaryAction);
            menuItem.addSecondaryAction(postDescription);
            menuCmp.menuMap.put(coord, chr, menuItem);
            getGame().keyLookup.put(chr, menuCmp);
            y++;

        }

        List<Ability> scrollAbilities = new ArrayList<>();
        if(inventoryCmp!=null)
        {
            for (Integer itemID : inventoryCmp.getScrollsIDs()) {
                Entity scrollEntity = getGame().getEntity(itemID);
                ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, scrollEntity);
                if (scrollCmp != null)
                    scrollAbilities.add(CmpMapper.getAbilityComp(scrollCmp.skill, scrollEntity));
            }
            finalLength = window.columnIndexes[2] - window.columnIndexes[1] - 5;
            x = 1;
            y = 0;
            Collections.sort(scrollAbilities, new SortAbilityBySkillType());
            for (Ability abilityCmp : scrollAbilities)
            {
                Coord coord = Coord.get(x, y);
                Character chr = null;
                IColoredString.Impl abilityLabel;
                if(getGame().gameState == GameState.PLAYING)
                {
                    chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                    getGame().globalMenuIndex++;
                }
                abilityLabel = getScrollLabel(focusEntity, abilityCmp, chr, finalLength);

                MenuItem menuItem = new MenuItem(abilityLabel);
                Runnable primaryAction = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(abilityCmp.isAvailable())
                            getEngine().getSystem(AISys.class).scheduleActionEvt(focusEntity, abilityCmp);
                    }
                };
                if(abilityCmp.aimable )
                {

                    PositionCmp positionCmp = (PositionCmp)CmpMapper.getComp(CmpType.POSITION, focusEntity);
                    primaryAction = new Runnable() {
                        @Override
                        public void run()
                        {
                            if(abilityCmp.isAvailable())
                            {
                                abilityCmp.apply(positionCmp.coord, positionCmp.coord);
                                getGame().getFocus().add(new AimingCmp(abilityCmp.getSkill(), abilityCmp.scroll()));
                                Entity eventEntity = new Entity();
                                GameStateEvt gameStateEvt = new GameStateEvt(GameState.AIMING);
                                eventEntity.add(gameStateEvt);
                                getEngine().addEntity(eventEntity);
                            }
                        }
                    };
                }
                menuItem.addPrimaryAction(primaryAction);
                menuCmp.menuMap.put(coord, chr, menuItem);
                getGame().keyLookup.put(chr, menuCmp);
                y++;
            }
        }


        List<Ability> preparedReactions = new ArrayList<>();
        for (Skill skill : codexCmp.getPreparedReactions()) {
            Ability ability = CmpMapper.getAbilityComp(skill, focusEntity);
            if (ability != null)
                preparedReactions.add(CmpMapper.getAbilityComp(skill, focusEntity));
        }
        x = 2;
        y = 0;
        for (Ability abilityCmp : preparedReactions)
        {
            Coord coord = Coord.get(x, y);

            IColoredString.Impl abilityLabel = getActionLabel(focusEntity, abilityCmp, null, 25);
            MenuItem menuItem = new MenuItem(abilityLabel);
            menuCmp.menuMap.put(coord, null, menuItem);
            y++;
        }

        x = 3;
        y = 0;
        for (StatusEffect statusEffect : getGame().getStatusEffects(focusEntity))
        {
            StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, focusEntity);
            Coord coord = Coord.get(x, y);
            IColoredString.Impl statusEffectLabel = new IColoredString.Impl();
            statusEffectLabel.append(statusEffectCmp.name);
            if(statusEffectCmp.lastTick!=null)
                statusEffectLabel.append(" "+(statusEffectCmp.lastTick-tickerCmp.tick), SColor.WHITE);
            MenuItem menuItem = new MenuItem(statusEffectLabel);
            menuCmp.menuMap.put(coord, null, menuItem);
            y++;
        }


    }
    private void updateTargetHotBar(Entity entity)
    {
        Entity focusTarget = getGame().getFocusTarget();
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, entity);
        WindowCmp window = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, entity);
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focusTarget);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, focusTarget);

        List<Ability> preparedAbilities = new ArrayList<>();
        for (Skill skill : codexCmp.getPreparedActions())
        {
            Ability ability = CmpMapper.getAbilityComp(skill, focusTarget);
            if (ability != null)
                preparedAbilities.add(CmpMapper.getAbilityComp(skill, focusTarget));
        }
        int finalLength = window.columnIndexes[1] - window.columnIndexes[0] - 2;
        int x = 0;
        int y = 0;
        for (Ability abilityCmp : preparedAbilities)
        {
            Coord coord = Coord.get(x, y);
            IColoredString.Impl abilityLabel = getActionLabel(focusTarget, abilityCmp, null, finalLength);
            MenuItem menuItem = new MenuItem(abilityLabel);
            menuCmp.menuMap.put(coord, null, menuItem);
            y++;
        }

        List<Ability> scrollAbilities = new ArrayList<>();
        for (Integer itemID : inventoryCmp.getScrollsIDs()) {
            Entity itemEntity = getGame().getEntity(itemID);
            ScrollCmp scrollCmp = null;

            scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, itemEntity);
            if (scrollCmp != null)
                scrollAbilities.add(CmpMapper.getAbilityComp(scrollCmp.skill, itemEntity));
        }
        finalLength = window.columnIndexes[2] - window.columnIndexes[1] - 2;
        x = 1;
        y = 0;
        Collections.sort(scrollAbilities, new SortAbilityBySkillType());
        for (Ability abilityCmp : scrollAbilities)
        {
            Coord coord = Coord.get(x, y);
            IColoredString.Impl abilityLabel = getScrollLabel(getGame().getScrollForSkill(abilityCmp.getSkill(), getGame().getFocusTarget()), abilityCmp, null, finalLength);
            MenuItem menuItem = new MenuItem(abilityLabel);
            menuCmp.menuMap.put(coord, null, menuItem);
            y++;
        }

        List<Ability> preparedReactions = new ArrayList<>();
        for (Skill skill : codexCmp.getPreparedReactions()) {
            Ability ability = CmpMapper.getAbilityComp(skill, focusTarget);
            if (ability != null)
                preparedReactions.add(CmpMapper.getAbilityComp(skill, focusTarget));
        }

        finalLength = window.columnIndexes[1] - window.columnIndexes[0] - 2;
        x = 2;
        y = 0;
        for (Ability abilityCmp : preparedReactions)
        {
            Coord coord = Coord.get(x, y);

            IColoredString.Impl abilityLabel = getActionLabel(focusTarget, abilityCmp, null, finalLength);
            MenuItem menuItem = new MenuItem(abilityLabel);
            menuCmp.menuMap.put(coord, null, menuItem);
            y++;
        }

        x = 3;
        y = 0;
        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        for (StatusEffect statusEffect : getGame().getStatusEffects(focusTarget))
        {
            StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, focusTarget);
            Coord coord = Coord.get(x, y);
            IColoredString.Impl statusEffectLabel = new IColoredString.Impl();
            statusEffectLabel.append(statusEffectCmp.name);
            if(statusEffectCmp.lastTick!=null)
            {

                statusEffectLabel.append(" "+(statusEffectCmp.lastTick-tickerCmp.tick), SColor.WHITE);
            }
            MenuItem menuItem = new MenuItem(statusEffectLabel);
            menuCmp.menuMap.put(coord, null, menuItem);
            y++;
        }
    }
    private void updateInventory(Entity entity)
    {
        Entity focusEntity = getGame().getFocus();

        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, entity);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, focusEntity);
        if(inventoryCmp==null)return;

        int x = 0;
        int y = 0;
        for(Integer id : inventoryCmp.getItemIDs())
        {
            Entity itemEntity = getGame().getEntity(id);
            if(itemEntity==null) continue;
            if(CmpMapper.getComp(CmpType.SCROLL, itemEntity)!=null) continue;;
            String name = ((NameCmp)CmpMapper.getComp(CmpType.NAME, itemEntity)).name;
            if(name=="Food") continue;
            Coord coord = Coord.get(x, y);
            IColoredString.Impl abilityLabel;
            Character chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
            CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, itemEntity);
            if(getGame().gameState==GameState.PLAYING)
            {
                abilityLabel = new IColoredString.Impl(chr+") ", SColor.WHITE);
                abilityLabel.append(charCmp.chr,charCmp.color);
                abilityLabel.append(" "+name,charCmp.color);
                getGame().globalMenuIndex++;
            }
            else {

                abilityLabel = new IColoredString.Impl();
                abilityLabel.append(charCmp.chr,charCmp.color);
                abilityLabel.append(" "+name,charCmp.color);
            }

            MenuItem menuItem = new MenuItem(abilityLabel);
            EquipmentCmp equipmentCmp = (EquipmentCmp) CmpMapper.getComp(CmpType.EQUIPMENT, itemEntity);
            Runnable primaryAction=null;
            Runnable secondaryAction=null;
            if(equipmentCmp!=null)
            {
                primaryAction = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        scheduleItemEquipEvt(focusEntity, id);
                    }
                };
                secondaryAction = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Entity eventEntity = new Entity();
                        ItemEvt itemEvt = new ItemEvt(itemEntity.hashCode(), focusEntity.hashCode(), ItemEvtType.DROP);
                        eventEntity.add(itemEvt);
                        getGame().engine.addEntity(eventEntity);
                    }
                };
            }
            menuItem.addPrimaryAction(primaryAction);
            menuItem.addSecondaryAction(secondaryAction);
            menuCmp.menuMap.put(coord, chr, menuItem);
            getGame().keyLookup.put(chr, menuCmp);
            y++;

        }

        IColoredString.Impl abilityLabel = new IColoredString.Impl("??ood Rations = "+ inventoryCmp.getFoodIDs().size(), SColor.BRIGHT_GOLD_BROWN);
        MenuItem menuItem = new MenuItem(abilityLabel);
        menuCmp.menuMap.put(Coord.get(0,13),null, menuItem);

        if(getGame().gameState==GameState.PLAYING || getGame().gameState==GameState.CAMPING)
        {
            x=1;
            y=0;
            for(EquipmentSlot slot : inventoryCmp.equipmentSlots.keySet())
            {
                Character chr = null;
                Integer equipmentID = inventoryCmp.getSlotEquippedID(slot);
                Entity itemEntity = null;
                Runnable primaryAction=null;
                Runnable secondaryAction=null;
                if(equipmentID!=null)
                {
                    chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                    getGame().globalMenuIndex++;
                    itemEntity = getGame().getEntity(equipmentID);
                    Entity finalItemEntity = itemEntity;
                    primaryAction = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Entity eventEntity = new Entity();
                            ItemEvt itemEvt = new ItemEvt(finalItemEntity.hashCode(), focusEntity.hashCode(), ItemEvtType.UNEQUIP);
                            eventEntity.add(itemEvt);
                            getGame().engine.addEntity(eventEntity);
                        }
                    };
                    secondaryAction = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Entity eventEntity = new Entity();
                            ItemEvt itemEvt = new ItemEvt(finalItemEntity.hashCode(), focusEntity.hashCode(), ItemEvtType.DROP);
                            eventEntity.add(itemEvt);
                            getGame().engine.addEntity(eventEntity);
                        }
                    };
                    getGame().keyLookup.put(chr, menuCmp);
                }
                menuItem = new MenuItem(getSlotLabel(equipmentID, chr, slot));
                menuItem.addPrimaryAction(primaryAction);
                menuItem.addSecondaryAction(secondaryAction);
                menuCmp.menuMap.put(Coord.get(x,y), chr, menuItem );
                y++;
            }
            for(Integer id : inventoryCmp.getScrollsIDs())
            {
                Entity scrollEntity = getGame().getEntity(id);
                if(scrollEntity==null) continue;
                String name = ((NameCmp)CmpMapper.getComp(CmpType.NAME, scrollEntity)).name;
                Coord coord = Coord.get(x, y);
                IColoredString.Impl scrollLabel;
                Character chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, scrollEntity);
                if(getGame().gameState==GameState.PLAYING)
                {
                    scrollLabel = new IColoredString.Impl(chr+") ", SColor.WHITE);
                    scrollLabel.append(charCmp.chr,charCmp.color);
                    scrollLabel.append(" "+name,charCmp.color);
                    getGame().globalMenuIndex++;
                }
                else {

                    scrollLabel = new IColoredString.Impl();
                    scrollLabel.append(charCmp.chr,charCmp.color);
                    scrollLabel.append(" "+name,charCmp.color);
                }
                Runnable secondaryAction = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Entity finalItemEntity = scrollEntity;
                        Entity eventEntity = new Entity();
                        ItemEvt itemEvt = new ItemEvt(finalItemEntity.hashCode(), focusEntity.hashCode(), ItemEvtType.DROP);
                        eventEntity.add(itemEvt);
                        getGame().engine.addEntity(eventEntity);
                    }
                };
                getGame().keyLookup.put(chr, menuCmp);

                menuItem = new MenuItem(scrollLabel);
                menuItem.addPrimaryAction(secondaryAction);
                menuItem.addSecondaryAction(secondaryAction);
                menuCmp.menuMap.put(coord, chr, menuItem);
                getGame().keyLookup.put(chr, menuCmp);
                y++;

            }
        }
    }
    private void updateCampMenu(Entity entity)
    {
        Entity focusEntity = getGame().getFocus();
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, entity);
        WindowCmp window = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, entity);
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focusEntity);
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, focusEntity);
        ManaPoolCmp manaPoolCmp = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, focusEntity);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, focusEntity);

        int finalLength = window.columnIndexes[1] - window.columnIndexes[0] - 2;
        int x = 0;
        int y = 0;
        for (Skill skill : codexCmp.known)
        {
            Ability abilityCmp = CmpMapper.getAbilityComp(skill, focusEntity);
            if(abilityCmp==null)
                abilityCmp = Ability.newAbilityCmp(skill, true);
            Ability finalAbilityCmp = abilityCmp;
            Runnable postDescription = new Runnable() {
                @Override
                public void run() {
                    finalAbilityCmp.postToLog(focusEntity, getGame());
                }
            };
            Coord coord = Coord.get(x, y);
            Character chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
            IColoredString.Impl abilityLabel = getCodexSkillLabel(skill, chr, finalLength, codexCmp.prepared.contains(skill));
            Character charKey = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
            MenuItem menuItem = new MenuItem(abilityLabel);
            Runnable primaryAction = null;
            if(codexCmp.getPreparedReactions().contains(skill) && skill != Skill.MELEE_ATTACK || codexCmp.getPreparedActions().contains(skill) && skill != Skill.MELEE_ATTACK)
            {
                primaryAction = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Entity eventEntity = new Entity();
                        CodexEvt codexEvt = new CodexEvt(focusEntity.hashCode(), null, Arrays.asList(skill), null);
                        eventEntity.add(codexEvt);
                        getGame().engine.addEntity(eventEntity);
                    }
                };
            } else if(Skill.affordToPrep(skill, (ManaPoolCmp)CmpMapper.getComp(CmpType.MANA_POOL, focusEntity))){

                primaryAction = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Entity eventEntity = new Entity();
                        CodexEvt codexEvt = new CodexEvt(focusEntity.hashCode(), null,null,  Arrays.asList(skill));
                        eventEntity.add(codexEvt);
                        getGame().engine.addEntity(eventEntity);
                    }
                };
            }

            menuItem.addPrimaryAction(primaryAction);
            menuItem.addSecondaryAction(postDescription);
            menuCmp.menuMap.put(coord, charKey, menuItem);

            getGame().keyLookup.put(charKey, menuCmp);


            y++;
            getGame().globalMenuIndex++;
        }

        finalLength = window.columnIndexes[1] - window.columnIndexes[0] - 2;
        x = 1;
        y = 0;
        for (StatType statType : StatType.CORE_STATS)
        {
            Coord coord = Coord.get(x, y);
            Character chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
            float statColor = SColor.WHITE.toFloatBits();
            if (!statsCmp.afford(statType, manaPoolCmp))
                statColor = SColor.lerpFloatColors(statColor, SColor.FLOAT_BLACK, 0.5f);
            IColoredString.Impl statLabel = new IColoredString.Impl(chr + ") " + statType.name()+" ",  SColor.colorFromFloat(statColor));
            for(School mana : statsCmp.getStatCost(statType)) statLabel.append('???', mana.color);

            Character charKey = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
            MenuItem menuItem = new MenuItem(statLabel);

            Runnable primaryAction = new Runnable()
            {
                @Override
                public void run() {
                    Entity eventEntity = new Entity();
                    StatEvt statEvt = new StatEvt(focusEntity.hashCode(), statType, 1);
                    eventEntity.add(statEvt);
                    getGame().engine.addEntity(eventEntity);
                }
            };
            Runnable secondaryAction = new Runnable()
            {
                @Override
                public void run() {
                    Entity eventEntity = new Entity();
                    StatEvt statEvt = new StatEvt(focusEntity.hashCode(), statType, -1);
                    eventEntity.add(statEvt);
                    getGame().engine.addEntity(eventEntity);
                }
            };
            menuItem.addPrimaryAction(primaryAction);
            menuItem.addSecondaryAction(secondaryAction);
            menuCmp.menuMap.put(coord, charKey, menuItem);
            getGame().keyLookup.put(charKey, menuCmp);


            y++;
            getGame().globalMenuIndex++;
        }

        if(!inventoryCmp.getFoodIDs().isEmpty())
        {
            x=2;
            y=0;
            char chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
            MenuItem eatFood = new MenuItem(new IColoredString.Impl(chr+") Eat Food", SColor.BRIGHT_GOLD_BROWN));

            getGame().globalMenuIndex++;
            Runnable primaryAction = new Runnable()
            {
                @Override
                public void run()
                {

                    inventoryCmp.consumeFood(inventoryCmp.getFoodIDs().get(0));
                    Entity eventEntity = new Entity();
                    StatusEffectEvt statusEffectEvt = new StatusEffectEvt(getGame().getGameTick(), null, StatusEffect.WELL_FED, null, getGame().getFocus().hashCode(), getGame().getFocus().hashCode(), null );
                    eventEntity.add(statusEffectEvt);
                    getEngine().addEntity(eventEntity);
                    statsCmp.rl = statsCmp.getMaxRestLvl();
                }
            };
            eatFood.addPrimaryAction(primaryAction);
            menuCmp.menuMap.put(Coord.get(x,y), chr, eatFood);
            getGame().keyLookup.put(chr, menuCmp);

        }
    }
    private void updateStartMenu(Entity entity)
    {
        //System.out.println("Updating Start Menu");
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, entity);

        Runnable primaryAction = new Runnable() {
            @Override
            public void run()
            {
                getGame().newGame();
            }
        };
        char chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
        String label = "Start Tutorial Level";
        if(getGame().depth>1)
            label = "Start Game";
        MenuItem menuItem = new MenuItem(new IColoredString.Impl(chr+") " + label, SColor.WHITE));
        menuItem.addPrimaryAction(primaryAction);

        int x=0; int y=0;
        menuCmp.menuMap.put(Coord.get(x,y), chr, menuItem );
        getGame().keyLookup.put(chr, menuCmp);
        getGame().globalMenuIndex++;
        y++;

        Entity player = getGame().player;

        primaryAction = new Runnable() {
            @Override
            public void run()
            {
                getGame().generatePlayer();
                getGame().depth = 1;

            }
        };
        chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
        menuItem = new MenuItem(new IColoredString.Impl(chr+") New Random Tutorial Build", SColor.WHITE));
        menuItem.addPrimaryAction(primaryAction);


        menuCmp.menuMap.put(Coord.get(x,y), chr, menuItem );
        getGame().keyLookup.put(chr, menuCmp);;
        getGame().globalMenuIndex++;
        y++;

        if(getGame().buildStorage.buildKeys!=null)
        {
            for(String key : getGame().buildStorage.buildKeys)
            {
                primaryAction = new Runnable() {
                    @Override
                    public void run()
                    {
                        StorageEvt storageEvt = new StorageEvt(key, StorageEvtType.LOAD_BUILD);
                        player.add(storageEvt);
                        getGame().depth = 2;

                    }
                };
                Runnable secondaryAction = new Runnable() {
                    @Override
                    public void run()
                    {
                        StorageEvt storageEvt = new StorageEvt(key, StorageEvtType.DELETE_BUILD);
                        player.add(storageEvt);
                        getGame().depth = 1;

                    }
                };
                chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                menuItem = new MenuItem(new IColoredString.Impl(chr+") Load Build: "+ key, SColor.WHITE));
                menuItem.addPrimaryAction(primaryAction);
                menuItem.addSecondaryAction(secondaryAction);
                menuCmp.menuMap.put(Coord.get(x,y), chr, menuItem );
                getGame().keyLookup.put(chr, menuCmp);;
                getGame().globalMenuIndex++;
                y++;
                //System.out.println("build choice added");
            }
        }





        y++;
        x++;
        y=12;

        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, getGame().getFocus());
        menuCmp.menuMap.put(Coord.get(x,y), null, new MenuItem(new IColoredString.Impl("Stat Increase Costs", SColor.WHITE)));

        y++;
        y++;
        for (StatType statType : StatType.CORE_STATS)
        {
            Coord coord = Coord.get(x, y);
            float statColor = SColor.WHITE.toFloatBits();
            IColoredString.Impl statLabel = new IColoredString.Impl(statType.name()+" ",  SColor.colorFromFloat(statColor));


            for(School mana : statsCmp.getStatCost(statType)) statLabel.append('???', mana.color);

            menuItem = new MenuItem(statLabel);

            menuCmp.menuMap.put(coord, null, menuItem);

            y++;
        }

    }

    private IColoredString.Impl getActionLabel(Entity performer, Ability abilityCmp, Character selectionKey, int totalLength)
    {

        Skill skill = abilityCmp.getSkill();
        float abilityColor = skill.school.color.toFloatBits();
        if (!abilityCmp.isAvailable())
            abilityColor = SColor.lerpFloatColors(abilityColor, SColor.FLOAT_BLACK, 0.5f);
        IColoredString.Impl coloredString = new IColoredString.Impl();
        if (selectionKey != null)
            coloredString.append(selectionKey.toString() + ") ", SColor.colorFromFloat(abilityColor));
        coloredString.append(skill.name + " ", SColor.colorFromFloat(abilityColor));
        for (int i = 0; i < (totalLength-skill.name.length()-9); i++)
            coloredString.append(' ', SColor.TRANSPARENT);
        for (School mana : skill.castingCost) {
            coloredString.append('???', mana.color);
        }
        coloredString.append(" " + (abilityCmp).aoe.getMaxRange() + " " + abilityCmp.getDamage(performer));
        return coloredString;
    }
    private IColoredString.Impl getScrollLabel(Entity scrollEntity, Ability abilityCmp, Character selectionKey, int totalLength)
    {

        Skill skill = abilityCmp.getSkill();
        float abilityColor = skill.school.color.toFloatBits();
        if (!abilityCmp.isAvailable())
            abilityColor = SColor.lerpFloatColors(abilityColor, SColor.FLOAT_BLACK, 0.5f);
        IColoredString.Impl coloredString = new IColoredString.Impl();
        if (selectionKey != null && abilityCmp.getSkill().skillType != Skill.SkillType.REACTION)
            coloredString.append(selectionKey.toString() + ") ", SColor.colorFromFloat(abilityColor));
        coloredString.append(skill.name + " ", SColor.colorFromFloat(abilityColor));
        for (int i = 0; i < (totalLength-skill.name.length()-9); i++)
            coloredString.append(' ', SColor.TRANSPARENT);

        coloredString.append('%', skill.school.color);
        coloredString.append(String.valueOf(" "+((Technique)abilityCmp).aoe.getMaxRange())+" "+abilityCmp.getDamage(scrollEntity) );

        return coloredString;
    }

    private IColoredString.Impl getSlotLabel(Integer itemID, Character chr, EquipmentSlot slot)
    {
        IColoredString.Impl slotLabel = new IColoredString.Impl();

        Entity itemEntity = getGame().getEntity(itemID);

        if(itemEntity!=null)
        {
            String name = ((NameCmp)CmpMapper.getComp(CmpType.NAME, itemEntity)).name;
            CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, itemEntity);
            slotLabel.append(chr+")", SColor.WHITE);
            slotLabel.append(" "+slot.abr+" ", SColor.WHITE);
            slotLabel.append(charCmp.chr, charCmp.color);
            slotLabel.append(" "+name, charCmp.color);
        }

        else slotLabel.append("   "+slot.abr, SColor.WHITE);


        return slotLabel;
    }
    private IColoredString.Impl getCodexSkillLabel(Skill skill, Character selectionKey, int totalLength, boolean prepared)
    {
        float abilityColor = skill.school.color.toFloatBits();
        if(!prepared) abilityColor = SColor.lerpFloatColors(abilityColor, SColor.FLOAT_BLACK, 0.5f);
        IColoredString.Impl coloredString = new IColoredString.Impl();

        coloredString.append(selectionKey.toString() + ") ", SColor.colorFromFloat(abilityColor));
        coloredString.append(skill.name + " ", SColor.colorFromFloat(abilityColor));
        for (int i = 0; i < (totalLength - coloredString.length() - skill.prepCost.length); i++)
            coloredString.append(' ', SColor.TRANSPARENT);
        for(School mana : skill.prepCost)
            coloredString.append('???', mana.color);

        return coloredString;
    }
    public int scheduleItemEquipEvt (Entity entity, int itemID)
    {
        TickerCmp ticker = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, entity);
        int scheduledTick = ticker.tick + statsCmp.getTTMoveBase();

        ItemEvt itemEvt = new ItemEvt(itemID, entity.hashCode(), ItemEvtType.EQUIP);
        ScheduledEvt scheduledEvt = new ScheduledEvt(scheduledTick,entity.hashCode(),itemEvt);
        ticker.actionQueue.add(scheduledEvt);

        return scheduledTick;
    }
}