package amoba.util;

import amoba.board.Board;
import amoba.board.Cell;
import amoba.board.Position;
import amoba.enums.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Lepesszabaly util
 *  - benne van-e a pozicio a tablan
 *  - ures-e a mezo
 *  - van-e szomszed
 *  - osszes valid lepes keresese
 */
public class MoveRulesUtil {

    /**
     * Pozicio - Tablahatar check, tablan vane
     *
     * @param board aktualis tabla
     * @param pos melyik pozicio
     * @return true ha a tabla hatarain belul van
     */
    public static boolean isInside(Board board, Position pos) {
        int rows = board.getRowSize();
        int cols = board.getColSize();

        int r = pos.getRow();
        int c = pos.getCol();

        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /**
     * Pozicio - Tablahatar check, tablan kivul vane
     *
     * @param board tabla
     * @param pos pozicio
     * @return true ha KIVUL van
     */
    public static boolean isOutside(Board board, Position pos) {
        return !isInside(board, pos);
    }

    /**
     * Symbol.EMPTY ?
     *
     * @param board tabla
     * @param pos pozicio
     * @return true ha a cella ures
     */
    public static boolean isEmpty(Board board, Position pos) {
        if (isOutside(board, pos)) {
            return false; // kivul
        }

        int cols = board.getColSize();
        int index = pos.getRow() * cols + pos.getCol();
        Cell cell = board.getCells().get(index);

        return cell.getSymbol() == Symbol.EMPTY;
    }

    /**
     * Szomszed check
     *
     * @param board tabla
     * @param pos pozicio amit nezunk
     * @return true ha van foglalt szomszed
     */
    public static boolean hasNeighbor(Board board, Position pos) {
        int[] dirs = {-1, 0, 1};

        for (int dx : dirs) {
            for (int dy : dirs) {

                if (dx == 0 && dy == 0) {
                    continue; // sajat mezo
                }

                int nr = pos.getRow() + dx;
                int nc = pos.getCol() + dy;

                Position neighPos = new Position(nr, nc);

                // ignore ha kivul esik a tablan
                if (isOutside(board, neighPos)) {
                    continue;
                }

                int cols = board.getColSize();
                int index = nr * cols + nc;
                Cell neighCell = board.getCells().get(index);

                if (neighCell.getSymbol() != Symbol.EMPTY) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Osszes valid lepes keresese:
     *  - ures mezo
     *  - van szomszed
     *
     * @param board aktualis tabla
     * @return lista az open poziciokrol
     */
    public static List<Position> findValidMoves(Board board) {
        List<Position> result = new ArrayList<>();

        int rows = board.getRowSize();
        int cols = board.getColSize();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Position p = new Position(r, c);

                if (isEmpty(board, p) && hasNeighbor(board, p)) {
                    result.add(p);
                }
            }
        }

        return result;
    }
}
