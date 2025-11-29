package amoba.user;

import amoba.enums.Symbol;

/**
 * Játékos - név + symbol + pontszám
 */
public class Player {

    private final String name;
    private final Symbol selectedSymbol;

    private int score;

    public Player(String name, Symbol selectedSymbol, int score) {
        this.name = name;
        this.selectedSymbol = selectedSymbol;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public Symbol getSelectedSymbol() {
        return selectedSymbol;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }

    @Override
    public String toString() {
        return name + " (" + selectedSymbol + ")";
    }
}
