package org.droidbattle.service;

import org.droidbattle.model.droid.*;
import org.droidbattle.model.weapon.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Game {
    private final Scanner scanner = new Scanner(System.in);
    private final List<Droid> droids = new ArrayList<>();
    private final Path lastBattle = Path.of("lastbattle.txt");

    private static final int MINIMUM_FOR_ONE_ON_ONE = 2;
    private static final int MINIMUM_FOR_TEAM_ON_TEAM = 6;

    public void runGame() {
        while (true) {
            System.out.print("\n----- Меню гри -----" +
                    "\n1 - Створити дроїда" +
                    "\n2 - Переглянути дроїдів" +
                    "\n3 - Видалити дроїда" +
                    "\n4 - Битва 1 на 1" +
                    "\n5 - Битва 3 на 3" +
                    "\n6 - Запустити останній бій" +
                    "\n0 - Вихід" +
                    "\nОберіть: ");

            String mvar = scanner.nextLine().trim();

            switch (mvar) {
                case "1":
                    createDroid();
                    break;
                case "2":
                    listDroids();
                    break;
                case "3":
                    deleteDroid();
                    break;
                case "4":
                    battleOneOnOne();
                    break;
                case "5":
                    battleTeamOnTeam();
                    break;
                case "6":
                    replayLastBattle();
                    break;
                case "0":
                    System.out.println("До побачення!");
                    return;
                default:
                    System.out.println("Помилка!");
                    break;
            }
        }
    }

    private void createDroid() {
        System.out.println("\n--------------------");
        System.out.print("Введіть ім'я: ");
        String name = scanner.nextLine().trim();

        System.out.print("Клас:" +
                "\n1 - Піхотинець" +
                "\n2 - Інженер" +
                "\n3 - Танк" +
                "\nОберіть: ");
        String cvar = scanner.nextLine().trim();

        System.out.print("Зброя:" +
                "\n1 - Спис" +
                "\n2 - Лук" +
                "\n3 - Меч" +
                "\nОберіть: ");
        String wvar = scanner.nextLine().trim();

        Weapon weapon = new SwordWeapon();
        switch (wvar) {
            case "1":
                weapon = new SpearWeapon();
                break;
            case "2":
                weapon = new BowWeapon();
                break;
            case "3":
                break;
            default: {
                System.out.println("Помилка! Обрано зброю - меч.");
                break;
            }
        };

        Droid droid = new InfantryDroid(name, weapon);
        switch (cvar) {
            case "1":
                break;
            case "2":
                droid = new RepairDroid(name, weapon);
                break;
            case "3":
                droid = new TankDroid(name, weapon);
                break;
            default: {
                System.out.println("Помилка! Обрано дроїда - піхотинець.");
            }
        }

        droids.add(droid);
        System.out.println("Створений дроїд: " + droid.brief());
    }

    private void listDroids() {
        if (droids.isEmpty()) {
            System.out.println("\n--------------------" +
                    "\nНі одного дроїду ще не було створено!");
            return;
        }

        System.out.println("\n------ Дроїди ------");
        for (int i = 0; i < droids.size(); i++) {
            System.out.println("(" + i + ") " + droids.get(i).detailed());
        }
    }

    private void deleteDroid() {
        listDroids();

        if (droids.isEmpty())
            return;

        System.out.print("Введіть якого дроїда видалити (-1 - вихід): ");
        int n = scanner.nextInt();

        if (n == -1)
            return;

        Droid removed = droids.remove(n);

        System.out.println("Видалено: " + removed.brief());
        scanner.nextLine();
    }

    private void battleOneOnOne() {
        if (droids.size() < MINIMUM_FOR_ONE_ON_ONE) {
            System.out.println("\n--------------------" +
                    "\nДля початку бою потрібно мати якнайменше 2 дроїда!");
            return;
        }

        listDroids();

        System.out.print("Оберіть команду A: ");
        int a = scanner.nextInt();

        System.out.print("Оберіть команду B: ");
        int b = scanner.nextInt();

        if (a == b) {
            System.out.println("Неможливо обрати повторно!");
            return;
        }

        Droid teamA = droids.get(a).copyForBattle();
        Droid teamB = droids.get(b).copyForBattle();

        Battle battle = new Battle(List.of(teamA), List.of(teamB));
        String log = battle.run();

        saveLastBattle(log);
        scanner.nextLine();
    }

    private void battleTeamOnTeam() {
        if (droids.size() < MINIMUM_FOR_TEAM_ON_TEAM) {
            System.out.println("\n--------------------" +
                    "\nДля початку бою потрібно мати щонайменше 6 дроїдів!");
            return;
        }

        listDroids();

        while (true) {
            System.out.println("Введіть індекси трьох дроїдів для команди A:");
            List<Integer> selectedA = new ArrayList<>();
            selectedA.add(scanner.nextInt());
            selectedA.add(scanner.nextInt());
            selectedA.add(scanner.nextInt());

            if (!areDroidsUnique(selectedA)) {
                System.out.println("Всі дроїди в команді A повинні бути унікальними!");
                continue;
            }

            System.out.println("Введіть індекси трьох дроїдів для команди B:");
            List<Integer> selectedB = new ArrayList<>();
            selectedB.add(scanner.nextInt());
            selectedB.add(scanner.nextInt());
            selectedB.add(scanner.nextInt());

            if (!areDroidsUnique(selectedB)) {
                System.out.println("Всі дроїди в команді B повинні бути унікальними!");
                continue;
            }

            if (!areTeamsUnique(selectedA, selectedB)) {
                System.out.println("Дроїди не повинні повторюватися між командами!");
                continue;
            }

            List<Droid> teamA = new ArrayList<>();
            List<Droid> teamB = new ArrayList<>();

            for (int i : selectedA)
                teamA.add(droids.get(i).copyForBattle());
            for (int i : selectedB)
                teamB.add(droids.get(i).copyForBattle());

            Battle battle = new Battle(teamA, teamB);
            String log = battle.run();

            saveLastBattle(log);
            scanner.nextLine();
            break;
        }
    }

    public boolean areDroidsUnique(List<Integer> list) {
        if (list == null || list.isEmpty())
            return false;

        Set<Integer> uniqueDroids = new HashSet<>(list);
        return uniqueDroids.size() == list.size();
    }

    public boolean areTeamsUnique(List<Integer> teamA, List<Integer> teamB) {
        for (int droid : teamA)
            if (teamB.contains(droid))
                return false;
        return true;
    }

    private void replayLastBattle() {
        System.out.println("\n--------------------");
        if (!Files.exists(lastBattle)) {
            System.out.println("Немає запису попереднього бою!");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(lastBattle);
            System.out.println("-- Попередній бій --");

            for (String l : lines)
                System.out.println(l);
        }
        catch (IOException e) {
            System.out.println("Помилка завантаження попередньої битви: " + e.getMessage());
        }
    }

    private void saveLastBattle(String log) {
        try {
            Files.writeString(lastBattle, log);
            System.out.println("Битву збережено");
        }
        catch (IOException e) {
            System.out.println("Помилка збереження битви: " + e.getMessage());
        }
    }
}
