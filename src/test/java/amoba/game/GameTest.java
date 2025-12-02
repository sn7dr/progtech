package amoba.game;

import amoba.board.Board;
import amoba.board.Cell;
import amoba.board.Position;
import amoba.enums.Symbol;
import amoba.util.PositionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Game osztaly viselkedes tesztek.
 */
class GameTest {

    /**
     * Helper – cella lekerese sor + oszlop alapjan.
     * (index formula: row * colSize + col)
     */
    private Cell getCell(final Board board, final int row, final int col) {
        int index = row * board.getColSize() + col;
        return board.getCells().get(index);
    }

    @Test
    void testValidMovePlacesSymbolAndSwitchesPlayer() {
        // arrange – default game: 10x10, X kozepen, O jon
        Game game = new Game(10, 10, "Alice");

        assertEquals(Symbol.O,
                game.getCurrentPlayer().getSelectedSymbol(),
                "Defaultban O jon, miutan X kozepre kerult.");

        // act – O lep egy szomszedos mezore (f5)
        boolean ok = game.playOneMove("f5");

        // assert
        assertTrue(ok, "A lepesnek ervenyesnek kell lennie.");

        Position pos = PositionUtil.fromAlgebraic("f5");
        Cell cell = getCell(game.getBoard(), pos.getRow(), pos.getCol());
        assertEquals(Symbol.O, cell.getSymbol(),
                "A mezore O-nak kell kerulnie.");

        // kovetkezo jatekos mar X
        assertEquals(Symbol.X,
                game.getCurrentPlayer().getSelectedSymbol(),
                "Lepes utan vissza kell valtani X-re.");
    }

    @Test
    void testInvalidFormatReturnsFalse() {
        // arrange
        Game game = new Game(10, 10, "Alice");

        // act
        boolean ok = game.playOneMove("xyz"); // rossz formatum

        // assert
        assertFalse(ok, "Rossz input formatumra false-t kell adni.");
    }

    @Test
    void testMoveOutsideBoardReturnsFalse() {
        // arrange – kissebb tabla
        Game game = new Game(4, 4, "Bob");

        // act – e5 kivul esik a 4x4-en
        boolean ok = game.playOneMove("e5");

        // assert
        assertFalse(ok, "Tablan kivuli lepesre false kell visszaterni.");
    }

    @Test
    void testMoveOnOccupiedCellReturnsFalse() {
        // arrange – default game, X mar kozepen (f6)
        Game game = new Game(10, 10, "Alice");

        // act – O probal ugyanoda lepni
        boolean ok = game.playOneMove("f6");

        // assert
        assertFalse(ok, "Foglalt mezore nem lehet lepni.");
    }

    @Test
    void testMoveWithoutNeighborReturnsFalse() {
        // arrange – ures tabla, kovetkezo jatekos X (nincs szomszed)
        Board board = new Board(10, 10);
        Game game = Game.fromLoadedState(board, Symbol.X, "Alice");

        // act – barmelyik lepes szomszed nelkul
        boolean ok = game.playOneMove("f5");

        // assert
        assertFalse(ok,
                "Szomszed nelkuli lepesre false-t kell adni (rule).");
    }

    @Test
    void testWinningMoveSetsGameOverAndWinner() {
        // arrange – boardon mar 4 X egymas mellett, most jon az 5.
        Board board = new Board(10, 10);

        // sor 0, oszlop 0..3 = X
        board.setCellSymbol(getCell(board, 0, 0), Symbol.X);
        board.setCellSymbol(getCell(board, 0, 1), Symbol.X);
        board.setCellSymbol(getCell(board, 0, 2), Symbol.X);
        board.setCellSymbol(getCell(board, 0, 3), Symbol.X);

        // jatek betoltott allapotbol, kovetkezo X jon
        Game game = Game.fromLoadedState(board, Symbol.X, "Winner");

        // act – X lep az 5. mezore (e1 -> row 0, col 4)
        boolean ok = game.playOneMove("e1");

        // assert
        assertTrue(ok, "Az 5. X lerakasa ervenyes lepes.");
        assertTrue(game.isGameOver(), "Win utan gameOver-nek true-nak kell lennie.");
        assertNotNull(game.getWinner(), "Gyoztesnek nem szabad nullnak lennie.");
        assertEquals("Winner", game.getWinner().getName(),
                "A gyoztesnek az X jatekosnak kell lennie.");
        assertEquals(Symbol.X,
                game.getWinner().getSelectedSymbol(),
                "A gyoztes jel X kell legyen.");
    }

    @Test
    void testBotMoveDoesNothingWhenNotOBTurn() {
        // arrange – default game: X kozepen, O jon
        Game game = new Game(10, 10, "Alice");

        // itt epp O jon, csinalunk egy lepest, hogy visszavaltson X-re
        boolean ok = game.playOneMove("f5");
        assertTrue(ok);
        assertEquals(Symbol.X,
                game.getCurrentPlayer().getSelectedSymbol(),
                "Most X kore kovetkezik.");

        // act – botMove X korben
        Position botPos = game.botMove();

        // assert
        assertNull(botPos, "Ha nem O jon, a bot nem lephet.");
    }
    @Test
    void testConstructorReplacesBlankNameWithDefault() {
        Game game = new Game(10, 10, "   ");
        assertEquals("Mr X", game.getXPlayer().getName(),
                "Ures nev eseten a default 'Mr X'-et varjuk.");
    }

    @Test
    void testConstructorReplacesNullNameWithDefault() {
        Game game = new Game(10, 10, null);
        assertEquals("Mr X", game.getXPlayer().getName(),
                "Null nev eseten is a default 'Mr X'-et varjuk.");
    }

}
