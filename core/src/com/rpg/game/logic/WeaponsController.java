package com.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.rpg.game.logic.utils.ObjectPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeaponsController extends ObjectPool<Weapon> {
    private GameController gc;
    private List<Weapon> prototypes;

    @Override
    protected Weapon newObject() {
        return new Weapon(gc);
    }

    public WeaponsController(GameController gc){
        this.gc = gc;
        this.loadPrototypes();
    }

    public void setup(float x, float y){
        Weapon out = getActiveElement();
        out.copyFrom(prototypes.get(MathUtils.random(0, prototypes.size() - 1)));
        forge(out);
        out.setPosition(x, y);
        out.activate();
    }

    public Weapon getOneFromAnyPrototype(){
        Weapon out = new Weapon(gc);
        out.copyFrom(prototypes.get(MathUtils.random(0, prototypes.size() - 1)));
        forge(out);
        return out;
    }

    public void forge(Weapon w){
        for (int i = 0; i < 30; i++) {
            if (MathUtils.random(100) < 5){
                w.setMaxDamage(w.getMaxDamage() + 1);
            }
        }
        if (w.getMinDamage() > w.getMaxDamage()){
            w.setMinDamage(w.getMaxDamage());
        }
    }

    public void update(float dt){
        checkPool();
    }

    public void loadPrototypes(){
        prototypes = new ArrayList<>();
        BufferedReader reader = null;
        try{
            reader = Gdx.files.internal("data/weapons.csv").reader(8192);
            reader.readLine();
            String line = null;
            while ((line = reader.readLine()) != null){
                prototypes.add(new Weapon(line));
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}