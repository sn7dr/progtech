package amoba;

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
 * Startmenü
 *  1) Új játék (10x10)
 *  2) Új játék egyedi mérettel
 *  3) Játék betöltése (board.txt)
 *  q) Kilépés
 *
 * Játék közben (X körében):
 *  - lépés - input
 *  - save -  board.txt-be
 *  - "q" -> kilépés
 */
public class Main {

    //  mentés file neve
    private static final String DEFAULT_SAVE = "board.txt";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // mentés info a menühöz
        SaveInfo info = GameSaveLoad.getSaveInfo(DEFAULT_SAVE);

        System.out.println("Main menu");
        System.out.println("1) Új játék (10x10)");
        System.out.println("2) Új játék egyedi mérettel");

        if (info.exists()) {
            System.out.println("3) Játék betöltése (Utolsó mentés: "
                    + info.lastModified() + ", Játékos: " + info.playerName() + ")");
        }

        System.out.println("q) Kilépés");
        System.out.print("Válassz: ");

        Game game = null;

        // startmenu loop
        while (game == null) {
            String choice = sc.nextLine().trim().toLowerCase();

            switch (choice) {
                case "1":
                    // default 10x10
                    String defaultName = askPlayerName(sc);
                    game = new Game(10, 10, defaultName);
                    System.out.println("\nÚj játék létrehozva (10x10). X középen kezd.");
                    break;

                case "2":
                    // egyedi méret
                    game = createCustomGame(sc);
                    if (game == null) {
                        // ha nem sikerült (pl. user elrontotta és kilépett), maradunk a menüben
                        System.out.print("Nem sikerült egyedi játékot létrehozni. Válassz újra: ");
                    } else {
                        System.out.println("\nÚj játék létrehozva egyedi mérettel.");
                        System.out.println("Tábla méret: " + game.getBoard().getRowSize()
                                + " x " + game.getBoard().getColSize());
                    }
                    break;

                case "3":
                    // csak akkor értelmes, ha van mentés
                    if (!info.exists()) {
                        System.out.print("Nincs elérhető mentés. Válassz mást: ");
                        break;
                    }

                    //loadgame
                    game = tryLoadGame(DEFAULT_SAVE);
                    if (game != null) {

                        System.out.println("Játék sikeresen betöltve – welcome back, "
                                + game.getXPlayer().getName() + "!");

                        System.out.println("Last saved at: " + info.lastModified());

                        System.out.println(game.getBoard().toStringWithCoords());
                    } else {
                        System.out.println("Nincs elérhető mentés vagy hiba történt. (" + DEFAULT_SAVE + ")");
                        System.out.print("Válassz újra: ");
                    }
                    break;

                case "q":
                    System.out.println("Kilépés...");
                    return;

                default:
                    System.out.print("Hibás választás, próbáld újra: ");
            }
        }

        // ===========================
        // GAME LOOP
        // ===========================
        while (!game.isGameOver()) {

            System.out.println(game.getBoard().toStringWithCoords());
            System.out.println("Következő játékos: " + game.getCurrentPlayer());

            // user
            if (game.getCurrentPlayer().getSelectedSymbol() == Symbol.X) {

                System.out.print("Lépés (pl. f5), vagy 'q' kilépéshez: ");
                String input = sc.nextLine().trim();

                // kilépés - megerősítés
                if (input.equalsIgnoreCase("q")) {

                    System.out.print("Biztos kilépsz? (y/n): ");
                    String confirm = sc.nextLine().trim().toLowerCase();

                    if (!confirm.equals("y")) {
                        System.out.println("Kilépés megszakítva.");
                        continue; // vissza a játékba
                    }

                    // mentés kérdése
                    System.out.print("Szeretnéd menteni a játékot kilépés előtt? (y/n): ");
                    String saveConfirm = sc.nextLine().trim().toLowerCase();

                    if (saveConfirm.equals("y")) {
                        handleSave(game);
                        System.out.println("Játék elmentve.");
                    }

                    System.out.println("Kilépés...");
                    return;
                }

                // normál lépés
                boolean ok = game.playOneMove(input);
                if (!ok) {
                    System.out.println("Érvénytelen lépés! (foglalt / nincs szomszéd / hibás formátum)");
                }
            }

            // bot
            else {
                System.out.println("Bot lép...");
                Position botPos = game.botMove();

                if (botPos == null) {
                    System.out.println("A bot nem tud érvényeset lépni (patt).");
                    break;
                }

                String alg = PositionUtil.toAlgebraic(botPos);
                System.out.println("Bot ide lépett: " + alg + "  " + botPos);
            }
        }

        // ===========================
        // GAME END
        // ===========================

        try {
            Files.deleteIfExists(Path.of(DEFAULT_SAVE));
            System.out.println("(Automatikus mentéstörlés: " + DEFAULT_SAVE + ")");
        } catch (IOException e) {
            System.out.println("Nem sikerült törölni a mentésfájlt: " + e.getMessage());
        }

        System.out.println("\nGG");
        System.out.println(game.getBoard().toStringWithCoords());

        if (game.getWinner() != null) {
            System.out.println("Győztes: " + game.getWinner());
        } else {
            System.out.println("Döntetlen.");
        }
    }

    /**
     * Player név bekérése, ha üres Mr X
     */
    private static String askPlayerName(Scanner sc) {
        System.out.print("Add meg a nevedet : ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            name = "Mr X";
        }
        return name;
    }

    /**
     * Egyedi méretű játék létrehozása.
     * Bekéri a sor / oszlop számot és az X játékos nevét.
     *  4 <= M <= N <= 25
     *
     * @param sc scanner a konzol inputhoz
     * @return Game vagy null, ha nem sikerült
     */
    private static Game createCustomGame(Scanner sc) {
        try {
            String playerName = askPlayerName(sc);

            System.out.print("Add meg a sorok számát (N, 4-25): ");
            String rowsLine = sc.nextLine().trim();
            int rows = Integer.parseInt(rowsLine);

            System.out.print("Add meg az oszlopok számát (M, 4-" + rows + "): ");
            String colsLine = sc.nextLine().trim();
            int cols = Integer.parseInt(colsLine);

            // Game konstruktor
            return new Game(rows, cols, playerName);

        } catch (Exception e) {
            System.out.println("Hibás méret (4 <= sor <= oszlop <= 25). " + e.getMessage());
            return null;
        }
    }

    /**
     * Mentés fix DEFAULT_SAVE file-ba.
     */
    private static void handleSave(Game game) {
        try {
            GameSaveLoad.saveGame(game, DEFAULT_SAVE);
            System.out.println("Játék elmentve: " + DEFAULT_SAVE);
        } catch (Exception e) {
            System.out.println("Hiba a mentéskor: " + e.getMessage());
        }
    }

    /**
     * Betöltés megadott fileból - hiba esetén null.
     */
    private static Game tryLoadGame(String filename) {
        try {
            return GameSaveLoad.loadGame(filename);
        } catch (Exception e) {
            return null;
        }
    }
}
