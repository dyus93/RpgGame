package com.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.rpg.game.logic.utils.MapElement;
import com.rpg.game.screen.utils.Assets;

import static com.rpg.game.logic.Weapon.Type.MELEE;
import static com.rpg.game.logic.Weapon.Type.RANGED;

public abstract class GameCharacter implements MapElement {
    public enum State{
        IDLE, MOVE, ATTACK, PURSUIT, RETREAT
    }

    static final int WIDTH = 60;
    static final int HEIGHT = 60;

    protected GameController gc;
    protected GameCharacter target;
    protected GameCharacter lastAttacker;
    protected TextureRegion[][] textures;
    protected TextureRegion textureHp;
    protected Weapon weapon;
    protected Vector2 position;
    protected Vector2 tmp;
    protected Vector2 tmp2;
    protected Vector2 dst;
    protected Circle area;
    protected State state;
    protected float attackTime;
    protected float stateTimer;
    protected float lifeTime;
    protected float visionRadius;
    protected float speed;
    protected float walkTime;
    protected float timePerFrame;
    protected float damageTimer;
    protected int hp, hpMax;
    protected int coins;

    public GameCharacter(GameController gc, int hpMax, float speed) {
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.gc = gc;
        this.dst = new Vector2(0.0f, 0.0f);
        this.tmp = new Vector2(0.0f, 0.0f);
        this.tmp2 = new Vector2(0.0f, 0.0f);
        this.position = new Vector2(0.0f, 0.0f);
        this.area = new Circle(0.0f, 0.0f, 15);
        this.hpMax = hpMax;
        this.hp = hpMax;
        this.speed = speed;
        this.state = State.IDLE;
        this.target = null;
        this.stateTimer = 1.0f;
        this.timePerFrame = 0.2f;
        this.coins = 0;
    }

    @Override
    public float getY() {
        return position.y;
    }

    public Vector2 getPosition(){
        return position;
    }

    public Circle getArea(){
        return area;
    }

    public int getCellX(){
        return (int)position.x / Map.CELL_WIDTH;
    }

    public int getCellY(){
        return (int)(position.y) / Map.CELL_HEIGHT;
    }

    public Weapon getWeapon(){
        return weapon;
    }

    public boolean isAlive(){
        return hp > 0;
    }

    public void addCoins(int amount){
        coins += amount;
    }

    public int getCurrentFrameIndex(){
        return (int)(walkTime / timePerFrame % textures[0].length);
    }

    public int restoreHp(float percent){
        int amount = (int)(hpMax * percent);
        if (hp + amount > hpMax){
            amount = hpMax - hp;
        }
        hp += amount;
        return amount;
    }

    public void update(float dt){
        lifeTime += dt;
        damageTimer -= dt;
        if (damageTimer < 0.0f){
            damageTimer = 0.0f;
        }
        if (state == State.ATTACK){
            dst.set(target.getPosition());
        }
        if (state == State.MOVE || state == State.RETREAT ||(state == State.ATTACK && this.position.dst(target.getPosition()) > weapon.getRange() - 10)){
            moveToDst(dt);
        }
        if (state == State.ATTACK && this.position.dst(target.getPosition()) < weapon.getRange()){
            attackTime += dt;
            if (attackTime > weapon.getSpeed()){
                attackTime = 0.0f;
                if (weapon.getType() == MELEE){
                    tmp.set(target.position).sub(position);
                    gc.getSpecialEffectsController().setupSwordSwing(position.x, position.y, tmp.angle());
                    target.takeDamage(this, weapon.generateDamage());
                }
                if (weapon.getType() == RANGED && target != null){
                    gc.getProjectilesController().setup(this, position.x, position.y, target.getPosition().x, target.getPosition().y, weapon.generateDamage());
                }
            }
        }
        slideFromWall(dt);
    }

