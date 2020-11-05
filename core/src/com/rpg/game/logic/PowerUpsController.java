package com.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.rpg.game.logic.utils.ObjectPool;
import com.rpg.game.screen.utils.Assets;

public class PowerUpsController extends ObjectPool<PowerUp> {
    private GameController gc;
    private TextureRegion[][] textures;

    @Override
    protected PowerUp newObject() {
        return new PowerUp(gc, textures);
    }

    public PowerUpsController(GameController gc){
        this.gc = gc;
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("powerUps")).split(60, 60);
    }

    public void setup(float x, float y){
        for (int i = 0; i < 5; i++) {
            if (MathUtils.random(100) < 25){
                getActiveElement().setup(x, y);
            }
        }
    }

    public void update(float dt){
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
