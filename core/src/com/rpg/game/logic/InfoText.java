package com.rpg.game.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.rpg.game.logic.utils.Poolable;

public class InfoText implements Poolable {
    private Color color;
    private StringBuilder text;
    private Vector2 position;
    private Vector2 velocity;
    private float time;
    private boolean active;

    @Override
    public boolean isActive() {
        return active;
    }

    public StringBuilder getText(){
        return text;
    }

    public com.badlogic.gdx.graphics.Color getColor() {
        return color;
    }

    public Vector2 getPosition() {
        return position;
    }

    public InfoText(){
        this.text = new StringBuilder();
        this.active = false;
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(50.0f, 200.0f);
        this.time = 0.0f;
        this.color = Color.GREEN;
    }

    public void setup(float x, float y, StringBuilder text, Color color){
        this.position.set(x, y);
        this.active = true;
        this.text.setLength(0);
        this.text.append(text);
        this.time = 1.0f;
        this.color = color;
    }

    public void update(float dt){
        position.mulAdd(velocity, dt);
        time -= dt;
        if (time <= 0.0f){
            active = false;
        }
    }
}
