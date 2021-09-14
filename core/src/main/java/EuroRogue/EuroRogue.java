package EuroRogue;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import EuroRogue.AbilityCmpSubSystems.DaggerThrow;
import EuroRogue.AbilityCmpSubSystems.IAbilityCmpSubSys;
import EuroRogue.AbilityCmpSubSystems.MeleeAttack;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WeaponCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.Components.PositionCmp;
import EuroRogue.EventComponents.LevelEvt;
import EuroRogue.Listeners.ActorListener;
import EuroRogue.StatusEffectCmps.Enlightened;
import EuroRogue.StatusEffectCmps.Exhausted;
import EuroRogue.StatusEffectCmps.Hungry;
import EuroRogue.StatusEffectCmps.LeatherArmorEfct;
import EuroRogue.StatusEffectCmps.MailArmorEfct;
import EuroRogue.StatusEffectCmps.PlateArmorEfct;
import EuroRogue.StatusEffectCmps.QStaffEfct;
import EuroRogue.StatusEffectCmps.StaffEfct;
import EuroRogue.StatusEffectCmps.Staggered;
import EuroRogue.StatusEffectCmps.Starving;
import EuroRogue.StatusEffectCmps.WaterWalking;
import EuroRogue.StatusEffectCmps.WellFed;
import EuroRogue.StatusEffectListeners.BurningListener;
import EuroRogue.StatusEffectListeners.CalescentListener;
import EuroRogue.StatusEffectListeners.ChilledListener;
import EuroRogue.StatusEffectListeners.DaggerListener;
import EuroRogue.StatusEffectListeners.EnlightenedListener;
import EuroRogue.StatusEffectListeners.EnragedListener;
import EuroRogue.StatusEffectListeners.ExhaustedListener;
import EuroRogue.StatusEffectListeners.FrozenListener;
import EuroRogue.StatusEffectListeners.HungryListener;
import EuroRogue.StatusEffectListeners.LeatherArmorListener;
import EuroRogue.StatusEffectListeners.MailArmorListener;
import EuroRogue.StatusEffectListeners.PlateArmorListener;
import EuroRogue.StatusEffectListeners.QStaffEffectListener;
import EuroRogue.StatusEffectListeners.StaffEffectListener;
import EuroRogue.StatusEffectListeners.StaggeredListener;
import EuroRogue.StatusEffectListeners.StarvingListener;
import EuroRogue.StatusEffectListeners.SwordEffectListener;
import EuroRogue.StatusEffectCmps.Burning;
import EuroRogue.StatusEffectCmps.Calescent;
import EuroRogue.StatusEffectCmps.Chilled;
import EuroRogue.StatusEffectCmps.DaggerEfct;
import EuroRogue.StatusEffectCmps.Enraged;
import EuroRogue.StatusEffectCmps.Frozen;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.SwordEfct;
import EuroRogue.Listeners.ItemListener;
import EuroRogue.StatusEffectListeners.WaterWalkingListener;
import EuroRogue.StatusEffectListeners.WellFedListener;
import EuroRogue.Systems.ActionSys;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.Systems.MakeCampSys;
import EuroRogue.Systems.CodexSys;
import EuroRogue.Systems.DamageApplicationSys;
import EuroRogue.Systems.DeathSys;
import EuroRogue.Systems.GameStateSys;
import EuroRogue.Systems.LevelSys;
import EuroRogue.Systems.LightingSys;
import EuroRogue.Systems.MenuUpdateSys;
import EuroRogue.Systems.NoiseSys;
import EuroRogue.Systems.StatSys;
import EuroRogue.Systems.StatusEffectEvtSys;
import EuroRogue.Systems.StatusEffectRemovalSys;
import EuroRogue.Systems.WinSysCamp;
import EuroRogue.Systems.WinSysGameOver;
import EuroRogue.Systems.WinSysHotBar;
import EuroRogue.Systems.WinSysDungeon;
import EuroRogue.Systems.WinSysLog;
import EuroRogue.Systems.WinSysMana;
import EuroRogue.Systems.WinSysStart;
import EuroRogue.Systems.WinSysStats;
import EuroRogue.Systems.WinSysInventory;
import EuroRogue.Systems.EventCleanUpSys;
import EuroRogue.Systems.FOVSys;
import EuroRogue.Systems.FocusTargetSys;
import EuroRogue.Systems.ItemSys;
import EuroRogue.Systems.MovementSys;
import EuroRogue.Systems.ReactionSys;
import EuroRogue.Systems.RestIdleCampSys;
import EuroRogue.Systems.AISys;
import EuroRogue.Systems.TickerSys;
import squidpony.SquidStorage;
import squidpony.StringKit;
import squidpony.squidai.AOE;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.FilterBatch;
import squidpony.squidgrid.gui.gdx.FloatFilters;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidMouse;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidgrid.mapping.SectionDungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EuroRogue extends ApplicationAdapter {

    public MyEngine engine = new MyEngine(this);
    public MobFactory mobFactory;
    public WeaponFactory weaponFactory;
    public ArmorFactory armorFactory;
    public FoodFactory foodFactory;
    public CmpMapper cmpMapper = new CmpMapper();
    public Integer globalMenuIndex = 0;
    public char[] globalMenuSelectionKeys = "1234567890-=qweruiop[]".toCharArray();
    public Entity gameOverWindow, startWindow, player, dungeonWindow, focusHotBar, targetHotBar, focusManaWindow, inventoryWindow, targetManaWindow, focusStatsWindow, targetStatsWindow, campWindow, ticker, logWindow, currentLevel;
    public  List<Entity> playingWindows, campingWindows, allWindows, startWindows, gameOverWindows;
    public float lastFrameTime;
    public GameState gameState;
    public String playerName = "Rodney";

    // FilterBatch is almost the same as SpriteBatch, but is a bit faster with SquidLib and allows color filtering
    private FilterBatch filterBatch;
    // a type of random number generator, see below
    public GWTRNG rng;

    public SectionDungeonGenerator dungeonGen;
    /** In number of cells */
    private static final int gridWidth = 166;
    /** In number of cells */
    private static final int gridHeight = 100;
    /** In number of cells */
    public static final int bigWidth = gridWidth ;
    /** In number of cells */
    public static final int bigHeight = gridHeight ;
    private static final int cellWidth = 9;
    /** The pixel height of a cell */
    private static final int cellHeight = 9;
    public SquidInput input, campInput, startInput;
    public InputMultiplexer inputProcessor;
    private final Color bgColor=Color.BLACK;
    public int depth = 0;


    public void newGame()
    {
        rng = new GWTRNG(playerName);
        mobFactory = new MobFactory(this, rng.nextInt());
        weaponFactory = new WeaponFactory(rng.nextInt());
        armorFactory = new ArmorFactory(rng.nextInt());
        foodFactory = new FoodFactory();
        engine.addSystem(new LevelSys(rng.nextInt(), mobFactory, weaponFactory, armorFactory));
        dungeonGen = new SectionDungeonGenerator(42, 42, new GWTRNG(rng.nextInt()));
        dungeonGen.addDoors(100, false);
        dungeonGen.addGrass(3, 15);
        //dungeonGen.addMaze(35);
        //dungeonGen.addWater(3, 15);
        dungeonGen.addLake(35);

        //dungeonGen.utility.closeDoors(preDungeon);


        currentLevel = new Entity();
        engine.addEntity(currentLevel);


        player = mobFactory.generateRndPlayer();
        player.add(new FocusCmp());
        Entity levelEvtEntity = new Entity();
        LevelEvt levelEvt = new LevelEvt();
        levelEvtEntity.add(levelEvt);
        engine.addEntity(levelEvtEntity);



    }
    public void initializeWindows()
    {
        dungeonWindow = new Entity();
        Stage dungeonStage = buildStage(42,22,23,23,42,42,cellWidth*3,cellHeight*3, DefaultResources.getStretchableSquareFont(), SColor.BLACK.toFloatBits());
        dungeonWindow.add(new WindowCmp((MySparseLayers) dungeonStage.getActors().get(0),dungeonStage, true));
        engine.addEntity(dungeonWindow);

        startWindow = new Entity();
        Stage startWinStage = buildStage(42,21,69,30,69,30,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.WHITE.toFloatBits());
        startWindow.add(new WindowCmp((MySparseLayers) startWinStage.getActors().get(0),startWinStage, false));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, startWindow)).columnIndexes = new int[]{1,25,40};
        startWindow.add(new MenuCmp());
        engine.addEntity(startWindow);

        gameOverWindow = new Entity();
        Stage gameOverStage = buildStage(46,26,60,30,60,30,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.WHITE.toFloatBits());
        gameOverWindow.add(new WindowCmp((MySparseLayers) gameOverStage.getActors().get(0),gameOverStage, false));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, gameOverWindow)).columnIndexes = new int[]{1,25,40};
        //gameOverWindow.add(new MenuCmp());
        engine.addEntity(gameOverWindow);

        campWindow = new Entity();
        Stage campWinStage = buildStage(42,21,69,30,69,30,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());
        campWindow.add(new WindowCmp((MySparseLayers) campWinStage.getActors().get(0),campWinStage, false));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, campWindow)).columnIndexes = new int[]{1,25,49, 65};
        campWindow.add(new MenuCmp());
        engine.addEntity(campWindow);

        Stage fmStage = buildStage(1,1,8,9,10,9,cellWidth*2,cellHeight*2, DefaultResources.getStretchableSquareFont(), SColor.BLACK.toFloatBits());

        focusManaWindow = new Entity();
        WindowCmp windowCmp = new WindowCmp((MySparseLayers) fmStage.getActors().get(0), fmStage, true);
        windowCmp.display.font.tweakHeight(cellHeight*2*1.755f).tweakWidth(cellWidth*2*1.75f).initBySize();

        focusManaWindow.add(windowCmp);
        //engine.addEntity(focusManaWindow);

        Stage tmStage = buildStage(1,94,8,9,10,9,cellWidth*2,cellHeight*2, DefaultResources.getStretchableSquareFont(), SColor.BLACK.toFloatBits());

        targetManaWindow = new Entity();
        windowCmp = new WindowCmp((MySparseLayers) tmStage.getActors().get(0), tmStage, true);
        windowCmp.display.font.tweakHeight(cellHeight*2*1.755f).tweakWidth(cellWidth*2*1.75f).initBySize();
        targetManaWindow.add(windowCmp);


        Stage fsStage = buildStage(1,20,40,18,40,18,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());

        focusStatsWindow = new Entity();
        focusStatsWindow.add(new WindowCmp((MySparseLayers) fsStage.getActors().get(0), fsStage, true));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, focusStatsWindow)).columnIndexes = new int[]{1,20,40};

        Stage tsStage = buildStage(1,56,40,19,40,18,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());

        targetStatsWindow = new Entity();
        targetStatsWindow.add(new WindowCmp((MySparseLayers) tsStage.getActors().get(0), tsStage, true));

        Stage logStage = buildStage(112,52,66,20,66,20,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());

        logWindow = new Entity();
        logWindow.add(new WindowCmp((MySparseLayers) logStage.getActors().get(0), logStage, true));
        logWindow.add(new LogCmp());

        Stage faStage = buildStage(17,0,106,10,106,10, cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());

        focusHotBar = new Entity();
        focusHotBar.add(new WindowCmp((MySparseLayers) faStage.getActors().get(0), faStage, true));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, focusHotBar)).columnIndexes = new int[]{1,28, 55,82};
        focusHotBar.add(new MenuCmp());
        engine.addEntity(focusHotBar);

        inventoryWindow = new Entity();
        engine.addEntity(inventoryWindow);
        Stage invStage = buildStage(112,20,66,16,66,16, cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());
        inventoryWindow.add(new WindowCmp((MySparseLayers) invStage.getActors().get(0), invStage, true));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, inventoryWindow)).columnIndexes = new int[]{1,32, 66};
        inventoryWindow.add(new MenuCmp());

        Stage taStage = buildStage(22,92,106,10,106,10, cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());

        targetHotBar = new Entity();
        targetHotBar.add(new WindowCmp((MySparseLayers) taStage.getActors().get(0), taStage, true));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, targetHotBar)).columnIndexes = new int[]{1,28, 55,82};
        targetHotBar.add(new MenuCmp());
        engine.addEntity(targetHotBar);

        allWindows = Arrays.asList(gameOverWindow, startWindow, dungeonWindow, campWindow, focusHotBar, targetHotBar, focusManaWindow, inventoryWindow, targetManaWindow, focusStatsWindow, targetStatsWindow, logWindow);
        playingWindows = Arrays.asList(dungeonWindow, focusHotBar, targetHotBar, focusManaWindow, inventoryWindow, targetManaWindow, focusStatsWindow, targetStatsWindow, logWindow);
        campingWindows = Arrays.asList(campWindow, focusManaWindow, focusHotBar, inventoryWindow, focusStatsWindow, logWindow);
        startWindows = Arrays.asList(startWindow);
        gameOverWindows = Arrays.asList(gameOverWindow, dungeonWindow, focusHotBar, targetHotBar, focusManaWindow, inventoryWindow, targetManaWindow, focusStatsWindow, targetStatsWindow, logWindow );

        for(Entity windowEntity : allWindows)
        {
            ((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, windowEntity)).display.setVisible(false);
        }
    }


    @Override
    public void create ()
    {
        ticker = new Entity();
        currentLevel = new Entity();
        engine.addEntity(currentLevel);
        engine.addEntity(ticker);
        ticker.add(new TickerCmp());
        engine.addSystem(new WinSysGameOver());
        engine.addSystem(new TickerSys());
        engine.addSystem(new CodexSys());
        engine.addSystem(new EventCleanUpSys());
        engine.addSystem(new WinSysLog());
        engine.addSystem(new WinSysStats());
        engine.addSystem(new ActionSys());
        engine.addSystem(new ReactionSys());
        engine.addSystem(new AnimationsSys());
        engine.addSystem(new RestIdleCampSys());
        engine.addSystem(new WinSysHotBar());
        engine.addSystem(new WinSysStart());
        engine.addSystem(new WinSysInventory());
        engine.addSystem(new MovementSys());
        engine.addSystem(new FOVSys());
        engine.addSystem(new AISys());
        engine.addSystem(new DamageApplicationSys());
        engine.addSystem(new FocusTargetSys());
        engine.addSystem(new WinSysDungeon());
        engine.addSystem(new WinSysMana());
        engine.addSystem(new ItemSys());
        engine.addSystem(new StatusEffectEvtSys());
        engine.addSystem(new MenuUpdateSys());
        engine.addSystem(new GameStateSys());
        engine.addSystem(new WinSysCamp());
        engine.addSystem(new StatSys());
        engine.addSystem(new LightingSys());
        engine.addSystem(new DeathSys());
        engine.addSystem(new StatusEffectRemovalSys());
        engine.addSystem(new NoiseSys());
        engine.addSystem(new MakeCampSys());

        Family actors = Family.all(AICmp.class).get();
        engine.addEntityListener(actors, new ActorListener(this));
        Family items = Family.all(ItemCmp.class, PositionCmp.class).get();
        engine.addEntityListener(items, new ItemListener(this));

        Family burning = Family.one(Burning.class).get();
        engine.addEntityListener(burning, new BurningListener(this));

        Family calescent = Family.one(Calescent.class).get();
        engine.addEntityListener(calescent, new CalescentListener(this));

        Family chilled = Family.one(Chilled.class).get();
        engine.addEntityListener(chilled, new ChilledListener(this));

        Family frozen   = Family.one(Frozen.class).get();
        engine.addEntityListener(frozen, new FrozenListener(this));

        Family enraged = Family.one(Enraged.class).get();
        engine.addEntityListener(enraged, new EnragedListener(this));

        Family enlightened = Family.one(Enlightened.class).get();
        engine.addEntityListener(enlightened, new EnlightenedListener(this));

        Family swordEfct = Family.one(SwordEfct.class).get();
        engine.addEntityListener(swordEfct, new SwordEffectListener(this));

        Family daggerEfct = Family.one(DaggerEfct.class).get();
        engine.addEntityListener(daggerEfct, new DaggerListener(this));

        Family qstaffEfct = Family.one(QStaffEfct.class).get();
        engine.addEntityListener(qstaffEfct, new QStaffEffectListener(this));

        Family staffEfct = Family.one(StaffEfct.class).get();
        engine.addEntityListener(staffEfct, new StaffEffectListener(this));

        Family exhausted = Family.one(Exhausted.class).get();
        engine.addEntityListener(exhausted, new ExhaustedListener(this));

        Family staggered = Family.one(Staggered.class).get();
        engine.addEntityListener(staggered, new StaggeredListener(this));

        Family waterWalking = Family.one(WaterWalking.class).get();
        engine.addEntityListener(waterWalking, new WaterWalkingListener(this));

        Family hungry = Family.one(Hungry.class).get();
        engine.addEntityListener(hungry, new HungryListener(this));

        Family wellFed = Family.one(WellFed.class).get();
        engine.addEntityListener(wellFed, new WellFedListener(this));

        Family starving = Family.one(Starving.class).get();
        engine.addEntityListener(starving, new StarvingListener(this));

        Family leatherArmor = Family.one(LeatherArmorEfct.class).get();
        engine.addEntityListener(leatherArmor, new LeatherArmorListener(this));

        Family mailArmor = Family.one(MailArmorEfct.class).get();
        engine.addEntityListener(mailArmor, new MailArmorListener(this));

        Family plateArmor = Family.one(PlateArmorEfct.class).get();
        engine.addEntityListener(plateArmor, new PlateArmorListener(this));

        // gotta have a random number generator. We can seed an RNG with any long we want, or even a String.
        // if the seed is identical between two runs, any random factors will also be identical (until user input may
        // cause the usage of an RNG to change). You can randomize the dungeon and several other initial settings by
        // just removing the String seed, making the line "rng = new GWTRNG();" . Keeping the seed as a default allows
        // changes to be more easily reproducible, and using a fixed seed is strongly recommended for tests. 

        // SquidLib has many methods that expect an IRNG instance, and there's several classes to choose from.
        // In this program we'll use GWTRNG, which will behave better on the HTML target than other generators.



        // YCwCmFilter multiplies the brightness (Y), warmth (Cw), and mildness (Cm) of a color 
        // This filters colors in a way we adjust over time, producing a sort of hue shift effect.
        // It can also be used to over- or under-saturate colors, change their brightness, or any combination of these.
        FloatFilters.YCwCmFilter warmMildFilter = new FloatFilters.YCwCmFilter(0.875f, 0.6f, 0.6f);

        // FilterBatch is exactly like libGDX' SpriteBatch, except it is a fair bit faster when the Batch color is set
        // often (which is always true for SquidLib's text-based display), and it allows a FloatFilter to be optionally
        // set that can adjust colors in various ways. The FloatFilter here, a YCwCmFilter, can have its adjustments to
        // brightness (Y, also called luma), warmth (blue/green vs. red/yellow) and mildness (blue/red vs. green/yellow)
        // changed at runtime, and the putMap() method does this. This can be very powerful; you might increase the
        // warmth of all colors (additively) if the player is on fire, for instance.
        filterBatch = new FilterBatch();
        //StretchViewport mainViewport = new StretchViewport(gridWidth * cellWidth, gridHeight * cellHeight);
                //manaViewport = new StretchViewport(8 * cellWidth, 10 * cellHeight*2),
                //targetManaViewport = new StretchViewport(8 * cellWidth, 10 * cellHeight*2);
        //mainViewport.setScreenBounds(0, 0, gridWidth * cellWidth, gridHeight * cellHeight);
        //manaViewport.setScreenBounds(0, 0, 8 * cellWidth, 10 * cellHeight*2 );

        //Here we make sure our Stage, which holds any text-based grids we make, uses our Batch.

        // the font will try to load Iosevka Slab as an embedded bitmap font with a MSDF effect (multi scale distance
        // field, a way to allow a bitmap font to stretch while still keeping sharp corners and round curves).
        // the MSDF effect is handled internally by a shader in SquidLib, and will switch to a different shader if a SDF
        // effect is used (SDF is called "Stretchable" in DefaultResources, where MSDF is called "Crisp").
        // this font is covered under the SIL Open Font License (fully free), so there's no reason it can't be used.
        // it also includes 4 text faces (regular, bold, oblique, and bold oblique) so methods in GDXMarkup can make
        // italic or bold text without switching fonts (they can color sections of text
        //MySparseLayers dungeonMySparseLayers = new MySparseLayers(bigWidth, bigHeight, cellWidth*3, cellHeight*3,
                //DefaultResources.getStretchableSquareFont(), 0, 0);
        //MySparseLayers manaMySparseLayers = new MySparseLayers(8, 10, cellWidth, cellHeight*2,
                //DefaultResources.getStretchableCodeFont(), 0, 0);



        initializeWindows();
        SColor.LIMITED_PALETTE[3] = SColor.DB_GRAPHITE;


        startInput = new SquidInput((key, alt, ctrl, shift) -> {
            for(Character chr : engine.getSystem(MenuUpdateSys.class).keyLookup.keySet())
            {
                if(chr == key)
                {
                    engine.getSystem(MenuUpdateSys.class).keyLookup.get(chr).menuMap.get(chr).runPrimaryAction();
                    return;
                }
                if(getShiftChar(chr)==key)
                {
                    engine.getSystem(MenuUpdateSys.class).keyLookup.get(chr).menuMap.get(chr).runSecondaryAction();
                    return;
                }
            }
            switch (key)
            {
                case SquidInput.BACKSPACE:
                    playerName = StringKit.safeSubstring(playerName, 0, playerName.length()-1);
                    return;
            }



            playerName = playerName + key;


        },
                //The second parameter passed to a SquidInput can be a SquidMouse, which takes mouse or touchscreen
                //input and converts it to grid coordinates (here, a cell is 10 wide and 20 tall, so clicking at the
                // pixel position 16,51 will pass screenX as 1 (since if you divide 16 by 10 and round down you get 1),
                // and screenY as 2 (since 51 divided by 20 rounded down is 2)).
                new SquidMouse(cellWidth, cellHeight, gridWidth, gridHeight, 0, 0, new InputAdapter() {



                }));

        campInput = new SquidInput((key, alt, ctrl, shift) -> {


            for(Character chr : engine.getSystem(MenuUpdateSys.class).keyLookup.keySet())
            {

                if(chr == key)
                {


                    engine.getSystem(MenuUpdateSys.class).keyLookup.get(chr).menuMap.get(chr).runPrimaryAction();
                    return;
                }
                if(getShiftChar(chr)==key)
                {
                    engine.getSystem(MenuUpdateSys.class).keyLookup.get(chr).menuMap.get(chr).runSecondaryAction();
                    return;
                }
            }
            if(key=='c')
            {
                Entity gameStateEvtEnt = new Entity();
                engine.addEntity(gameStateEvtEnt);
                gameStateEvtEnt.add(new GameStateEvt(GameState.PLAYING));

                AISys aiSys = engine.getSystem(AISys.class);
                aiSys.scheduleRestEvt(getFocus());
            }

        },
                //The second parameter passed to a SquidInput can be a SquidMouse, which takes mouse or touchscreen
                //input and converts it to grid coordinates (here, a cell is 10 wide and 20 tall, so clicking at the
                // pixel position 16,51 will pass screenX as 1 (since if you divide 16 by 10 and round down you get 1),
                // and screenY as 2 (since 51 divided by 20 rounded down is 2)).
                new SquidMouse(cellWidth, cellHeight, gridWidth, gridHeight, 0, 0, new InputAdapter() {


                    // if the user clicks and there are no awaitedMoves queued up, generate toCursor if it
                    // hasn't been generated already by mouseMoved, then copy it over to awaitedMoves.
                   /* @Override
                    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                        // This is needed because we center the camera on the player as he moves through a dungeon that is three
                        // screens wide and three screens tall, but the mouse still only can receive input on one screen's worth
                        // of cells. (gridWidth >> 1) halves gridWidth, pretty much, and that we use to get the centered
                        // position after adding to the player's position (along with the gridHeight).
                        Coord focusLocation = getFocus().getComponent(PositionCmp.class).coord;
                        screenX += focusLocation.x - (gridWidth >> 1);
                        screenY += focusLocation.y - (gridHeight >> 1);
                        // we also need to check if screenX or screenY is out of bounds.
                        return screenX >= 0 && screenY >= 0 && screenX < bigWidth && screenY < bigHeight;
                *//*if (awaitedMoves.isEmpty()) {
                    if (toCursor.isEmpty()) {
                        cursor = Coord.get(screenX, screenY);
                        //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
                        // player position to the position the user clicked on. The "PreScanned" part is an optimization
                        // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
                        // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
                        // moves, we only need to do a fraction of the work to find the best path with that info.
                        toCursor.clear();
                        playerToCursor.findPathPreScanned(toCursor, cursor);
                        //findPathPreScanned includes the current cell (goal) by default, which is helpful when
                        // you're finding a path to a monster or loot, and want to bump into it, but here can be
                        // confusing because you would "move into yourself" as your first move without this.
                        if(!toCursor.isEmpty())
                            toCursor.remove(0);
                    }
                    //awaitedMoves.addAll(toCursor);
                }*//*
                    }

                    @Override
                    public boolean touchDragged(int screenX, int screenY, int pointer) {
                        return mouseMoved(screenX, screenY);
                    }

                    // causes the path to the mouse position to become highlighted (toCursor contains a list of Coords that
                    // receive highlighting). Uses DijkstraMap.findPathPreScanned() to find the path, which is rather fast.
                    @Override
                    public boolean mouseMoved(int screenX, int screenY) {
                *//*if(!awaitedMoves.isEmpty())
                    return false;*//*
                        // This is needed because we center the camera on the player as he moves through a dungeon that is three
                        // screens wide and three screens tall, but the mouse still only can receive input on one screen's worth
                        // of cells. (gridWidth >> 1) halves gridWidth, pretty much, and that we use to get the centered
                        // position after adding to the player's position (along with the gridHeight).
               *//* Coord focusLocation = getFocus().getComponent(PositionCmp.class).coord;
                screenX += focusLocation.x - (gridWidth >> 1);
                screenY += focusLocation.y - (gridHeight >> 1);
                // we also need to check if screenX or screenY is out of bounds.
                if(screenX < 0 || screenY < 0 || screenX >= bigWidth || screenY >= bigHeight ||
                        (cursor.x == screenX && cursor.y == screenY))
                {
                    return false;
                }
                cursor = Coord.get(screenX, screenY);
                //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
                // player position to the position the user clicked on. The "PreScanned" part is an optimization
                // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
                // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
                // moves, we only need to do a fraction of the work to find the best path with that info.

                toCursor.clear();
                playerToCursor.findPathPreScanned(toCursor, cursor);
                //findPathPreScanned includes the current cell (goal) by default, which is helpful when
                // you're finding a path to a monster or loot, and want to bump into it, but here can be
                // confusing because you would "move into yourself" as your first move without this.
                if(!toCursor.isEmpty())
                    toCursor.remove(0);*//*
                        return false;
                    }*/
                }));

        input = new SquidInput((key, alt, ctrl, shift) -> {
            Entity focus = getFocus();
            Coord focusPosition  = focus.getComponent(PositionCmp.class).coord;
            TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, ticker);
            if(!tickerCmp.getScheduledActions(getFocus()).isEmpty()) return;


            for(Character chr : engine.getSystem(MenuUpdateSys.class).keyLookup.keySet())
            {
                if(chr == key)
                {
                    engine.getSystem(MenuUpdateSys.class).keyLookup.get(chr).menuMap.get(chr).runPrimaryAction();
                    return;
                }
                if(getShiftChar(chr)==key)
                {
                    engine.getSystem(MenuUpdateSys.class).keyLookup.get(chr).menuMap.get(chr).runSecondaryAction();
                    return;
                }
            }

            int newX = focusPosition.x;
            int newY = focusPosition.y;

            switch (key)
            {
                case SquidInput.UP_ARROW:
                case 'k':
                case 'K':
                {

                    newX = focusPosition.x;
                    newY = focusPosition.y - 1;
                    break;
                }

                case SquidInput.UP_LEFT_ARROW:
                case 't':
                case 'T':
                {

                    newX = focusPosition.x - 1;
                    newY = focusPosition.y - 1;
                    break;
                }

                case SquidInput.UP_RIGHT_ARROW:
                case 'y':
                case 'Y':
                {

                    newX = focusPosition.x + 1;
                    newY = focusPosition.y - 1;
                    break;
                }

                case SquidInput.DOWN_LEFT_ARROW:
                case 'b':
                case 'B':
                {

                    newX = focusPosition.x - 1;
                    newY = focusPosition.y + 1;
                    break;
                }

                case SquidInput.DOWN_RIGHT_ARROW:
                case 'n':
                case 'N':
                {

                    newX = focusPosition.x + 1;
                    newY = focusPosition.y + 1;
                    break;
                }

                case SquidInput.DOWN_ARROW:
                case 'j':
                case 'J':
                {

                    newX = focusPosition.x;
                    newY = focusPosition.y + 1;

                    break;
                }
                case SquidInput.LEFT_ARROW:
                case 'h':
                case 'H':
                {

                    newX = focusPosition.x - 1;
                    newY = focusPosition.y;

                    break;
                }
                case SquidInput.RIGHT_ARROW:
                case 'l':
                case 'L':
                {

                    newX = focusPosition.x + 1;
                    newY = focusPosition.y;

                    break;
                }
                case SquidInput.CENTER_ARROW:
                case ' ':

                {
                    engine.getSystem(AISys.class).scheduleRestEvt(getFocus());
                    return;
                }

                case SquidInput.ESCAPE:
                {
                    Gdx.app.exit();
                    return;
                }
                case 'v':
                case 'V':
                    if(!getFocus().getComponent(AICmp.class).visibleEnemies.isEmpty())
                    {
                        Entity nearestEnemy = getEntity(getFocus().getComponent(AICmp.class).visibleEnemies.get(0));
                        switchFocus(nearestEnemy);

                    }
                    return;

                case 'c':
                case 'C':
                    engine.getSystem(AISys.class).scheduleCampEvt(getFocus());


                    break;

                case 'f':
                case 'F':

                    /*effectEntity = new Entity();
                    effectEntity.add(new StatusEffectEvt(StatusEffect.CALESCENT,  focus.hashCode(), true));
                    engine.addEntity(effectEntity);
                    break;*/

                case SquidInput.TAB:

                    AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, focus);
                    int veIndex = 0;
                    if(aiCmp.target!=null) veIndex = aiCmp.visibleEnemies.indexOf(aiCmp.target)+1;
                    if(veIndex>=aiCmp.visibleEnemies.size())veIndex=0;
                    if(!aiCmp.visibleEnemies.isEmpty())
                    {
                        aiCmp.target = aiCmp.visibleEnemies.get(veIndex);
                        Entity newFocusTarget = getEntity(aiCmp.target);
                        Entity currentFocusTarget = getFocusTarget();
                        if(currentFocusTarget==null) getEntity(aiCmp.target).add(new FocusTargetCmp());
                        else newFocusTarget.add(currentFocusTarget.remove(FocusTargetCmp.class));
                    }
                    break;

                case 'g':
                case 'G':
                    LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, currentLevel);
                    Coord focusLocation = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, focus)).coord;
                    for(Coord location:levelCmp.items.positions())
                    {
                        if(location.equals(focusLocation))
                        {
                           AISys aiSys =  engine.getSystem(AISys.class);
                           aiSys.scheduleItemEvt(getFocus(), levelCmp.items.get(location).hashCode(), ItemEvtType.PICKUP);
                        }
                    }
                    break;

                case 'd':
                case 'D':
                    levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, currentLevel);
                    focusLocation = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, focus)).coord;
                    if(!levelCmp.items.positions().contains(focusLocation))
                    {

                        Integer itemID = ((InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, focus)).getItemIDs().get(0);
                        ItemEvt itemEvt = new ItemEvt(itemID, focus.hashCode(), ItemEvtType.DROP);
                        focus.add(itemEvt);

                    }
                    break;

                case '>':
                    char[][] decoDungeon = ((LevelCmp)CmpMapper.getComp(CmpType.LEVEL, currentLevel)).decoDungeon;
                    focusLocation = ((PositionCmp)CmpMapper.getComp(CmpType.POSITION, getFocus())).coord;
                    if(decoDungeon[focusLocation.x][focusLocation.y]=='>')
                    {
                        depth++;
                        Entity levelEvtEntity = new Entity();
                        LevelEvt levelEvt = new LevelEvt();
                        levelEvtEntity.add(levelEvt);
                        engine.addEntity(levelEvtEntity);
                    }
                    break;
                case 'U':
                case 'u':
                    InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, focus);
                    Integer itemID = inventoryCmp.getSlotEquippedID( EquipmentSlot.RIGHT_HAND_WEAP);
                    if(itemID==null) break;
                    Entity itemEvtEntity = new Entity();
                    ItemEvt itemEvt = new ItemEvt(itemID, focus.hashCode(), ItemEvtType.UNEQUIP);
                    itemEvtEntity.add(itemEvt);
                    engine.addEntity(itemEvtEntity);
                    break;

                case 'p':
                case 'P':

                    CodexCmp codexCm = (CodexCmp)CmpMapper.getComp(CmpType.CODEX,getFocus());
                    for(Skill skill : codexCm.prepared)
                    {
                        IAbilityCmpSubSys abilityCmpSubSys = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(skill, getFocus());
                        if(abilityCmpSubSys!=null) getFocus().remove(abilityCmpSubSys.getClass());
                    }
                    getFocus().remove(StatsCmp.class);
                    getFocus().remove(CodexCmp.class);
                    getFocus().remove(ManaPoolCmp.class);
                    getFocus().add(getRandomStats(12));
                    getFocus().add(new ManaPoolCmp());
                    getFocus().add( new CodexCmp());
                    mobFactory.setRandomSkillSet(getFocus());


                    return;

                case '[':
                case '{':
                    //depth++;
                    Entity levelEvtEntity = new Entity();
                    LevelEvt levelEvt = new LevelEvt();
                    levelEvtEntity.add(levelEvt);
                    engine.addEntity(levelEvtEntity);
                    break;

                case 'z':
                case 'Z':
                    getFocus().add(new Hungry());
                    break;

                case 'x':
                case 'X':
                    Entity focusTarget = getFocusTarget();
                    if(focusTarget!=null)
                    {
                        FOVCmp fovCmp = (FOVCmp) CmpMapper.getComp(CmpType.FOV, focusTarget);
                        System.out.println();
                        System.out.println(fovCmp.visible);
                    }
                    break;

            }

            Coord newPosition = Coord.get(newX,newY);
            if(focusPosition.equals(newPosition)) return;
            AICmp aiCmp = (AICmp)CmpMapper.getComp(CmpType.AI,getFocus());
            Direction direction = Direction.toGoTo(focusPosition, newPosition);
            if (newX >= 0 && newY >= 0 && newX < bigWidth && newY < bigHeight
                    && aiCmp.dijkstraMap.costMap[newX][newY]!= DijkstraMap.WALL)
            {

                if(!currentLevel.getComponent(LevelCmp.class).isOccupied(newPosition))
                {
                    AISys aiSys = engine.getSystem(AISys.class);
                    double terrainCost = aiCmp.dijkstraMap.costMap[focusPosition.x][focusPosition.y];
                    aiSys.scheduleMoveEvt(getFocus(), direction, terrainCost);


                }else{
                    MeleeAttack meleeAttackAb = focus.getComponent(MeleeAttack.class);
                    if(meleeAttackAb.isAvailable())
                    {
                        AICmp focusAI = focus.getComponent(AICmp.class);
                        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, focus);

                        focusAI.target = currentLevel.getComponent(LevelCmp.class).actors.get(newPosition);
                        ActionEvt meleeAttack = new ActionEvt(getFocus().hashCode(), null, Skill.MELEE_ATTACK, focusAI.target, statsCmp.getWeaponDamage(), meleeAttackAb.getStatusEffects());
                        ScheduledEvt scheduledEvt = new ScheduledEvt(getGameTick()+statsCmp.getTTMelee(), focus.hashCode(), meleeAttack);

                        ticker.getComponent(TickerCmp.class).actionQueue.add(scheduledEvt);
                    }
                }
            }
            else
            {
                //dungeonWindow.getComponent(WindowCmp.class).display.bump(pg, direction, 0.15f);
            }
        },
                //The second parameter passed to a SquidInput can be a SquidMouse, which takes mouse or touchscreen
                //input and converts it to grid coordinates (here, a cell is 10 wide and 20 tall, so clicking at the
                // pixel position 16,51 will pass screenX as 1 (since if you divide 16 by 10 and round down you get 1),
                // and screenY as 2 (since 51 divided by 20 rounded down is 2)).
                new SquidMouse(cellWidth, cellHeight, gridWidth, gridHeight, 0, 0, new InputAdapter() {


            // if the user clicks and there are no awaitedMoves queued up, generate toCursor if it
            // hasn't been generated already by mouseMoved, then copy it over to awaitedMoves.
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                // This is needed because we center the camera on the player as he moves through a dungeon that is three
                // screens wide and three screens tall, but the mouse still only can receive input on one screen's worth
                // of cells. (gridWidth >> 1) halves gridWidth, pretty much, and that we use to get the centered
                // position after adding to the player's position (along with the gridHeight).
                Coord focusLocation = getFocus().getComponent(PositionCmp.class).coord;
                screenX += focusLocation.x - (gridWidth >> 1);
                screenY += focusLocation.y - (gridHeight >> 1);
                // we also need to check if screenX or screenY is out of bounds.
                return screenX >= 0 && screenY >= 0 && screenX < bigWidth && screenY < bigHeight;
                /*if (awaitedMoves.isEmpty()) {
                    if (toCursor.isEmpty()) {
                        cursor = Coord.get(screenX, screenY);
                        //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
                        // player position to the position the user clicked on. The "PreScanned" part is an optimization
                        // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
                        // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
                        // moves, we only need to do a fraction of the work to find the best path with that info.
                        toCursor.clear();
                        playerToCursor.findPathPreScanned(toCursor, cursor);
                        //findPathPreScanned includes the current cell (goal) by default, which is helpful when
                        // you're finding a path to a monster or loot, and want to bump into it, but here can be
                        // confusing because you would "move into yourself" as your first move without this.
                        if(!toCursor.isEmpty())
                            toCursor.remove(0);
                    }
                    //awaitedMoves.addAll(toCursor);
                }*/
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return mouseMoved(screenX, screenY);
            }

            // causes the path to the mouse position to become highlighted (toCursor contains a list of Coords that
            // receive highlighting). Uses DijkstraMap.findPathPreScanned() to find the path, which is rather fast.
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                /*if(!awaitedMoves.isEmpty())
                    return false;*/
                // This is needed because we center the camera on the player as he moves through a dungeon that is three
                // screens wide and three screens tall, but the mouse still only can receive input on one screen's worth
                // of cells. (gridWidth >> 1) halves gridWidth, pretty much, and that we use to get the centered
                // position after adding to the player's position (along with the gridHeight).
               /* Coord focusLocation = getFocus().getComponent(PositionCmp.class).coord;
                screenX += focusLocation.x - (gridWidth >> 1);
                screenY += focusLocation.y - (gridHeight >> 1);
                // we also need to check if screenX or screenY is out of bounds.
                if(screenX < 0 || screenY < 0 || screenX >= bigWidth || screenY >= bigHeight ||
                        (cursor.x == screenX && cursor.y == screenY))
                {
                    return false;
                }
                cursor = Coord.get(screenX, screenY);
                //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
                // player position to the position the user clicked on. The "PreScanned" part is an optimization
                // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
                // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
                // moves, we only need to do a fraction of the work to find the best path with that info.

                toCursor.clear();
                playerToCursor.findPathPreScanned(toCursor, cursor);
                //findPathPreScanned includes the current cell (goal) by default, which is helpful when
                // you're finding a path to a monster or loot, and want to bump into it, but here can be
                // confusing because you would "move into yourself" as your first move without this.
                if(!toCursor.isEmpty())
                    toCursor.remove(0);*/
                return false;
            }
        }));

        input.setRepeatGap(180);
        campInput.setRepeatGap(240);
        inputProcessor = new InputMultiplexer(dungeonWindow.getComponent(WindowCmp.class).stage, startInput, input, campInput);
        campInput.setIgnoreInput(true);
        Gdx.input.setInputProcessor(inputProcessor);


        lastFrameTime = System.currentTimeMillis();
        Entity eventEntity = new Entity();
        GameStateEvt gameStateEvt =  new GameStateEvt(GameState.STARTING);
        eventEntity.add(gameStateEvt);
        engine.addEntity(eventEntity);
    }
    @Override
    public void render ()
    {
       /* System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(engine.getEntities().size());
        for(Entity entity : engine.getEntities())
        {
            System.out.println("++++");
            System.out.println(entity);
            for(Component component : entity.getComponents()) System.out.println(component.getClass());
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");*/
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(bgColor.r / 255.0f, bgColor.g / 255.0f, bgColor.b / 255.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        lastFrameTime = System.currentTimeMillis();


        filterBatch.begin();
        filterBatch.end();
        Gdx.graphics.setTitle("EuroRogue Demo running at FPS: " + Gdx.graphics.getFramesPerSecond());

        engine.update(System.currentTimeMillis()-lastFrameTime);

    }
    @Override
    public void resize(int width, int height)
    { super.resize(width, height);
    }
    public Entity getEntity(Integer entityID)
    {

        Entity entity = null;
        if(entityID==null) return null;

        for(Entity  ent: engine.getEntities())
        {
            if(entityID.equals(ent.hashCode()))
            {
                entity=ent;
                break;
            }
        }
        return entity;
    }
    public Entity generateScroll(Coord loc, Skill skill)
    {
        Entity scroll = new Entity();
        scroll.add(new NameCmp(skill.name+" Scroll"));
        scroll.add(new PositionCmp(loc));
        scroll.add(new ItemCmp(ItemType.SCROLL));
        scroll.add(new CharCmp('%', skill.school.color));
        scroll.add(new ScrollCmp(skill));
        IAbilityCmpSubSys abilityCmpSubSys = IAbilityCmpSubSys.newAbilityCmp(skill);
        abilityCmpSubSys.setScrollID(scroll.hashCode());
        abilityCmpSubSys.setScroll(true);
        scroll.add(abilityCmpSubSys);
        ((IAbilityCmpSubSys) CmpMapper.getAbilityComp(skill, scroll)).setScroll(true);
        StatsCmp statsCmp = new StatsCmp();
        statsCmp.setStr(skill.strReq);
        statsCmp.setDex(skill.dexReq);
        statsCmp.setCon(skill.constReq);
        statsCmp.setIntel(skill.intReq);
        statsCmp.setPerc(skill.percReq);
        scroll.add(statsCmp);
        return scroll;
    }
    public Entity generateManaITem(Coord loc, School school)
    {
        Entity manaItem = new Entity();
        manaItem.add(new NameCmp(school.name+" Mana"));
        if(loc!=null)
            manaItem.add(new PositionCmp(loc));
        manaItem.add(new ItemCmp(ItemType.MANA));
        manaItem.add(new CharCmp('■', school.color));
        manaItem.add(new ManaCmp(school));
        return manaItem;

    }
    public ArrayList<Integer> getScrollIDs(Entity actor)
    {
        ArrayList<Integer> scrollIDs = new ArrayList<>();
        InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY,actor);
        for(Integer id:inventoryCmp.getScrollsIDs())
        {
            Entity itemEntity = getEntity(id);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, itemEntity);
            if(scrollCmp!=null) scrollIDs.add(id);
        }
        return scrollIDs;
    }
    public Entity getScrollForSkill(Skill skill, Entity actor)
    {

        for(Integer id:getScrollIDs(actor))
        {
            Entity scrollEntity = getEntity(id);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, scrollEntity);
            if(scrollCmp.skill==skill) return scrollEntity;
        }
        return null;
    }
    public ArrayList<IAbilityCmpSubSys> getAvailableAbilities(Entity actor)
    {
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, actor);
        ArrayList<IAbilityCmpSubSys> abilities = new ArrayList<>();
        for(Skill skill:codexCmp.prepared)
        {
            IAbilityCmpSubSys abilityCmp = (IAbilityCmpSubSys)CmpMapper.getAbilityComp(skill, actor);
            if(abilityCmp.isAvailable())abilities.add(abilityCmp);
        }
        for(Integer scrollID:getScrollIDs(actor))
        {
            Entity scrollEntity = getEntity(scrollID);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL,scrollEntity);
            IAbilityCmpSubSys abilityCmp = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(scrollCmp.skill,scrollEntity);
            if(abilityCmp.isAvailable())abilities.add(abilityCmp);

        }
        return abilities;
    }
    public ArrayList<Entity> getAvailableScrolls(Entity actor)
    {
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, actor);
        ArrayList<Entity> scrolls = new ArrayList<>();

        for(Integer scrollID:getScrollIDs(actor))
        {
            Entity scrollEntity = getEntity(scrollID);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL,scrollEntity);
            IAbilityCmpSubSys abilityCmp = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(scrollCmp.skill,scrollEntity);
            if(abilityCmp.isAvailable())scrolls.add(scrollEntity);

        }
        return scrolls;
    }
    public Integer getGameTick()
    { return ticker.getComponent(TickerCmp.class).tick; }
    public Entity getFocus()
    {
        return engine.getEntitiesFor(Family.all(FocusCmp.class).get()).get(0);
    }
    public Entity getFocusTarget()
    {
        ImmutableArray<Entity> possibleTargetInArray =engine.getEntitiesFor(Family.all(FocusTargetCmp.class).get());
        if(possibleTargetInArray.size()==0) return null;
        return possibleTargetInArray.get(0);
    }
    public void switchFocus(Entity entity)
    {
        getFocus().remove(FocusCmp.class);
        entity.add(new FocusCmp());
    }
    private Stage buildStage(int x, int y, int gridWidth, int gridHeight, int bigWidth, int bigHeight, int cellWidth, int cellHeight, TextCellFactory font, float bgColor)
    {
        Viewport stageViewport = new ScalingViewport(Scaling.none, gridWidth * cellWidth, gridHeight * cellHeight);
        Stage stage = new Stage(stageViewport, filterBatch);
        stage.getViewport().setScreenBounds(x*EuroRogue.cellWidth, y*EuroRogue.cellHeight, gridWidth * cellWidth, gridHeight * cellHeight);
        MySparseLayers stageDisplay = new MySparseLayers(bigWidth, bigHeight, cellWidth, cellHeight, font);
        stageDisplay.setPosition(0f, 0f);
        stage.addActor(stageDisplay);
        stageDisplay.defaultPackedBackground = bgColor;
        stageDisplay.font.tweakWidth(cellWidth * 1.1f).tweakHeight(cellHeight * 1.1f).initBySize();
        stage.getActors().get(0).setVisible(true);
        return stage;
    }
    public void updateAbilities(Entity entity)
    {
        if(entity==null) return;
        ArrayList<IAbilityCmpSubSys> codexAbilityCmps = new ArrayList<>();
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, entity);

        for(Skill skill : codexCmp.prepared)
        {
            IAbilityCmpSubSys ability = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(skill,entity);
            if(ability!=null) codexAbilityCmps.add(ability);

        }

        for (Integer itemID : getScrollIDs(entity)) {
            Coord actorPosition = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, entity)).coord;
            Entity itemEntity = getEntity(itemID);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, itemEntity);

            IAbilityCmpSubSys abilityCmp = (IAbilityCmpSubSys) CmpMapper.getAbilityComp(scrollCmp.skill, itemEntity);
            AICmp aiCmp = ((AICmp) CmpMapper.getComp(CmpType.AI, entity));

            AOE aoe = abilityCmp.getAOE();
            abilityCmp.updateScrollAOE(itemEntity, aoe, actorPosition);
            abilityCmp.setTargets(aoe.idealLocations(aiCmp.getTargetLocations(abilityCmp.getTargetType(), this), null));

            if(aiCmp.target!=null && abilityCmp.getSkill().skillType!=Skill.SkillType.REACTION && abilityCmp.getSkill().skillType!=Skill.SkillType.BUFF)
            {
                abilityCmp.setAvailable( !abilityCmp.getTargets().isEmpty() && abilityCmp.getActive() &&
                        abilityCmp.getTargets().containsKey(((PositionCmp)CmpMapper.getComp(CmpType.POSITION, getEntity(aiCmp.target))).coord));
            }


            else  if(abilityCmp.getSkill().skillType==Skill.SkillType.REACTION || abilityCmp.getSkill().skillType==Skill.SkillType.BUFF )
            {
                abilityCmp.setAvailable(!abilityCmp.getTargets().isEmpty() && abilityCmp.getActive());
            }

            else abilityCmp.setAvailable(false);

            abilityCmp.setDamage(itemEntity);
            abilityCmp.setTTPerform(itemEntity);

        }
        for (IAbilityCmpSubSys abilityCmp : codexAbilityCmps)
        {
            Skill skill = abilityCmp.getSkill();
            AICmp aiCmp = ((AICmp) CmpMapper.getComp(CmpType.AI, entity));
            ManaPoolCmp manaPool = (ManaPoolCmp) CmpMapper.getComp(CmpType.MANA_POOL, entity);
            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);
            Entity weaponEntity = getEntity(inventoryCmp.getSlotEquippedID(EquipmentSlot.RIGHT_HAND_WEAP));
            WeaponType weaponType = null;
            if(weaponEntity!=null && skill.weaponReq!=null)
            {
                WeaponCmp weaponCmp = (WeaponCmp) CmpMapper.getComp(CmpType.WEAPON, weaponEntity);
                weaponType = weaponCmp.weaponType;
            }
            AOE aoe = abilityCmp.getAOE();
            abilityCmp.updateAOE(entity, aoe);
            abilityCmp.setTargets(aoe.idealLocations(aiCmp.getTargetLocations(abilityCmp.getTargetType(), this), null));

            if(aiCmp.target!=null && skill.skillType!=Skill.SkillType.REACTION && skill.skillType!=Skill.SkillType.BUFF)
                abilityCmp.setAvailable(manaPool.canAfford(skill) &! abilityCmp.getTargets().isEmpty() && abilityCmp.getActive() &&
                    abilityCmp.getTargets().containsKey(((PositionCmp)CmpMapper.getComp(CmpType.POSITION, getEntity(aiCmp.target))).coord) && skill.weaponReq==weaponType);

            else  if(skill.skillType==Skill.SkillType.REACTION || skill.skillType==Skill.SkillType.BUFF)
                abilityCmp.setAvailable(manaPool.canAfford(skill) &! abilityCmp.getTargets().isEmpty() && abilityCmp.getActive() && skill.weaponReq==weaponType);

            else abilityCmp.setAvailable(false);
            if(skill == Skill.DAGGER_THROW && abilityCmp.isAvailable())
            {
                ((DaggerThrow)abilityCmp).itemID = weaponEntity.hashCode();
                ((DaggerThrow)abilityCmp).chr = weaponType.chr;
                ((DaggerThrow)abilityCmp).statusEffects = ((MeleeAttack) CmpMapper.getAbilityComp(Skill.MELEE_ATTACK, entity)).getStatusEffects();

            }

            abilityCmp.setDamage(entity);
            abilityCmp.setTTPerform(entity);
        }
    }

    public ArrayList<StatusEffect> getStatusEffects(Entity entity)
    {
        ArrayList<StatusEffect> statusEffects = new ArrayList<>();
        for(StatusEffect statusEffect: StatusEffect.values())
        {
            if(CmpMapper.getStatusEffectComp(statusEffect, entity)!=null) statusEffects.add(statusEffect);
        }
        return statusEffects;

    }
    private char getShiftChar(char chr)
    {
        switch (chr)
        {
            case '1': return '!';
            case '2': return '@';
            case '3': return '#';
            case '4': return '$';
            case '5': return '%';
            case '6': return '^';
            case '7': return '&';
            case '8': return '*';
            case '9': return '(';
            case '0': return ')';
            case '-': return '_';
            case '=': return '+';
            case 'q': return 'Q';
            case 'w': return 'W';
            case 'e': return 'E';
            case 'r': return 'R';
            case 't': return 'T';
            case 'y': return 'Y';
            case 'u': return 'U';
            case 'i': return 'I';
            case 'o': return 'O';
            case 'p': return 'P';
            case '[': return '{';
            case ']': return '}';
        }
        return ' ';
    }
    public void getInput()
    {

        switch (gameState)
        {
            case LOADING:
                break;
            case PLAYING:

                if(input.hasNext())
                {

                    this.input.next();
                }

                break;

            case CAMPING:

                if(campInput.hasNext())
                {

                    campInput.next();
                }
                break;

            case STARTING:

                if(startInput.hasNext())
                {

                    startInput.next();
                }
                break;

        }
    }
    public StatsCmp getRandomStats(int total)
    {

        HashMap<StatType, Integer> stats = new HashMap<>();
        stats.put(StatType.STR, 1);
        stats.put(StatType.DEX, 1);
        stats.put(StatType.CON, 1);
        stats.put(StatType.INTEL, 1);
        stats.put(StatType.PERC, 1);

        for (int i = 0; i < total-5; i++)
        {
            StatType stat = rng.getRandomElement(stats.keySet());
            stats.put(stat, stats.get(stat)+1);
        }

        StatsCmp statsCmp = new StatsCmp();
        statsCmp.setStr(stats.get(StatType.STR));
        statsCmp.setDex(stats.get(StatType.DEX));
        statsCmp.setCon(stats.get(StatType.CON));
        statsCmp.setPerc(stats.get(StatType.PERC));
        statsCmp.setIntel(stats.get(StatType.INTEL));
        statsCmp.hp=statsCmp.getMaxHP();

        return statsCmp;

    }
}
// An explanation of hexadecimal float/double literals was mentioned earlier, so here it is.
// The literal 0x1p-9f is a good example; it is essentially the same as writing 0.001953125f,
// (float)Math.pow(2.0, -9.0), or (1f / 512f), but is possibly faster than the last two if the
// compiler can't optimize float division effectively, and is a good tool to have because these
// hexadecimal float or double literals always represent numbers accurately. To contrast,
// 0.3 - 0.2 is not equal to 0.1 with doubles, because tenths are inaccurate with floats and
// doubles, and hex literals won't have the option to write an inaccurate float or double.
// There's some slightly confusing syntax used to write these literals; the 0x means the first
// part uses hex digits (0123456789ABCDEF), but the p is not a hex digit and is used to start
// the "p is for power" exponent section. In the example, I used -9 for the power; this is a
// base 10 number, and is used to mean a power of 2 that the hex digits will be multiplied by.
// Because the -9 is a base 10 number, the f at the end is not a hex digit, and actually just
// means the literal is a float, in the same way 1.5f is a float. 2.0 to the -9 is the same as
// 1.0 / Math.pow(2.0, 9.0), but re-calculating Math.pow() is considerably slower if you run it
// for every cell during every frame. Though this is most useful for negative exponents because
// there are a lot of numbers after the decimal point to write out with 0.001953125 or the like,
// it is also sometimes handy when you have an integer or long written in hexadecimal and want
// to make it a float or double. You could use the hex long 0x9E3779B9L, for instance, but to
// write that as a double you would use 0x9E3779B9p0 , not the invalid syntax 0x9E3779B9.0 .
// We use p0 there because 2 to the 0 is 1, so multiplying by 1 gets us the same hex number.
// Very large numbers can also benefit by using a large positive exponent; using p10 and p+10
// as the last part of a hex literal are equivalent. You can see the hex literal for any given
// float with Float.toHexString(float), or for a double with Double.toHexString(double) .
// SColor provides the packed float versions of all color constants as hex literals in the
// documentation for each SColor.
// More information here: https://blogs.oracle.com/darcy/hexadecimal-floating-point-literals