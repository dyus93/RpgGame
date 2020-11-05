package com.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.rpg.game.screen.utils.Assets;

public class Map {
    private class Obstacle{
        int index;
        float scale;
        float offset;
        boolean isAirPassable;

        public Obstacle() {
            this.index = MathUtils.random(0, 6);
            this.scale = MathUtils.random(0.7f, 1.4f);
            this.offset = MathUtils.random(-12, 12);
            this.isAirPassable = false;
            if (this.index == 4 || this.index == 6){
                this.isAirPassable = true;
            }
        }
    }
    public static final int MAP_CELLS_WIDTH = 40;
    public static final int MAP_CELLS_HEIGHT = 32;
    public static final int OBSTACLES_COUNT = 100;

    public static final int CELL_WIDTH = 80;
    public static final int CELL_HEIGHT = 40;

    private Obstacle[][] data;
    private TextureRegion grassTexture;
    private TextureRegion[] obstaclesTexture;

    public Map(){
        this.data = new Obstacle[MAP_CELLS_WIDTH][MAP_CELLS_HEIGHT];
        for (int i = 0; i < OBSTACLES_COUNT; i++) {
            int x = MathUtils.random(MAP_CELLS_WIDTH - 1);
            int y = MathUtils.random(MAP_CELLS_HEIGHT - 1);
            data[x][y] = new Obstacle();
        }
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.obstaclesTexture = new TextureRegion(Assets.getInstance().getAtlas().findRegion("trees")).split(80, 120)[0];
    }

    public int getWidthLimit(){
        return MAP_CELLS_WIDTH * CELL_WIDTH;
    }

    public int getHeightLimit(){
        return MAP_CELLS_HEIGHT * CELL_HEIGHT;
    }

    public boolean isAirPassable(int cellX, int cellY){
        return data[cellX][cellY] == null || data[cellX][cellY].isAirPassable;
    }

    public boolean isGroundPassable(int cellX, int cellY){
        return data[cellX][cellY] == null ;
    }

    public boolean isGroundPassable(Vector2 position){
        return isGroundPassable((int)(position.x / CELL_WIDTH), (int)(position.y / CELL_HEIGHT));
    }

    public void renderGround(SpriteBatch batch, int x, int y){
        batch.draw(grassTexture, x * CELL_WIDTH, y * CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
    }

    public void renderUpper(SpriteBatch batch, int x, int y){
        if (data[x][y] != null){
            batch.draw(obstaclesTexture[data[x][y].index],x * CELL_WIDTH + data[x][y].offset, y * CELL_HEIGHT, 40, 30, 80, 120, data[x][y].scale, data[x][y].scale, 0);
        }
    }
}
