package amoba.board;

import amoba.enums.Symbol;

/**
 * Egy mező a táblán.
 * Koordináta - POsition - mi van rajta Symbol
 */
public class Cell {

    // a mező helye a táblán - sor - oszlop
    private Position position;

    // Symbl a a mezőn X, O, üres
    private Symbol symbol;

    /**
     *  Konstruktor – mező helye + jel
     *
     * @param position pozíció sor - oszlop
     * @param symbol Symbl a a mezőn X, O, üres
     */
    public Cell(Position position, Symbol symbol) {
        this.position = position;
        this.symbol = symbol;
    }

    /**
     * Visszaadja a mező pozícióját
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Beállítja a mező pozícióját
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Symbol az adott mezőn
     */
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Set Symbl az adottmezőn
     */
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
}
