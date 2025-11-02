package org.droidbattle.service;

import org.droidbattle.model.droid.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

public class Battle {
    private List<Droid> teamA, teamB;
    private Random rnd = new Random();
    private StringBuilder log = new StringBuilder();

    private static final int MAX_ROUNDS = 100, LOG_LIMIT = 1000000, NO_ALIVE_DROIDS = 0, ELECTRIC_TURNS = 3,
            DAMAGE_WITH_SHIELD = -1, NO_DAMAGE = 0, FIRST_ROUND = 1, MINIMUM_ALIVE_DROIDS = 1;
    private static final double INFANTRY_ACCURACY = 0.25, ELECTRIC_ACCURACY = 0.30, REPAIR_ACCURACY = 0.60, HIT_CHANCE = 100.0;
    private static final float TANK_ACCURACY = 0.5f, HEALTH = 0.2f, AVERAGE = 2f;

    public Battle(List<Droid> teamA, List<Droid> teamB) {
        this.teamA = new ArrayList<>(teamA);
        this.teamB = new ArrayList<>(teamB);
    }

    public String run() {
        log.append("-- Початок битви --\n");
        log.append("Команда A: ").append(names(teamA)).append("\n");
        log.append("Команда B: ").append(names(teamB)).append("\n");

        int round = FIRST_ROUND;
        while (aliveCount(teamA) > NO_ALIVE_DROIDS && aliveCount(teamB) > NO_ALIVE_DROIDS && round <= MAX_ROUNDS) {
            log.append("-- Раунд " + (round++) + " --\n");

            takeTurn(teamA, teamB);
            if (aliveCount(teamB) == NO_ALIVE_DROIDS)
                break;

            takeTurn(teamB, teamA);
            log.append("\n");

            if (log.length() > LOG_LIMIT) {
                log.append("...лог закінчено через обмеження розміру!\n");
                break;
            }
        }

        if (round > MAX_ROUNDS)
            log.append("Бій припинено після ").append(MAX_ROUNDS).append(" раундів — нічия!\n");

        if (aliveCount(teamA) > NO_ALIVE_DROIDS && aliveCount(teamB) == NO_ALIVE_DROIDS)
            log.append("Команда A виграла!\n");
        else if (aliveCount(teamB) > NO_ALIVE_DROIDS && aliveCount(teamA) == NO_ALIVE_DROIDS)
            log.append("Команда B виграла!\n");
        else if (aliveCount(teamA) == NO_ALIVE_DROIDS && aliveCount(teamB) == NO_ALIVE_DROIDS)
            log.append("Нічия! Всі дроїди зламані\n");
        else
            log.append("Нічия після обмеження раундів або закінчення логу.\n");

        log.append("\n---- Результат ----\n");
        System.out.println("--------------------");

        log.append("Команда A: ");
        for (Droid droid : teamA)
            log.append(droid.brief()).append("\n");

        log.append("Команда B: ");
        for (Droid droid : teamB)
            log.append(droid.brief()).append("\n");

        String out = log.toString();
        System.out.println(out);
        return out;
    }

    private void takeTurn(List<Droid> activeTeam, List<Droid> enemyTeam) {

        for (Droid activeDroid : activeTeam) {
            if (!activeDroid.isAlive())
                continue;

            activeDroid.startTurn();

            if (activeDroid instanceof InfantryDroid id) {
                boolean useElectric = rnd.nextDouble() < INFANTRY_ACCURACY && id.canUseElectric();

                if (useElectric) {
                    Droid target = chooseTarget(enemyTeam);
                    if (target != null)
                        useElectric(id, target);
                }
                else {
                    Droid target = chooseTarget(enemyTeam);

                    if (target != null)
                        useAttack(activeDroid, target);
                }
            }
            else if (activeDroid instanceof RepairDroid rd) {
                Droid ally = null;

                if (enemyTeam.size() > MINIMUM_ALIVE_DROIDS) {
                    ally = chooseAllyToHeal(activeTeam);
                }

                if (ally != null && rnd.nextDouble() < REPAIR_ACCURACY)
                    useHeal(rd, ally);
                else {
                    Droid target = chooseTarget(enemyTeam);

                    if (target != null)
                        useAttack(activeDroid, target);
                }
            }
            else if (activeDroid instanceof TankDroid td) {
                Droid ally = null;

                if (enemyTeam.size() > MINIMUM_ALIVE_DROIDS) {
                    ally = chooseAllyToShield(activeTeam);
                }

                if (ally != null && rnd.nextDouble() < TANK_ACCURACY)
                    performShield(td, ally);
                else {
                    Droid target = chooseTarget(enemyTeam);

                    if (target != null)
                        useAttack(activeDroid, target);
                }
            }
            else {
                Droid target = chooseTarget(enemyTeam);

                if (target != null)
                    useAttack(activeDroid, target);
            }

            if (aliveCount(enemyTeam) == NO_ALIVE_DROIDS)
                break;
        }
    }

