package org.droidbattle.model.droid;

import org.droidbattle.model.weapon.Weapon;

public class RepairDroid extends Droid {
    public RepairDroid(String name, Weapon weapon) {
        super(name, 200, 5f, 30, 70f, weapon);
    }
}
