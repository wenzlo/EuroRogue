package EuroRogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.AbilityCmpSubSystems.MeleeAttack;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.AI.AIRatCmp;
import EuroRogue.Components.AI.AISnakeCmp;
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.ObjectCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.ShrineCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.UiBgLightingCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LevelEvt;
import EuroRogue.EventComponents.ShrineEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.EventComponents.StorageEvt;
import EuroRogue.EventComponents.StorageEvtType;
import EuroRogue.Listeners.ActorListener;
import EuroRogue.Listeners.ItemListener;
import EuroRogue.Listeners.ObjectListener;
import EuroRogue.StatusEffectCmps.Bleeding;
import EuroRogue.StatusEffectCmps.Burning;
import EuroRogue.StatusEffectCmps.Calescent;
import EuroRogue.StatusEffectCmps.Chilled;
import EuroRogue.StatusEffectCmps.DaggerEfct;
import EuroRogue.StatusEffectCmps.Enlightened;
import EuroRogue.StatusEffectCmps.Enraged;
import EuroRogue.StatusEffectCmps.Exhausted;
import EuroRogue.StatusEffectCmps.Frozen;
import EuroRogue.StatusEffectCmps.Hungry;
import EuroRogue.StatusEffectCmps.LeatherArmorEfct;
import EuroRogue.StatusEffectCmps.MailArmorEfct;
import EuroRogue.StatusEffectCmps.PlateArmorEfct;
import EuroRogue.StatusEffectCmps.QStaffEfct;
import EuroRogue.StatusEffectCmps.StaffEfct;
import EuroRogue.StatusEffectCmps.Staggered;
import EuroRogue.StatusEffectCmps.Stalking;
import EuroRogue.StatusEffectCmps.Starving;
import EuroRogue.StatusEffectCmps.StatusEffect;
import EuroRogue.StatusEffectCmps.SwordEfct;
import EuroRogue.StatusEffectCmps.WaterWalking;
import EuroRogue.StatusEffectCmps.WellFed;
import EuroRogue.StatusEffectListeners.BleedingListener;
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
import EuroRogue.StatusEffectListeners.StalkingListener;
import EuroRogue.StatusEffectListeners.StarvingListener;
import EuroRogue.StatusEffectListeners.SwordEffectListener;
import EuroRogue.StatusEffectListeners.WaterWalkingListener;
import EuroRogue.StatusEffectListeners.WellFedListener;
import EuroRogue.Systems.AI.AISys;
import EuroRogue.Systems.AI.AISysRat;
import EuroRogue.Systems.AI.AISysSnake;
import EuroRogue.Systems.ActionSys;
import EuroRogue.Systems.AimSys;
import EuroRogue.Systems.AnimationsSys;
import EuroRogue.Systems.CodexSys;
import EuroRogue.Systems.DamageApplicationSys;
import EuroRogue.Systems.DayNightCycleSys;
import EuroRogue.Systems.DeathSys;
import EuroRogue.Systems.DungeonLightingSys;
import EuroRogue.Systems.EventCleanUpSys;
import EuroRogue.Systems.FOVSys;
import EuroRogue.Systems.FocusTargetSys;
import EuroRogue.Systems.GameStateSys;
import EuroRogue.Systems.ItemSys;
import EuroRogue.Systems.LevelSys;
import EuroRogue.Systems.MakeCampSys;
import EuroRogue.Systems.MenuUpdateSys;
import EuroRogue.Systems.MovementSys;
import EuroRogue.Systems.NoiseSys;
import EuroRogue.Systems.ParticleSys;
import EuroRogue.Systems.ReactionSys;
import EuroRogue.Systems.RestIdleCampSys;
import EuroRogue.Systems.ShrineSys;
import EuroRogue.Systems.StatSys;
import EuroRogue.Systems.StatusEffectEvtSys;
import EuroRogue.Systems.StatusEffectRemovalSys;
import EuroRogue.Systems.StorageSys;
import EuroRogue.Systems.TickerSys;
import EuroRogue.Systems.Win.WinSysCamp;
import EuroRogue.Systems.Win.WinSysCampUiBg;
import EuroRogue.Systems.Win.WinSysDunOverlay;
import EuroRogue.Systems.Win.WinSysDungeon;
import EuroRogue.Systems.Win.WinSysGameOver;
import EuroRogue.Systems.Win.WinSysHotBar;
import EuroRogue.Systems.Win.WinSysInventory;
import EuroRogue.Systems.Win.WinSysLog;
import EuroRogue.Systems.Win.WinSysMana;
import EuroRogue.Systems.Win.WinSysSaveBuild;
import EuroRogue.Systems.Win.WinSysShrine;
import EuroRogue.Systems.Win.WinSysShrineUiBg;
import EuroRogue.Systems.Win.WinSysStart;
import EuroRogue.Systems.Win.WinSysStats;
import EuroRogue.Systems.Win.WinSysUiBg;
import squidpony.StringKit;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.FilterBatch;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidMouse;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidgrid.mapping.Placement;
import squidpony.squidgrid.mapping.SectionDungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;
//TODO Scroll Case; Alternate emeny # and ENemy lvl increase each depth
public class EuroRogue extends ApplicationAdapter {

    public MyEngine engine = new MyEngine(this);
    public MobFactory mobFactory;
    public WeaponFactory weaponFactory;
    public ArmorFactory armorFactory;
    public FoodFactory foodFactory;
    public ScrollFactory scrollFactory;
    public ObjectFactory objectFactory;
    public CmpMapper cmpMapper = new CmpMapper();
    public Integer globalMenuIndex = 0;
    public char[] globalMenuSelectionKeys = "1234567890-=qwertuiop[]asdfhjkl;'zx".toCharArray();
    public HashMap<Character, MenuCmp> keyLookup = new HashMap<>();
    public Entity saveBuildWindow, uiBackgrounds, worldMapWindow, shrineWindow, shrineWinBG, gameOverWindow, startWindow, player, dungeonWindow, dungeonOverlayWindow, focusHotBar, targetHotBar, focusManaWindow, inventoryWindow, targetManaWindow, focusStatsWindow, targetStatsWindow, campWindow, campWInBg, ticker, logWindow, currentLevel;
    public  List<Entity> playingWindows, campingWindows, allWindows, uiBgWindows, startWindows, gameOverWindows, shrineWindows, saveBuildWindows;
    public float lastFrameTime;
    public GameState gameState;
    public String playerName = "Eddard";
    public int depth = 1;

    // FilterBatch is almost the same as SpriteBatch, but is a bit faster with SquidLib and allows color filtering
    public FilterBatch filterBatch;
    // a type of random number generator, see below
    public GWTRNG rng, characterGenRng;

    public SectionDungeonGenerator dungeonGen;
    public Placement placement;
    /** In number of cells */
    private static final int gridWidth = 180;
    /** In number of cells */
    private static final int gridHeight = 114;
    /** In number of cells */
    public static final int bigWidth = gridWidth ;
    /** In number of cells */
    public static final int bigHeight = gridHeight ;
    public static final int cellWidth = 9;
    /** The pixel height of a cell */
    public static final int cellHeight = 9;
    public SquidInput input, aimInput, campInput, startInput, shrineInput, saveBuildInput;
    public InputMultiplexer inputProcessor;
    private final Color bgColor=Color.BLACK;
    public Storage buildStorage;


