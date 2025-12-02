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
 * Game  - tablainfo, jatekosok, lepesek, win-check, bot, usernev.
 */
public final class Game {

    /** Default tabla sorok szama. */
    private static final int DEFAULT_ROWS = 10;

    /** Default tabla oszlopok szama. */
    private static final int DEFAULT_COLS = 10;

    // tabla
    /** A jatektabla. */
    private final Board board;

    // ket jatekos index - 0 = X, 1 = O
    /** A ket jatekos tombje: 0 = X, 1 = O. */
    private final Player[] players = new Player[2];

    // eppen hanyas indexu jatekos jon (0 vagy 1)
    /** Eppen hanyas indexu jatekos jon (0 vagy 1). */
    private int currentIndex = 0;

    // randombot set
    /** Az aktualisan hasznalt bot implementacio. */
    private Bot bot = new RandomBot();

    // jatek vege, gyoztes
    /** Jatek vege flag. */
    private boolean gameOver;

    /** A gyoztes jatekos, vagy null ha meg nincs / patt. */
    private Player winner;

    /**
     * Alap konstruktorok - 10x10.
     */
    public Game() {
        this(DEFAULT_ROWS, DEFAULT_COLS, "Mr X");
    }

    /**
     * Altalanos konstruktor - custom size sor oszlop.
     *
     * @param rows sorok szama
     * @param cols oszlopok szama
     * @param playerXName player neve
     */
    public Game(final int rows, final int cols, final String playerXName) {
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
     * Privat konstruktor - betoltott tablahoz.
     *
     * @param loadedBoard betoltott tabla
     * @param nextSymbol kovetkezo jatekos jele
     * @param playerXName X jatekos neve
     */
    private Game(final Board loadedBoard,
                 final Symbol nextSymbol,
                 final String playerXName) {
        this.board = loadedBoard;

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
     * Betoltott jatek letrehozasa - GameSaveLoad utilra.
     *
     * @param board betoltott tabla
     * @param nextSymbol kovetkezo jatekos jele
     * @param playerXName X jatekos neve
     * @return uj Game a betoltott allapottal
     */
    public static Game fromLoadedState(final Board board,
                                       final Symbol nextSymbol,
                                       final String playerXName) {
        return new Game(board, nextSymbol, playerXName);
    }

    /**
     * Bot strategia allitasa - pl. teszthez vagy masik bothoz.
     *
     * @param botStrategy uj bot implementacio
     */
    public void setBot(final Bot botStrategy) {
        this.bot = botStrategy;
    }

    /**
     * Aktualis jatekos.
     *
     * @return az eppen kovetkezo jatekos
     */
    public Player getCurrentPlayer() {
        return players[currentIndex];
    }

    /**
     * getBoard.
     *
     * @return a jatektabla
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Patt ellenorzes: van-e barmi ervenyes lepes meg.
     *
     * @return true, ha nincs tobb ervenyes lepes
     */
    public boolean isStalemate() {
        return MoveRulesUtil.findValidMoves(board).isEmpty();
    }

    /**
     * Jatek vege flag.
     *
     * @return true, ha vege a jateknak
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Gyoztes lekerese -- null - nincs meg / patt.
     *
     * @return a gyoztes jatekos vagy null
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * X jatekos (index 0) – nev menteshez.
     *
     * @return az X jatekos
     */
    public Player getXPlayer() {
        return players[0];
    }

    /**
     * X kozepre, valtas O-ra.
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
     * Cella lekerese egy Position alapjan.
     *
     * @param pos melyik pozicion levo cella kell
     * @return a keresett Cell
     */
    private Cell getCellAt(final Position pos) {
        int rows = board.getRowSize();
        int cols = board.getColSize();

        int r = pos.getRow();
        int c = pos.getCol();

        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            String message = "Pozicio kivul esik a tablan: "
                    + "Sor=" + r + " Oszlop=" + c;
            throw new IllegalArgumentException(message);
        }

        int index = r * cols + c;
        return board.getCells().get(index);
    }

    /**
     * Move - karakter placement a boardra.
     *
     * @param input pl. "f5"
     * @return true ha sikeres a lepes, false ha nem
     */
    public boolean playOneMove(final String input) {
        if (gameOver) {
            return false;
        }

        Position pos;
        try {
            pos = PositionUtil.fromAlgebraic(input);
        } catch (IllegalArgumentException e) {
            // formatum hiba
            return false;
        }

        // tabla-hatar check
        if (MoveRulesUtil.isOutside(board, pos)) {
            return false;
        }

        // foglalt mezo
        if (!MoveRulesUtil.isEmpty(board, pos)) {
            return false;
        }

        // szomszed check - kiveve autoplacement midre
        if (!MoveRulesUtil.hasNeighbor(board, pos)) {
            return false;
        }

        Symbol symbol = getCurrentPlayer().getSelectedSymbol();
        Cell cell = getCellAt(pos);

        if (!board.setCellSymbol(cell, symbol)) {
            return false;
        }

        // win-check
        if (WinChecker.isWinningMove(board, pos, symbol)) {
            gameOver = true;
            winner = getCurrentPlayer();
        } else if (isStalemate()) {
            // sehol nincs tobb ervenyes lepes - patt
            gameOver = true;
            winner = null;
        }

        // ha meg nincs vege, jatekos valtasa
        if (!gameOver) {
            currentIndex = 1 - currentIndex;
        }

        return true;
    }

    /**
     * BOT lepes - RandomBottal.
     *
     * @return bot altal lepett pozicio, vagy null ha nem tud lepni
     */
    public Position botMove() {
        if (gameOver) {
            return null;
        }

        // bot csak O symbol korben lep
        if (getCurrentPlayer().getSelectedSymbol() != Symbol.O) {
            return null;
        }

        // Bot choose validra
        Position chosen = bot.chooseMove(board, Symbol.O);

        // pattkezeles
        if (chosen == null) {
            // nincs hova lépni - patt
            gameOver = true;
            winner = null; // döntetlen
            return null;
        }

        // jel lerakasa
        Cell cell = getCellAt(chosen);
        board.setCellSymbol(cell, Symbol.O);

        // win-check a bot lepesere
        if (WinChecker.isWinningMove(board, chosen, Symbol.O)) {
            gameOver = true;
            winner = getCurrentPlayer();
        } else if (isStalemate()) {
            // nincs tobb ervenyes lepes - patt
            gameOver = true;
            winner = null;
        }

        // ha meg nincs vege, jatekos valtasa
        if (!gameOver) {
            currentIndex = 1 - currentIndex;
        }

        return chosen;
    }
}
