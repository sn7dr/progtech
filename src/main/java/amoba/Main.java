package amoba;

public class Main {
    public static void main(String[] args) {

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
            System.out.println("A hashCode különbözik (hiba)");
        }

        // BOARD TESZT

        System.out.println("\nBoard teszt");

        Board board = new Board(); // üres 3x3 táblát hoz létre

        System.out.println("Üres tábla:");
        System.out.println(board);  // toString() hívódik → pontokkal kirajzolja a mezőket

        // X középre
        board.place(new Position(1, 1), Player.X);

        // O bal felső sarokba
        board.place(new Position(0, 0), Player.O);

        System.out.println("2 lépés után:");
        System.out.println(board);  // újra kirajzolja a táblát


        // GAME TESZT
        System.out.println("\nGame teszt");

        Game g = new Game(); // X kezd

        System.out.println(g.getCurrentPlayer()+ " következik\n");
        g.playOneMove("a3");
        System.out.println(g.getBoard());

        System.out.println(g.getCurrentPlayer()+ " következik\n");
        g.playOneMove("b3");
        System.out.println(g.getBoard());

        System.out.println(g.getCurrentPlayer()+ " következik\n");
        g.playOneMove("c3");
        System.out.println(g.getBoard());

        System.out.println("\nTeszt vége");
    }
}
