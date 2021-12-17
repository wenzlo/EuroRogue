package EuroRogue.Systems.Win;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.List;

import EuroRogue.AbilityCmpSubSystems.Ability;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AI.AICmp;
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.StatusEffectCmps.StatusEffect;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidgrid.mapping.LineKit;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;


public class WinSysDungeon extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public WinSysDungeon()
    {
        super.priority = 10;
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(LevelCmp.class).get());
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime)
    {
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        if(!windowCmp.display.isVisible()) return;
        putMap(getGame().currentLevel);

        Stage stage = windowCmp.stage;

        TextCellFactory.Glyph pg = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, getGame().getFocus())).glyph;

        stage.getCamera().position.x = pg.getX();
        stage.getCamera().position.y =  pg.getY();

        stage.act();
        stage.getViewport().apply(false);

        getGame().filterBatch.begin();
        getGame().filterBatch.end();

        stage.draw();
        
    }

    private void putMap(Entity levelEntity)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL,levelEntity);
        LightingCmp lightingCmp = (LightingCmp)CmpMapper.getComp(CmpType.LIGHTING, levelEntity);
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        FOVCmp focusFov = ((FOVCmp) CmpMapper.getComp(CmpType.FOV,getGame().getFocus()));
        StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, getGame().getFocus());

        Coord focusPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION,getGame().getFocus())).coord;
        AimingCmp aimingCmp = (AimingCmp) CmpMapper.getComp(CmpType.AIMING, getGame().getFocus());
        Ability aimAbility = null;
        if(aimingCmp!=null) aimAbility = CmpMapper.getAbilityComp(aimingCmp.skill, getGame().getFocus());
        LineKit.pruneLines(levelCmp.lineDungeon, focusFov.seen, levelCmp.prunedDungeon);

        MySparseLayers display = windowCmp.display;
        display.clear();
        display.put(/*levelCmp.prunedDungeon, lightingCmp.fgLighting,*/ lightingCmp.bgLighting);

        GreasedRegion allCaves = getGame().dungeonGen.placement.finder.allCaves.copy();
        GreasedRegion caveWalls = allCaves.fringe8way();

        for (int x = Math.max(0, focusPos.x - (display.gridWidth >> 1) - 1), i = 0; x < levelCmp.decoDungeon[0].length && i < display.gridWidth + 2; x++, i++)
        {
            for (int y = Math.max(0, focusPos.y - (display.gridHeight >> 1) - 1), j = 0; y < levelCmp.decoDungeon.length  && j < windowCmp.display.gridHeight + 2; y++, j++)
            {
                Coord coord = Coord.get(x,y);
                if (focusFov.fov[x][y] > 0.0 && focusFov.visible.contains(coord) ) {

                    if (levelCmp.floors.contains(coord))
                        display.put(x, y, levelCmp.decoDungeon[x][y], lightingCmp.fgLighting[x][y]);

                    else if (focusFov.seen.contains(coord) && caveWalls.contains(coord))
                        display.put(x, y, '#', lightingCmp.fgLighting[x][y]);
                    else if (focusFov.seen.contains(coord))
                        display.put(x, y, levelCmp.prunedDungeon[x][y], lightingCmp.fgLighting[x][y]);

                    if (focusFov.nightVision[x][y] > 0 && lightingCmp.fgLightLevel[x][y] < statsCmp.getLightDetectionLvl()) {

                        if (levelCmp.floors.contains(coord))
                            display.put(x, y, levelCmp.decoDungeon[x][y], SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), SColor.GREEN_BAMBOO.toFloatBits(), (float) Math.min(1, focusFov.nightVision[x][y] + 0.1f)));

                        else if (!levelCmp.floors.contains(x, y) && caveWalls.contains(coord))
                            display.put(x, y, '#', SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], 0.30f));

                        else if (!levelCmp.floors.contains(x, y))
                            display.put(x, y, levelCmp.prunedDungeon[x][y], SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], 0.30f));

                    }

                } else if(focusFov.seen.contains(coord)) {


                    if (!levelCmp.floors.contains(x, y) && caveWalls.contains(coord))
                        display.put(x, y, '#', SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], 0.30f));

                    else if (!levelCmp.floors.contains(x, y))
                        display.put(x, y, levelCmp.prunedDungeon[x][y], SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], 0.30f));
                }



                if(getGame().dungeonGen.stairsDown == coord  ) display.put(x, y, '>', SColor.SLATE_GRAY);
                if(getGame().dungeonGen.stairsUp == coord  ) display.put(x, y, '<', SColor.SLATE_GRAY);



                AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, getGame().getFocus());
                if(aiCmp.alerts.containsValue(coord) &! focusFov.visible.contains(x,y))
                {

                    display.put(x, y, '!', SColor.WHITE);
                }
            }
        }
        for(Coord coord : levelCmp.items.positions())
        {
            if(!focusFov.visible.contains(coord)) continue;
            Integer itemID = levelCmp.items.get(coord);

            Entity itemEntity = getGame().getEntity(itemID);
            CharCmp charCmp = (CharCmp)CmpMapper.getComp(CmpType.CHAR, itemEntity);
            display.put(coord.x, coord.y, charCmp.chr, charCmp.color);

        }
        //applyTargetPathFilter(display);
        applyStalkingFilter(lightingCmp, levelCmp);




    }

    public void applyTargetPathFilter(MySparseLayers display)
    {
        Entity focusTarget = getGame().getFocusTarget();
        if(focusTarget!=null)
        {
            StatsCmp statsCmp = (StatsCmp)CmpMapper.getComp(CmpType.STATS, focusTarget);
            AICmp aiCmp = CmpMapper.getAIComp(statsCmp.mobType.aiType, focusTarget);
            List<Coord> path = aiCmp.pathToFollow;
            if(!path.isEmpty())
                for(Coord coord : path)
                    display.put(coord.x, coord.y, '.', SColor.WHITE);
        }
    }

    private void applyStalkingFilter(LightingCmp lightingCmp, LevelCmp levelCmp)
    {
        Entity focus = getGame().getFocus();
        if(CmpMapper.getStatusEffectComp(StatusEffect.STALKING, focus)==null)
            return;

        StatsCmp focusStats = (StatsCmp) CmpMapper.getComp(CmpType.STATS, focus);
        AICmp aiCmp = CmpMapper.getAIComp(focusStats.mobType.aiType, focus);

        double visibilityLvl = focusStats.getVisibleLightLvl();
        GreasedRegion exposed = new GreasedRegion(lightingCmp.fgLightLevel, visibilityLvl).not();

        for(Integer id : aiCmp.visibleEnemies)
        {
            Entity enemy = getGame().getEntity(id);
            if(enemy==null) continue;

            FOVCmp enemyFOV = (FOVCmp) CmpMapper.getComp(CmpType.FOV, enemy);
            exposed.or(new GreasedRegion(enemyFOV.nightVision, 0.0).not());


        }
        MySparseLayers display = ((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).display;
        FOVCmp fovCmp = (FOVCmp) CmpMapper.getComp(CmpType.FOV, focus);
        for(Coord coord : exposed)
        {
            if( levelCmp.floors.contains(coord) &! levelCmp.isBlocked(coord) && fovCmp.visible.contains(coord))
            {
                display.put(coord.x, coord.y, '.', SColor.BENI_DYE);
            }
        }
    }
    /*public void applyAOEfilter(MySparseLayers display, LevelCmp levelCmp)
    {
        Coord focusPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION,getGame().getFocus())).coord;
        for (int x = Math.max(0, focusPos.x - (display.gridWidth >> 1) - 1), i = 0; x < levelCmp.decoDungeon[0].length && i < display.gridWidth + 2; x++, i++)
        {
            for (int y = Math.max(0, focusPos.y - (display.gridHeight >> 1) - 1), j = 0; y < levelCmp.decoDungeon.length  && j < windowCmp.display.gridHeight + 2; y++, j++)
            {

            }
        }
    }*/



}
