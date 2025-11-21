package amoba;

public class Game {

    private final Board board;       // a tábla
    private Player currentPlayer;    // ki lép

    public Game() {
        this.board = new Board();
        this.currentPlayer = Player.X; // X kezd
    }

    // A Main majd megadja a felhasználó inputját plb3
    public boolean playOneMove(String input) {

        // input -> pozíció
        Position pos = Position.converter(input);

        // mező foglalt?
        if (!board.isEmpty(pos)) {
            return false; // sikertelen lépés
        }

        // ha üres, tegyük le a jelet
        board.place(pos, currentPlayer);

        // játékos váltás
        currentPlayer = currentPlayer.next();

        return true; // sikeres lépés
    }

    // A Main ezzel kérheti le a táblát kiíráshoz
    public Board getBoard() {
        return board;
    }

    // Main innen tudja, ki jön
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
