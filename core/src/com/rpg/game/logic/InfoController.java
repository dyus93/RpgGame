package com.rpg.game.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.rpg.game.logic.utils.ObjectPool;

public class InfoController extends ObjectPool<InfoText> {
    private StringBuilder stringBuilder;

    @Override
    protected InfoText newObject() {
        return new InfoText();
    }

    public InfoController() {
        this.stringBuilder = new StringBuilder();
    }

    public void setupAnyAmount(float x, float y, Color color, String prefix, int amount){
        InfoText infoText = getActiveElement();
        stringBuilder.setLength(0);
        stringBuilder.append(prefix).append(amount);
        infoText.setup(x + MathUtils.random(-20, 20), y + MathUtils.random(-20, 20), stringBuilder, color);
    }

    public void render(SpriteBatch batch, BitmapFont font){
        for (int i = 0; i < activeList.size(); i++) {
            InfoText infoText = activeList.get(i);
            font.setColor(infoText.getColor());
            font.draw(batch, infoText.getText(), infoText.getPosition().x, infoText.getPosition().y);
        }
        font.setColor(Color.WHITE);
    }

    public void update(float dt){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
