package com.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.rpg.game.logic.utils.ObjectPool;
import com.rpg.game.screen.utils.Assets;

public class SpecialEffectsController extends ObjectPool<SpecialEffects> {
    private TextureRegion[][] texturesSwordSwing;

    @Override
    protected SpecialEffects newObject() {
        return new SpecialEffects();
    }

    public SpecialEffectsController(){
        this.texturesSwordSwing = new TextureRegion(Assets.getInstance().getAtlas().findRegion("swinganim502")).split(50, 50);
    }

    public void setupSwordSwing(float x, float y, float angle){
        getActiveElement().setup(texturesSwordSwing[MathUtils.random(0, 5)], x, y, angle);
    }

    public void render(SpriteBatch batch){
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).render(batch, null);
        }
    }

    public void update(float dt){
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
    }
}
