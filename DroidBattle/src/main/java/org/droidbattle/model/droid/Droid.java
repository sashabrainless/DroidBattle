package org.droidbattle.model.droid;
import org.droidbattle.model.weapon.Weapon;

import java.util.Random;

public class Droid {
    protected String name;
    protected int maxHealth, health, shield, electricCounter = 0;
    protected float baseAttack, accuracy;
    protected Weapon weapon;

    protected Random rnd = new Random();

    public Droid(String name, int maxHealth, float baseAttack, int baseShield, float accuracy, Weapon weapon) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.baseAttack = baseAttack;
        this.shield = baseShield;
        this.accuracy = accuracy;
        this.weapon = weapon;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getShield() {
        return shield;
    }

    public float getBaseAttack() {
        return baseAttack;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int takeDamage(int damageAmount) {
        if (electricCounter > 0) {
            int extraElectricDamage = 5;
            damageAmount += extraElectricDamage;
            System.out.println(name + " отримав " + extraElectricDamage + " pt. пошкодження електрикою.\n");
        }

        int remainingHealth = damageAmount;
        int damageToShield = Math.min(shield, remainingHealth);
        shield -= damageToShield;
        remainingHealth -= damageToShield;
        int damageToHealth = Math.min(health, remainingHealth);
        health -= damageToHealth;
        return damageToShield + damageToHealth;
    }

    public void heal(int damageAmount) {
        if (health <= 0)
            return;
        health = Math.min(maxHealth, health + damageAmount);
    }

    public void addShield(int damageAmount) {
        shield += damageAmount;
    }

    public void electricAttack(int turns) {
        electricCounter = Math.max(electricCounter, turns);
    }

    public void startTurn() {
        if (electricCounter > 0)
            electricCounter--;
    }

    public String brief() {
        return "Ім'я: " + name + " Клас: " + getClass().getSimpleName() + " HP: " + health + " SP: " + shield;
    }

    public String detailed() {
        return "Ім'я: " + name + " Клас: " + getClass().getSimpleName() + " HP: " + health + "/" + maxHealth + " SP: " + shield +
                " ATK: " + baseAttack + " ACC: " + accuracy + " WPN: " + weapon.getName();
    }

    public Droid copyForBattle() {
        if (this instanceof InfantryDroid)
            return new InfantryDroid(name, weapon);
        if (this instanceof RepairDroid)
            return new RepairDroid(name, weapon);
        if (this instanceof TankDroid)
            return new TankDroid(name, weapon);
        return null;
    }
}
