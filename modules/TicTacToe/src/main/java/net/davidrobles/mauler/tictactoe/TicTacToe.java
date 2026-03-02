package net.davidrobles.mauler.tictactoe;

import net.davidrobles.mauler.core.AbstractGame;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Outcome;
import net.davidrobles.mauler.core.util.SpeedTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicTacToe extends AbstractGame implements Game<TicTacToe> {

    public static final int SIZE = 3;
    public enum Cell { CROSS, NOUGHT, EMPTY }

    private int crosses, noughts;
    private static final int[] WINNING_PATTERNS = {7, 56, 448, 73, 146, 292, 273, 84};

    public TicTacToe() {
        reset();
    }

    public Cell getCell(int cellIndex) {
        if ((crosses & (1L << cellIndex)) != 0L)
            return Cell.CROSS;
        if ((noughts & (1L << cellIndex)) != 0L)
            return Cell.NOUGHT;
        return Cell.EMPTY;
    }

    public Cell getCell(int row, int col) {
        return getCell(SIZE * row + col);
    }

    public Cell[] getBoard() {
        Cell[] boardArray = new Cell[9];
        for (int i = 0; i < 9; i++)
            boardArray[i] = getCell(i);
        return boardArray;
    }

    private boolean checkWin(int board) {
        for (int pattern : WINNING_PATTERNS)
            if ((board & pattern) == pattern)
                return true;
        return false;
    }

    private int getCurrentBitboard() {
        return getCurPlayer() == 0 ? crosses : noughts;
    }

    private void setCurrentBitboard(int bitboard) {
        if (getCurPlayer() == 0)
            crosses = bitboard;
        else
            noughts = bitboard;
    }

    private List<Integer> legalMoves() {
        List<Integer> moves = new ArrayList<Integer>();
        if (getNumMoves() > 0) {
            int legal = ~(crosses | noughts);
            for (int i = 0; i < 9; i++)
                if ((legal & (1 << i)) != 0)
                    moves.add(i);
        }
        return moves;
    }

    private boolean isWin() {
        return checkWin(crosses) || checkWin(noughts);
    }

    private int emptyCells() {
        return 9 - Integer.bitCount(crosses | noughts);
    }

    //////////
    // Game //
    //////////

    @Override
    public TicTacToe copy() {
        TicTacToe gameCopy = new TicTacToe();
        gameCopy.crosses = crosses;
        gameCopy.noughts = noughts;
        return gameCopy;
    }

    @Override
    public int getCurPlayer() {
        return (emptyCells() + 1) % 2;
    }

    @Override
    public String[] getMoves() {
        List<String> moves = new ArrayList<String>();
        for (Integer move : legalMoves())
            moves.add(String.valueOf(move));
        return moves.toArray(new String[moves.size()]);
    }

    @Override
    public int getNumMoves() {
        return isWin() ? 0 : emptyCells();
    }

    @Override
    public int getNumPlayers() {
        return 2;
    }

    @Override
    public boolean isOver() {
        return getNumMoves() == 0;
    }

    @Override
    public void makeMove(int move) {
        if (move < 0 || move >= getNumMoves())
            throw new IllegalArgumentException("Illegal move!");
        setCurrentBitboard(getCurrentBitboard() | (1 << legalMoves().get(move)));
    }

    @Override
    public String getName() {
        return "Tic-tac-toe";
    }

    @Override
    public TicTacToe newInstance() {
        return new TicTacToe();
    }

    @Override
    public void reset() {
        crosses = noughts = 0;
    }

    // TODO: This method is too long
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (!isOver()) {
            builder.append(("Player: " + getCurPlayer() + "\n"));
            builder.append(("Moves:  " + Arrays.toString(legalMoves().toArray()) + "\n"));
        } else {
            builder.append("Game over\n");
        }
        builder.append("\n");
        for (int i = 0; i < 9; i++) {
            if ((crosses & (1 << i)) != 0)
                builder.append(" X ");
            else if ((noughts & (1 << i)) != 0)
                builder.append(" O ");
            else
                builder.append(" - ");
            if (i % 3 == 2)
                builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TicTacToe)) return false;
        TicTacToe other = (TicTacToe) obj;
        return crosses == other.crosses && noughts == other.noughts;
    }

    @Override
    public int hashCode() {
        return 31 * crosses + noughts;
    }

    @Override
    public Outcome[] getOutcome() {
        if (!isOver())
            return new Outcome[] {Outcome.NA, Outcome.NA};
        if (checkWin(crosses))
            return new Outcome[] {Outcome.WIN, Outcome.LOSS};
        if (checkWin(noughts))
            return new Outcome[] {Outcome.LOSS, Outcome.WIN};
        return new Outcome[] {Outcome.DRAW, Outcome.DRAW};
    }

    public static void main(String[] args) {
        TicTacToe tic = new TicTacToe();
        System.out.println(SpeedTest.gameSpeed(tic, 10));
    }
}
