package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Arrays;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.ArmorFactory;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EuroRogue;
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
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
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
    private ArrayList<Entity> entitiesToAdd = new ArrayList<>();

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
        if(entities.size()==0) return;
        ImmutableArray<Entity> eventsFamily = getEngine().getEntitiesFor(Family.one(StatEvt.class, GameStateEvt.class, ActionEvt.class, CodexEvt.class, MoveEvt.class, ItemEvt.class,
                RestEvt.class, StatusEffectEvt.class, AnimateGlyphEvt.class).get());
        if(eventsFamily.size()>0) return;
        entitiesToAdd.clear();

        MySparseLayers display = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).display;
        display.glyphs.clear();

        LevelCmp oldLevelCmp = getGame().currentLevel.remove(LevelCmp.class);

        if(oldLevelCmp!=null)
        {

            for(Integer id:oldLevelCmp.actors)
            {
                Entity entity = getGame().getEntity(id);
                if(entity == getGame().player) continue;
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
            for(Integer id : oldLevelCmp.objects)
            {
                getEngine().removeEntity(getGame().getEntity(id));
            }

        }




        LevelEvt levelEvt = (LevelEvt) CmpMapper.getComp(CmpType.LEVEL_EVT, entities.get(0));
        levelEvt.processed=true;

        Entity newLevel = newLevel();
        while(newLevel == null)
        {
            newLevel=newLevel();

        }


        LevelCmp newLevelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, newLevel);

        Entity player = getGame().player;
        player.remove(GlyphsCmp.class);

        TickerCmp newTickerCmp = new TickerCmp();
        getGame().ticker.remove(TickerCmp.class);
        getGame().ticker.add(newTickerCmp);



        WindowCmp dungeonWindowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);

        PositionCmp playerPositionCmp = new PositionCmp(getGame().dungeonGen.stairsUp);
        getGame().player.add(playerPositionCmp);

        AICmp playerAiCmp = new AICmp(newLevelCmp.bareDungeon, newLevelCmp.decoDungeon, new ArrayList(Arrays.asList(TerrainType.STONE, TerrainType.MOSS, TerrainType.SHALLOW_WATER, TerrainType.BRIDGE)));
        player.remove(AICmp.class);
        player.add(playerAiCmp);
        AICmp newAIcmp = (AICmp)CmpMapper.getComp(CmpType.AI, player);

        player.remove(FOVCmp.class);
        player.add(new FOVCmp(newLevelCmp.decoDungeon[0].length,newLevelCmp.decoDungeon.length));
        Coord loc = getGame().dungeonGen.stairsUp;
        GlyphsCmp glyphsCmp = new GlyphsCmp(display, '@', SColor.WHITE, loc.x, loc.y);
        glyphsCmp.leftGlyph = display.glyph('•', SColor.WHITE, loc.x, loc.y);
        glyphsCmp.rightGlyph = display.glyph('•', SColor.WHITE, loc.x, loc.y);
        player.add(glyphsCmp);
        Light light = new Light(Coord.get(getGame().dungeonGen.stairsUp.x*3+1, getGame().dungeonGen.stairsUp.y*3+1), new Radiance(5, SColor.COSMIC_LATTE.toFloatBits()) );
        dungeonWindowCmp.lightingHandler.addLight(light.hashCode(), light);

        glyphsCmp.glyph.setName(light.hashCode() + " " + player.hashCode()+ " actor");



        playerAiCmp.dijkstraMap.initializeCost(playerAiCmp.getTerrainCosts(newLevelCmp.decoDungeon));
        player.add(new FOVCmp(EuroRogue.bigWidth,EuroRogue.bigHeight));
        newLevelCmp.actors.add(getGame().dungeonGen.stairsUp, player.hashCode(), player.hashCode());
        for(Skill skill : ((CodexCmp)CmpMapper.getComp(CmpType.CODEX, player)).prepared )
        {
            CmpMapper.getAbilityComp(skill, player).setMap(newLevelCmp.decoDungeon);
        }


        getGame().currentLevel=newLevel;
        getEngine().addEntity(newLevel);

        for(Entity entity : entitiesToAdd)
        {
            getEngine().addEntity(entity);
        }

       /* System.out.println("Final new Levl");
        for(char[] line : newLevelCmp.decoDungeon)
        {
            System.out.println(line);
        }*/
        Entity eventEntity = new Entity();
        eventEntity.add(new GameStateEvt(GameState.PLAYING));
        getEngine().addEntity(eventEntity);
        //System.out.println("new level actors "+newLevelCmp.actors);

    }

    public Entity newLevel()
    {

        Entity newLevel = new Entity();

        SerpentMapGenerator serpentMapGenerator = new SerpentMapGenerator(42, 42, new GWTRNG(rng.nextInt()));


        serpentMapGenerator.putWalledBoxRoomCarvers(5);
        serpentMapGenerator.putWalledRoundRoomCarvers(5);
        serpentMapGenerator.putCaveCarvers(5);
        getGame().dungeonGen.addLake(15);
        serpentMapGenerator.generate();

        getGame().dungeonGen.generate(serpentMapGenerator.getDungeon(), serpentMapGenerator.getEnvironment());
        char[][] finalDungeon = getGame().dungeonGen.getDungeon();
        LevelCmp levelCmp  = new LevelCmp(finalDungeon, getGame().dungeonGen.getBareDungeon(), serpentMapGenerator.getEnvironment());

        GreasedRegion stairsUpFOV = new GreasedRegion(squidpony.squidgrid.FOV.reuseFOV(levelCmp.resistance, new double[levelCmp.resistance.length][levelCmp.resistance[0].length] , getGame().dungeonGen.stairsUp.x, getGame().dungeonGen.stairsUp.y),0.0).not();
        GreasedRegion stairsDownFOV = new GreasedRegion(squidpony.squidgrid.FOV.reuseFOV(levelCmp.resistance, new double[levelCmp.resistance.length][levelCmp.resistance[0].length] , getGame().dungeonGen.stairsDown.x, getGame().dungeonGen.stairsDown.y),0.0).not();

        Coord shrine1Coord = null;
        Coord shrine2Coord = null;
        Entity shrine1 = null;
        Entity shrine2 = null;
        ArrayList<School> schools = new ArrayList(Arrays.asList(School.values()));
        for(OrderedSet<Coord> centers : getGame().dungeonGen.placement.getCenters())
        {
            if(stairsUpFOV.contains(centers.first()) && shrine1Coord==null)
            {

                shrine1Coord = centers.first();
                School school = rng.getRandomElement(schools);
                schools.remove(school);
                shrine1 = objectFactory.getShrine(shrine1Coord, school);



            }
            if(stairsDownFOV.contains(centers.first()) && shrine2Coord==null)
            {

                shrine2Coord = centers.first();
                School school = rng.getRandomElement(schools);
                schools.remove(school);
                shrine2 = objectFactory.getShrine(shrine2Coord, school);


            }
        }
        if(shrine1==null || shrine2==null )
        {
            return null;
        }

        levelCmp.resistance = MyDungeonUtility.generateSimpleResistances(levelCmp.decoDungeon);
        levelCmp.decoDungeon[getGame().dungeonGen.stairsUp.x][getGame().dungeonGen.stairsUp.y]='<';
        levelCmp.decoDungeon[getGame().dungeonGen.stairsDown.x][getGame().dungeonGen.stairsDown.y]='>';
        LightingCmp lightingCmp = new LightingCmp(levelCmp.decoDungeon);

        newLevel.add(levelCmp);
        newLevel.add(lightingCmp);
        WindowCmp dungeonWindowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);

        dungeonWindowCmp.lightingHandler = new LightHandler(MyDungeonUtility.generateResistances3x3(levelCmp.decoDungeon), SColor.BLACK, Radius.CIRCLE, 0, dungeonWindowCmp.display);
        //TODO use deco instead of line
        dungeonWindowCmp.lightingHandler.lightList.clear();
        addShrine(shrine1, newLevel);
        addShrine(shrine2, newLevel);



        GreasedRegion spwnCrds = new GreasedRegion();
        spwnCrds.addAll(new GreasedRegion(levelCmp.decoDungeon, '.'));


        //player.add(new FocusCmp());




        spwnCrds.andNot(stairsUpFOV);

        for(int i=0;i<10;i++)
        {

           /* Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);

            Skill skill = rng.getRandomElement(Arrays.asList(Skill.values()));
            getEngine().addEntity(getGame().generateScroll(itemLoc, skill, levelCmp));*/

            /*Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);

            getEngine().addEntity(weaponFactory.newRndWeapon(itemLoc));*/

            /*Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);

            getEngine().addEntity(armorFactory.newRndArmor(itemLoc));

            itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);*/

            //engine.addEntity(weaponFactory.newTorch(itemLoc));

        }
        FOV fov = new FOV();
        for(int i=0;i<10;i++)
        {
            Coord loc = rng.getRandomElement(spwnCrds);

            GreasedRegion deadZone = new GreasedRegion(fov.calculateFOV(levelCmp.resistance, loc.x, loc.y, 12, Radius.CIRCLE), 0.0).not();

            spwnCrds.andNot(deadZone);

            Entity mob = mobFactory.generateRndMob(loc, levelCmp,"Enemy "+i, getGame().depth);
            CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, mob);
            for(Skill skill : codexCmp.prepared)
            {
                CmpMapper.getAbilityComp(skill, mob).setMap(levelCmp.bareDungeon);
            }
            entitiesToAdd.add(mob);

        }
        for(int i=0;i<3;i++)
        {
            Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);
            entitiesToAdd.add(getGame().foodFactory.generateFoodITem(itemLoc));
        }


        return newLevel;

    }

    private void addShrine(Entity shrineEntity, Entity levelEntity)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL,levelEntity);
        Coord position = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION, shrineEntity)).coord;
        CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, shrineEntity);
        levelCmp.objects.add(position, shrineEntity.hashCode(), shrineEntity.hashCode());

        levelCmp.bareDungeon[position.x][position.y] = '#';
        levelCmp.decoDungeon[position.x][position.y] = charCmp.chr;
        levelCmp.colors[position.x][position.y] = charCmp.color.toFloatBits();
        levelCmp.resistance = MyDungeonUtility.generateResistances(levelCmp.decoDungeon);
        levelEntity.add(new LightingCmp(levelCmp.decoDungeon));

        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;
        LightCmp lightCmp = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, shrineEntity);


        Light light = new Light(Coord.get(position.x*3+1, position.y*3+1), new Radiance(lightCmp.level, lightCmp.color, lightCmp.flicker, lightCmp.strobe) );
        lightHandler.addLight(shrineEntity.hashCode(), light);

        entitiesToAdd.add(shrineEntity);

    }

}
