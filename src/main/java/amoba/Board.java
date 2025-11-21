package amoba;

public class Board {

    private final int size = 3; // egyszerű 3x3-as tábla
    private final Player[][] cells; // kétdimenziós tömb a mezőknek

    // Konstruktor - létrehozza az üres táblát
    public Board() {
        cells = new Player[size][size];
        // nincs szükség extra kitöltésre, alapból null = üres mező
    }

    // Megadjuk, mi van egy adott pozícióban
    public Player get(Position pos) {
        return cells[pos.row()][pos.col()];
    }

    // Megnézzük, üres-e a mező
    public boolean isEmpty(Position pos) {
        return get(pos) == null;
    }

    // Lépés: egy játékos jelet tesz a pozícióra, ha üres
    public void place(Position pos, Player player) {
        if (!isEmpty(pos)) {
            throw new IllegalArgumentException("A mező már foglalt: " + pos);
        }
        cells[pos.row()][pos.col()] = player;
    }

    // Egyszerű kiírás a tábláról
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Player p = cells[r][c];
                sb.append(p == null ? "." : p.toString());
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
