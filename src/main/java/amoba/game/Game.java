package amoba.game;

import amoba.board.Board;
import amoba.board.Cell;
import amoba.board.Position;
import amoba.bot.Bot;
import amoba.bot.RandomBot;
import amoba.enums.Symbol;
import amoba.user.Player;
import amoba.util.MoveRulesUtil;
import amoba.util.PositionUtil;

/**
 * Game  - tablainfo, jatekosok, lepesek, win-check, bot, usernév
 */
public class Game {

    // tabla
    private final Board board;

    // ket jatekos index - 0 = X, 1 = O
    private final Player[] players = new Player[2];

    // eppen hanyas indexu jatekos jon (0 vagy 1)
    private int currentIndex = 0;

    // randombot set
    private Bot bot = new RandomBot();

    // jatek vege, gyoztes
    private boolean gameOver;
    private Player winner;

    /**
     * Alap konstruktorok - 10x10
     */
    public Game() {
        this(10, 10, "Mr X");

    }

    /**
     * Altalanos konstruktor - custom siz sor oszlop
     *
     * @param rows sorok szama
     * @param cols oszlopok szama
     * @param playerXName player neve
     */
    public Game(int rows, int cols, String playerXName) {
        this.board = new Board(rows, cols);

        String effectiveName = (playerXName == null || playerXName.isBlank())
                ? "Mr X"
                : playerXName.trim();

        players[0] = new Player(effectiveName, Symbol.X, 0);
        players[1] = new Player("Mr Robot", Symbol.O, 0);

        this.gameOver = false;
        this.winner = null;

        placeStartingX();
    }


    /**
     * Privat konstruktor - betoltott tablahoz
     */
    private Game(Board board, Symbol nextSymbol, String playerXName) {
        this.board = board;

        String effectiveName = (playerXName == null || playerXName.isBlank())
                ? "Mr X"
                : playerXName.trim();

        players[0] = new Player(effectiveName, Symbol.X, 0);
        players[1] = new Player("Mr Robot", Symbol.O, 0);

        this.gameOver = false;
        this.winner = null;

        // kovetkezo jatekos - X vagy O
        this.currentIndex = (nextSymbol == Symbol.X) ? 0 : 1;
    }


    /**
     * Betoltott jatek letrehozasa - GameSaveLoad utilra
     */
    public static Game fromLoadedState(Board board, Symbol nextSymbol, String playerXName) {
        return new Game(board, nextSymbol, playerXName);
    }

    /**
     * Bot strategia allitasa - pl. teszthez vagy masik bothoz
     */
    public void setBot(Bot bot) {
        this.bot = bot;
    }

    /**
     * Aktualis jatekos
     */
    public Player getCurrentPlayer() {
        return players[currentIndex];
    }

    /**
     * getboard
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Jatek vege flag
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Gyoztes lekerese -- null - nincs még / patt
     */
    public Player getWinner() {
        return winner;
    }
    /**
     * X játékos (index 0) – név mentéshez
     */
    public Player getXPlayer() {
        return players[0];
    }

    /**
     * X kozepre, valtas O ra
     */
    private void placeStartingX() {
        int centerRow = board.getRowSize() / 2;
        int centerCol = board.getColSize() / 2;

        Position center = new Position(centerRow, centerCol);

        // sajat helperrel kerjuk le a cellat
        Cell centerCell = getCellAt(center);

        board.setCellSymbol(centerCell, Symbol.X);

        // utana O
        currentIndex = 1;
    }

    /**
     * Cella lekerese egy Position alapjan
     */
    private Cell getCellAt(Position pos) {
        int rows = board.getRowSize();
        int cols = board.getColSize();

        int r = pos.getRow();
        int c = pos.getCol();

        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            throw new IllegalArgumentException("Pozicio kivul esik a tablan: Sor=" + r + " Oszlop=" + c);
        }

        int index = r * cols + c;
        return board.getCells().get(index);
    }

    /**
     * Move - karakter placement a boardra
     *
     * @param input pl f5
     * @return true ha sikeres a lepes, false ha nem
     */
    public boolean playOneMove(String input) {
        if (gameOver) {
            return false;
        }

        Position pos;
        try {
            pos = PositionUtil.fromAlgebraic(input);
        } catch (IllegalArgumentException e) {
            return false; // formatum hiba
        }

        // tabla-hatar check
        if (MoveRulesUtil.isOutside(board, pos)) {
            return false;
        }

        // foglalt mezo
        if (!MoveRulesUtil.isEmpty(board, pos)) {
            return false;
        }

        // szomszed check - kivéve autoplacement midre
        if (!MoveRulesUtil.hasNeighbor(board, pos)) {
            return false;
        }

        Symbol symbol = getCurrentPlayer().getSelectedSymbol();
        Cell cell = getCellAt(pos);
        boolean ok = board.setCellSymbol(cell, symbol);
        if (!ok) {
            return false;
        }

        // win-check
        if (WinChecker.isWinningMove(board, pos, symbol)) {
            gameOver = true;
            winner = getCurrentPlayer();
        }

        // ha meg nincs vege, jatekos valtasa
        if (!gameOver) {
            currentIndex = 1 - currentIndex;
        }

        return true;
    }

    /**
     * BOT lepes -  RandomBottal
     *
     * @return bot cell, vagy null ha nem tud lepni
     */
    public Position botMove() {
        if (gameOver) {
            return null;
        }

        // bot csak O symbol körnél lép
        if (getCurrentPlayer().getSelectedSymbol() != Symbol.O) {
            return null;
        }

        // Bot choose validra
        Position chosen = bot.chooseMove(board, Symbol.O);

        if (chosen == null) {
            // patt - nincs hova lepni - validMoves ures
            return null;
        }

        // jel lerakasa
        Cell cell = getCellAt(chosen);
        board.setCellSymbol(cell, Symbol.O);

        // win-check a bot lepesere
        if (WinChecker.isWinningMove(board, chosen, Symbol.O)) {
            gameOver = true;
            winner = getCurrentPlayer();
        }

        // ha meg nincs vege, jatekos valtasa
        if (!gameOver) {
            currentIndex = 1 - currentIndex;
        }

        return chosen;
    }
}
