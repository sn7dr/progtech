package amoba;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private final Board board;
    private Player currentPlayer;
    private final List<Position> freePositions; // összes szabad mező
    private final Random rand = new Random();   // random a botnak

    // játék vége flag + győztes
    private boolean gameOver;         // true ha már vége a játéknak
    private Player winner;            // ha null, akkor még nincs győztes vagy patt

    // alapértelmezett játék - 10x10-es tábla
    public Game() {
        this(10, 10);
    }

    // általános konstruktor - tetszőleges N sor, M oszlop
    public Game(int rows, int cols) {
        this.board = new Board(rows, cols);
        this.currentPlayer = Player.X; // X kezd
        this.gameOver = false;
        this.winner = null;

        // összes mezőt betesszük egy listába szabadnak
        freePositions = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                freePositions.add(new Position(r, c));
            }
        }

        // automatikus kezdőlépés középre X-szel
        placeStartingX();
    }

    // középre helyezi az X-et - automata kezdés
    private void placeStartingX() {
        int centerRow = board.rows() / 2;
        int centerCol = board.cols() / 2;

        Position center = new Position(centerRow, centerCol);
        board.place(center, Player.X);    // X letesz
        freePositions.remove(center);     // kivesz a szabad listából

        currentPlayer = Player.O;         // következő player
    }
    // privát konstruktor - betöltött táblából
    private Game(Board board, Player currentPlayer) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.gameOver = false;
        this.winner = null;

        // szabad mezők újraszámolása
        this.freePositions = new ArrayList<>();
        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                Position p = new Position(r, c);
                if (board.isEmpty(p)) {
                    freePositions.add(p);
                }
            }
        }
    }
    // Legalább 1 szomszéd mező (8 irány) legyen foglalt (X vagy O)
    private boolean hasNeighbor(Position pos) {
        int[] dirs = {-1, 0, 1};

        for (int dx : dirs) {
            for (int dy : dirs) {
                if (dx == 0 && dy == 0) {
                    continue; // saját mező, ezt kihagyjuk
                }

                int nr = pos.row() + dx;
                int nc = pos.col() + dy;

                // ha kilóg a tábláról, kihagyjuk
                if (nr < 0 || nr >= board.rows()
                        || nc < 0 || nc >= board.cols()) {
                    continue;
                }

                Position neighbor = new Position(nr, nc);
                if (!board.isEmpty(neighbor)) {
                    return true; // találtunk nem üres szomszédot -> oké
                }
            }
        }

        return false; // sehol nincs szomszéd
    }

    // 5 egymás mellett ellenőrzése
    private boolean isWinningMove(Position pos, Player player) {
        // 4 irány - vertikális, horizontális, két átló (bal, jobb)
        int[][] directions = {
                {1, 0},   // függőleges
                {0, 1},   // vízszintes
                {1, 1},   // főátló
                {1, -1}   // mellékátló
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            // induláskor 1, mert maga a most lerakott bábu is számít
            int count = 1;

            // egyik irányba számolunk
            count += countDirection(pos, dx, dy, player);
            // ellentétes irányba is
            count += countDirection(pos, -dx, -dy, player);

            if (count >= 5) {
                return true; // találtunk legalább 5-öt egymás mellett
            }
        }
        return false;
    }

    // adott irányban megszámoljuk, hány ugyanilyen bábu van egymás után
    private int countDirection(Position start, int dx, int dy, Player player) {
        int r = start.row() + dx;
        int c = start.col() + dy;
        int count = 0;

        while (r >= 0 && r < board.rows()
                && c >= 0 && c < board.cols()) {

            Position p = new Position(r, c);
            Player cell = board.get(p);

            if (cell == player) {
                count++;
                r += dx;
                c += dy;
            } else {
                break; // más jel vagy üres - vége a sorozatnak
            }
        }

        return count;
    }

    // lépés próba
    public boolean playOneMove(String input) {
        // ha már vége a játéknak, nem lépünk tovább
        if (gameOver) {
            return false;
        }

        Position pos = Position.converter(input);

        // foglalt mező check
        if (!board.isEmpty(pos)) {
            return false;
        }

        // szomszéd check
        if (!hasNeighbor(pos)) {
            return false;
        }

        // minden pipa - letesszük a jelet
        board.place(pos, currentPlayer);

        // win-check a mostani lépésre
        if (isWinningMove(pos, currentPlayer)) {
            gameOver = true;
            winner = currentPlayer;
        }

        freePositions.remove(pos);   // már nem szabad ez a mező

        // játékos váltás - csak ha még nincs vége
        if (!gameOver) {
            currentPlayer = currentPlayer.next();
        }

        return true;
    }

    // BOT lép -  random választ az összes szabad hely közül - szomszédos
    public Position botMove() {

        // ha már vége, a bot sem lép
        if (gameOver) {
            return null;
        }

        // még1x check hogy a bot jön-e
        if (currentPlayer != Player.O) {
            return null;
        }

        // listbe az összes szabad mező + szomszéd check
        List<Position> validMoves = new ArrayList<>();
        for (Position p : freePositions) {
            if (hasNeighbor(p)) {        // szomszéd check
                validMoves.add(p);
            }
        }

        if (validMoves.isEmpty()) {
            return null; // nincs hova lépni patt
        }

        // választunk egy random indexet
        int index = rand.nextInt(validMoves.size());
        Position chosen = validMoves.get(index);

        // végrehajtjuk a lépést a táblán
        board.place(chosen, currentPlayer);

        // win-check a bot lépésére
        if (isWinningMove(chosen, currentPlayer)) {
            gameOver = true;
            winner = currentPlayer;
        }

        freePositions.remove(chosen);  // globális szabad-listából is kivesszük

        // kör vissza X-re - csak ha nincs vége
        if (!gameOver) {
            currentPlayer = currentPlayer.next();
        }

        return chosen; // visszaadjuk, hova lépett a bot (log, print)
    }
    // játék mentése fájlba - első sor: sorok space oszlopok space következő játékos (X vagy O)
    // utána \n és a tábla sorai jelekkel
    public void saveToFile(String path) {
        try {
            StringBuilder sb = new StringBuilder();

            // fejlec
            sb.append(board.rows())
                    .append(" ")
                    .append(board.cols())
                    .append(" ")
                    .append(currentPlayer)  // X vagy O
                    .append("\n");

            // tábla tartalma
            for (int r = 0; r < board.rows(); r++) {
                for (int c = 0; c < board.cols(); c++) {
                    Position p = new Position(r, c);
                    Player cell = board.get(p);
                    sb.append(cell == null ? "\u00B7" : cell.toString());
                }
                sb.append("\n");
            }

            java.nio.file.Files.writeString(java.nio.file.Path.of(path), sb.toString());

        } catch (Exception e) {
            throw new RuntimeException("Hiba a játék mentése közben: " + e.getMessage(), e);
        }
    }
    // játék betöltése fájlból
    public static Game loadFromFile(String path) {
        try {
            java.util.List<String> lines =
                    java.nio.file.Files.readAllLines(java.nio.file.Path.of(path));

            if (lines.isEmpty()) {
                throw new IllegalArgumentException("Üres fájl: " + path);
            }

            // első sor: sor, oszlop, kövi játékos
            String header = lines.get(0).trim();
            String[] parts = header.split("\\s+");
            if (parts.length < 3) {
                throw new IllegalArgumentException("Hibás fejléc sor: " + header);
            }

            int rows = Integer.parseInt(parts[0]);
            int cols = Integer.parseInt(parts[1]);
            Player nextPlayer = Player.valueOf(parts[2]); // "X" vagy "O"

            Board board = new Board(rows, cols);

            // tábla sorok betöltése
            for (int r = 0; r < rows; r++) {
                String line = lines.get(r + 1);
                if (line.length() < cols) {
                    throw new IllegalArgumentException("Túl rövid sor a fájlban: " + line);
                }
                for (int c = 0; c < cols; c++) {
                    char ch = line.charAt(c);
                    if (ch == 'X') {
                        board.place(new Position(r, c), Player.X);
                    } else if (ch == 'O') {
                        board.place(new Position(r, c), Player.O);
                    } else {
                        // bármi más jel ( üres pont, stb.) - üres
                    }
                }
            }

            // új Game object a betöltött táblával és a következő játékossal
            return new Game(board, nextPlayer);

        } catch (Exception e) {
            throw new RuntimeException("Hiba a játék betöltése közben: " + e.getMessage(), e);
        }
    }

    // getterek
    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    // játék vége getter
    public boolean isGameOver() {
        return gameOver;
    }

    // győztes getter - nullnál még nincs vége / patt
    public Player getWinner() {
        return winner;
    }
}
