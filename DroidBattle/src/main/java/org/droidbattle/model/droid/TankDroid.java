package org.droidbattle.model.droid;

import org.droidbattle.model.weapon.Weapon;

public class TankDroid extends Droid {
    public TankDroid(String name, Weapon weapon) {
        super(name, 300, 5f, 50, 80f, weapon);
    }
}