    public void changePosition(float x, float y){
        position.set(x, y);
        if (position.x < 0.1f){
            position.x = 0.1f;
        }
        if (position.y < 0.1f){
            position.y = 0.1f;
        }
        if (position.x > Map.MAP_CELLS_WIDTH * 80 - 1){
            position.x = Map.MAP_CELLS_WIDTH * 80 - 1;
        }
        if (position.y > Map.MAP_CELLS_HEIGHT * 80 - 1){
            position.y = Map.MAP_CELLS_HEIGHT * 80 - 1;
        }
        area.setPosition(position.x, position.y - 20);
    }

    public void changePosition(Vector2 newPosition){
        changePosition(newPosition.x, newPosition.y);
    }

    public void moveToDst(float dt){
        tmp.set(dst).sub(position).nor().scl(speed);
        tmp2.set(position);
        walkTime += dt;
        if(position.dst(dst) > speed * dt){
            changePosition(position.x + tmp.x * dt, position.y + tmp.y * dt);
        }else{
            changePosition(dst);
            state  = State.IDLE;
        }
        if (!gc.getMap().isGroundPassable(getCellX(), getCellY())){
            changePosition(tmp2.x + tmp.x * dt, tmp2.y);
            if (!gc.getMap().isGroundPassable(getCellX(), getCellY())){
                changePosition(tmp2.x, tmp2.y + tmp.y * dt);
                if (!gc.getMap().isGroundPassable(getCellX(), getCellY())){
                    changePosition(tmp2);
                }
            }
        }
    }

    public boolean takeDamage(GameCharacter attacker, int amount){
        lastAttacker = attacker;
        hp -= amount;
        damageTimer += 0.4f;
        if (damageTimer > 1.0f){
            damageTimer = 1.0f;
        }
        if (hp <= 0){
            onDeath();
            return true;
        }
        return false;
    }

    public void resetAttackState(){
        dst.set(position);
        state = State.IDLE;
        target = null;
    }
    
    public void slideFromWall(float dt){
        if (!gc.getMap().isGroundPassable(position)){
            tmp.set(position).sub(getCellX() * Map.CELL_WIDTH + Map.CELL_WIDTH / 2.0f, getCellY() * Map.CELL_HEIGHT + Map.CELL_HEIGHT / 2.0f).nor().scl(60.0f);
            changePosition(position.x + tmp.x * dt, position.y + tmp.y * dt);
        }
    }

    public  void onDeath(){
        for (int i = 0; i < gc.getAllCharacters().size(); i++) {
            GameCharacter gameCharacter = gc.getAllCharacters().get(i);
            if (gameCharacter.target == this){
                gameCharacter.resetAttackState();
            }
        }
    }
    @Override
    public void render(SpriteBatch batch, BitmapFont font){
        TextureRegion currentRegion = textures[0][getCurrentFrameIndex()];
        if (dst.x > position.x){
            if (currentRegion.isFlipX()){
                currentRegion.flip(true, false);
            }
        }else
        {
            if (!currentRegion.isFlipX()){
                currentRegion.flip(true, false);
            }
        }
        batch.setColor(1.0f, 1.0f - damageTimer, 1.0f - damageTimer, 1.0f);
        batch.draw(currentRegion, position.x - 30, position.y - 15, 30, 30,60, 60, 1.0f, 1.0f, 0);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        batch.setColor(0.2f, 0.2f, 0.2f, 1.0f);
        batch.draw(textureHp, position.x - 32, position.y + 28, 64, 14);

        float n = (float) hp / hpMax;
        float shock = damageTimer * 5.0f;
        batch.setColor(1.0f - n, n, 0.0f, 1.0f);
        batch.draw(textureHp, position.x - 30 + MathUtils.random(-shock, shock), position.y + 30 + MathUtils.random(-shock, shock), 60 * ((float) hp / hpMax), 10);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.draw(batch, String.valueOf(hp), position.x - 30 + MathUtils.random(-shock, shock), position.y + 42 + MathUtils.random(-shock, shock), 60, 1, false);
    }
}
