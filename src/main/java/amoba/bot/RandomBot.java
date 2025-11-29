package amoba.bot;

import amoba.board.Board;
import amoba.board.Position;
import amoba.enums.Symbol;
import amoba.util.MoveRulesUtil;

import java.util.List;
import java.util.Random;

/**
 * random bot - teljesen veletlen valid poziciot valaszt
 */
public class RandomBot implements Bot {

    private final Random rand = new Random();

    @Override
    public Position chooseMove(Board board, Symbol symbol) {

        // osszes valid lepes utilbol
        List<Position> validMoves = MoveRulesUtil.findValidMoves(board);

        if (validMoves.isEmpty()) {
            return null; // patt
        }

        int index = rand.nextInt(validMoves.size());
        return validMoves.get(index);
    }
}
