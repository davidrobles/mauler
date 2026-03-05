package net.davidrobles.mauler.breakthrough;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.davidrobles.mauler.core.GameResult;
import net.davidrobles.mauler.core.ObservableGame;

public class Breakthrough extends ObservableGame<Breakthrough> {
    public enum Cell {
        BLACK,
        WHITE,
        EMPTY
    }

    private final int rows, cols, cells;
    private int starter, current;
    private long black, white;
    private Random rng = new Random();
    private List<int[]> moves = new ArrayList<int[]>();

    public Breakthrough() {
        this(new Random());
    }

    public Breakthrough(Random rng) {
        this(8, 8, rng);
    }

    public Breakthrough(int rows, int cols, Random rng) {
        this.rows = rows;
        this.cols = cols;
        this.cells = rows * cols;
        this.rng = rng;
        reset();
    }

    public int getCount(int player) {
        return player == 0 ? Long.bitCount(black) : Long.bitCount(white);
    }

    // returns the type of the cell
    public Cell getCell(int cellIndex) {
        // black stone in the cell
        if ((black & (1L << cellIndex)) != 0L) return Cell.BLACK;

        // white stone in the cell
        if ((white & (1L << cellIndex)) != 0L) return Cell.WHITE;

        // no stones in the cell
        return Cell.EMPTY;
    }

    public Cell getCell(int row, int col) {
        return getCell(cellCoordsToIndex(row, col));
    }

    public Cell getCell(String cellStr) {
        int row = Integer.valueOf(String.valueOf(cellStr.charAt(1)));
        int col = getCol(cellStr.charAt(0));

        return getCell(row, col);
    }

    public Point getPoint(String cellStr) {
        int row = Integer.valueOf(String.valueOf(cellStr.charAt(1)));
        int col = getCol(cellStr.charAt(0));
        return new Point(col, row);
    }

    private int getCol(char letter) {
        for (int i = 0; i < cols; i++) if ((char) ('a' + (i)) == letter) return i;

        return -838448;
    }

    public String cellToString(int cellIndex) {
        return "" + (char) ('a' + (cellIndex % cols)) + ((cellIndex / rows) + 1);
    }

    public String cellToString(int row, int col) {
        return cellToString(cellCoordsToIndex(row, col));
    }

    private int cellCoordsToIndex(int row, int col) {
        return cols * row + col;
    }

    public String curPlayerStr() {
        return current == 0 ? "Black" : "White";
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    private List<int[]> getMovesArray() {
        if (moves.isEmpty()) calculateMoves();

        return moves;
    }

    //////////
    // Game //
    //////////

    @Override
    public Breakthrough copy() {
        Breakthrough bk = new Breakthrough(rows, cols, rng);
        bk.black = black;
        bk.white = white;
        bk.current = current;

        return bk;
    }

    @Override
    public int getCurPlayer() {
        return current;
    }

    @Override
    public int getNumPlayers() {
        return 2;
    }

    @Override
    public boolean isOver() {
        return getMovesArray().isEmpty();
    }

    @Override
    public int getNumMoves() {
        return getMovesArray().size();
    }

    @Override
    public String getName() {
        return "Breakthrough";
    }

    @Override
    public List<String> getMoves() {
        List<String> bkMoves = new ArrayList<>();

        for (int[] move : getMovesArray())
            bkMoves.add(cellToString(move[0]) + cellToString(move[1]));

        return List.copyOf(bkMoves);
    }

    @Override
    public Optional<GameResult[]> getOutcome() {
        return Optional.empty(); // TODO: implement outcome detection
    }

    @Override
    public void makeMove(int move) {
        List<int[]> movesArray = getMovesArray();

        if (move < 0 || move >= movesArray.size()) throw new IllegalArgumentException();

        int[] moveTaken = movesArray.get(move);

        // move source
        removePiece(moveTaken[0]);
        removePiece(moveTaken[1]);

        if (current == 0) black |= (1L << moveTaken[1]);
        else white |= (1L << moveTaken[1]);

        current = (current + 1) % 2;

        movesArray.clear();
    }

    private void removePiece(int cellIndex) {
        black &= ~(1L << cellIndex);
        white &= ~(1L << cellIndex);
    }

    @Override
    public void reset() {
        current = 0;
        //        current = starter = rng.nextInt(2);
        black = 65535L;
        white = -281474976710656L;
        calculateMoves();
    }

    private void calculateMoves() {
        moves.clear();

        for (int i = 0; i < cells; i++) {
            long curBit = (1L << i);

            if (current == 0 && (black & curBit) != 0L) {
                // DOWN
                if (((curBit << cols) & (black | white)) == 0L) moves.add(new int[] {i, i + cols});

                // DOWN LEFT
                //                if ((((curBit << (cols - 1)) & RIGHT_MASK) & ((black | white))) ==
                // 0L)
                //                    moves.add(new int[] { i, i + cols - 1 });

                // DOWN RIGHT
            } else if (current == 1 && (white & curBit) != 0L) {
                // UP
                if (((curBit >> cols) & (black | white)) == 0L) moves.add(new int[] {i, i - cols});

                // UP LEFT

                // UP RIGHT
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Breakthrough)) return false;
        Breakthrough other = (Breakthrough) obj;
        return rows == other.rows
                && cols == other.cols
                && current == other.current
                && black == other.black
                && white == other.white;
    }

    @Override
    public int hashCode() {
        int result = rows;
        result = 31 * result + cols;
        result = 31 * result + current;
        result = 31 * result + (int) (black ^ (black >>> 32));
        result = 31 * result + (int) (white ^ (white >>> 32));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Current player: %s\n", curPlayerStr()));
        builder.append(String.format("Blacks count: %d\n", Long.bitCount(black)));
        builder.append(String.format("Whites count: %d\n", Long.bitCount(white)));

        //        if (legal == PASS)
        //            builder.append(String.format("Moves count: 1 (Pass)\n"));
        //        else
        //            builder.append(String.format("Moves count: %d\n\n", Long.bitCount(legal)));

        builder.append("   ");

        for (int i = 0; i < rows; i++) builder.append((" " + ((char) ('a' + i)) + " "));

        builder.append("\n");

        for (int i = 0; i < cells; i++) {
            if (i % rows == 0) builder.append((" " + (8 - i / rows) + " "));

            if ((black & (1L << i)) != 0) builder.append(" \u25C9 ");
            else if ((white & (1L << i)) != 0) builder.append(" \u25CE ");
            else builder.append(" - ");

            if (i % rows == rows - 1) builder.append((" " + (8 - i / rows) + " \n"));
        }

        builder.append("   ");

        for (int i = 0; i < rows; i++) builder.append((" " + (char) ('a' + i) + " "));

        builder.append("\n");

        return builder.toString();
    }

    public static final long RIGHT_MASK = 9187201950435737471L;
    public static final long LEFT_MASK = -72340172838076674L;
    public static final long UP_MASK = -256L;
    public static final long DOWN_MASK = 72057594037927935L;

    public static void main(String[] args) {
        Random rng = new Random();
        Breakthrough bk = new Breakthrough(rng);
        System.out.println(bk);

        while (!bk.isOver())
        //        for (int i = 0; i < 20; i++)
        {
            bk.makeMove(rng.nextInt(bk.getNumMoves()));
            System.out.println(bk);
        }
    }
}
