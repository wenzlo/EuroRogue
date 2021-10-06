package EuroRogue.Systems;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Stage;


import EuroRogue.AbilityCmpSubSystems.Skill;
import EuroRogue.CmpMapper;
import EuroRogue.CmpType;
import EuroRogue.Components.AICmp;
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.GameState;
import EuroRogue.MyEntitySystem;
import EuroRogue.MySparseLayers;
import EuroRogue.AbilityCmpSubSystems.Ability;
import squidpony.squidai.BlastAOE;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidgrid.mapping.LineKit;
import squidpony.squidmath.Coord;


public class WinSysDungeon extends MyEntitySystem
{
    private ImmutableArray<Entity> entities;

    public WinSysDungeon()
    {
        super.priority = 8;
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
        if(!((WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow)).display.isVisible()) return;
        putMap(getGame().currentLevel);

    }

    private void putMap(Entity levelEntity)
    {
        LevelCmp levelCmp = (LevelCmp) CmpMapper.getComp(CmpType.LEVEL,levelEntity);
        LightingCmp lightingCmp = (LightingCmp)CmpMapper.getComp(CmpType.LIGHTING, levelEntity);
        WindowCmp windowCmp = (WindowCmp)CmpMapper.getComp(CmpType.WINDOW, getGame().dungeonWindow);
        FOVCmp focusFov = ((FOVCmp) CmpMapper.getComp(CmpType.FOV,getGame().getFocus()));
        Coord focusPos = ((PositionCmp) CmpMapper.getComp(CmpType.POSITION,getGame().getFocus())).coord;
        AimingCmp aimingCmp = (AimingCmp) CmpMapper.getComp(CmpType.AIMING, getGame().getFocus());
        Ability aimAbility = null;
        if(aimingCmp!=null) aimAbility = (Ability) CmpMapper.getAbilityComp(aimingCmp.skill, getGame().getFocus());
        LineKit.pruneLines(levelCmp.lineDungeon, focusFov.seen, levelCmp.prunedDungeon);
        MySparseLayers display = windowCmp.display;
        display.clear();
        display.put(levelCmp.prunedDungeon, lightingCmp.fgLighting, lightingCmp.bgLighting);

        for (int x = Math.max(0, focusPos.x - (display.gridWidth >> 1) - 1), i = 0; x < levelCmp.decoDungeon[0].length && i < display.gridWidth + 2; x++, i++)
        {
            for (int y = Math.max(0, focusPos.y - (display.gridHeight >> 1) - 1), j = 0; y < levelCmp.decoDungeon.length  && j < windowCmp.display.gridHeight + 2; y++, j++)
            {
                if (focusFov.los[x][y] > 0.0 && lightingCmp.fgLightLevel[x][y]>0) {
                    // Here we use a convenience method in SparseLayers that puts a char at a specified position (the
                    // closer to FLOAT_LIGHTING (sixth parameter) based on how visible the cell is (seventh parameter,
                    // comes from the FOV calculations) in a way that fairly-quickly changes over time.
                    // This effect appears to shrink and grow in a circular area around the player, with the lightest
                    // cells around the player and dimmer ones near the edge of vision. This lighting is "consistent"
                    // because all cells at the same distance will have the same amount of lighting applied.
                    // We use prunedDungeon here so segments of walls that the player isn't aware of won't be shown.
                    if (levelCmp.floors.contains(Coord.get(x, y)))
                        display.put(x, y, levelCmp.decoDungeon[x][y], lightingCmp.fgLighting[x][y]);

                    else
                        display.put(x, y, levelCmp.prunedDungeon[x][y], lightingCmp.fgLighting[x][y]);

                }else if(focusFov.nightVision[x][y]>0){

                    if (levelCmp.floors.contains(Coord.get(x, y)))
                        display.put(x, y, levelCmp.decoDungeon[x][y], SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), SColor.GREEN_BAMBOO.toFloatBits(), (float) focusFov.nightVision[x][y]));

                    else
                        display.put(x, y, levelCmp.prunedDungeon[x][y], SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), SColor.GREEN_BAMBOO.toFloatBits(), (float)focusFov.nightVision[x][y]*1.3f));


                }else if (focusFov.seen.contains(x, y) ){



                        if(getGame().gameState== GameState.AIMING)
                        {
                            if(aimAbility!=null)
                            {
                                Coord coord = Coord.get(x,y);
                                char chr = levelCmp.decoDungeon[x][y];
                                if(!levelCmp.floors.contains(coord)) chr = levelCmp.prunedDungeon[x][y];
                                if(aimAbility.possibleTargets(focusPos, levelCmp.resistance).contains(x,y)) display.put(x, y, chr, SColor.GREEN_BAMBOO);
                                if(aimAbility.aoe.findArea().keySet().contains(coord)) display.put(x, y, chr, SColor.SAFETY_ORANGE);
                            }
                        }

                        else if(!levelCmp.floors.contains(x,y)) display.put(x, y, levelCmp.prunedDungeon[x][y], SColor.lerpFloatColors(SColor.BLACK.toFloatBits(), levelCmp.colors[x][y], 0.12f));
                        if(getGame().dungeonGen.stairsDown == Coord.get(x,y)  ) display.put(x, y, '>', SColor.SLATE_GRAY);
                        if(getGame().dungeonGen.stairsUp == Coord.get(x,y)  ) display.put(x, y, '<', SColor.SLATE_GRAY);

                }
                AICmp aiCmp = (AICmp) CmpMapper.getComp(CmpType.AI, getGame().getFocus());
                if(aiCmp.alerts.contains(Coord.get(x,y)) &! focusFov.visible.contains(x,y))
                {

                    display.put(x, y, '!', SColor.WHITE);
                }
                Coord cell = Coord.get(x,y);
                Integer potentialItemID = levelCmp.items.get(cell);
                if(potentialItemID!=null && focusFov.visible.contains(cell))
                {
                    Entity item = getGame().getEntity(potentialItemID);
                    CharCmp charCmp = (CharCmp) CmpMapper.getComp(CmpType.CHAR, item);
                    display.put(cell.x, cell.y, charCmp.chr, charCmp.color);
                }
            }
        }

        Stage stage = windowCmp.stage;

        TextCellFactory.Glyph pg = ((GlyphsCmp) CmpMapper.getComp(CmpType.GLYPH, getGame().getFocus())).glyph;

        stage.getCamera().position.x = pg.getX();
        stage.getCamera().position.y =  pg.getY();
        stage.act();
        stage.getViewport().apply(false);
        stage.draw();

    }

}
