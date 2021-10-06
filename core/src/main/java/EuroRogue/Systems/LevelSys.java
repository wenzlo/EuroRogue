package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;


import java.util.ArrayList;
import java.util.Arrays;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.ObjectType;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.ArmorFactory;
import EuroRogue.CmpMapper;

import EuroRogue.CmpType;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.CodexEvt;

import EuroRogue.EventComponents.GameStateEvt;

import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LevelEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.EventComponents.RestEvt;
import EuroRogue.EventComponents.StatEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.GameState;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.MobFactory;
import EuroRogue.MyDungeonUtility;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.ObjectFactory;
import EuroRogue.School;
import EuroRogue.TerrainType;
import EuroRogue.WeaponFactory;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidgrid.mapping.MixedGenerator;
import squidpony.squidgrid.mapping.Placement;
import squidpony.squidgrid.mapping.SerpentMapGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.OrderedSet;


public class LevelSys extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;
    private GWTRNG rng;
    private MobFactory mobFactory;
    private WeaponFactory weaponFactory;
    private ArmorFactory armorFactory;
    private ObjectFactory objectFactory;

    public LevelSys(int seed, MobFactory mobFactory, WeaponFactory weaponFactory, ArmorFactory armorFactory, ObjectFactory objectFactory)
    {
        this.rng = new GWTRNG(seed);
        this.mobFactory = mobFactory;
        this.weaponFactory = weaponFactory;
        this.armorFactory = armorFactory;
        this.objectFactory = objectFactory;
    }


    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine)
    {
        entities = engine.getEntitiesFor(Family.all(LevelEvt.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        ImmutableArray<Entity> eventsFamily = getEngine().getEntitiesFor(Family.one(StatEvt.class, GameStateEvt.class, ActionEvt.class, CodexEvt.class, MoveEvt.class, ItemEvt.class,
                RestEvt.class, StatusEffectEvt.class, AnimateGlyphEvt.class).get());
        if(eventsFamily.size()>0) return;
        for(Entity entity : entities)
        {

            LevelEvt levelEvt = (LevelEvt) CmpMapper.getComp(CmpType.LEVEL_EVT, entity);
            levelEvt.processed=true;

            newLevel();
        }
    }

    public void newLevel()
    {

        Entity player = getGame().player;
        player.remove(GlyphsCmp.class);
        MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).display;
        display.glyphs.clear();
        TickerCmp newTickerCmp = new TickerCmp();
        getGame().ticker.remove(TickerCmp.class);
        getGame().ticker.add(newTickerCmp);



        LevelCmp oldLevelCmp = getGame().currentLevel.remove(LevelCmp.class);
        if(oldLevelCmp!=null)
        {
            for(Integer id:oldLevelCmp.actors)
            {
                Entity entity = getGame().getEntity(id);
                if(id.equals(getGame().getFocus().hashCode())) continue;
                InventoryCmp inventoryCmp = (InventoryCmp) CmpMapper.getComp(CmpType.INVENTORY, entity);


                for(Integer itemID : inventoryCmp.getAllItemIDs())
                {
                    Entity itemEntity = getGame().getEntity(itemID);

                    getEngine().removeEntity(itemEntity);
                }
                getEngine().removeEntity(entity);
            }
            for(Integer id:oldLevelCmp.items)
            {
                getEngine().removeEntity(getGame().getEntity(id));
            }
        }


        WindowCmp dungeonWindowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);



        SerpentMapGenerator serpentMapGenerator = new SerpentMapGenerator(42, 42, new GWTRNG(rng.nextInt()));


        serpentMapGenerator.putWalledBoxRoomCarvers(5);
        serpentMapGenerator.putWalledRoundRoomCarvers(5);
        serpentMapGenerator.putCaveCarvers(5);
        getGame().dungeonGen.addLake(15);
        char[][] preDungeon = serpentMapGenerator.generate();
        LevelCmp newLevel  = new LevelCmp(getGame().dungeonGen.generate(preDungeon, serpentMapGenerator.getEnvironment()), getGame().dungeonGen.getBareDungeon(), serpentMapGenerator.getEnvironment());
        newLevel.decoDungeon[getGame().dungeonGen.stairsUp.x][getGame().dungeonGen.stairsUp.y]='<';
        newLevel.decoDungeon[getGame().dungeonGen.stairsDown.x][getGame().dungeonGen.stairsDown.y]='>';
        LightingCmp lightingCmp = new LightingCmp(newLevel.decoDungeon);
        getGame().currentLevel.add(newLevel);
        getGame().currentLevel.remove(LightingCmp.class);
        getGame().currentLevel.add(lightingCmp);



        newLevel.resistance = MyDungeonUtility.generateSimpleResistances(newLevel.decoDungeon);

        dungeonWindowCmp.lightingHandler = new LightHandler(MyDungeonUtility.generateResistances3x3(newLevel.lineDungeon), SColor.BLACK, Radius.CIRCLE, 0, dungeonWindowCmp.display);
        dungeonWindowCmp.lightingHandler.lightList.clear();
        PositionCmp playerPositionCmp = new PositionCmp(getGame().dungeonGen.stairsUp);
        getGame().player.add(playerPositionCmp);
        AICmp aiCmp = new AICmp(newLevel.decoDungeon, new ArrayList(Arrays.asList(TerrainType.STONE, TerrainType.MOSS, TerrainType.SHALLOW_WATER, TerrainType.BRIDGE)));
        player.remove(AICmp.class);
        player.add(aiCmp);
        player.remove(FOVCmp.class);
        player.add(new FOVCmp(newLevel.decoDungeon[0].length,newLevel.decoDungeon.length));
        Coord loc = getGame().dungeonGen.stairsUp;
        GlyphsCmp glyphsCmp = new GlyphsCmp(display, '@', SColor.WHITE, loc.x, loc.y);
        glyphsCmp.leftGlyph = display.glyph('•', SColor.WHITE, loc.x, loc.y);
        glyphsCmp.rightGlyph = display.glyph('•', SColor.WHITE, loc.x, loc.y);
        player.add(glyphsCmp);
        Light light = new Light(Coord.get(getGame().dungeonGen.stairsUp.x*3+1, getGame().dungeonGen.stairsUp.y*3+1), new Radiance(5, SColor.COSMIC_LATTE.toFloatBits()) );
        dungeonWindowCmp.lightingHandler.addLight(light.hashCode(), light);
        glyphsCmp.glyph.setName(light.hashCode() + " " + player.hashCode()+ " actor");



        aiCmp.decoDungeon=newLevel.decoDungeon;
        aiCmp.dijkstraMap.initializeCost(aiCmp.getTerrainCosts(aiCmp.decoDungeon));
        player.add(new FOVCmp(getGame().bigWidth,getGame().bigHeight));
        newLevel.actors.add(getGame().dungeonGen.stairsUp, player.hashCode(), player.hashCode());

        GreasedRegion spwnCrds = new GreasedRegion();
        spwnCrds.addAll(new GreasedRegion(newLevel.decoDungeon, '.'));

        GreasedRegion stairsUpFOV = new GreasedRegion(squidpony.squidgrid.FOV.reuseFOV(newLevel.resistance, new double[newLevel.resistance.length][newLevel.resistance[0].length] , getGame().dungeonGen.stairsUp.x, getGame().dungeonGen.stairsUp.y),0.0).not();
        GreasedRegion stairsDownFOV = new GreasedRegion(squidpony.squidgrid.FOV.reuseFOV(newLevel.resistance, new double[newLevel.resistance.length][newLevel.resistance[0].length] , getGame().dungeonGen.stairsDown.x, getGame().dungeonGen.stairsDown.y),0.0).not();

        if(getGame().getEntity(player.hashCode())==null)
        {
            getEngine().addEntity(player);
        }
        //player.add(new FocusCmp());

        Coord shrine1Coord = null;
        Coord shrine2Coord = null;
        ArrayList<School> schools = new ArrayList(Arrays.asList(School.values()));
        for(OrderedSet<Coord> centers : getGame().dungeonGen.placement.getCenters())
        {
            if(stairsUpFOV.contains(centers.first()) && shrine1Coord==null)
            {

                shrine1Coord = centers.first();
                School school = rng.getRandomElement(schools);
                schools.remove(school);
                Entity shrine = objectFactory.getShrine(shrine1Coord, school);
                getEngine().addEntity(shrine);


            }
            if(stairsDownFOV.contains(centers.first()) && shrine2Coord==null)
            {

                shrine2Coord = centers.first();
                School school = rng.getRandomElement(schools);
                schools.remove(school);
                Entity shrine = objectFactory.getShrine(shrine2Coord, school);
                getEngine().addEntity(shrine);

            }
        }
        if(shrine1Coord==null || shrine2Coord==null )
        {
            newLevel();
            return;
        }


        spwnCrds.andNot(stairsUpFOV);

        for(int i=0;i<10;i++)
        {

           /* Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);

            Skill skill = rng.getRandomElement(Arrays.asList(Skill.values()));
            getEngine().addEntity(getGame().generateScroll(itemLoc, skill, newLevel));*/

            /*Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);

            getEngine().addEntity(weaponFactory.newRndWeapon(itemLoc));*/

            Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);

            getEngine().addEntity(armorFactory.newRndArmor(itemLoc));

            itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);

            //engine.addEntity(weaponFactory.newTorch(itemLoc));

        }
        for(int i=0;i<10;i++)
        {
            loc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(loc);

            Entity mob = mobFactory.generateRndMob(loc, "Enemy "+i, getGame().depth);
            CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, mob);
            for(Skill skill : codexCmp.prepared)
            {
                CmpMapper.getAbilityComp(skill, mob).setMap(newLevel.bareDungeon);
            }
        }
        for(int i=0;i<3;i++)
        {
            Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);
            getEngine().addEntity(getGame().foodFactory.generateFoodITem(itemLoc));
        }
        CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, getGame().player);
        for(Skill skill : codexCmp.prepared)
        {
            CmpMapper.getAbilityComp(skill, getGame().player).setMap(newLevel.bareDungeon);
        }
        getEngine().getSystem(NoiseSys.class);
        Entity eventEntity = new Entity();
        eventEntity.add(new GameStateEvt(GameState.PLAYING));
        getEngine().addEntity(eventEntity);

    }
}
