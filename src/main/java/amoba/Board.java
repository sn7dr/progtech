package amoba;

public class Board {

    // N sor, M oszlop  - 4 <= M <= N <= 25
    private final int rows; // N - sorok száma
    private final int cols; // M - oszlopok száma
    private final Player[][] cells; // kétdimenziós tömb a mezőknek

    // Konstruktor - létrehozza az üres NxM táblát
    public Board(int rows, int cols) {
        // egyszerű ellenőrzés a feladat szerint
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Sorok/oszlopok száma nem lehet 0 vagy negatív");
        }
        if (cols > rows) {
            throw new IllegalArgumentException("Az oszlopok száma nem lehet nagyobb, mint a sorok száma!");
        }
        if (cols < 4 || rows > 25) {
            // 4 <= M <= N <= 25
            throw new IllegalArgumentException("Érvénytelen tábla méret! Legalább 4 oszlop legyen, és kevesebb mint 26 sor!");
        }

        this.rows = rows;
        this.cols = cols;
        this.cells = new Player[rows][cols];
        // nincs szükség extra kitöltésre, alapból null = üres mező
    }

    // opcionális getterek
    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    // Megadjuk mi van egy adott pozícióban
    public Player get(Position pos) {
        return cells[pos.row()][pos.col()];
    }

    // ures e a mezo
    public boolean isEmpty(Position pos) {
        return get(pos) == null;
    }

    // pozicio megjelolese ha ures
    public void place(Position pos, Player player) {
        if (!isInside(pos)) {
            throw new IllegalArgumentException("A pozíció kívül esik a táblán: " + pos);
        }
        if (!isEmpty(pos)) {
            throw new IllegalArgumentException("A mező már foglalt: " + pos);
        }
        cells[pos.row()][pos.col()] = player;
    }

    // benne van-e a táblában az adott pozíció
    private boolean isInside(Position pos) {
        return pos.row() >= 0 && pos.row() < rows
                && pos.col() >= 0 && pos.col() < cols;
    }
    // tábla kiírása koordinátákkal (a-j felül, 1-10 oldalt)
    public String toStringWithCoords() {
        StringBuilder sb = new StringBuilder();

        // felső betűsor
        sb.append("   "); // behúzás
        for (int c = 0; c < cols; c++) {
            char ch = (char) ('a' + c);
            sb.append(ch).append(" ");
        }
        sb.append("\n");

        // sorok
        for (int r = 0; r < rows; r++) {
            // sorszám kiírás
            if (r + 1 < 10) sb.append(" "); // igazít
            sb.append(r + 1).append(" ");

            for (int c = 0; c < cols; c++) {
                Player p = cells[r][c];
                sb.append(p == null ? "\u00B7" : p.toString());
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // kiírás a tábláról
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Player p = cells[r][c];
                sb.append(p == null ? "\u00B7" : p.toString()); //center pont unicodeja, ne alul legyen a sima .
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
