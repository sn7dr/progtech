package amoba.enums;

/**
 * Jelek a táblára -
 * X  – player 1
 * O  – player 2 (bot lesz)
 * EMPTY – üres mező (közép pont)
 */
public enum Symbol {

    X("X"),

    O("O"),

    @SuppressWarnings("UnnecessaryUnicodeEscape") EMPTY("\u00B7");

    // a jel string prnthez
    private final String symbol;

    /**
     * Konstruktor – enum belső érték
     * @param symbol a karakter/string, amit kiírunk a táblán
     */
    Symbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Jel - X, O, ·)
     */
    public String getSymbol() {
        return symbol;
    }
}
