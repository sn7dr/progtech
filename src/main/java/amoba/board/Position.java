package amoba.board;

/**
 * Pozíció a táblán - szimpla sor - oszlop
 * A sor és oszlop indexelés 0tól
 */
public final class Position {

    // hanyadik sorban
    private final int row;

    // hanyadik oszlopban
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        // debughoz tostring
        return "(" + row + "," + col + ")";
    }
}
