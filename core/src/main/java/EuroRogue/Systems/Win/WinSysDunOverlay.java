package EuroRogue.Systems.Win;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.SortAbilityBySkillType;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
import EuroRogue.EventComponents.CodexEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.GameState;
import EuroRogue.MenuItem;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.School;
import EuroRogue.SortSkillBySkillType;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.StatusEffectCmp;
import EuroRogue.Systems.AI.AISys;
import squidpony.StringKit;
import EuroRogue.IColoredString;
import squidpony.squidai.Technique;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

public class WinSysDunOverlay extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private String restBar =    "------------------------------------------------";
    private String healthBar =  "------------------------------------------------";
    private String tRestBar =    "------------------------------------------------";
    private String tHealthBar =  "------------------------------------------------";
    private List<TextCellFactory.Glyph> glyphs = new ArrayList<>();
    private ShaderProgram outlineShader = new ShaderProgram(DefaultResources.vertexShader, DefaultResources.outlineFragmentShader);
    private ShaderProgram defaultShader = new ShaderProgram(DefaultResources.vertexShader, DefaultResources.fragmentShader);

    public WinSysDunOverlay()
    {
        super.priority = 10;
    }


    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.one(CodexEvt.class, ItemEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        getGame().globalMenuIndex = 0;
        getGame().keyLookup.clear();
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonOverlayWindow);

        EuroRogue game = getGame();
        if(!windowCmp.display.isVisible()) return;

        MySparseLayers display = windowCmp.display;

        Stage stage = game.dungeonOverlayWindow.getComponent(WindowCmp.class).stage;
        Entity focus = getGame().getFocus();


        display.clear();
        //
        Entity target = getGame().getFocusTarget();
        if(target!=null)
        {
            putTargetHealthEnergyBars(display, target);
        }
        putHealthEnergyBars(display);


        stage.getViewport().apply(false);
        stage.act();
        stage.draw();

        display.font.shader = defaultShader;
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focus);
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, game.dungeonOverlayWindow);
        menuCmp.menuMap.clear();
        int x = 11;
        int y = 37;

        Collections.sort(codexCmp.prepared, new SortSkillBySkillType());
        for(Skill skill : codexCmp.prepared)
        {
            Ability abilityCmp = CmpMapper.getAbilityComp(skill, focus);
            Character key = getGame().globalMenuSelectionKeys[game.globalMenuIndex];
            if(skill.skillType == Skill.SkillType.REACTION || game.gameState == GameState.CAMPING || game.gameState == GameState.STARTING) key = null;
            IColoredString.Impl al = getActionLabel(abilityCmp, key, 10);
            display.put(x, y, al);

            MenuItem menuItem = new MenuItem(al);
            Runnable primaryAction = new Runnable()
            {
                @Override
                public void run()
                {
                    if(abilityCmp.isAvailable())
                        getEngine().getSystem(AISys.class).scheduleActionEvt(focus, abilityCmp);
                }
            };
            if(abilityCmp.aimable )
            {

                PositionCmp positionCmp = (PositionCmp)CmpMapper.getComp(CmpType.POSITION, focus);
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
            Runnable postDescription = new Runnable() {
                @Override
                public void run() {
                    abilityCmp.postToLog(focus, getGame());
                }
            };
            Coord coord = Coord.get(x,y);
            menuItem.addPrimaryAction(primaryAction);
            menuItem.addSecondaryAction(postDescription);
            menuCmp.menuMap.put(coord, key, menuItem);
            getGame().keyLookup.put(key, menuCmp);


            if(skill.skillType != Skill.SkillType.REACTION || game.gameState == GameState.CAMPING || game.gameState == GameState.STARTING) game.globalMenuIndex++;
            x=x+al.length()+1;

        }


        for(Integer scrollID : getGame().getScrollIDs(focus))
        {
            Entity scrollEnt = getGame().getEntity(scrollID);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, scrollEnt);
            Ability abilityCmp = CmpMapper.getAbilityComp(scrollCmp.skill, scrollEnt);
            Character key = getGame().globalMenuSelectionKeys[game.globalMenuIndex];
            if(scrollCmp.skill.skillType == Skill.SkillType.REACTION || game.gameState == GameState.CAMPING || game.gameState == GameState.STARTING) key = null;
            IColoredString.Impl al = getActionLabel(abilityCmp, key, 10);
            display.put(x, y, al);

            MenuItem menuItem = new MenuItem(al);
            Runnable primaryAction = new Runnable()
            {
                @Override
                public void run()
                {
                    if(abilityCmp.isAvailable())
                        getEngine().getSystem(AISys.class).scheduleActionEvt(focus, abilityCmp);
                }
            };
            if(abilityCmp.aimable )
            {

                PositionCmp positionCmp = (PositionCmp)CmpMapper.getComp(CmpType.POSITION, focus);
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
            Runnable postDescription = new Runnable() {
                @Override
                public void run() {
                    abilityCmp.postToLog(focus, getGame());
                }
            };
            Coord coord = Coord.get(x,y);
            menuItem.addPrimaryAction(primaryAction);
            menuItem.addSecondaryAction(postDescription);
            menuCmp.menuMap.put(coord, key, menuItem);
            getGame().keyLookup.put(key, menuCmp);

            if(scrollCmp.skill.skillType != Skill.SkillType.REACTION || game.gameState == GameState.CAMPING || game.gameState == GameState.STARTING) game.globalMenuIndex++;
            x=x+al.length()+1;
        }

        //System.out.println(game.keyLookup.keySet());
        y = 33;
        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, game.ticker);
        for (StatusEffect statusEffect : getGame().getStatusEffects(focus))
        {

            StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, focus);
            if(!statusEffectCmp.display) continue;
            IColoredString.Impl statusEffectLabel = new IColoredString.Impl();
            statusEffectLabel.append(statusEffectCmp.name);

            if(statusEffectCmp.lastTick!=null)
                statusEffectLabel.append(" "+(statusEffectCmp.lastTick-tickerCmp.tick), SColor.WHITE);

            x = 67 - statusEffectLabel.length();
            display.put(x,y,statusEffectLabel);
            y--;
        }
        stage.draw();

        display.font.shader = outlineShader;

    }

    private IColoredString.Impl getActionLabel(Ability abilityCmp, Character selectionKey, int totalLength)
    {

        Skill skill = abilityCmp.getSkill();
        float abilityColor = skill.school.color.toFloatBits();
        if (!abilityCmp.isAvailable())
            abilityColor = SColor.lerpFloatColors(abilityColor, SColor.FLOAT_BLACK, 0.5f);
        IColoredString.Impl coloredString = new IColoredString.Impl();
        if (selectionKey != null)
            coloredString.append(selectionKey.toString() + ")", SColor.colorFromFloat(abilityColor));

        String[] split = StringKit.split(skill.name, " ");
        String nameTag = "";
        for(String string : split)
            nameTag = nameTag + string.substring(0,1);
        coloredString.append(nameTag+" ", SColor.colorFromFloat(abilityColor));
        for (int i = 0; i < (totalLength-skill.name.length()-9); i++)
            coloredString.append(' ', SColor.TRANSPARENT);

        if(abilityCmp.scroll())
            coloredString.append('%', abilityCmp.skill.school.color);
        else
            for (School mana : skill.castingCost) {
                coloredString.append('■', mana.color);
            }
        //coloredString.append(" " + (abilityCmp).aoe.getMaxRange() + " " + abilityCmp.getDamage(performer));
        return coloredString;
    }

    private void updateFocusHotBar() {
        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, getGame().ticker);
        Entity focusEntity = getGame().getFocus();
        MenuCmp menuCmp = (MenuCmp) CmpMapper.getComp(CmpType.MENU, getGame().dungeonOverlayWindow);
        WindowCmp window = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonOverlayWindow);
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focusEntity);
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, focusEntity);
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        List<Ability> preparedAbilities = new ArrayList<>();
        for (Skill skill : codexCmp.getPreparedActions()) {
            Ability ability = CmpMapper.getAbilityComp(skill, focusEntity);

            if (ability != null)
                preparedAbilities.add(CmpMapper.getAbilityComp(skill, focusEntity));
        }
        int finalLength = window.columnIndexes[2] - window.columnIndexes[1] - 5;
        int x = 0;
        int y = 0;
        for (Ability abilityCmp : preparedAbilities) {
            Runnable postDescription = new Runnable() {
                @Override
                public void run() {
                    abilityCmp.postToLog(focusEntity, getGame());
                }
            };
            Coord coord = Coord.get(x, y);
            Character chr = null;

            IColoredString.Impl abilityLabel;
            if (getGame().gameState == GameState.PLAYING) {
                chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                getGame().globalMenuIndex++;
            }
            abilityLabel = getActionLabel(abilityCmp, chr, finalLength);

            MenuItem menuItem = new MenuItem(abilityLabel);
            Runnable primaryAction = new Runnable() {
                @Override
                public void run() {
                    if (abilityCmp.isAvailable())
                        getEngine().getSystem(AISys.class).scheduleActionEvt(focusEntity, abilityCmp);
                }
            };
            if (abilityCmp.aimable) {

                PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, focusEntity);
                primaryAction = new Runnable() {
                    @Override
                    public void run() {
                        if (abilityCmp.isAvailable()) {
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
        if (inventoryCmp != null) {
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
            for (Ability abilityCmp : scrollAbilities) {
                Coord coord = Coord.get(x, y);
                Character chr = null;
                IColoredString.Impl abilityLabel;
                if (getGame().gameState == GameState.PLAYING) {
                    chr = getGame().globalMenuSelectionKeys[getGame().globalMenuIndex];
                    getGame().globalMenuIndex++;
                }
                abilityLabel = getScrollLabel(focusEntity, abilityCmp, chr, finalLength);

                MenuItem menuItem = new MenuItem(abilityLabel);
                Runnable primaryAction = new Runnable() {
                    @Override
                    public void run() {
                        if (abilityCmp.isAvailable())
                            getEngine().getSystem(AISys.class).scheduleActionEvt(focusEntity, abilityCmp);
                    }
                };
                if (abilityCmp.aimable) {

                    PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, focusEntity);
                    primaryAction = new Runnable() {
                        @Override
                        public void run() {
                            if (abilityCmp.isAvailable()) {
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
        for (Ability abilityCmp : preparedReactions) {
            Coord coord = Coord.get(x, y);

            IColoredString.Impl abilityLabel = getActionLabel(abilityCmp, null, 25);
            MenuItem menuItem = new MenuItem(abilityLabel);
            menuCmp.menuMap.put(coord, null, menuItem);
            y++;
        }

        x = 3;
        y = 0;
        for (StatusEffect statusEffect : getGame().getStatusEffects(focusEntity)) {
            StatusEffectCmp statusEffectCmp = (StatusEffectCmp) CmpMapper.getStatusEffectComp(statusEffect, focusEntity);
            Coord coord = Coord.get(x, y);
            IColoredString.Impl statusEffectLabel = new IColoredString.Impl();
            statusEffectLabel.append(statusEffectCmp.name);
            if (statusEffectCmp.lastTick != null)
                statusEffectLabel.append(" " + (statusEffectCmp.lastTick - tickerCmp.tick), SColor.WHITE);
            MenuItem menuItem = new MenuItem(statusEffectLabel);
            menuCmp.menuMap.put(coord, null, menuItem);
            y++;
        }
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

    private void putHealthEnergyBars(MySparseLayers display)
    {
        Entity focus = getGame().getFocus();

        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, focus);

        float percentage = Math.min(1,(float)statsCmp.getRestLvl()/(float)statsCmp.getMaxRestLvl());
        int maxLength = restBar.length();

        String rb = restBar.substring(0, Math.round(maxLength*percentage));

        percentage = Math.min(1, (float)statsCmp.getHp()/(float)statsCmp.getMaxHP());
        maxLength = healthBar.length();
        String hb = healthBar.substring(0, Math.round(maxLength*percentage));
        float hbColor = SColor.lerpFloatColors(SColor.RED.toFloatBits(), SColor.GREEN.toFloatBits(),  percentage);
        display.put(11,38,hb, hbColor, SColor.TRANSPARENT.toFloatBits());
        display.put(11,39,rb, SColor.LIGHT_YELLOW_DYE);
    }

    private void putTargetHealthEnergyBars(MySparseLayers display, Entity target)
    {
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, target);

        float percentage = (float)statsCmp.getRestLvl()/(float)statsCmp.getMaxRestLvl();
        int maxLength = tRestBar.length();

        String rb = tRestBar.substring(0, Math.round(maxLength*percentage));

        percentage = (float)statsCmp.getHp()/(float)statsCmp.getMaxHP();
        maxLength = tHealthBar.length();
        String hb = tHealthBar.substring(0, Math.round(maxLength*percentage));
        float hbColor = SColor.lerpFloatColors(SColor.RED.toFloatBits(), SColor.GREEN.toFloatBits(),  percentage);
        display.put(11,0,hb, hbColor, SColor.TRANSPARENT.toFloatBits());
        display.put(11,1,rb, SColor.LIGHT_YELLOW_DYE);
    }

}
