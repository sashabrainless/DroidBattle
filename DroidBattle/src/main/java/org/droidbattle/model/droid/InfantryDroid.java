package org.droidbattle.model.droid;

import org.droidbattle.model.weapon.Weapon;

public class InfantryDroid extends Droid {
    private int electricMaxUses = 2, electricUsesLeft, electricDamage = 20;

    public InfantryDroid(String name, Weapon weapon) {
        super(name, 250, 10f, 20, 80f, weapon);
        this.electricUsesLeft = electricMaxUses;
    }

    public boolean canUseElectric() {
        return electricUsesLeft > 0;
    }

    public void useElectric() {
        if (electricUsesLeft > 0)
            electricUsesLeft--;
    }

    public int getElectricDamage() {
        return electricDamage;
    }
}