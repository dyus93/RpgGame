package com.rpg.game.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rpg.game.logic.GameController;
import com.rpg.game.logic.WorldRenderer;

public class GameScreen extends AbstractScreen {
    private GameController gc;
    private WorldRenderer worldRenderer;

    public GameScreen(SpriteBatch batch){
        super(batch);
    }

    @Override
    public void show() {
        gc = new GameController();
        worldRenderer = new WorldRenderer(gc, batch);
    }

    @Override
    public void render(float delta) {
        gc.update(delta);
        worldRenderer.render();
    }

    @Override
    public void dispose() {
        gc.dispose();
    }
}
