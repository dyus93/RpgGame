package com.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.rpg.game.screen.ScreenManager;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private PowerUpsController powerUpsController;
    private Hero hero;
    private Map map;
    private SpecialEffectsController specialEffectsController;
    private ProjectilesController projectilesController;
    private MonsterController monsterController;
    private InfoController infoController;
    private Music music;
    private List<GameCharacter> allCharacters;
    private Vector2 tmp, tmp2;
    private Vector2 mouse;
    private WeaponsController weaponsController;
    private float worldTimer;

    public float getWorldTimer() {
        return worldTimer;
    }

    public Vector2 getMouse() {
        return mouse;
    }

    public Hero getHero() {
        return hero;
    }

    public SpecialEffectsController getSpecialEffectsController() {
        return specialEffectsController;
    }

    public PowerUpsController getPowerUpsController() {
        return powerUpsController;
    }

    public List<GameCharacter> getAllCharacters() {
        return allCharacters;
    }

    public MonsterController getMonsterController() {
        return monsterController;
    }

    public Map getMap(){
        return map;
    }

    public InfoController getInfoController() {
        return infoController;
    }

    public ProjectilesController getProjectilesController(){
        return projectilesController;
    }

    public WeaponsController getWeaponsController() {
        return weaponsController;
    }

    public GameController(){
        this.allCharacters = new ArrayList<>();
        this.tmp = new Vector2(0.0f, 0.0f);
        this.tmp2 = new Vector2(0.0f, 0.0f);
        this.projectilesController = new ProjectilesController(this);
        this.map = new Map();
        this.infoController = new InfoController();
        this.weaponsController = new WeaponsController(this);
        this.powerUpsController = new PowerUpsController(this);
        this.specialEffectsController = new SpecialEffectsController();
        this.hero = new Hero(this);
        this.monsterController = new MonsterController(this, 25);
        this.mouse = new Vector2(0, 0);
        this.music = Gdx.audio.newMusic(Gdx.files.internal("audio/music.wav"));
        this.music .setLooping(true);
        this.music.play();
    }

    public void update(float dt){
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        ScreenManager.getInstance().getViewport().unproject(mouse);
        worldTimer += dt;
        allCharacters.clear();
        allCharacters.add(hero);
        allCharacters.addAll(monsterController.getActiveList());
        hero.update(dt);
        monsterController.update(dt);
        checkCollisions();
        projectilesController.update(dt);
        weaponsController.update(dt);
        powerUpsController.update(dt);
        specialEffectsController.update(dt);
    }

    public void collideUnits(GameCharacter u1, GameCharacter u2){
        if(u1.getArea().overlaps(u2.getArea())){
            tmp.set(u1.getArea().x, u1.getArea().y);
            tmp.sub(u2.getArea().x, u2.getArea().y);
            float halfInterLen = ((u1.getArea().radius + u2.getArea().radius) - tmp.len()) / 2.0f;
            tmp.nor();
            tmp2.set(u1.getPosition().mulAdd(tmp, halfInterLen));
            if (map.isGroundPassable(tmp2)) {
                u1.changePosition(tmp2);
            }
            tmp2.set(u2.getPosition().mulAdd(tmp, -halfInterLen));
            if(map.isGroundPassable(tmp2)) {
                u2.changePosition(tmp2);
            }
        }
    }

    public void checkCollisions(){
        for (int i = 0; i < monsterController.getActiveList().size(); i++) {
            Monster m = monsterController.getActiveList().get(i);
            collideUnits(hero, m);
        }
        for (int i = 0; i < monsterController.getActiveList().size() - 1; i++) {
            Monster m = monsterController.getActiveList().get(i);
            for (int j = i + 1; j < monsterController.getActiveList().size(); j++) {
                Monster m2 = monsterController.getActiveList().get(j);
                collideUnits(m, m2);
            }
        }
        for (int i = 0; i < weaponsController.getActiveList().size(); i++) {
            Weapon w = weaponsController.getActiveList().get(i);
            if (hero.getPosition().dst(w.getPosition()) < 20){
                w.consume(hero);
            }
        }
        for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
            Projectile p = projectilesController.getActiveList().get(i);
            if (!map.isAirPassable(p.getCellX(), p.getCellY())){
                p.deactivate();
                continue;
            }
            if (p.getPosition().dst(hero.getPosition()) < 18 && p.getOwner() != hero){
                p.deactivate();
                hero.takeDamage(p.getOwner(), p.getDamage());
            }
            for (int j = 0; j < monsterController.getActiveList().size(); j++) {
                Monster m = monsterController.getActiveList().get(j);
                if (p.getOwner() == m){
                    continue;
                }
                if (p.getPosition().dst(m.getPosition()) < 18){
                    p.deactivate();
                    m.takeDamage(p.getOwner(), p.getDamage());
                }
            }
        }
        for (int i = 0; i < getPowerUpsController().getActiveList().size(); i++) {
            PowerUp p = powerUpsController.getActiveList().get(i);
            if (p.getPosition().dst(hero.getPosition()) < 24){
                p.consume(hero);
            }
        }
    }

    public void dispose(){
        hero.dispose();
        music.dispose();
    }
}
