package amoba.board;

import amoba.enums.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * A tábla rowSize x colSize  rács - minden mező egy külön Cell
 */
public class Board {

    // sorok száma (N)
    private final int rowSize;

    // oszlopok száma (M)
    private final int colSize;

    // az összes mező a táblán (soronként egymás után töltve)
    List<Cell> cells = new ArrayList<>();

    /**
     * Új tábla - 4 <= M <= N <= 25
     * @param rowSize sorok száma (N)
     * @param colSize oszlopok száma (M)
     */
    public Board(int rowSize, int colSize) {

        // mértcheck
        if (rowSize < 1 || colSize < 1) {
            throw new IllegalArgumentException("Sorok/oszlopok száma nem lehet 0 vagy negatív.");
        }

        // M nem lehet nagyobb, mint N
        if (colSize > rowSize) {
            throw new IllegalArgumentException("Az oszlopok száma nem lehet nagyobb, mint a sorok száma!");
        }

        // 4 <= M <= N <= 25
        if (colSize < 4 || rowSize > 25) {
            throw new IllegalArgumentException("Érvénytelen tábla méret! Legalább 4 oszlop legyen, és legfeljebb 25 sor!");
        }

        this.rowSize = rowSize;
        this.colSize = colSize;
        initBoard(); // tábla feltöltése üres mezőkkel
    }


    /**
     * Getterek és seterek
     * 1. sorzám
     */
    public int getRowSize() {
        return rowSize;
    }

    /**
     * 2. oszlp száma
     */
    public int getColSize() {
        return colSize;
    }

    /**
     * 3. öszes Cell a táblán
     */
    public List<Cell> getCells() {
        return cells;
    }

    /**
     * A tábla init - Cell fill Empty symbolra
     */
    private void initBoard() {
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                Position position = new Position(row, col);
                cells.add(new Cell(position, Symbol.EMPTY));
            }
        }
    }

    /**
     * Symbol set a cellára
     *
     * @param cell   melyik cellát frissítjük
     * @param symbol jel ami rákerül
     * @return true, ha sikerült a set - false ha a cella foglalt
     */
    public boolean setCellSymbol(Cell cell, Symbol symbol) {
        if (cell != null && cell.getSymbol() == Symbol.EMPTY) {
            cell.setSymbol(symbol);
            return true;
        }

        return false;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int rows = rowSize;
        int cols = colSize;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int index = r * cols + c;
                Symbol s = cells.get(index).getSymbol();

                if (s == Symbol.X) sb.append('X');
                else if (s == Symbol.O) sb.append('O');
                else sb.append('·');
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Kirajzolás koordinátákkal (a–j, 1–10)
     */
    public String toStringWithCoords() {
        StringBuilder sb = new StringBuilder();

        int rows = rowSize;
        int cols = colSize;

        // oszlop betűk
        sb.append("  ");
        for (int c = 0; c < cols; c++) {
            char colLetter = (char) ('a' + c);
            sb.append(colLetter).append(" ");
        }
        sb.append("\n");

        // sorok száma + jel
        for (int r = 0; r < rows; r++) {
            sb.append(r + 1).append(" ");
            for (int c = 0; c < cols; c++) {
                int index = r * cols + c;
                Symbol s = cells.get(index).getSymbol();

                if (s == Symbol.X) sb.append("X ");
                else if (s == Symbol.O) sb.append("O ");
                else sb.append("· ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}
