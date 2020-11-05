package com.rpg.game.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.rpg.game.logic.utils.Consumable;
import com.rpg.game.logic.utils.MapElement;
import com.rpg.game.logic.utils.Poolable;

public class PowerUp implements Poolable, Consumable, MapElement {
    public enum Type{
        COINS(0), MEDKIT(1);

        int index;

        Type(int index){
            this.index = index;
        }
    }

    private GameController gc;
    private Type type;
    private TextureRegion[][] textures;
    private Vector2 position;
    private Vector2 velocity;
    private float time;
    private boolean active;

    public PowerUp(GameController gc, TextureRegion[][] textures) {
        this.gc = gc;
        this.textures = textures;
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void consume(GameCharacter gameCharacter){
        active = false;
        switch (type){
            case MEDKIT:
                int restored = gameCharacter.restoreHp(0.1f);
                gc.getInfoController().setupAnyAmount(gameCharacter.position.x, gameCharacter.position.y, Color.GREEN, "+", restored);
                break;
            case COINS:
                int amount = MathUtils.random(3, 10);
                gameCharacter.addCoins(amount);
                gc.getInfoController().setupAnyAmount(gameCharacter.position.x, gameCharacter.position.y, Color.YELLOW, "+", amount);
                break;
        }
    }

    public void setup(float x, float y){
        position.set(x, y);
        velocity.set(MathUtils.random(-50.0f, 50.0f), MathUtils.random(-50.0f, 50.0f));
        time = 0.0f;
        type = Type.values()[MathUtils.random(0, 1)];
        active = true;
    }

    public void update(float dt){
        time += dt;
        position.mulAdd(velocity, dt);
        if (time > 4.0f){
            active = false;
        }
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(textures[type.index][0], position.x - 30, position.y - 30);
    }

    @Override
    public int getCellX() {
        return (int)(position.x / Map.CELL_WIDTH);
    }

    @Override
    public int getCellY() {
        return (int)(position.y / Map.CELL_HEIGHT);
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
