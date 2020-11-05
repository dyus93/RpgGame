package com.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.rpg.game.logic.utils.MapElement;
import com.rpg.game.screen.ScreenManager;
import com.rpg.game.screen.utils.Assets;

import java.util.*;

public class WorldRenderer {
    private GameController gc;
    private SpriteBatch batch;
    private BitmapFont font24;
    private BitmapFont font14;
    private List<MapElement>[] drawables;
    private Vector2 pov;
    private Comparator<MapElement> yComparator;
    private FrameBuffer frameBuffer;
    private TextureRegion frameBufferRegion;
    private ShaderProgram shaderProgram;

    public WorldRenderer(GameController gameController, SpriteBatch batch) {
        this.gc = gameController;
        this.batch = batch;
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        this.font14 = Assets.getInstance().getAssetManager().get("fonts/font14.ttf");
        this.pov = new Vector2(0, 0);
        this.drawables = new ArrayList[Map.MAP_CELLS_HEIGHT];
        for (int i = 0; i < drawables.length; i++) {
            drawables[i] = new ArrayList<>();
        }
        this.yComparator = new Comparator<MapElement>() {
            @Override
            public int compare(MapElement o1, MapElement o2) {
                return (int) (o2.getY() - o1.getY());
            }
        };
        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, ScreenManager.WORLD_WIDTH, ScreenManager.WORLD_HEIGHT, false);
        this.frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        this.frameBufferRegion.flip(false, true);
        this.shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/vertex.glsl").readString(), Gdx.files.internal("shaders/fragment.glsl").readString());
        if (!shaderProgram.isCompiled()){
            throw new IllegalArgumentException("Error compiling shader:" + shaderProgram.getLog());
        }
    }

    public void render(){
        pov.set(gc.getHero().position);
        if (pov.x < ScreenManager.HALF_WORLD_WIDTH){
            pov.x = ScreenManager.HALF_WORLD_WIDTH;
        }
        if (pov.y < ScreenManager.HALF_WORLD_HEIGHT){
            pov.y = ScreenManager.HALF_WORLD_HEIGHT;
        }
        if (pov.x > gc.getMap().getWidthLimit() - ScreenManager.HALF_WORLD_WIDTH){
            pov.x = gc.getMap().getWidthLimit() - ScreenManager.HALF_WORLD_WIDTH;
        }
        if (pov.y > gc.getMap().getHeightLimit() - ScreenManager.HALF_WORLD_HEIGHT){
            pov.y = gc.getMap().getHeightLimit() - ScreenManager.HALF_WORLD_HEIGHT;
        }
        ScreenManager.getInstance().pointCameraTo(pov);
        for (int j = 0; j < drawables.length; j++) {
            drawables[j].clear();
        }
        drawables[gc.getHero().getCellY()].add(gc.getHero());
        for (int i = 0; i < gc.getMonsterController().getActiveList().size(); i++) {
            Monster m = gc.getMonsterController().getActiveList().get(i);
            drawables[m.getCellY()].add(m);
        }
        for (int i = 0; i < gc.getProjectilesController().getActiveList().size(); i++) {
            Projectile p = gc.getProjectilesController().getActiveList().get(i);
            drawables[p.getCellY()].add(p);
        }
        for (int i = 0; i < gc.getWeaponsController().getActiveList().size(); i++) {
            Weapon w = gc.getWeaponsController().getActiveList().get(i);
            drawables[w.getCellY()].add(w);
        }
        for (int i = 0; i < gc.getPowerUpsController().getActiveList().size(); i++) {
            PowerUp p = gc.getPowerUpsController().getActiveList().get(i);
            drawables[p.getCellY()].add(p);
        }
        for (int i = 0; i < drawables.length; i++) {
            Collections.sort(drawables[i], yComparator);
        }
        frameBuffer.begin();

        Gdx.gl.glClearColor(0, 0, 0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderGround(batch, x, y);
            }
        }
        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0 ; y--) {
            for (int i = 0; i < drawables[y].size(); i++) {
                drawables[y].get(i).render(batch, font14);
            }
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderUpper(batch, x, y);
            }
        }
        gc.getSpecialEffectsController().render(batch);
        gc.getInfoController().render(batch, font24);
        batch.end();

        frameBuffer.end();
        ScreenManager.getInstance().resetCamera();

        batch.begin();
        batch.setShader(shaderProgram);
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("time"), gc.getWorldTimer());
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("px"), pov.x / ScreenManager.WORLD_WIDTH);
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("py"), pov.y / ScreenManager.WORLD_HEIGHT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(frameBufferRegion, 0, 0);
        batch.end();

        batch.setShader(null);

        batch.begin();
        gc.getHero().renderGUI(batch, font24);
        batch.end();

        ScreenManager.getInstance().pointCameraTo(pov);
    }
}
