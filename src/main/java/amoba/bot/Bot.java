package amoba.bot;

import amoba.board.Board;
import amoba.board.Position;
import amoba.enums.Symbol;

/**
 * Bot interface
 */
public interface Bot {

    /**
     * Bot pozicio valasztas board alapjan
     *
     * @param board aktualis tabla
     * @param symbol milyen jellel lep (O)
     * @return a valasztott pozicio vagy null ha nem tud lepni
     */
    Position chooseMove(Board board, Symbol symbol);
}