    private Droid chooseTarget(List<Droid> team) {
        List<Droid> alive = new ArrayList<>();

        for (Droid droid : team)
            if (droid.isAlive())
                alive.add(droid);

        if (alive.isEmpty())
            return null;

        return alive.get(rnd.nextInt(alive.size()));
    }

    private Droid chooseAllyToHeal(List<Droid> team) {
        List<Droid> injured = new ArrayList<>();

        for (Droid droid : team)
            if (droid.isAlive() && droid.getHealth() < droid.getMaxHealth())
                injured.add(droid);

        if (injured.isEmpty())
            return null;

        return injured.get(rnd.nextInt(injured.size()));
    }

    private Droid chooseAllyToShield(List<Droid> team) {
        List<Droid> alive = new ArrayList<>();

        for (Droid droid : team)
            if (droid.isAlive())
                alive.add(droid);

        if (alive.isEmpty())
            return null;

        return alive.get(rnd.nextInt(alive.size()));
    }

    private void useAttack(Droid attacker, Droid target) {
        float attackValue = (attacker.getBaseAttack() + attacker.getWeapon().getAttack()) / AVERAGE;
        float hitChance = (attacker.getAccuracy() + attacker.getWeapon().getAccuracy()) / AVERAGE;
        boolean hit = rnd.nextDouble() < hitChance / HIT_CHANCE;

        if (hit) {
            int damage = Math.round(attackValue);
            int dealt = target.takeDamage(damage);

            if (dealt == NO_DAMAGE) {
                if (target.isAlive()) {
                    target.heal(DAMAGE_WITH_SHIELD);
                    log.append(attacker.getName()).append(" влучив, але броня поглинула ураження; ").append(target.getName()).append(" втрачає 1 HP pt. щоб уникнути зациклення.\n");
                }
            }
            else {
                log.append(attacker.getName()).append(" атакує ").append(target.getName()).append(" на ").append(dealt).append(" pt. пошкодження.\n");
            }

        }
        else {
            log.append(attacker.getName()).append(" атакує ").append(target.getName()).append(" але промахується.\n");
        }
    }

    private void useElectric(InfantryDroid inf, Droid target) {
        if (!inf.canUseElectric()) {
            useAttack(inf, target);
            return;
        }

        float electricAttack = inf.getElectricDamage();
        boolean hit = rnd.nextDouble() < ELECTRIC_ACCURACY;

        if (hit) {
            int dealt = target.takeDamage(Math.round(electricAttack));
            target.electricAttack(ELECTRIC_TURNS);
            inf.useElectric();
            log.append(inf.getName()).append(" використовує електричну атаку на ").append(target.getName()).append(" і задає ").append(dealt).append(" pt. пошкодження. ").append(target.getName()).append(" електризовано.\n");
        }
        else {
            inf.useElectric();
            log.append(inf.getName()).append(" використовує електричну атаку на ").append(target.getName()).append(" але промахується.\n");
        }
    }

    private void useHeal(RepairDroid rep, Droid ally) {
        int heal = Math.round(rep.getMaxHealth() * HEALTH);
        ally.heal(heal);
        log.append(rep.getName()).append(" лікує ").append(ally.getName()).append(" на ").append(heal).append(" HP pt.\n");
    }

    private void performShield(TankDroid tank, Droid ally) {
        int shieldAdd = Math.round(tank.getMaxHealth() * HEALTH);
        ally.addShield(shieldAdd);
        log.append(tank.getName()).append(" надає ").append(ally.getName()).append(" ").append(shieldAdd).append(" SP pt.\n");
    }

    private int aliveCount(List<Droid> team) {
        int count = NO_ALIVE_DROIDS;
        for (Droid droid : team)
            if (droid.isAlive())
                count++;
        return count;
    }

    private String names(List<Droid> team) {
        StringJoiner joined = new StringJoiner(", ");
        for (Droid droid : team)
            joined.add(droid.getName());
        return joined.toString();
    }
}
