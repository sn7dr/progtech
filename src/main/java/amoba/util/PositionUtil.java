package amoba.util;

import amoba.board.Position;

/**
 * Pozicio segedfuggvenyek.
 * Itt kezeljuk az algebrai formatumot (pl. "f5") <-> Position atalakitasat.
 */
public class PositionUtil {

    /**
     * String inputból POsition - pl f5
     * Elso karakter - oszlop betu f
     * Masodik - sor szam 5 - de 1 tol indul
     *
     * @param input f5
     * @return Position sor, oszlop - index 0 tól!
     */
    public static Position fromAlgebraic(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Null input");
        }

        String trimmed = input.trim().toLowerCase();
        if (trimmed.length() < 2) {
            throw new IllegalArgumentException("Helytelen formátum, kérlek add meg helyesen! ('f5') " + input);
        }

        char colChar = trimmed.charAt(0);
        if (colChar < 'a' || colChar > 'z') {
            throw new IllegalArgumentException("Hibás oszlop, kérlek add meg helyesen! ('f5'): " + colChar);
        }

        int col = colChar - 'a';

        String rowPart = trimmed.substring(1);
        int row;
        try {
            row = Integer.parseInt(rowPart) - 1; // 1 lesz 0 indexen
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Hibás szorszám, kérlek add meg helyesen! 'f5'): " + input, e);
        }

        return new Position(row, col);
    }

    /**
     * Position algebrai string ((5,2) -> "c6").
     *
     * @param pos pozicio (sor, oszlop) 0tól
     * @return "f5" formátummal
     */
    public static String toAlgebraic(Position pos) {
        if (pos == null) {
            throw new IllegalArgumentException("Null pozicio");
        }

        char colLetter = (char) ('a' + pos.getCol());
        int rowNumber = pos.getRow() + 1; // 1től indul

        return "" + colLetter + rowNumber;
    }
}
