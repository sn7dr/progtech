package amoba;

public class Main {
    public static void main(String[] args) {

        int rows = 5;
        int cols = 5;

        // Player teszt
        System.out.println("Player teszt");
        Player current = Player.X; // X kezd
        System.out.println("Kezdő játékos: " + current);

        for (int i = 0; i < 5; i++) {
            current = current.next(); // váltás a másik játékosra
            System.out.println("Következő játékos: " + current);
        }

        System.out.println("\nPosition teszt");
        Position p1 = new Position(0, 0);
        System.out.println("Kézzel létrehozott pozíció: " + p1);

        Position p2 = Position.converter("b3");
        System.out.println("Szövegből ('b3') konvertált pozíció: " + p2);

        if (p1.equals(p2)) {
            System.out.println("p1 és p2 egyenlő (ugyanaz a sor és oszlop) " + p1);
        } else {
            System.out.println("p1 és p2 különböző pozíció " + p1 + " " + p2);
        }

        System.out.println("\nHash teszt ");
        System.out.println("p1 hash: " + p1.hashCode());
        System.out.println("p2 hash: " + p2.hashCode());

        if (p1.hashCode() == p2.hashCode()) {
            System.out.println("A hashCode egyezik");
        } else {
            System.out.println("A hashCode különbözik");
        }

        // BOARD TESZT

        System.out.println("\nBoard teszt");

        //Board board = new Board(10,10); // alap 10x10 táblát hoz létre
        Board board = new Board(rows,cols); // custom táblát hoz létre


        System.out.println("Üres tábla:");
        System.out.println(board);  // toString() hívódik → pontokkal kirajzolja a mezőket

        // X középre
        board.place(new Position(1, 1), Player.X);

        // O bal felső sarokba
        board.place(new Position(0, 0), Player.O);

        System.out.println(board);  // újra kirajzolja a táblát


        // GAME TESZT
        System.out.println("\nGame teszt");

        Game g = new Game(rows, cols); // X kezd

        System.out.println(g.getCurrentPlayer()+ " következik\n");
        g.playOneMove("a3");
        System.out.println(g.getBoard());

        System.out.println(g.getCurrentPlayer()+ " következik\n");
        g.playOneMove("b3");
        System.out.println(g.getBoard());

        System.out.println(g.getCurrentPlayer()+ " következik\n");
        g.playOneMove("c3");
        System.out.println(g.getBoard());


        // Szándékos failtest - foglaltmező trigger
        String input = "c3";
        boolean ok = g.playOneMove(input);

        if (!ok) {
            Position pos = Position.converter(input);
            System.out.println("Nem sikerült a lépés, mert foglalt: " + pos);
        }

        System.out.println("\nTeszt vége");
    }
}
