package amoba.util;

import amoba.board.Board;
import amoba.board.Cell;
import amoba.enums.Symbol;
import amoba.game.Game;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Game save / load util
 * Itt van a fajlkezeles, a Game csak a jatekmenettel foglalkozik.
 */
public class GameSaveLoad {

    /**
     * Jatek mentese fileba
     * Formatum:
     *  elso sor:  rows cols nextSymbol playerName
     *  utana soronkent a tabla X,O,· jelekkel
     *
     * @param game melyik jatekot mentjuk
     * @param path fajlnev "board.txt"
     */
    public static void saveGame(Game game, String path) {
        try {
            Board board = game.getBoard();
            Symbol nextSymbol = game.getCurrentPlayer().getSelectedSymbol();

            // X játékos neve - headerbe
            String playerName = game.getXPlayer().getName();

            StringBuilder sb = new StringBuilder();

            // 1 sor a txtben - sor, oszlop, kov jatekos, playerName
            sb.append(board.getRowSize())
                    .append(" ")
                    .append(board.getColSize())
                    .append(" ")
                    .append(nextSymbol.name()) // "X" vagy "O"
                    .append(" ")
                    .append(playerName)
                    .append("\n");

            // tabla tartalma
            for (int r = 0; r < board.getRowSize(); r++) {
                for (int c = 0; c < board.getColSize(); c++) {
                    int index = r * board.getColSize() + c;
                    Cell cell = board.getCells().get(index);
                    Symbol s = cell.getSymbol();

                    if (s == Symbol.X) {
                        sb.append('X');
                    } else if (s == Symbol.O) {
                        sb.append('O');
                    } else {
                        //noinspection UnnecessaryUnicodeEscape
                        sb.append('\u00B7'); // EMPTY
                    }
                }
                sb.append("\n");
            }

            Files.writeString(Path.of(path), sb.toString());

        } catch (Exception e) {
            throw new RuntimeException("Hiba a jatek mentese kozben: " + e.getMessage(), e);
        }
    }

    /**
     * Jatek betoltese filbol
     * Formatum:
     *  elso sor:  rows cols nextSymbol playerName
     *  utana soronkent a tabla X,O,· jelekkel
     *
     * @param path fajlnev "board.txt"
     * @return uj Game objektum a betoltott allapottal
     */
    public static Game loadGame(String path) {
        try {
            List<String> lines = Files.readAllLines(Path.of(path));

            if (lines.isEmpty()) {
                throw new IllegalArgumentException("Ures file: " + path);
            }

            // elso sor: size + kovi jatekos + nev
            String header = lines.get(0).trim();
            String[] parts = header.split("\\s+");
            if (parts.length < 3) {
                throw new IllegalArgumentException("Hibas elso sor: " + header);
            }

            int rows = Integer.parseInt(parts[0]);
            int cols = Integer.parseInt(parts[1]);
            Symbol nextSymbol = Symbol.valueOf(parts[2]); // "X" vagy "O"

            // playerName (ha a headerben benne van)
            String playerName = "Mr X";
            if (parts.length >= 4) {
                StringBuilder nameBuilder = new StringBuilder();
                for (int i = 3; i < parts.length; i++) {
                    if (i > 3) {
                        nameBuilder.append(" ");
                    }
                    nameBuilder.append(parts[i]);
                }
                playerName = nameBuilder.toString();
            }

            Board board = new Board(rows, cols);

            // tabla sorok
            for (int r = 0; r < rows; r++) {
                String line = lines.get(r + 1);
                if (line.length() < cols) {
                    throw new IllegalArgumentException("Tul rovid sor a fajlban: " + line);
                }

                for (int c = 0; c < cols; c++) {
                    char ch = line.charAt(c);

                    int index = r * cols + c;
                    Cell cell = board.getCells().get(index);

                    if (ch == 'X') {
                        board.setCellSymbol(cell, Symbol.X);
                    } else if (ch == 'O') {
                        board.setCellSymbol(cell, Symbol.O);
                    } else {
                        // minden mas: ures marad (EMPTY)
                    }
                }
            }

            // Game fromLoadedState - nem dob be uj Xt kozepre, nev visszatoltve
            return Game.fromLoadedState(board, nextSymbol, playerName);

        } catch (Exception e) {
            throw new RuntimeException("Hiba a jatek betoltese kozben: " + e.getMessage(), e);
        }
    }

    /**
     * Mentés meta-infó lekérése a főmenühöz:
     *  - létezik-e a file
     *  - utolsó mentés ideje
     *  - X játékos neve
     */
    public static SaveInfo getSaveInfo(String path) {
        try {
            Path p = Path.of(path);

            if (!Files.exists(p)) {
                return new SaveInfo(false, null, null);
            }

            long lastMod = Files.getLastModifiedTime(p).toMillis();
            String formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastMod);

            List<String> lines = Files.readAllLines(p);
            if (lines.isEmpty()) {
                return new SaveInfo(false, null, null);
            }

            String header = lines.get(0).trim();
            String[] parts = header.split("\\s+");

            String playerName = "Mr X";
            if (parts.length >= 4) {
                StringBuilder sb = new StringBuilder();
                for (int i = 3; i < parts.length; i++) {
                    if (i > 3) {
                        sb.append(" ");
                    }
                    sb.append(parts[i]);
                }
                playerName = sb.toString();
            }

            return new SaveInfo(true, formatted, playerName);

        } catch (Exception e) {
            return new SaveInfo(false, null, null);
        }
    }
}