    public static final String outlineFragmentShader = "#ifdef GL_ES\n"
            + "precision mediump float;\n"
            + "precision mediump int;\n"
            + "#endif\n"
            + "\n"
            + "uniform sampler2D u_texture;\n"
            + "uniform float u_smoothing;\n"
            + "varying vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "\n"
            + "void main() {\n"
            + "  if(u_smoothing <= 0.0) {\n"
            + "    float smoothing = -u_smoothing;\n"
            + "	   vec4 box = vec4(v_texCoords-0.000125, v_texCoords+0.000125);\n"
            + "    vec2 sample0 = texture2D(u_texture, v_texCoords).ra;\n"
            + "    vec2 sample1 = texture2D(u_texture, box.xy).ra;\n"
            + "    vec2 sample2 = texture2D(u_texture, box.zw).ra;\n"
            + "    vec2 sample3 = texture2D(u_texture, box.xw).ra;\n"
            + "    vec2 sample4 = texture2D(u_texture, box.zy).ra;\n"
            + "	   float asum = smoothstep(0.5 - smoothing, 0.5 + smoothing, min(sample0.x, sample0.y)) + 0.5 * (\n"
            + "                 smoothstep(0.5 - smoothing, 0.5 + smoothing, min(sample1.x, sample1.y)) +\n"
            + "                 smoothstep(0.5 - smoothing, 0.5 + smoothing, min(sample2.x, sample2.y)) +\n"
            + "                 smoothstep(0.5 - smoothing, 0.5 + smoothing, min(sample3.x, sample3.y)) +\n"
            + "                 smoothstep(0.5 - smoothing, 0.5 + smoothing, min(sample4.x, sample4.y)));\n"
            + "    gl_FragColor = vec4(v_color.rgb, (asum / 3.0) * v_color.a);\n"
            + "	 } else {\n"
            + "    vec2 radistance = texture2D(u_texture, v_texCoords).ra;\n"
            + "    float distance = min(radistance.x, radistance.y);\n"
            + "	   vec2 box = vec2(0.0, 0.00375 * (u_smoothing + 0.0825));\n"
            + "	   float asum = 0.7 * (smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, distance) + \n"
            + "                   smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, texture2D(u_texture, v_texCoords + box.xy).a) +\n"
            + "                   smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, texture2D(u_texture, v_texCoords - box.xy).a) +\n"
            + "                   smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, texture2D(u_texture, v_texCoords + box.yx).a) +\n"
            + "                   smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, texture2D(u_texture, v_texCoords - box.yx).a)),\n"
            /*+ "	   vec4 box = vec4(v_texCoords-0.000625, v_texCoords+0.000625);\n"
            + "    vec2 sample0 = texture2D(u_texture, v_texCoords).ra;\n"
            + "    vec2 sample1 = texture2D(u_texture, box.xy).ra;\n"
            + "    vec2 sample2 = texture2D(u_texture, box.zw).ra;\n"
            + "    vec2 sample3 = texture2D(u_texture, box.xw).ra;\n"
            + "    vec2 sample4 = texture2D(u_texture, box.zy).ra;\n"
            + "	   float asum = smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, min(sample0.x, sample0.y)) + 0.5 * (\n"
            + "                 smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, min(sample1.x, sample1.y)) +\n"
            + "                 smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, min(sample2.x, sample2.y)) +\n"
            + "                 smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, min(sample3.x, sample3.y)) +\n"
            + "                 smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, min(sample4.x, sample4.y))),\n"*/
//            + "                 char = step(distance, 0.35);\n"
            + "                 fancy = step(0.480, distance);\n"//line thickness original 0.55
//            + "                 outline = clamp((distance * 0.8 - 0.415) * 18.0, 0.0, 1.0);\n"
            + "	   gl_FragColor = vec4(v_color.rgb * fancy, (asum + step(0.3, distance)) * v_color.a);\n"
            + "  }\n"
            + "}\n";


