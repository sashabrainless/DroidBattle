package org.droidbattle.model.weapon;

public class Weapon {
    private String name;
    private int attack;
    private float accuracy; // percent

    protected Weapon(String name, int attack, float accuracy) {
        this.name = name;
        this.attack = attack;
        this.accuracy = accuracy;
    }

    public int getAttack() {
        return attack;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public String getName() {
        return name;
    }
}
