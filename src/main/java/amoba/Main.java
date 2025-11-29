package amoba;

import amoba.board.Board;
import amoba.board.Position;
import amoba.enums.Symbol;
import amoba.game.Game;
import amoba.util.GameSaveLoad;
import amoba.util.PositionUtil;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        int rows = 10;
        int cols = 10;

        // ===========================
        // BOARD TESZT
        // ===========================
        System.out.println("Board teszt");

        Board board = new Board(rows, cols); // új tábla

        System.out.println("Üres tábla (koordinátákkal):");
        System.out.println(board.toStringWithCoords());


        // ===========================
        // GAME TESZT
        // ===========================
        System.out.println("\nGame teszt");

        Game g = new Game(rows, cols); // X középen, O jön

        System.out.println("Kezdő tábla (X középen):");
        System.out.println(g.getBoard().toStringWithCoords());
        System.out.println(g.getCurrentPlayer() + " következik\n");

        // 1. lépés: O lép egy szomszédos mezőre - f5
        String lep1 = "f5";
        boolean ok1 = g.playOneMove(lep1);
        System.out.println("Lépés: " + lep1 + ", sikeres? " + (ok1 ? "siker" : "nem sikerült"));
        System.out.println(g.getBoard().toStringWithCoords());
        System.out.println(g.getCurrentPlayer() + " következik\n");

        // 2. lépés: X lép egy másik szomszédra - f4
        String lep2 = "f4";
        boolean ok2 = g.playOneMove(lep2);
        System.out.println("Lépés: " + lep2 + ", sikeres? " + (ok2 ? "siker" : "nem sikerült"));
        System.out.println(g.getBoard().toStringWithCoords());
        System.out.println(g.getCurrentPlayer() + " következik\n");

        // BOT TESZT - botMove
        System.out.println("Bot lépése:");
        Position botPos = g.botMove();
        if (botPos != null) {
            System.out.println("Bot lépett: "
                    + PositionUtil.toAlgebraic(botPos) + "  " + botPos);
        } else {
            System.out.println("Bot nem tudott lépni.");
        }
        System.out.println(g.getBoard().toStringWithCoords());
        System.out.println(g.getCurrentPlayer() + " következik\n");

        // Szándékos failtest - foglalt mező trigger f5
        boolean ok = g.playOneMove(lep1);
        if (!ok) {
            System.out.println("Nem sikerült a lépés, mert foglalt: " + lep1);
        }

        // szomszéd nélküli lépés teszt a1 messze
        String input2 = "a1";
        boolean ok2b = g.playOneMove(input2);
        if (!ok2b) {
            System.out.println("Nem sikerült a lépés, mert nincs szomszéd: " + input2);
        }

        // új game auto-start
        System.out.println("\nGame auto-start teszt");
        Game game2 = new Game(rows, cols);
        System.out.println(game2.getBoard().toStringWithCoords());


        // ===========================
        // INTERAKTÍV GAME LOOP
        // ===========================
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nInteraktív játék (X = ember, O = bot)");
        System.out.println("Kilépéshez írd be: q\n");

        Game game3 = new Game(rows, cols); // új játék, tiszta tábla, X középen

        while (!game3.isGameOver()) {
            System.out.println(game3.getBoard().toStringWithCoords());
            System.out.println(game3.getCurrentPlayer() + " következik.");

            if (game3.getCurrentPlayer().getSelectedSymbol() == Symbol.X) {
                System.out.print("Add meg a lépésed (pl. f5), vagy 'q' kilépéshez: ");
                String line = scanner.nextLine().trim();

                if (line.equalsIgnoreCase("q")) {
                    System.out.println("Kilépés a játékból.");
                    break;
                }
                if (line.isEmpty()) {
                    System.out.println("Üres input, próbáld újra.");
                    continue;
                }

                boolean realok = game3.playOneMove(line);
                System.out.println("Lépés: " + line + (realok ? " siker" : " fail"));

                if (!realok) {
                    System.out.println("Érvénytelen lépés (foglalt mező vagy nincs szomszéd), próbáld újra.\n");
                }

            } else { // O - bot
                System.out.println("Bot lépése...");
                Position realbotPos = game3.botMove();

                if (realbotPos == null) {
                    System.out.println("A bot nem tud érvényeset lépni, patt helyzet (döntetlen).");
                    break;
                }

                String algebraic = PositionUtil.toAlgebraic(realbotPos);
                System.out.println("Bot ide lépett: " + algebraic + " " + realbotPos + "\n");
            }
        }

        // ha valódi győzelem miatt állt le
        if (game3.isGameOver()) {
            System.out.println("\nVégső tábla:");
            System.out.println(game3.getBoard().toStringWithCoords());
            if (game3.getWinner() != null) {
                System.out.println("Játék vége! Győztes: " + game3.getWinner());
            } else {
                System.out.println("Játék vége! Döntetlen.");
            }
        }


        // ===========================
        // SAVE / LOAD TESZT
        // ===========================
        System.out.println("\nMentés/betöltés gyors teszt");

        Game saveLoadTest = new Game(rows, cols); // 10x10

        System.out.println("Kezdő tábla (X középen):");
        System.out.println(saveLoadTest.getBoard().toStringWithCoords());

        System.out.println("Bot lép:");
        saveLoadTest.botMove();
        System.out.println(saveLoadTest.getBoard().toStringWithCoords());

        System.out.print("Adj meg egy lépést (pl. f5): ");
        String userStep = scanner.nextLine().trim();

        boolean okStep = saveLoadTest.playOneMove(userStep);
        System.out.println("Lépés eredménye: " + (okStep ? "siker" : "fail"));

        saveLoadTest.botMove();

        System.out.println("Tábla mentés előtt:");
        System.out.println(saveLoadTest.getBoard().toStringWithCoords());

        // mentés utilal
        GameSaveLoad.saveGame(saveLoadTest, "board.txt");
        System.out.println("Játék elmentve: board.txt");

        // betöltés utilal
        Game loadedGame = GameSaveLoad.loadGame("board.txt");

        System.out.println("Betöltött tábla:");
        System.out.println(loadedGame.getBoard().toStringWithCoords());
        System.out.println("Következő játékos (betöltve): " + loadedGame.getCurrentPlayer());

        System.out.print("Adj meg egy lépést (pl. f5): ");
        userStep = scanner.nextLine().trim();

        okStep = loadedGame.playOneMove(userStep);
        System.out.println("Lépés eredménye: " + (okStep ? "siker" : "fail"));

        System.out.println("Final tábla:");
        System.out.println(loadedGame.getBoard().toStringWithCoords());
        System.out.println("\nTeszt vége");
    }
}
