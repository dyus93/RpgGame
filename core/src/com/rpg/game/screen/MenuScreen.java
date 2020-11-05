package com.rpg.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.rpg.game.screen.utils.Assets;

public class MenuScreen extends AbstractScreen{
    private BitmapFont font72;
    private Stage stage;

    public MenuScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf");
        createGui();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font72.draw(batch,"RPG  Game 2020", 0, 500, 1280, Align.center, false);
        batch.end();
        stage.draw();
    }

    public void createGui(){
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());

        BitmapFont font14 = Assets.getInstance().getAssetManager().get("/fonts/font24.ttf");
        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("simpleButton"), null, null, font14);
        TextButton btnNewGame = new TextButton("New Game", menuBtnStyle);
        btnNewGame.setPosition(480, 300);
        TextButton btnExitGame = new TextButton("Exit Game", menuBtnStyle);
        btnExitGame.setPosition(480, 200);

        btnNewGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });
        btnExitGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(btnNewGame);
        stage.addActor(btnExitGame);
        skin.dispose();
    }
    
    public void update(float dt){
        stage.act(dt);
    }
}
