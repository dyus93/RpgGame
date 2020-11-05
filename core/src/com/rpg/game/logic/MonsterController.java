package com.rpg.game.logic;

import com.rpg.game.logic.utils.ObjectPool;

public class MonsterController extends ObjectPool<Monster> {
    private GameController gc;
    private float innerTimer;
    private float spawnPeriod;

    @Override
    protected Monster newObject() {
        return new Monster(gc);
    }

    public MonsterController(GameController gc, int initialCount){
        this.gc = gc;
        this.spawnPeriod = 30.0f;
        for (int i = 0; i < initialCount; i++) {
            getActiveElement().generateNew();
        }
    }

    public void update(float dt){
        innerTimer += dt;
        if (innerTimer > spawnPeriod){
            innerTimer = 0.0f;
            getActiveElement().generateNew();
        }
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
