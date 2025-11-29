package amoba.game;

import amoba.board.Board;
import amoba.board.Cell;
import amoba.board.Position;
import amoba.enums.Symbol;

/**
 * Win-check logika
 * Check hogy van-e 5 egyforma symbol lerakva
 */
public class WinChecker {

    /**
     * 4 iranyba :
     *  - fuggoleges
     *  - vizszintes
     *  - foatlo
     *  - mellekatlo
     *
     * @param board a tabla
     * @param pos hova raktuk le a jelet
     * @param symbol milyen jel X vagy O vagy EPTY
     * @return true ha van legalabb 5 egymas mellett
     */
    public static boolean isWinningMove(Board board, Position pos, Symbol symbol) {
        int[][] directions = {
                {1, 0},   // fuggoleges
                {0, 1},   // vizszintes
                {1, 1},   // foatlo
                {1, -1}   // mellekatlo
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            int count = 1; // maga az uj babu mar benne van

            // egyik irany
            count += countDirection(board, pos, dx, dy, symbol);
            // masik irany
            count += countDirection(board, pos, -dx, -dy, symbol);

            if (count >= 5) {
                return true;
            }
        }

        return false;
    }

    /**
     * Irányonként count hány symbol van sorban törés nélkül
     *
     * @param board a tabla
     * @param start honnan indulunk  - latest pos
     * @param dx sor iranya (-1,0,1)
     * @param dy oszlop iranya (-1,0,1)
     * @param symbol milyen jelet keresunk
     * @return hany darab van egy iranyban tores nelkul
     */
    private static int countDirection(Board board, Position start, int dx, int dy, Symbol symbol) {
        int rows = board.getRowSize();
        int cols = board.getColSize();

        int r = start.getRow() + dx;
        int c = start.getCol() + dy;
        int count = 0;

        // tabla hataran belul
        while (r >= 0 && r < rows && c >= 0 && c < cols) {

            int index = r * cols + c;
            Cell cell = board.getCells().get(index);
            Symbol cellSymbol = cell.getSymbol();

            if (cellSymbol == symbol) {
                count++;
                r += dx;
                c += dy;
            } else {
                // mas jel vagy ures - megszakad
                break;
            }
        }

        return count;
    }
}
