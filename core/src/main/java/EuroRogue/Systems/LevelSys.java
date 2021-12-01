package EuroRogue.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Arrays;

import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.ArmorFactory;
import EuroRogue.ArmorType;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.EquipmentSlot;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.ItemType;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.NoiseMapCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.WindowCmp;
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
import EuroRogue.LevelType;
import EuroRogue.Light;
import EuroRogue.LightHandler;
import EuroRogue.MobFactory;
import EuroRogue.MobType;
import EuroRogue.MyDungeonUtility;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.ObjectFactory;
import EuroRogue.School;
import EuroRogue.WeaponFactory;
import EuroRogue.WeaponType;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Measurement;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidgrid.mapping.SectionDungeonGenerator;
import squidpony.squidgrid.mapping.SerpentMapGenerator;
import squidpony.squidgrid.mapping.styled.TilesetType;
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
    private ArrayList<Entity> actorsToAdd = new ArrayList<>();
    private ArrayList<Entity> itemsToAdd = new ArrayList<>();

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
        actorsToAdd.clear();
        itemsToAdd.clear();

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

                ParticleEffectsCmp peCmp = (ParticleEffectsCmp)CmpMapper.getComp(CmpType.PARTICLES, entity);
                for(TextCellFactory.Glyph glyph : peCmp.particleEffectsMap.keySet()) {
                    for(ParticleEffectsCmp.ParticleEffect effect : peCmp.particleEffectsMap.get(glyph).keySet())
                    {
                        peCmp.removeEffect(glyph, effect, display);
                    }

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

        Entity newLevel = null;
        if(levelEvt.type== LevelType.START)
        {

            while(newLevel == null)
            {
                newLevel=newTutorialLevel();

            }
        }
        else if(getGame().depth==2)
        {
            while(newLevel == null)
            {
                newLevel=newLevel(LevelType.CAVES);

            }
        }
        else
        {
            while(newLevel == null)
            {
                newLevel=newLevel(levelEvt.type);
            }
        }

        //LevelCmp newLevelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, newLevel);

        TickerCmp newTickerCmp = new TickerCmp();
        getGame().ticker.remove(TickerCmp.class);
        getGame().ticker.add(newTickerCmp);

        WindowCmp dungeonWindowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);

        getGame().engine.removeEntity(getGame().currentLevel);
        getGame().currentLevel=newLevel;
        getEngine().addEntity(getGame().currentLevel);

        LightingCmp lightingCmp = (LightingCmp) CmpMapper.getComp(CmpType.LIGHTING, newLevel);
        dungeonWindowCmp.lightingHandler = new LightHandler(lightingCmp.resistance3x3, SColor.BLACK, Radius.CIRCLE, 0, dungeonWindowCmp.display);
        dungeonWindowCmp.lightingHandler.lightList.clear();

        for(Entity entity : itemsToAdd)
        {
            getEngine().addEntity(entity);
        }

        for(Entity entity : actorsToAdd)
        {
            getEngine().addEntity(entity);
        }
        Entity player = getGame().player;
        addPlayer(player);
       /* System.out.println("Final new Levl");
        for(char[] line : newLevelCmp.decoDungeon)
        {
            System.out.println(line);
        }*/
        Entity eventEntity = new Entity();
        eventEntity.add(new GameStateEvt(GameState.PLAYING));
        getEngine().addEntity(eventEntity);

    }

    public Entity newLevel(LevelType levelType)
    {
        Entity newLevel = new Entity();

        LevelCmp levelCmp;
        LightingCmp lightingCmp;
        double ambientLightLvl = rng.between(0.0f, 1.0f);
        double minAmbientLight = 0f;
        double maxAmbientLightLvl = 0.8f;


        switch (levelType)
        {

            case START:
                //return newTutorialLevel();
            case CAVES:
                ambientLightLvl = 0.0;
                getGame().dungeonGen = new SectionDungeonGenerator(42, 42);
                getGame().dungeonGen.addWater(3,20);
                getGame().dungeonGen.addGrass(3, 20);
                //getGame().dungeonGen.addDoors(100, true);
                getGame().dungeonGen.generate(TilesetType.CAVES_LIMIT_CONNECTIVITY);

                SColor.LIMITED_PALETTE[0] = SColor.STEAMED_CHESTNUT;

                SColor.LIMITED_PALETTE[0] = SColor.DB_LEAD;
                ambientLightLvl = 0f;
                maxAmbientLightLvl = 0f;
                minAmbientLight = 0f;



                break;
            case ROOMS:
                getGame().dungeonGen = new SectionDungeonGenerator(42, 42);
                SerpentMapGenerator serpentMapGenerator = new SerpentMapGenerator(42, 42, new GWTRNG(rng.nextInt()));

                serpentMapGenerator.putWalledBoxRoomCarvers(5);
                serpentMapGenerator.putWalledRoundRoomCarvers(1);
                serpentMapGenerator.generate();


                //getGame().dungeonGen.addLake(15);
                //getGame().dungeonGen.addGrass(3, 20);
                getGame().dungeonGen.addDoors(100, true);
                getGame().dungeonGen.generate(serpentMapGenerator.getDungeon(), serpentMapGenerator.getEnvironment());



                break;
            case LAKE_TOWN:
                getGame().dungeonGen = new SectionDungeonGenerator(42, 42);
                getGame().dungeonGen.addLake(15);
                getGame().dungeonGen.addGrass(3, 20);
                getGame().dungeonGen.addDoors(100, true);
                getGame().dungeonGen.generate(TilesetType.ROOMS_AND_CORRIDORS_B);


                break;

            case MIXED:
                getGame().dungeonGen = new SectionDungeonGenerator(42, 42);
                serpentMapGenerator = new SerpentMapGenerator(42, 42, new GWTRNG(rng.nextInt()));
                getGame().dungeonGen.addGrass(3, 20);
                getGame().dungeonGen.addDoors(100, true);
                serpentMapGenerator.putWalledBoxRoomCarvers(5);
                serpentMapGenerator.putCaveCarvers(2);
                serpentMapGenerator.generate();
                getGame().dungeonGen.generate(serpentMapGenerator.getDungeon(), serpentMapGenerator.getEnvironment());


                break;
            default:
                throw new IllegalStateException("Unexpected value: " + levelType);
        }

        DijkstraMap dijkstraMap = new DijkstraMap(getGame().dungeonGen.getDungeon(), Measurement.EUCLIDEAN);
        dijkstraMap.setGoals(new Coord[]{getGame().dungeonGen.stairsDown});
        for(Coord coord : new GreasedRegion(getGame().dungeonGen.getDungeon(), '~'))
        {
            dijkstraMap.costMap[coord.x][coord.y] = 4;
        }
        dijkstraMap.scan();
        ArrayList<Coord> path = dijkstraMap.findPath(100, null, null,getGame().dungeonGen.stairsUp, getGame().dungeonGen.stairsDown);
        for(Coord coord : path)
        {
            if(getGame().dungeonGen.getDungeon()[coord.x][coord.y] == '~')
            {
                getGame().dungeonGen.getDungeon()[coord.x][coord.y] = ',';
            }

        }
        levelCmp  = new LevelCmp(getGame().dungeonGen);

        lightingCmp = new LightingCmp(levelCmp.lineDungeon, levelCmp.bgColors, minAmbientLight, maxAmbientLightLvl, ambientLightLvl);
        newLevel.add(levelCmp);
        newLevel.add(lightingCmp);

        GreasedRegion stairsUpFOV = new GreasedRegion(squidpony.squidgrid.FOV.reuseFOV(levelCmp.resistance, new double[levelCmp.resistance.length][levelCmp.resistance[0].length] , getGame().dungeonGen.stairsUp.x, getGame().dungeonGen.stairsUp.y),0.0).not();
        GreasedRegion stairsDownFOV = new GreasedRegion(squidpony.squidgrid.FOV.reuseFOV(levelCmp.resistance, new double[levelCmp.resistance.length][levelCmp.resistance[0].length] , getGame().dungeonGen.stairsDown.x, getGame().dungeonGen.stairsDown.y),0.0).not();
        GreasedRegion spwnCrds = new GreasedRegion();
        spwnCrds.addAll(new GreasedRegion(levelCmp.decoDungeon, '.'));
        spwnCrds.andNot(stairsUpFOV);
        GreasedRegion pathGR = new GreasedRegion();
        pathGR.addAll(path);
        spwnCrds.andNot(pathGR);
        int numShrines = 0;
        ArrayList<School> schools = new ArrayList(Arrays.asList(School.values()));
        OrderedSet<OrderedSet<Coord>> roomCenters = getGame().dungeonGen.placement.getCenters();

        for(OrderedSet<Coord> roomCenter : roomCenters)
        {
            if(numShrines==4) break;
            School school = rng.getRandomElement(schools);
            schools.remove(school);
            Coord position = roomCenter.randomItem(rng);

            addShrine(objectFactory.getShrine(position, school), newLevel);
            spwnCrds.remove(position);
            numShrines++;
        }
        for(OrderedSet<Coord> alongWall : getGame().dungeonGen.placement.getAlongStraightWalls())
        {
            if(numShrines==4) break;
            School school = rng.getRandomElement(schools);
            schools.remove(school);
            Coord position = alongWall.randomItem(rng);
            addShrine(objectFactory.getShrine(position, school), newLevel);
            spwnCrds.remove(position);
            numShrines++;
            if(numShrines==4)break;
        }
        for(int i=numShrines; i<4; i++)
        {
            Coord caveLoc = rng.getRandomElement(spwnCrds);
            School school = rng.getRandomElement(schools);
            schools.remove(school);

            addShrine(objectFactory.getShrine(caveLoc, school), newLevel);
            spwnCrds.remove(caveLoc);
        }


        levelCmp.decoDungeon[getGame().dungeonGen.stairsUp.x][getGame().dungeonGen.stairsUp.y]='<';
        levelCmp.decoDungeon[getGame().dungeonGen.stairsDown.x][getGame().dungeonGen.stairsDown.y]='>';




        for(int i=0;i<3;i++)
        {

           /* Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);

            Skill skill = rng.getRandomElement(Arrays.asList(Skill.values()));
            getEngine().addEntity(getGame().generateScroll(itemLoc, skill, levelCmp));*/

            Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);

            getEngine().addEntity(weaponFactory.newRndWeapon(itemLoc));

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

            Entity mob = mobFactory.generateRndMob(loc,"Enemy "+i, getGame().depth);
            CodexCmp codexCmp = (CodexCmp) CmpMapper.getComp(CmpType.CODEX, mob);
            for(Skill skill : codexCmp.prepared)
            {
                CmpMapper.getAbilityComp(skill, mob).setMap(levelCmp.bareDungeon);
            }
            actorsToAdd.add(mob);
            StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, mob);
            if(statsCmp.getPerc() <4) addTorch(mob);
            if(codexCmp.prepared.contains(Skill.DAGGER_THROW) || codexCmp.prepared.contains(Skill.STALK)) addWeapon(mob, WeaponType.DAGGER);
            else  addRndWeapon(mob);

            if(statsCmp.getDex()>statsCmp.getStr())addArmor(mob, ArmorType.LEATHER);
            else if(statsCmp.getStr() > 7) addArmor(mob, ArmorType.PLATE);
            else if(statsCmp.getStr()+statsCmp.getDex() > 7) addArmor(mob, ArmorType.MAIL);
            else addArmor(mob, ArmorType.LEATHER);


        }
        for(int i=0;i<2;i++)
        {
            Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);
            itemsToAdd.add(getGame().foodFactory.generateFoodITem(itemLoc));
        }
        for(int i=0;i<4;i++)
        {
            Coord loc = rng.getRandomElement(new GreasedRegion(levelCmp.decoDungeon, '.'));

            //GreasedRegion deadZone = new GreasedRegion(fov.calculateFOV(levelCmp.resistance, loc.x, loc.y, 8, Radius.CIRCLE), 0.0).not();

            //spwnCrds.andNot(deadZone);
            if(loc==null) break;
            Entity mob = mobFactory.generateMob(MobType.RAT, loc, levelCmp, getGame().depth);
            actorsToAdd.add(mob);

        }
        for(int i=0;i<4;i++)
        {
            Coord loc = rng.getRandomElement(new GreasedRegion(levelCmp.decoDungeon, '"'));

            //GreasedRegion deadZone = new GreasedRegion(fov.calculateFOV(levelCmp.resistance, loc.x, loc.y, 8, Radius.CIRCLE), 0.0).not();

            //spwnCrds.andNot(deadZone);
            if(loc==null) break;
            Entity mob = mobFactory.generateMob(MobType.SNAKE, loc, levelCmp, getGame().depth);
            actorsToAdd.add(mob);

        }

        return newLevel;

    }
    public Entity newTutorialLevel()
    {

        Entity newLevel = new Entity();

        SerpentMapGenerator serpentMapGenerator = new SerpentMapGenerator(24, 24, new GWTRNG(rng.nextInt()));
        serpentMapGenerator.putWalledBoxRoomCarvers(1);
        serpentMapGenerator.putCaveCarvers(1);

        serpentMapGenerator.generate();

        getGame().dungeonGen.addDoors(100, true);
        getGame().dungeonGen.addGrass(3, 25);
        getGame().dungeonGen.addWater(3, 25);
        //getGame().dungeonGen.addTraps(3,1);
        getGame().dungeonGen.generate(serpentMapGenerator.getDungeon(), serpentMapGenerator.getEnvironment());

        LevelCmp levelCmp  = new LevelCmp(getGame().dungeonGen);

        levelCmp.decoDungeon[getGame().dungeonGen.stairsUp.x][getGame().dungeonGen.stairsUp.y]='<';
        levelCmp.decoDungeon[getGame().dungeonGen.stairsDown.x][getGame().dungeonGen.stairsDown.y]='>';

        newLevel.add(levelCmp);
        StatsCmp statsCmp = (StatsCmp) CmpMapper.getComp(CmpType.STATS, getGame().getFocus());
        LightingCmp lightingCmp = new LightingCmp(levelCmp.lineDungeon, levelCmp.bgColors, 0.0, 0.8, statsCmp.getLightDetectionLvl());
        newLevel.add(lightingCmp);
        WindowCmp dungeonWindowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);

        dungeonWindowCmp.lightingHandler = new LightHandler(MyDungeonUtility.generateSimpleResistances3x3(levelCmp.decoDungeon), SColor.BLACK, Radius.CIRCLE, 0, dungeonWindowCmp.display);
        dungeonWindowCmp.lightingHandler.lightList.clear();

        GreasedRegion spwnCrds = new GreasedRegion();
        spwnCrds.addAll(new GreasedRegion(levelCmp.decoDungeon, '.'));

        int numShrines = 0;
        ArrayList<School> schools = new ArrayList(Arrays.asList(School.values()));
        OrderedSet<OrderedSet<Coord>> roomCenters = getGame().dungeonGen.placement.getCenters();

        for(OrderedSet<Coord> roomCenter : roomCenters)
        {
            if(numShrines==5) break;
            School school = rng.getRandomElement(schools);
            schools.remove(school);
            Coord position = roomCenter.randomItem(rng);

            addShrine(objectFactory.getShrine(position, school), newLevel);
            spwnCrds.remove(position);
            numShrines++;
        }
        for(OrderedSet<Coord> alongWall : getGame().dungeonGen.placement.getAlongStraightWalls())
        {
            if(numShrines==5) break;
            School school = rng.getRandomElement(schools);
            schools.remove(school);
            Coord position = alongWall.randomItem(rng);
            addShrine(objectFactory.getShrine(position, school), newLevel);
            spwnCrds.remove(position);
            numShrines++;
            if(numShrines==4)break;
        }
        for(int i=numShrines; i<5; i++)
        //for(Coord caveLoc : getGame().dungeonGen.placement.finder.allCaves)
        {
            Coord caveLoc = rng.getRandomElement(new GreasedRegion(levelCmp.decoDungeon, '.'));
            School school = rng.getRandomElement(schools);
            schools.remove(school);

            addShrine(objectFactory.getShrine(caveLoc, school), newLevel);
            spwnCrds.remove(caveLoc);
        }
        //player.add(new FocusCmp());

        //spwnCrds.andNot(stairsUpFOV);

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
        //FOV fov = new FOV();
        for(int i=0;i<6;i++)
        {
            Coord loc = rng.getRandomElement(spwnCrds);

            //GreasedRegion deadZone = new GreasedRegion(fov.calculateFOV(levelCmp.resistance, loc.x, loc.y, 8, Radius.CIRCLE), 0.0).not();

            //spwnCrds.andNot(deadZone);

            Entity mob = mobFactory.generateMob(MobType.RAT, loc, levelCmp, getGame().depth);
            actorsToAdd.add(mob);

        }
        for(int i=0;i<2;i++)
        {
            Coord loc = rng.getRandomElement(spwnCrds);

            //GreasedRegion deadZone = new GreasedRegion(fov.calculateFOV(levelCmp.resistance, loc.x, loc.y, 8, Radius.CIRCLE), 0.0).not();

            //spwnCrds.andNot(deadZone);

            Entity mob = mobFactory.generateMob(MobType.SNAKE, loc, levelCmp, getGame().depth);
            actorsToAdd.add(mob);

        }
        for(int i=0;i<3;i++)
        {
            Coord itemLoc = rng.getRandomElement(spwnCrds);
            spwnCrds.remove(itemLoc);
            itemsToAdd.add(getGame().foodFactory.generateFoodITem(itemLoc));
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
        levelCmp.resistance = MyDungeonUtility.generateSimpleResistances(levelCmp.decoDungeon);

        itemsToAdd.add(shrineEntity);

    }

    public void addTorch(Entity mob)
    {
        Entity torch = new Entity();
        torch.add(new NameCmp("Torch"));
        torch.add(new ItemCmp(ItemType.TORCH));
        torch.add(new EquipmentCmp(new EquipmentSlot[]{EquipmentSlot.LEFT_HAND_WEAP}, rng.between(3, 6), SColor.LIGHT_YELLOW_DYE.toFloatBits()));
        torch.add(new CharCmp('*', SColor.DARK_BROWN));
        torch.add(new LightCmp(0, SColor.BLACK.toFloatBits()));
        InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, mob);
        inventoryCmp.put(torch.hashCode());

        itemsToAdd.add(torch) ;
    }
    public void addRndWeapon(Entity mob)
    {
        Entity weapon  = weaponFactory.newRndWeapon();
        InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, mob);
        inventoryCmp.put(weapon.hashCode());

        itemsToAdd.add(weapon) ;

    }
    public void addWeapon(Entity mob, WeaponType weaponType)
    {
        Entity weapon  = weaponFactory.newBasicWeapon(weaponType);
        InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, mob);
        inventoryCmp.put(weapon.hashCode());

        itemsToAdd.add(weapon) ;
    }

    public void addRndArmor(Entity mob)
    {
        ArrayList<ArmorType> armorTypes = new ArrayList(Arrays.asList(ArmorType.values()));
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, mob);
        if(statsCmp.getStr()<6) armorTypes.remove(ArmorType.PLATE);
        if(statsCmp.getDex()+statsCmp.getStr()<6) armorTypes.remove(ArmorType.MAIL);
        Entity armor  = armorFactory.newRndArmor(null, armorTypes);
        InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, mob);
        inventoryCmp.put(armor.hashCode());

        itemsToAdd.add(armor) ;

    }
    public void addArmor(Entity mob, ArmorType armorType)
    {
        Entity armor  = armorFactory.newBasicArmor(armorType, null);
        InventoryCmp inventoryCmp = (InventoryCmp)CmpMapper.getComp(CmpType.INVENTORY, mob);
        inventoryCmp.put(armor.hashCode());
        itemsToAdd.add(armor) ;

    }

    public void addPlayer(Entity player)
    {

        player.remove(PositionCmp.class);
        player.remove(FOVCmp.class);

        WindowCmp windowCmp = (WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        GlyphsCmp glyphsCmp = (GlyphsCmp)CmpMapper.getComp(CmpType.GLYPH, player);
        if(glyphsCmp!=null)
        {
            windowCmp.display.removeGlyph(glyphsCmp.glyph);
            windowCmp.display.removeGlyph(glyphsCmp.leftGlyph);
            windowCmp.display.removeGlyph(glyphsCmp.rightGlyph);
            player.remove(GlyphsCmp.class);
        }


        PositionCmp playerPositionCmp = new PositionCmp(getGame().dungeonGen.stairsUp);
        Coord position = playerPositionCmp.coord;
        getGame().player.add(playerPositionCmp);

        CharCmp charCmp = (CharCmp)CmpMapper.getComp(CmpType.CHAR, player);
        glyphsCmp = new GlyphsCmp(windowCmp.display, charCmp.chr, '•','•',charCmp.color, position.x, position.y);
        player.add(glyphsCmp);



        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL, getGame().currentLevel);

        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, player);
        AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, player);
        aiCmp.dijkstraMap = new DijkstraMap(levelCmp.bareDungeon, Measurement.EUCLIDEAN);
        aiCmp.dijkstraMap.initializeCost(aiCmp.getTerrainCosts(levelCmp.decoDungeon));
        aiCmp.movementCosts = aiCmp.getTerrainCosts(levelCmp.decoDungeon);

        levelCmp.actors.put(position, player.hashCode(), player.hashCode());

        LightCmp lightCmp = (LightCmp)CmpMapper.getComp(CmpType.LIGHT, player);




        LightHandler lightHandler = ((WindowCmp) CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).lightingHandler;

        Light light = new Light(Coord.get(position.x*3+1, position.y*3+1), new Radiance(lightCmp.level, lightCmp.color) );
        Light leftLight = new Light(Coord.get(position.x*3+1, position.y*3+1), new Radiance(0, SColor.BLACK.toFloatBits()) );
        Light rightLight = new Light(Coord.get(position.x*3+1, position.y*3+1), new Radiance(0, SColor.BLACK.toFloatBits()) );

        lightHandler.addLight(light.hashCode(), light);
        lightHandler.addLight(leftLight.hashCode(), leftLight);
        lightHandler.addLight(rightLight.hashCode(), rightLight);
        glyphsCmp.glyph.setName(light.hashCode() + " " + player.hashCode()+ " actor");
        glyphsCmp.leftGlyph.setName(leftLight.hashCode() + " " + player.hashCode()+ " actorLeft");
        glyphsCmp.rightGlyph.setName(rightLight.hashCode() + " " + player.hashCode()+ " actorRight");

        player.add(new FOVCmp(levelCmp.bareDungeon.length, levelCmp.bareDungeon[0].length));
        getEngine().getSystem(FOVSys.class).updateFOV(player);

        player.remove(NoiseMapCmp.class);
        player.add(new NoiseMapCmp(levelCmp.bareDungeon));
        for(Skill skill : ((CodexCmp)CmpMapper.getComp(CmpType.CODEX, player)).prepared )
        {
            CmpMapper.getAbilityComp(skill, player).setMap(levelCmp.decoDungeon);
        }






    }


}
