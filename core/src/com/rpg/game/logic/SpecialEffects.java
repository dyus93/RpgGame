package com.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.rpg.game.logic.utils.MapElement;
import com.rpg.game.logic.utils.Poolable;

public class SpecialEffects implements Poolable, MapElement {
    private static final int WIDTH = 50;
    private static final int WD2 = WIDTH / 2;
    private static final int HEIGHT = 50;
    private static final int HD2 = HEIGHT / 2;

    private TextureRegion[] textures;
    private Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private float lifetime;
    private float angle;
    private float timePerFrame;

    @Override
    public int getCellX() {
        return (int) position.x / Map.CELL_WIDTH;
    }

    @Override
    public int getCellY() {
        return (int) position.y / Map.CELL_HEIGHT;
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition(){
        return position;
    }

    public SpecialEffects(){
        this.textures = null;
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
        this.timePerFrame = 0.1f;
        this.active = false;
    }

    public void setup(TextureRegion[] textures, float x, float y, float angle){
        this.textures = textures;
        this.position.set(x, y);
        this.velocity.set(180.0f * MathUtils.cosDeg(angle), 180.0f * MathUtils.sinDeg(angle));
        this.angle = angle;
        this.lifetime = 0.0f;
        this.active = true;
    }

    public void deactivate(){
        active = false;
    }

    public int getCurrentFrameIndex(){
        return (int)(lifetime / timePerFrame) % textures.length;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(textures[getCurrentFrameIndex()], position.x - WD2, position.y - HD2, WD2, HD2, WIDTH, HEIGHT, 1.2f, 1.2f, angle);
        if (MathUtils.random(100) < 5){
            batch.draw(textures[getCurrentFrameIndex()], position.x - WD2, position.y - HD2, WD2, HD2, WIDTH, HEIGHT, 1.5f, 1.5f, angle);
        }
    }

    public void update(float dt){
        lifetime += dt;
        position.mulAdd(velocity, dt);
        if (lifetime > textures.length * timePerFrame){
            deactivate();
        }
    }
}
