package amoba;

import java.util.Objects; //kesobb hash-hez

public final class Position { //Value O. - final - nem örökölhető
    private final int row; //immutabilis - létrehozás után nem változik
    private final int col;

    //új object létrehozása -kap egy sort és egy oszlopot (konstruktor)
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    //getter
    public int row() {
        return row;
    }

    public int col() {
        return col;
    }


    //szövegből pozíció
    public static Position converter(String s) {
        if (s == null || s.length() < 2) //Üresség ellenőrzés
            throw new IllegalArgumentException("Rossz formátum! A helyes: a1"); //exception
        char colCh = Character.toLowerCase(s.charAt(0)); //kisbetut csinalunk

        //A betű ASCII kódját kivonjuk a colchból, betűből számot csinál a=0, b=1 -
        int col = colCh - 'a';
        //a char tipusu intből számot - de 0 tól kezdünk szóval -1
        int row = Integer.parseInt(s.substring(1)) - 1;
        //új pozíció ezekkel az értékekkel (pl a1 - 0,0)
        return new Position(row, col);
    }

    //összehasonlítás
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; //ugyan az a példány
        if (!(o instanceof Position)) return false; //pozicio egyaltalan?
        Position that = (Position) o;
        return row == that.row && col == that.col;
    }

    //hasheli a sort es oszlopot
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    //pozicio kiiratas
    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
