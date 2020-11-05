package com.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.rpg.game.logic.utils.ObjectPool;
import com.rpg.game.screen.utils.Assets;

public class ProjectilesController extends ObjectPool<Projectile> {
    private GameController gc;
    private TextureRegion projectileTextureRegion;

    @Override
    protected Projectile newObject() {
        return new Projectile(gc);
    }

    public ProjectilesController(GameController gc){
        this.gc = gc;
        this.projectileTextureRegion = Assets.getInstance().getAtlas().findRegion("arrow");
    }

    public void setup(GameCharacter owner, float x, float y, float targetX, float targetY, int damage){
        getActiveElement().setup(owner, projectileTextureRegion, x, y, targetX, targetY, damage);
    }

    public void update(float dt){
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
