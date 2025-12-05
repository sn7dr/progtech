package amoba;

import amoba.db.ScoreService;
import amoba.board.Position;
import amoba.enums.Symbol;
import amoba.game.Game;
import amoba.util.GameSaveLoad;
import amoba.util.PositionUtil;
import amoba.util.SaveInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Startmenu.
 *  1) Uj jatek (10x10)
 *  2) Uj jatek egyedi merettel
 *  3) Jatek betoltese (board.txt)
 *  q) Kilepes.
 *
 * Jatek kozben (X koreben):
 *  - lepes - input
 *  - save - board.txt-be
 *  - "q" -> kilepes.
 */
public final class Main {

    // mentes file neve
    /** Alapertelmezett mentes fajl. */
    private static final String DEFAULT_SAVE = "board.txt";

    /** Default tabla sorok szama. */
    private static final int DEFAULT_ROWS = 10;

    /** Default tabla oszlopok szama. */
    private static final int DEFAULT_COLS = 10;

    /** Minimalis tabla meret (sor/oszlop). */
    private static final int MIN_SIZE = 4;

    /** Maximalis tabla meret (sor). */
    private static final int MAX_SIZE = 25;

    private Main() {
        // utility / entry class, ne peldanyositsuk
    }

    /**
     * Belepes pontja.
     *
     * @param args parancssori argumentumok (nem hasznaljuk)
     */
    public static void main(final String[] args) {

        // repo up meg DB init
        ScoreService scoreService = new ScoreService();

        Scanner sc = new Scanner(System.in);

        Game game = showStartMenuAndCreateGame(sc, scoreService);
        if (game == null) {
            // user kilepett a menubol
            return;
        }

        runGameLoop(sc, game);
        handleGameEnd(game, scoreService);
    }

    /**
     * Startmenu kirajzolasa es jatek letrehozasa / betoltese.
     *
     * @param sc scanner a konzol inputhoz
     * @param scoreService leaderboardhoz logikai service
     * @return letrehozott vagy betoltott Game, vagy null ha q a choice
     */
    private static Game showStartMenuAndCreateGame(
            final Scanner sc,
            final ScoreService scoreService) {

        // mentes info a menuhoz
        SaveInfo info = GameSaveLoad.getSaveInfo(DEFAULT_SAVE);

        printMainMenu(info);

        Game game = null;

        // startmenu loop
        while (game == null) {
            String choice = sc.nextLine().trim().toLowerCase();

            switch (choice) {
                case "1":
                    // default 10x10
                    String defaultName = askPlayerName(sc);
                    game = new Game(DEFAULT_ROWS, DEFAULT_COLS, defaultName);
                    System.out.println("\nUj jatek letrehozva (10x10). "
                            + "X kozepen kezd.");
                    break;

                case "2":
                    // egyedi meret
                    game = createCustomGame(sc);
                    if (game == null) {
                        // ha nem sikerult maradunk a menuben
                        System.out.print(
                                "Nem sikerult egyedi jatekot letrehozni. "
                                        + "Valassz ujra: "
                        );
                    } else {
                        System.out.println("\nUj jatek egyedi merettel");
                        System.out.println("Tabla meret: "
                                + game.getBoard().getRowSize()
                                + " x "
                                + game.getBoard().getColSize());
                    }
                    break;

                case "3":
                    // csak akkor ertelmes, ha van mentes
                    if (!info.exists()) {
                        System.out.print("Nincs elerheto mentes. ");
                        break;
                    }

                    // loadgame
                    game = tryLoadGame(DEFAULT_SAVE);
                    if (game != null) {

                        System.out.println("Jatek sikeresen betoltve – "
                                + "welcome back, "
                                + game.getXPlayer().getName()
                                + "!");

                        System.out.println(" "
                                + "Last saved at: " + info.lastModified());
                        System.out.println(
                                game.getBoard().toStringWithCoords());
                    } else {
                        System.out.println(
                                "Nincs elerheto mentes vagy hiba tortent. ("
                                        + DEFAULT_SAVE
                                        + ")"
                        );
                        System.out.print("Valassz ujra: ");
                    }
                    break;

                case "4":
                    // csak leaderboard megjelenitese, jatek meg nincs
                    scoreService.printHighScores();
                    // utana ujra kirajzoljuk a menut
                    printMainMenu(info);
                    break;

                case "q":
                    System.out.println("Kilepes...");
                    return null;

                default:
                    System.out.print("Hibas valasztas, probald ujra: ");
                    break;
            }
        }

        return game;
    }

    /**
     * Fomenu kiirasa, mentes infoval.
     *
     * @param info mentes meta-informacio
     */
    private static void printMainMenu(final SaveInfo info) {
        System.out.println("Main menu");
        System.out.println("1) Uj jatek (10x10)");
        System.out.println("2) Uj jatek egyedi merettel");

        if (info.exists()) {
            System.out.println("3) Jatek betoltese (Utolso mentes: "
                    + info.lastModified()
                    + ", Jatekos: "
                    + info.playerName()
                    + ")");
        }

        System.out.println("4) Leaderboard");

        System.out.println("q) Kilepes");
        System.out.print("Valassz: ");
    }