    public void newGame()
    {

        rng = new GWTRNG(playerName);
        weaponFactory = new WeaponFactory(rng.nextInt());
        armorFactory = new ArmorFactory(rng.nextInt());
        foodFactory = new FoodFactory();
        objectFactory = new ObjectFactory(new GWTRNG(rng.nextInt()));
        mobFactory = new MobFactory(this, rng.nextInt(), weaponFactory, armorFactory);
        scrollFactory = new ScrollFactory();

        engine.addSystem(new LevelSys(rng.nextInt(), mobFactory, weaponFactory, armorFactory, objectFactory, scrollFactory));
        dungeonGen = new SectionDungeonGenerator(42, 42, new GWTRNG(rng.nextInt()));

        Entity eventEntity = new Entity();

        LevelEvt levelEvt = new LevelEvt(LevelType.START);

        if( depth>1)
        {
            List<LevelType> levelTypes = new ArrayList<>();
            Collections.addAll(levelTypes, LevelType.values());

            levelTypes.remove(LevelType.START);
            //TODO move rng level tye selection to level sys
            levelEvt = new LevelEvt(rng.getRandomElement(levelTypes));
        }


        eventEntity.add(levelEvt);

        engine.addEntity(eventEntity);
    }
    private void initializeWindows()
    {
        ShaderProgram outlineShader = new ShaderProgram(DefaultResources.vertexShader, outlineFragmentShader);
        shrineWindow = new Entity();
        Stage shrineStage = buildStage(21, 43,28,18,27,17 ,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.WHITE.toFloatBits());
        shrineWindow.add(new WindowCmp((MySparseLayers) shrineStage.getActors().get(0),shrineStage, false));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, shrineWindow)).columnIndexes = new int[]{3,25};
        WindowCmp windowCmp = new WindowCmp((MySparseLayers)shrineStage.getActors().get(0),shrineStage, true);
        windowCmp.display.font.bottomPadding(1).initBySize();

        shrineWindow.add(new MenuCmp());
        engine.addEntity(shrineWindow);

        uiBackgrounds = new Entity();

        TextCellFactory font = DefaultResources.getStretchableSquareFont();
        Stage uiBgStage = buildStage(0,0,gridWidth/4,gridHeight/4+1,gridWidth/4,gridHeight/4+1, cellWidth*4, cellHeight*4, font, SColor.BLACK.toFloatBits());
        windowCmp = new WindowCmp((MySparseLayers)uiBgStage.getActors().get(0),uiBgStage, true);
        windowCmp.display.font.tweakWidth(cellWidth*3+9 * 0.8f).tweakHeight(cellHeight*4 * 1.15f).initBySize();

        windowCmp.display.font.shader = outlineShader;
        uiBackgrounds.add(windowCmp);
        uiBackgrounds.add(new UiBgLightingCmp(windowCmp.display.gridWidth, windowCmp.display.gridHeight));
        engine.addEntity(uiBackgrounds);

        dungeonWindow = new Entity();

        font = DefaultResources.getStretchableSquareFont();
        Stage dungeonStage = buildStage(4,4,20,20,42,42,cellWidth*4,cellHeight*4, font, SColor.BLACK.toFloatBits());
        windowCmp = new WindowCmp((MySparseLayers)dungeonStage.getActors().get(0),dungeonStage, true);
        windowCmp.display.font.tweakWidth(cellWidth*3+9 * 0.8f).tweakHeight(cellHeight*3+9 * 1.15f).initBySize();

        windowCmp.display.font.shader = outlineShader;
        dungeonWindow.add(windowCmp);
        engine.addEntity(dungeonWindow);

        dungeonOverlayWindow = new Entity();

        font = DefaultResources.getStretchableCodeFont();
        Stage dungeonOlStage = buildStage(4,4,80,40,80,40,cellWidth,cellHeight*2, font, SColor.BLACK.toFloatBits());
        windowCmp = new WindowCmp((MySparseLayers)dungeonOlStage.getActors().get(0),dungeonOlStage, true);
        windowCmp.display.font.tweakWidth(cellWidth*1.25f ).tweakHeight(cellHeight*2.5f).initBySize();
        dungeonOverlayWindow.add(new ParticleEffectsCmp());
        windowCmp.display.addLayer();
        dungeonOverlayWindow.add(new MenuCmp());

        windowCmp.display.font.shader = outlineShader;
        dungeonOverlayWindow.add(windowCmp);
        engine.addEntity(dungeonOverlayWindow);

        shrineWinBG = new Entity();

        font = DefaultResources.getStretchableSquareFont();
        int width = ((MySparseLayers) shrineStage.getActors().get(0)).gridWidth/4;
        int height = ((MySparseLayers) shrineStage.getActors().get(0)).gridHeight/2;
        Stage shrineBgStage = buildStage(20, 44, width, height, width, height ,cellWidth*4,cellHeight*4, font, SColor.WHITE.toFloatBits());
        windowCmp = new WindowCmp((MySparseLayers)shrineBgStage.getActors().get(0),shrineBgStage, true);
        windowCmp.display.font.tweakWidth(cellWidth*3+9 * 0.8f).tweakHeight(cellHeight*4 * 1.15f).initBySize();

        windowCmp.display.font.shader = outlineShader;
        shrineWinBG.add(windowCmp);
        shrineWinBG.add(new UiBgLightingCmp(windowCmp.display.gridWidth, windowCmp.display.gridHeight));
        engine.addEntity(shrineWinBG);

        saveBuildWindow = new Entity();

        Stage saveBuildWinStage = buildStage(50,29,56,30,56,30,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.WHITE.toFloatBits());
        saveBuildWindow.add(new WindowCmp((MySparseLayers)saveBuildWinStage.getActors().get(0),saveBuildWinStage, false));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, saveBuildWindow)).columnIndexes = new int[]{1,25,40};
        saveBuildWindow.add(new MenuCmp());
        engine.addEntity(saveBuildWindow);

        startWindow = new Entity();

        Stage startWinStage = buildStage(17,17,56,30,56,30,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.WHITE.toFloatBits());
        startWindow.add(new WindowCmp((MySparseLayers)startWinStage.getActors().get(0),startWinStage, false));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, startWindow)).columnIndexes = new int[]{2,35,40};
        startWindow.add(new MenuCmp());
        engine.addEntity(startWindow);

        gameOverWindow = new Entity();

        Stage gameOverStage = buildStage(17,17,60,30,60,30,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.WHITE.toFloatBits());
        gameOverWindow.add(new WindowCmp((MySparseLayers)gameOverStage.getActors().get(0),gameOverStage, false));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, gameOverWindow)).columnIndexes = new int[]{1,25,40};
        engine.addEntity(gameOverWindow);

        campWindow = new Entity();

        Stage campWinStage = buildStage(17,17,59,30,59,30,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());
        campWindow.add(new WindowCmp( (MySparseLayers)campWinStage.getActors().get(0),campWinStage, false));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, campWindow)).columnIndexes = new int[]{2,25,40, 65};
        campWindow.add(new MenuCmp());
        engine.addEntity(campWindow);

        campWInBg = new Entity();

        font = DefaultResources.getStretchableSquareFont();
        width = ((MySparseLayers) campWinStage.getActors().get(0)).gridWidth/4+2;
        height = ((MySparseLayers) campWinStage.getActors().get(0)).gridHeight/2+1;
        Stage campBgStage = buildStage(4, 4, width, height, width, height ,cellWidth*4,cellHeight*4, font, SColor.WHITE.toFloatBits());
        windowCmp = new WindowCmp((MySparseLayers)campBgStage.getActors().get(0), campBgStage, true);
        windowCmp.display.font.tweakWidth(cellWidth*3+9 * 0.8f).tweakHeight(cellHeight*4 * 1.15f).initBySize();

        windowCmp.display.font.shader = outlineShader;
        campWInBg.add(windowCmp);
        campWInBg.add(new UiBgLightingCmp(windowCmp.display.gridWidth, windowCmp.display.gridHeight));
        engine.addEntity(campWInBg);

        Stage fmStage = buildStage(3,2,10,10,10,10,cellWidth+3,cellHeight+3, DefaultResources.getStretchableSquareFont(), SColor.BLACK.toFloatBits());

        focusManaWindow = new Entity();

        windowCmp = new WindowCmp( (MySparseLayers)fmStage.getActors().get(0), fmStage, true);
        windowCmp.display.font.tweakHeight(cellHeight*2.70f).tweakWidth(cellWidth*2.65f).initBySize();
        windowCmp.display.font.shader = outlineShader;

        focusManaWindow.add(windowCmp);

        Stage tmStage = buildStage(3,71,10,10,10,10,cellWidth+3,cellHeight+3, DefaultResources.getStretchableSquareFont(), SColor.BLACK.toFloatBits());

        targetManaWindow = new Entity();

        windowCmp = new WindowCmp( (MySparseLayers)tmStage.getActors().get(0), tmStage, true);
        windowCmp.display.font.tweakHeight(cellHeight*2.70f).tweakWidth(cellWidth*2.65f).initBySize();
        targetManaWindow.add(windowCmp);
        windowCmp.display.font.shader = outlineShader;


        /*Stage fsStage = buildStage(2,22,40,18,40,18,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());
        focusStatsWindow = new Entity();

        focusStatsWindow.add(new WindowCmp( (MySparseLayers)fsStage.getActors().get(0), fsStage, true));
        windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, focusStatsWindow);
        windowCmp.columnIndexes = new int[]{1,20,40};

        Stage tsStage = buildStage(2,57,40,19,40,18,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());

        targetStatsWindow = new Entity();

        targetStatsWindow.add(new WindowCmp( (MySparseLayers)tsStage.getActors().get(0), tsStage, true));*/

        Stage logStage = buildStage(86,33,66,26,66,26,cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());

        logWindow = new Entity();

        logWindow.add(new WindowCmp((MySparseLayers)logStage.getActors().get(0), logStage, true));
        logWindow.add(new LogCmp());

       /* Stage faStage = buildStage(70,1,108,10,108,10, cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());

        focusHotBar = new Entity();

        focusHotBar.add(new WindowCmp((MySparseLayers)faStage.getActors().get(0), faStage, true));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, focusHotBar)).columnIndexes = new int[]{1,28, 55,82};
        focusHotBar.add(new MenuCmp());
        engine.addEntity(focusHotBar);
*/
        inventoryWindow = new Entity();

        engine.addEntity(inventoryWindow);
        Stage invStage = buildStage(86,1,66,16,66,16, cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());
        inventoryWindow.add(new WindowCmp((MySparseLayers)invStage.getActors().get(0), invStage, true));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, inventoryWindow)).columnIndexes = new int[]{1,32, 66};
        inventoryWindow.add(new MenuCmp());


       /* targetHotBar = new Entity();
        Stage taStage = buildStage(22,93,110,10,110,10, cellWidth,cellHeight*2, DefaultResources.getStretchableCodeFont(), SColor.BLACK.toFloatBits());

        targetHotBar.add(new WindowCmp((MySparseLayers)taStage.getActors().get(0), taStage, true));
        ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, targetHotBar)).columnIndexes = new int[]{1,28, 55,82};
        targetHotBar.add(new MenuCmp());
        engine.addEntity(targetHotBar);*/

        allWindows = Arrays.asList(dungeonOverlayWindow, saveBuildWindow, shrineWinBG, shrineWindow, gameOverWindow, startWindow, dungeonWindow, campWindow, campWInBg, /*focusHotBar, targetHotBar,*/ focusManaWindow, inventoryWindow, targetManaWindow, /*focusStatsWindow, targetStatsWindow,*/ logWindow);
        uiBgWindows = Arrays.asList(saveBuildWindow, dungeonOverlayWindow,/*startWindow,*/ dungeonWindow, /*focusHotBar, targetHotBar,*/ /*focusManaWindow,*/ inventoryWindow, /*targetManaWindow,*/ /*focusStatsWindow, targetStatsWindow,*/ logWindow);

        playingWindows = Arrays.asList(dungeonOverlayWindow, dungeonWindow, focusHotBar, targetHotBar, focusManaWindow, inventoryWindow, targetManaWindow, /*focusStatsWindow, targetStatsWindow,*/ logWindow);
        shrineWindows = Arrays.asList(shrineWinBG, shrineWindow, dungeonWindow, focusHotBar, targetHotBar, focusManaWindow, inventoryWindow, targetManaWindow, /*focusStatsWindow, targetStatsWindow,*/ logWindow);
        campingWindows = Arrays.asList(dungeonOverlayWindow, /*campWInBg,*/ campWindow, /*dungeonWindow,*/ /*focusHotBar, targetHotBar,*/ focusManaWindow, inventoryWindow, targetManaWindow, focusStatsWindow, targetStatsWindow, logWindow);
        startWindows = Arrays.asList(focusManaWindow, /*inventoryWindow,*/ dungeonOverlayWindow,/*focusHotBar, focusStatsWindow,*/ startWindow);
        gameOverWindows = Arrays.asList(gameOverWindow, dungeonWindow, /*focusHotBar, targetHotBar,*/ focusManaWindow, inventoryWindow, targetManaWindow, /*focusStatsWindow, targetStatsWindow,*/ logWindow );
        saveBuildWindows = Arrays.asList(focusManaWindow, inventoryWindow, /*focusHotBar, focusStatsWindow,*/ saveBuildWindow);


        for(Entity windowEntity : allWindows)
        {
            ((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, windowEntity)).display.setVisible(false);
        }
    }
    private void initializeListeners()
    {
        Family bleedingEvt = Family.one(StatusEffectEvt.class, Bleeding.class).get();
        engine.addEntityListener(bleedingEvt, new BleedingListener(this));

        Family actors = Family.one(AICmp.class, AIRatCmp.class, AISnakeCmp.class).get();
        engine.addEntityListener(actors, new ActorListener(this));

        Family items = Family.all(ItemCmp.class, PositionCmp.class).get();
        engine.addEntityListener(items, new ItemListener(this));

        Family objects = Family.all(ObjectCmp.class).get();
        engine.addEntityListener(objects, new ObjectListener(this));

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

        Family stalking = Family.one(Stalking.class).get();
        engine.addEntityListener(stalking, new StalkingListener(this));

        Family swordEfct = Family.one(SwordEfct.class).get();
        engine.addEntityListener(swordEfct, new SwordEffectListener(this));

        Family daggerEfct = Family.one(DaggerEfct.class).get();
        engine.addEntityListener(daggerEfct, new DaggerListener(this));

        Family qStaffEfct = Family.one(QStaffEfct.class).get();
        engine.addEntityListener(qStaffEfct, new QStaffEffectListener(this));

        Family staggered = Family.one(Staggered.class).get();
        engine.addEntityListener(staggered, new StaggeredListener(this));

        Family staffEfct = Family.one(StaffEfct.class).get();
        engine.addEntityListener(staffEfct, new StaffEffectListener(this));

        Family exhausted = Family.one(Exhausted.class).get();
        engine.addEntityListener(exhausted, new ExhaustedListener(this));

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
    }
    private void initializeSystems()
    {
        engine.addSystem(new StorageSys());
        engine.addSystem(new DayNightCycleSys());
        engine.addSystem(new AimSys());
        //engine.addSystem(new DetectedSys());
        engine.addSystem(new FOVSys());
        engine.addSystem(new DungeonLightingSys());
        engine.addSystem(new ParticleSys());
        engine.addSystem(new WinSysDungeon());
        engine.addSystem(new WinSysDunOverlay());
        engine.addSystem(new WinSysUiBg());
        engine.addSystem(new WinSysShrineUiBg());
        engine.addSystem(new WinSysCampUiBg());
        engine.addSystem(new WinSysShrine(((WindowCmp)(CmpMapper.getComp(CmpType.WINDOW, shrineWindow))).display));
        engine.addSystem(new WinSysGameOver());
        engine.addSystem(new ShrineSys());
        engine.addSystem(new TickerSys());
        engine.addSystem(new StatusEffectRemovalSys());
        engine.addSystem(new CodexSys());
        engine.addSystem(new EventCleanUpSys());
        engine.addSystem(new WinSysLog());
        //engine.addSystem(new WinSysStats());
        engine.addSystem(new ActionSys());
        engine.addSystem(new ReactionSys());
        engine.addSystem(new AnimationsSys());
        engine.addSystem(new RestIdleCampSys());
        //engine.addSystem(new WinSysHotBar());
        engine.addSystem(new WinSysSaveBuild());
        engine.addSystem(new WinSysStart());
        engine.addSystem(new WinSysInventory());
        engine.addSystem(new MovementSys());

        engine.addSystem(new AISys());
        engine.addSystem(new AISysRat());
        engine.addSystem(new AISysSnake());
        engine.addSystem(new DamageApplicationSys());
        engine.addSystem(new FocusTargetSys());

        engine.addSystem(new WinSysMana());
        engine.addSystem(new ItemSys());
        engine.addSystem(new StatusEffectEvtSys());
        engine.addSystem(new MenuUpdateSys());
        engine.addSystem(new GameStateSys());
        engine.addSystem(new WinSysCamp());
        engine.addSystem(new StatSys());

        engine.addSystem(new DeathSys());
        engine.addSystem(new NoiseSys());
        engine.addSystem(new MakeCampSys());

    }
    @Override
    public void create ()
    {
        Gdx.graphics.setVSync(false);
        Gdx.graphics.setForegroundFPS(0);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Crosshair);

       /* System.out.println(Gdx.app.getPreferences("PLayer1"));
        Preferences prefs = Gdx.app.getPreferences( "Player1" );

        // Clears the values
        prefs.clear();

        // Saves the change
        prefs.flush();
        System.out.println(Gdx.app.getPreferences("PLayer1").get());*/


        buildStorage = new Storage("EuroRogue");
        System.out.println("at creation "+buildStorage.get("BuildKeys", "Keys", buildStorage.buildKeys.getClass()));
        ticker = new Entity();
        ticker.add(new TickerCmp());
        currentLevel = new Entity();
        engine.addEntity(currentLevel);
        engine.addEntity(ticker);
        rng = new GWTRNG(playerName);
        weaponFactory = new WeaponFactory(rng.nextInt());
        armorFactory = new ArmorFactory(rng.nextInt());
        foodFactory = new FoodFactory();
        objectFactory = new ObjectFactory(new GWTRNG(rng.nextInt()));
        mobFactory = new MobFactory(this, rng.nextInt(), weaponFactory, armorFactory);

        filterBatch = new FilterBatch();
        initializeWindows();
        initializeSystems();
        initializeListeners();
        SColor.LIMITED_PALETTE[0] = SColor.DB_LEAD;
        SColor.LIMITED_PALETTE[3] = SColor.DB_GRAPHITE;
        SColor.LIMITED_PALETTE[23] = SColor.CW_DARK_AZURE;
        SColor.LIMITED_PALETTE[24] = SColor.CW_LIGHT_AZURE;

        saveBuildInput = new SquidInput((key, alt, ctrl, shift) ->
        {

            WinSysSaveBuild system = engine.getSystem(WinSysSaveBuild.class);
            if(keyLookup.containsKey(key) || keyLookup.containsKey(getUnshiftedChar(key)))
            {

                if(shift) keyLookup.get(getUnshiftedChar(key)).menuMap.get(getUnshiftedChar(key)).runSecondaryAction();
                else keyLookup.get(key).menuMap.get(key).runPrimaryAction();
                return;
            }
            switch (key)
            {
                case SquidInput.BACKSPACE:
                    system.buildName = StringKit.safeSubstring(system.buildName, 0, system.buildName.length()-1);
                    ((NameCmp) CmpMapper.getComp(CmpType.NAME, player)).name = playerName;
                    return;

                case '>':
                    return;

            }

            system.buildName = system.buildName + key;

        },
                //The second parameter passed to a SquidInput can be a SquidMouse, which takes mouse or touchscreen
                //input and converts it to grid coordinates (here, a cell is 10 wide and 20 tall, so clicking at the
                // pixel position 16,51 will pass screenX as 1 (since if you divide 16 by 10 and round down you get 1),
                // and screenY as 2 (since 51 divided by 20 rounded down is 2)).
                new SquidMouse(cellWidth, cellHeight, gridWidth, gridHeight, 0, 0, new InputAdapter() {



                }));

        startInput = new SquidInput((key, alt, ctrl, shift) ->
        {


            if(keyLookup.containsKey(key) || keyLookup.containsKey(getUnshiftedChar(key)))
            {

                if(shift) keyLookup.get(getUnshiftedChar(key)).menuMap.get(getUnshiftedChar(key)).runSecondaryAction();
                else keyLookup.get(key).menuMap.get(key).runPrimaryAction();
                return;
            }
            switch (key)
            {
                case SquidInput.BACKSPACE:
                    playerName = StringKit.safeSubstring(playerName, 0, playerName.length()-1);
                    ((NameCmp) CmpMapper.getComp(CmpType.NAME, player)).name = playerName;
                    return;

                case '4':
                    Entity eventEntity = new Entity();

                    eventEntity.add( new StorageEvt("TestBuildSave", StorageEvtType.SAVE_BUILD) );
                    engine.addEntity(eventEntity);
                    return;


                case '5':
                    eventEntity = new Entity();

                    eventEntity.add( new StorageEvt("TestBuildSave", StorageEvtType.LOAD_BUILD) );
                    engine.addEntity(eventEntity);
                    return;
            }

            playerName = playerName + key;
            ((NameCmp) CmpMapper.getComp(CmpType.NAME, player)).name = playerName;

        },
                //The second parameter passed to a SquidInput can be a SquidMouse, which takes mouse or touchscreen
                //input and converts it to grid coordinates (here, a cell is 10 wide and 20 tall, so clicking at the
                // pixel position 16,51 will pass screenX as 1 (since if you divide 16 by 10 and round down you get 1),
                // and screenY as 2 (since 51 divided by 20 rounded down is 2)).
                new SquidMouse(cellWidth, cellHeight, gridWidth, gridHeight, 0, 0, new InputAdapter() {



                }));

        shrineInput = new SquidInput((key, alt, ctrl, shift) -> {

            if(keyLookup.containsKey(key) || keyLookup.containsKey(getUnshiftedChar(key)))
            {
                if(shift) keyLookup.get(getUnshiftedChar(key)).menuMap.get(getUnshiftedChar(key)).runSecondaryAction();
                else keyLookup.get(key).menuMap.get(key).runPrimaryAction();
                return;
            }

            if(key=='c'|| key == SquidInput.ESCAPE)
            {
                shrineInput.setIgnoreInput(true);
                Entity gameStateEvtEnt = new Entity();
                engine.addEntity(gameStateEvtEnt);
                gameStateEvtEnt.add(new GameStateEvt(GameState.PLAYING));

            }
        },

                new SquidMouse(cellWidth, cellHeight, gridWidth, gridHeight, 0, 0, new InputAdapter() {}));
        campInput = new SquidInput((key, alt, ctrl, shift) -> {

            if(keyLookup.containsKey(key) || keyLookup.containsKey(getUnshiftedChar(key)))
            {
                if(shift) keyLookup.get(getUnshiftedChar(key)).menuMap.get(getUnshiftedChar(key)).runSecondaryAction();
                else keyLookup.get(key).menuMap.get(key).runPrimaryAction();
                return;
            }
            /*for(Character chr : keyLookup.keySet())
            {

                if(chr == key)
                {


                    keyLookup.get(chr).menuMap.get(chr).runPrimaryAction();
                    return;
                }
                if(getShiftChar(chr)==key)
                {
                    keyLookup.get(chr).menuMap.get(chr).runSecondaryAction();
                    return;
                }
            }*/
            if(key=='c'|| key == SquidInput.ESCAPE)
            {
                campInput.setIgnoreInput(true);
                Entity gameStateEvtEnt = new Entity();
                engine.addEntity(gameStateEvtEnt);
                gameStateEvtEnt.add(new GameStateEvt(GameState.PLAYING));

                AISys aiSys = engine.getSystem(AISys.class);
                aiSys.scheduleRestEvt(getFocus());

                CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, getFocus());
                LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, currentLevel);
                for(Skill skill : codexCmp.prepared)
                {
                    Ability ability = CmpMapper.getAbilityComp(skill, getFocus());
                    ability.setMap(levelCmp.decoDungeon);
                }
            }
        },

                new SquidMouse(cellWidth, cellHeight, gridWidth, gridHeight, 0, 0, new InputAdapter() {}));

        aimInput = new SquidInput((key, alt, ctrl, shift) -> {

            AimingCmp aimingCmp = (AimingCmp) CmpMapper.getComp(CmpType.AIMING, getFocus());
            LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, currentLevel);
            Ability aimAbility = CmpMapper.getAbilityComp(aimingCmp.skill, getFocus());
            if(aimingCmp.scroll)
                aimAbility = CmpMapper.getAbilityComp(aimingCmp.skill, getScrollForSkill(aimingCmp.skill, getFocus()));

            if(aimingCmp==null) return;

            if(keyLookup.containsKey(key) || keyLookup.containsKey(getUnshiftedChar(key)))
            {
                if(shift) keyLookup.get(getUnshiftedChar(key)).menuMap.get(getUnshiftedChar(key)).runSecondaryAction();
                else keyLookup.get(key).menuMap.get(key).runPrimaryAction();
                return;
            }

            Direction direction = Direction.NONE;

            switch(key) {
                case SquidInput.ESCAPE:
                    getFocus().remove(AimingCmp.class);
                    Entity gameStateEvtEnt = new Entity();
                    engine.addEntity(gameStateEvtEnt);
                    gameStateEvtEnt.add(new GameStateEvt(GameState.PLAYING));
                    break;

                case SquidInput.UP_ARROW:
                    direction = Direction.UP;
                    break;
                case SquidInput.UP_RIGHT_ARROW:
                    direction = Direction.UP_RIGHT;
                    break;
                case SquidInput.RIGHT_ARROW:
                    direction = Direction.RIGHT;
                    break;
                case SquidInput.DOWN_RIGHT_ARROW:
                    direction = Direction.DOWN_RIGHT;
                    break;

                case SquidInput.DOWN_ARROW:
                    direction = Direction.DOWN;
                    break;

                case SquidInput.DOWN_LEFT_ARROW:
                    direction = Direction.DOWN_LEFT;
                    break;
                case SquidInput.LEFT_ARROW:
                    direction = Direction.LEFT;
                    break;
                case SquidInput.UP_LEFT_ARROW:
                    direction = Direction.UP_LEFT;
                    break;

                case SquidInput.CENTER_ARROW:
                    if(aimAbility.isAvailable())
                        engine.getSystem(AISys.class).scheduleActionEvt(getFocus(), aimAbility);
                        Entity eventEntity = new Entity();
                        GameStateEvt gameStateEvt = new GameStateEvt(GameState.PLAYING);
                        eventEntity.add(gameStateEvt);
                        engine.addEntity(eventEntity);

                        return;

            }

            PositionCmp positionCmp = (PositionCmp) CmpMapper.getComp(CmpType.POSITION, getFocus());
            Coord newAimCoord = aimAbility.getTargetedLocation().translate(direction);
            if(aimAbility.possibleTargets(positionCmp.coord, levelCmp.resistance).contains(newAimCoord)
                    && levelCmp.floors.contains(newAimCoord))
            {
                aimAbility.apply(positionCmp.coord, newAimCoord);
            }
        },
                new SquidMouse(cellWidth, cellHeight, gridWidth, gridHeight, 0, 0, new InputAdapter() {}));

        input = new SquidInput((key, alt, ctrl, shift) ->
        {
            Entity focus = getFocus();
            Coord focusPosition  = focus.getComponent(PositionCmp.class).coord;
            TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, ticker);
            LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, currentLevel);
            CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, focus);
            if(!tickerCmp.getScheduledActions(getFocus()).isEmpty()) return;
            if(keyLookup.containsKey(key) || keyLookup.containsKey(getUnshiftedChar(key)))
            {

                if(shift) keyLookup.get(getUnshiftedChar(key)).menuMap.get(getUnshiftedChar(key)).runSecondaryAction();
                else
                    keyLookup.get(key).menuMap.get(key).runPrimaryAction();
                return;
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
                case SquidInput.PAGE_UP:
                {
                    engine.getSystem(WinSysLog.class).scrollIndex++;
                    engine.getSystem(WinSysLog.class).scrollIndex++;
                    break;
                }
                case SquidInput.PAGE_DOWN:
                {
                    engine.getSystem(WinSysLog.class).scrollIndex--;
                    engine.getSystem(WinSysLog.class).scrollIndex--;

                    break;
                }
                case SquidInput.END:
                {
                    engine.getSystem(WinSysLog.class).scrollIndex = 0;
                    break;
                }
                case SquidInput.HOME:
                {
                    LogCmp logCmp = (LogCmp)CmpMapper.getComp(CmpType.LOG, logWindow);
                    engine.getSystem(WinSysLog.class).scrollIndex = -(logCmp.logEntries.size()-16);
                    break;
                }
                case 'S':
                {
                    StatsCmp focusStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, focus);
                    focusStats.postToLog(this);
                    break;
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
                    StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, focus);
                    AICmp aiCmp = (AICmp) CmpMapper.getAIComp(statsCmp.mobType.aiType, focus);
                    int veIndex = 0;
                    if(shift)
                    {
                        if(aiCmp.target!=null)
                            if(aiCmp.visibleNeutrals.contains(aiCmp.target))veIndex = aiCmp.visibleNeutrals.indexOf(aiCmp.target)+1;
                        if(veIndex>=aiCmp.visibleNeutrals.size()) veIndex=0;
                        if(!aiCmp.visibleNeutrals.isEmpty())
                        {
                            aiCmp.target = aiCmp.visibleNeutrals.get(veIndex);
                            Entity newFocusTarget = getEntity(aiCmp.target);
                            Entity currentFocusTarget = getFocusTarget();
                            if(currentFocusTarget==null) getEntity(aiCmp.target).add(new FocusTargetCmp());
                            else newFocusTarget.add(currentFocusTarget.remove(FocusTargetCmp.class));
                        }

                    } else {

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
                    }


                    break;

                case 'g':
                case 'G':
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


                case '>':
                    char[][] decoDungeon = ((LevelCmp)CmpMapper.getComp(CmpType.LEVEL, currentLevel)).decoDungeon;
                    focusLocation = ((PositionCmp)CmpMapper.getComp(CmpType.POSITION, getFocus())).coord;
                    if(decoDungeon[focusLocation.x][focusLocation.y]=='>')
                    {
                        if(depth==1)
                        {
                            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, focus);
                            for(Integer id : inventoryCmp.getEquippedIDs())
                            {
                                Entity eventEntity = new Entity();
                                ItemEvt itemEvt = new ItemEvt(id, focus.hashCode(), ItemEvtType.UNEQUIP);
                                eventEntity.add(itemEvt);
                                engine.addEntity(eventEntity);
                            }
                            GameStateEvt gameStateEvt = new GameStateEvt(GameState.SAVE_BUILD);
                            player.add(gameStateEvt);
                            return;
                        }
                        depth++;
                        InventoryCmp inventoryCmp = ( InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, focus);
                        Entity evtEntity = new Entity();
                        List<LevelType> levelTypes = new ArrayList<>();
                        Collections.addAll(levelTypes, LevelType.values());

                        levelTypes.remove(LevelType.START);
                        //TODO move rng level tye selection to level sys
                        LevelEvt levelEvt = new LevelEvt(rng.getRandomElement(levelTypes));
                        CampEvt campEvt = new CampEvt(focus.hashCode(), inventoryCmp.getEquippedIDs());
                        evtEntity.add(levelEvt);
                        focus.add(campEvt);
                        engine.addEntity(evtEntity);
                    }
                    break;

                case 'p':
                case 'P':

                    CodexCmp codexCm = (CodexCmp)CmpMapper.getComp(CmpType.CODEX,getFocus());
                    for(Skill skill : codexCm.prepared)
                    {
                        Ability ability = CmpMapper.getAbilityComp(skill, getFocus());
                        if(ability!=null) getFocus().remove(ability.getClass());
                    }
                    getFocus().remove(StatsCmp.class);
                    getFocus().remove(CodexCmp.class);
                    getFocus().remove(ManaPoolCmp.class);
                    getFocus().add(mobFactory.getRandomStats(9+depth*2, MobType.PLAYER));
                    statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, getFocus());
                    getFocus().add(new ManaPoolCmp(statsCmp.getNumAttunedSlots()));
                    getFocus().add( new CodexCmp());
                    mobFactory.setRandomSkillSet(getFocus(), MobType.PLAYER);

                    return;

                case '[':
                case '{':
                    depth++;
                    Entity levelEvtEntity = new Entity();
                    List<LevelType> levelTypes = new ArrayList<>();
                    levelTypes.addAll(Arrays.asList(LevelType.values()));

                    levelTypes.remove(LevelType.START);

                    LevelEvt levelEvt = new LevelEvt(rng.getRandomElement(levelTypes));
                    levelEvtEntity.add(levelEvt);
                    engine.addEntity(levelEvtEntity);
                    break;

                case 'z':
                case 'Z':

                    levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, currentLevel);
                    engine.getSystem(DayNightCycleSys.class).dayNightCycleForward();
                    break;

                case 'x':
                case 'X':

                    levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, currentLevel);
                    engine.getSystem(DayNightCycleSys.class).dayNightCycleBackward();
                    break;

                case 'm':
                    /*FOVCmp fovCmp = (FOVCmp)CmpMapper.getComp(CmpType.FOV, getFocus());
                    statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, getFocus());
                    lightingCmp = (LightingCmp)CmpMapper.getComp(CmpType.LIGHTING, currentLevel);
                    GreasedRegion gr = new GreasedRegion(fovCmp.visible);
                    System.out.println(statsCmp.getLightDetectionLvl());
                    System.out.println(lightingCmp.minAmbientLight);
                    System.out.println(gr);*/


                    StatsCmp targetStats = (StatsCmp)CmpMapper.getComp(CmpType.STATS,getFocusTarget());
                    aiCmp = CmpMapper.getAIComp(targetStats.mobType.aiType, getFocusTarget());
                    FOVCmp fovCmp = (FOVCmp)CmpMapper.getComp(CmpType.FOV, getFocusTarget());

                    break;

            }

            Coord newPosition = Coord.get(newX,newY);

            if(focusPosition.equals(newPosition)) return;
            StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, focus);
            AICmp aiCmp = (AICmp) CmpMapper.getAIComp(statsCmp.mobType.aiType, focus);
            Direction direction = Direction.toGoTo(focusPosition, newPosition);
            if(levelCmp.decoDungeon[newX][newY]=='??')
            {

                Integer shrineID = levelCmp.objects.get( Coord.get(newX, newY));
                Entity shrineEntity = getEntity(shrineID);
                ShrineCmp shrineCmp  = (ShrineCmp)CmpMapper.getComp(CmpType.SHRINE, shrineEntity);
                shrineEntity.add(new ShrineEvt(focus.hashCode(), shrineID, shrineCmp.school));
                return;
            }
            if (newX >= 0 && newY >= 0 && newX < bigWidth && newY < bigHeight
                    && aiCmp.dijkstraMap.costMap[newX][newY]!= DijkstraMap.WALL)
            {

                if(!currentLevel.getComponent(LevelCmp.class).isBlocked(newPosition))
                {

                    AISys aiSys = engine.getSystem(AISys.class);
                    double terrainCost = aiCmp.movementCosts[focusPosition.x][focusPosition.y];
                    aiSys.scheduleMoveEvt(getFocus(), direction, terrainCost);


                }else if(focus.getComponent(MeleeAttack.class).available){
                    MeleeAttack meleeAttackAb = focus.getComponent(MeleeAttack.class);
                    if(meleeAttackAb.isAvailable())
                    {
                        engine.getSystem(AISys.class).scheduleActionEvt(getFocus(), meleeAttackAb);
                    }
                } else {

                    AISys aiSys = engine.getSystem(AISys.class);
                    aiSys.rotate(focus, newPosition);
                }
            } else {
                AISys aiSys = engine.getSystem(AISys.class);
                aiSys.rotate(focus, newPosition);
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

        input.setRepeatGap(160);
        campInput.setRepeatGap(240);
        shrineInput.setRepeatGap(240);
        saveBuildInput.setRepeatGap(240);
        inputProcessor = new InputMultiplexer(dungeonWindow.getComponent(WindowCmp.class).stage, input, shrineInput, startInput, saveBuildInput, campInput, aimInput);
        campInput.setIgnoreInput(true);
        Gdx.input.setInputProcessor(inputProcessor);

        /*currentLevel = new Entity();
        engine.addEntity(currentLevel);*/

        generatePlayer();
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


        /*filterBatch.begin();
        filterBatch.end();*/
        //Long mem = Gdx.app.getJavaHeap() + Gdx.app.getNativeHeap();

        ;
        //Gdx.graphics.setTitle("EuroRogue Demo running at FPS: " + Gdx.graphics.getFramesPerSecond()+" - Total Entities: "+engine.getEntities().size()+" - Memory usage: JavaHeap = "+
                //Gdx.app.getJavaHeap()+" nativeHeap "+Gdx.app.getNativeHeap());

        engine.update(System.currentTimeMillis()-lastFrameTime);
        //engine.getSystem(WinSysUiBg.class).update(System.currentTimeMillis()-lastFrameTime);


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
    public Entity generateScroll(Coord loc, Skill skill, LevelCmp levelCmp)
    {
        Entity scroll = new Entity();
        scroll.add(new NameCmp(skill.name+" Scroll"));
        scroll.add(new PositionCmp(loc));
        scroll.add(new ItemCmp(ItemType.SCROLL));
        scroll.add(new CharCmp('%', skill.school.color));
        scroll.add(new ScrollCmp(skill));
        Ability ability = Ability.newAbilityCmp(skill, false);
        ability.setScrollID(scroll.hashCode());
        ability.setScroll(true);
        ability.setMap(levelCmp.decoDungeon);
        scroll.add(ability);

        return scroll;
    }
    public Entity generateManaITem(Coord loc, School school)
    {
        Entity manaItem = new Entity();
        manaItem.add(new NameCmp(school.name+" Mana"));
        if(loc!=null)
            manaItem.add(new PositionCmp(loc));
        manaItem.add(new ItemCmp(ItemType.MANA));
        manaItem.add(new CharCmp('???', school.color));
        manaItem.add(new ManaCmp(school));
        manaItem.add(new LightCmp(1, school.color.toFloatBits(), 0.4f, 0f));
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
    public Ability getScrollAbilityCmp(Skill skill, Entity actor)
    {
        for(Integer id:getScrollIDs(actor))
        {
            Entity scrollEntity = getEntity(id);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, scrollEntity);
            if(scrollCmp.skill==skill) return CmpMapper.getAbilityComp(skill, scrollEntity);
        }
        return null;
    }
    public ArrayList<Ability> getAvailableAbilities(Entity actor)
    {
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, actor);
        ArrayList<Ability> abilities = new ArrayList<>();
        for(Skill skill:codexCmp.prepared)
        {
            Ability abilityCmp = CmpMapper.getAbilityComp(skill, actor);
            if(abilityCmp.isAvailable())abilities.add(abilityCmp);
        }
        for(Integer scrollID:getScrollIDs(actor))
        {
            Entity scrollEntity = getEntity(scrollID);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL,scrollEntity);
            Ability abilityCmp = CmpMapper.getAbilityComp(scrollCmp.skill,scrollEntity);
            if(abilityCmp.isAvailable())abilities.add(abilityCmp);

        }
        return abilities;
    }
    public ArrayList<Entity> getAvailableScrolls(Entity actor)
    {
        ArrayList<Entity> scrolls = new ArrayList<>();

        for(Integer scrollID:getScrollIDs(actor))
        {
            Entity scrollEntity = getEntity(scrollID);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL,scrollEntity);
            Ability abilityCmp = CmpMapper.getAbilityComp(scrollCmp.skill,scrollEntity);
            if(abilityCmp.isAvailable())scrolls.add(scrollEntity);
        }
        return scrolls;
    }
    public Integer getGameTick()
    { return ticker.getComponent(TickerCmp.class).tick; }
    public Entity getFocus()
    {
        ImmutableArray<Entity> focusArray = engine.getEntitiesFor(Family.all(FocusCmp.class).get());

        if(focusArray.size()==0) return player;
        else return focusArray.get(0);
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
        stageDisplay.font.tweakWidth(cellWidth * 1.1f).tweakHeight(cellHeight * 1.15f).initBySize();
        stageDisplay.setPosition(0f, 0f);
        stage.addActor(stageDisplay);
        stageDisplay.defaultPackedBackground = bgColor;
        stage.getActors().get(0).setVisible(true);
        return stage;
    }
    public void generatePlayer()
    {
        if(player!=null)
        {
            InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, player);
            for(Integer itemID : inventoryCmp.getAllItemIDs())
            {
                Entity itemEntity = getEntity(itemID);
                engine.removeEntity(itemEntity);
            }
            engine.removeEntity(player);
        }
        player = mobFactory.generateRndPlayer();
        engine.addEntity(player);



        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, player);
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, player);
        if(statsCmp.getPerc() <4) {
            Entity torch = weaponFactory.newTorch();
            engine.addEntity(torch);
            InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, player);
            inventoryCmp.put(torch.hashCode());
        }
        if(codexCmp.prepared.contains(Skill.DAGGER_THROW)) {
            Entity dagger = weaponFactory.newBasicWeapon(WeaponType.DAGGER);
            weaponFactory.addOnHitSE(dagger, StatusEffect.CHILLED, TargetType.ENEMY);
            engine.addEntity(dagger);
            InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, player);
            inventoryCmp.put(dagger.hashCode());
        }
        else  {
            Entity rndWeapon = weaponFactory.newRndWeapon();
            engine.addEntity(rndWeapon);
            InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, player);
            inventoryCmp.put(rndWeapon.hashCode());
        }

        if(statsCmp.getDex()>statsCmp.getStr()){
            Entity armor = armorFactory.newBasicArmor(ArmorType.LEATHER, null);
            engine.addEntity(armor);
            InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, player);
            inventoryCmp.put(armor.hashCode());
        }
        else if(statsCmp.getStr() > 6) {
            Entity armor = armorFactory.newBasicArmor(ArmorType.PLATE, null);
            engine.addEntity(armor);
            InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, player);
            inventoryCmp.put(armor.hashCode());
        }
        else if(statsCmp.getStr()+statsCmp.getDex() > 6) {
            Entity armor = armorFactory.newBasicArmor(ArmorType.MAIL, null);
            engine.addEntity(armor);
            InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, player);
            inventoryCmp.put(armor.hashCode());
        }
        else {
            Entity armor = armorFactory.newBasicArmor(ArmorType.LEATHER, null);
            engine.addEntity(armor);
            InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, player);
            inventoryCmp.put(armor.hashCode());
        }



    }
    public void updateAbilities(Entity entity)
    {
        if(entity==null || gameState == GameState.AIMING) return;
        ArrayList<Ability> codexAbilityCmps = new ArrayList<>();
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, entity);

        for(Skill skill : codexCmp.prepared)
        {
            Ability ability = CmpMapper.getAbilityComp(skill,entity);
            if(ability!=null) codexAbilityCmps.add(ability);

        }

        for (Integer itemID : getScrollIDs(entity)) {
            Entity itemEntity = getEntity(itemID);
            ScrollCmp scrollCmp = (ScrollCmp) CmpMapper.getComp(CmpType.SCROLL, itemEntity);

            Ability abilityCmp = CmpMapper.getAbilityComp(scrollCmp.skill, itemEntity);

            updateAbility(abilityCmp, entity);

        }
        for (Ability abilityCmp : codexAbilityCmps)
        {
            updateAbility(abilityCmp, entity);
        }
    }
    public void updateAbility(Ability abilityCmp, Entity entity)
    {
        if(abilityCmp==null || entity ==null) return;
        abilityCmp.updateAOE(entity);

        TickerCmp tickerCmp = (TickerCmp) CmpMapper.getComp(CmpType.TICKER, ticker);
        if(!tickerCmp.getScheduledActions(entity).isEmpty()) return;
        abilityCmp.setAvailable(entity, this);

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
    private char getUnshiftedChar(char chr)
    {
        switch (chr)
        {
            case '!': return '1';
            case '@': return '2';
            case '#': return '3';
            case '$': return '4';
            case '%': return '5';
            case '^': return '6';
            case '&': return '7';
            case '*': return '8';
            case '(': return '9';
            case ')': return '0';
            case '_': return '-';
            case '+': return '=';
            case 'Q': return 'q';
            case 'W': return 'w';
            case 'E': return 'e';
            case 'R': return 'r';
            case 'T': return 't';
            case 'Y': return 'y';
            case 'U': return 'u';
            case 'I': return 'i';
            case 'O': return 'o';
            case 'P': return 'p';
            case '{': return '[';
            case '}': return ']';
        }
        return ' ';
    }
    public void getInput()
    {

        switch (gameState)
        {
            case GAME_OVER:
            case SAVE_BUILD:

                if(saveBuildInput.hasNext()) saveBuildInput.next();
                break;


            case PLAYING:

                if(input.hasNext()) this.input.next();
                break;

            case CAMPING:

                if(campInput.hasNext()) campInput.next();
                break;

            case AIMING:

                if(aimInput.hasNext()) aimInput.next();
                break;

            case STARTING:

                if(startInput.hasNext()) startInput.next();
                break;

            case SHRINE:

                if(shrineInput.hasNext()) shrineInput.next();
                break;


        }
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