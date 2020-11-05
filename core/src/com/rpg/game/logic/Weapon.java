package com.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.rpg.game.logic.utils.Consumable;
import com.rpg.game.logic.utils.MapElement;
import com.rpg.game.logic.utils.Poolable;
import com.rpg.game.screen.utils.Assets;

public class Weapon implements Poolable, MapElement, Consumable {
    public enum WeaponClass{
        SWORD, SPEAR, AXE, MACE, BOW, CROSSBOW;

        public static WeaponClass fromString(String in){
            switch (in){
                case "SWORD":
                    return SWORD;
                case "SPEAR":
                    return SPEAR;
                case "AXE":
                    return AXE;
                case "MACE":
                    return MACE;
                case "BOW":
                    return BOW;
                case "CROSSBOW":
                    return CROSSBOW;
                default:
                    throw new RuntimeException("Unknown weapon type");
            }
        }
    }

    public enum Type{
        MELEE, RANGED;

        public static Type fromString(String in){
            switch (in){
                case "MELEE":
                    return MELEE;
                case "RANGED":
                    return RANGED;
                default:
                    throw new RuntimeException("Unknown weapon class");
            }
        }
    }

    private GameController gc;
    private WeaponClass weaponClass;
    private TextureRegion texture;
    private Vector2 position;
    private Type type;
    private String title;
    private int minDamage;
    private int maxDamage;
    private float speed;
    private float range;
    private boolean active;

    @Override
    public void consume(GameCharacter gc){
        gc.getWeapon().copyFrom(this);
        active = false;
    }

    @Override
    public float getY() {
        return position.y;
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
    public boolean isActive() {
        return active;
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(int minDamage){
        this.minDamage = minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage){
        this.maxDamage = maxDamage;
    }

    public String getDps(){
        return String.valueOf((int)((maxDamage + minDamage) / 2.0f / speed));
    }

    public void setRange(float range){
        this.range = range;
    }

    public void activate(){
        this.active = true;
    }

    public Vector2 getPosition(){
        return position;
    }

    public int generateDamage(){
        return MathUtils.random(minDamage, maxDamage);
    }

    public float getSpeed() {
        return speed;
    }

    public float getRange() {
        return range;
    }

    public void setPosition(float x, float y){
        this.position.set(x, y);
    }

    public Weapon(GameController gc){
        this.gc = gc;
        this.position = new Vector2(0.0f, 0.0f);

    }

    public Weapon(String line) {
        String[] tokens = line.split(",");
        this.weaponClass = WeaponClass.fromString(tokens[0].trim());
        this.type = Type.fromString(tokens[1].trim());
        this.title = tokens[2].trim();
        this.minDamage = Integer.parseInt(tokens[3].trim());
        this.maxDamage = Integer.parseInt(tokens[4].trim());
        this.speed = Float.parseFloat(tokens[5].trim());
        this.range = Float.parseFloat(tokens[6].trim());
        if (this.type == Type.MELEE){
            texture = Assets.getInstance().getAtlas().findRegion("weaponMelee");
        }else{
            texture = Assets.getInstance().getAtlas().findRegion("weaponRanged");
        }
    }

    public void copyFrom(Weapon from){
        this.type = from.type;
        this.weaponClass = from.weaponClass;
        this.title = from.title;
        this.range = from.range;
        this.maxDamage = from.maxDamage;
        this.minDamage = from.minDamage;
        this.speed = from.speed;
        this.texture = from.texture;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 0.8f, 0.8f, 0.0f);
    }
}