    /**
     * Teljes game loop: user + bot lepesek, amig vege nincs.
     *
     * @param sc   scanner a konzol inputhoz
     * @param game az aktualis jatek
     */
    private static void runGameLoop(final Scanner sc, final Game game) {
        // ===========================
        // GAME LOOP
        // ===========================
        while (!game.isGameOver()) {

            System.out.println(game.getBoard().toStringWithCoords());
            System.out.println("Kovetkezo jatekos: " + game.getCurrentPlayer());

            // user
            if (game.getCurrentPlayer().getSelectedSymbol() == Symbol.X) {

                System.out.print("Lepes (pl. f5), vagy 'q' kilepeshez: ");
                String input = sc.nextLine().trim();

                // kilepes - megerosites
                if (input.equalsIgnoreCase("q")) {

                    System.out.print("Biztos kilepsz? (y/n): ");
                    String confirm = sc.nextLine().trim().toLowerCase();

                    if (!"y".equals(confirm)) {
                        System.out.println("Kilepes megszakitva.");
                        continue; // vissza a jatekba
                    }

                    // mentes kerese
                    System.out.print(
                            "Szeretned menteni a jatekot kilepes elott? (y/n): "
                    );
                    String saveConfirm = sc.nextLine().trim().toLowerCase();

                    if ("y".equals(saveConfirm)) {
                        handleSave(game);
                        System.out.println("Jatek elmentve.");
                    }

                    System.out.println("Kilepes...");
                    return;
                }

                // normal lepes
                boolean ok = game.playOneMove(input);
                if (!ok) {
                    System.out.println(
                            "Ervenytelen lepes! (foglalt / nincs szomszed / "
                                    + "hibas formatum)"
                    );
                    continue; // vissza a kovetkezo korre, patt-check ne fusson
                }

                // kulon patt kiiras userre
                if (game.isGameOver() && game.getWinner() == null) {
                    System.out.println("Nincs tobb ervenyes lepes – patt!");
                    break;
                }

            } else {
                // bot
                System.out.println("Bot lep...");
                Position botPos = game.botMove();

                if (botPos == null) {
                    System.out.println(
                            "A bot nem tud ervenyeset lepni (patt)."
                    );
                    break;
                }

                String alg = PositionUtil.toAlgebraic(botPos);
                System.out.println("Bot ide lepett: " + alg + "  " + botPos);
            }
        }
    }

    /**
     * Jatek vege utani kezeles: mentes torles, vegso tabla, gyoztes kiirasa.
     *
     * @param game         az aktualis jatek
     * @param scoreService leaderboardhoz logikai service
     */
    private static void handleGameEnd(final Game game,
                                      final ScoreService scoreService) {
        // ===========================
        // GAME END
        // ===========================

        // ha a jatek NINCS vege NE toroljuk a mentest
        if (!game.isGameOver()) {
            System.out.println(
                    "\nJatek megszakitva. A mentes (ha keszult) megmarad: "
                            + DEFAULT_SAVE
            );
            return;
        }

        // ide csak akkor jutunk, ha gameOver = true (win / patt)
        try {
            Files.deleteIfExists(Path.of(DEFAULT_SAVE));
            System.out.println(
                    "(Automatikus mentestorles: "
                            + DEFAULT_SAVE
                            + ")"
            );
        } catch (IOException e) {
            System.out.println(
                    "Nem sikerult torolni a mentesfajlt: " + e.getMessage()
            );
        }

        System.out.println("\nGG");
        System.out.println(game.getBoard().toStringWithCoords());

        if (game.getWinner() != null) {
            System.out.println("Gyoztes: " + game.getWinner());
            scoreService.recordGameResult(game);
        } else {
            System.out.println("Dontetlen.");
        }
    }

    /**
     * Player nev bekerese, ha ures akkor "Mr X".
     *
     * @param sc scanner a konzol inputhoz
     * @return a jatekos neve vagy "Mr X"
     */
    private static String askPlayerName(final Scanner sc) {
        System.out.print("Add meg a nevedet : ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            name = "Mr X";
        }
        return name;
    }

    /**
     * Egyedi meretu jatek letrehozasa.
     * Bekerjuk a sor / oszlop szamot es az X jatekos nevet.
     *  4 <= M <= N <= 25.
     *
     * @param sc scanner a konzol inputhoz
     * @return Game vagy null, ha nem sikerult
     */
    private static Game createCustomGame(final Scanner sc) {
        try {
            String playerName = askPlayerName(sc);

            System.out.print(
                    "Add meg a sorok szamat (N, "
                            + MIN_SIZE
                            + "-"
                            + MAX_SIZE
                            + "): "
            );
            String rowsLine = sc.nextLine().trim();
            int rows = Integer.parseInt(rowsLine);

            System.out.print(
                    "Add meg az oszlopok szamat (M, "
                            + MIN_SIZE
                            + "-"
                            + rows
                            + "): "
            );
            String colsLine = sc.nextLine().trim();
            int cols = Integer.parseInt(colsLine);

            // Game konstruktor
            return new Game(rows, cols, playerName);

        } catch (Exception e) {
            System.out.println(
                    "Hibas meret ("
                            + MIN_SIZE
                            + " <= sor <= oszlop <= "
                            + MAX_SIZE
                            + "). "
                            + e.getMessage()
            );
            return null;
        }
    }

    /**
     * Mentes fix DEFAULT_SAVE file-ba.
     *
     * @param game melyik jatekot mentjuk
     */
    private static void handleSave(final Game game) {
        try {
            GameSaveLoad.saveGame(game, DEFAULT_SAVE);
            System.out.println("Jatek elmentve: " + DEFAULT_SAVE);
        } catch (Exception e) {
            System.out.println("Hiba a menteskor: " + e.getMessage());
        }
    }

    /**
     * Betoltes megadott filebol - hiba eseten null.
     *
     * @param filename mentes fajl neve
     * @return betoltott Game vagy null
     */
    private static Game tryLoadGame(final String filename) {
        try {
            return GameSaveLoad.loadGame(filename);
        } catch (Exception e) {
            return null;
        }
    }
}
