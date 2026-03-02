package net.davidrobles.mauler.tictactoe;

import net.davidrobles.mauler.core.ObservableGame;
import net.davidrobles.mauler.core.GameResult;
import net.davidrobles.mauler.core.util.SpeedTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 3×3 Tic-tac-toe, represented with two 9-bit integer bitboards.
 *
 * <p>Bit {@code i} in {@code crosses} (resp. {@code noughts}) is set when player 0
 * (resp. player 1) occupies cell {@code i}, where cells are numbered row-major:
 * <pre>
 *   0 | 1 | 2
 *  ---+---+---
 *   3 | 4 | 5
 *  ---+---+---
 *   6 | 7 | 8
 * </pre>
 *
 * <p>Player 0 is always X (crosses) and moves first.
 */
public class TicTacToe extends ObservableGame<TicTacToe>
{
    public static final int SIZE = 3;
    public static final int CELLS = SIZE * SIZE;

    public enum Cell { CROSS, NOUGHT, EMPTY }

    private int crosses, noughts;

    // All eight winning lines as bitmasks
    private static final int[] WINNING_PATTERNS = { 7, 56, 448, 73, 146, 292, 273, 84 };

    public TicTacToe()
    {
        reset();
    }

    // -------------------------------------------------------------------------
    // Board queries
    // -------------------------------------------------------------------------

    public Cell getCell(int cellIndex)
    {
        if ((crosses & (1 << cellIndex)) != 0) return Cell.CROSS;
        if ((noughts & (1 << cellIndex)) != 0) return Cell.NOUGHT;
        return Cell.EMPTY;
    }

    public Cell getCell(int row, int col)
    {
        return getCell(SIZE * row + col);
    }

    public Cell[] getBoard()
    {
        Cell[] board = new Cell[CELLS];
        for (int i = 0; i < CELLS; i++)
            board[i] = getCell(i);
        return board;
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private boolean checkWin(int board)
    {
        for (int pattern : WINNING_PATTERNS)
            if ((board & pattern) == pattern)
                return true;
        return false;
    }

    private boolean isWin()
    {
        return checkWin(crosses) || checkWin(noughts);
    }

    private int emptyCells()
    {
        return CELLS - Integer.bitCount(crosses | noughts);
    }

    private int currentBitboard()
    {
        return getCurPlayer() == 0 ? crosses : noughts;
    }

    private void setCurrentBitboard(int bitboard)
    {
        if (getCurPlayer() == 0)
            crosses = bitboard;
        else
            noughts = bitboard;
    }

    private List<Integer> legalMoveIndices()
    {
        List<Integer> moves = new ArrayList<>();
        if (getNumMoves() > 0) {
            int empty = ~(crosses | noughts);
            for (int i = 0; i < CELLS; i++)
                if ((empty & (1 << i)) != 0)
                    moves.add(i);
        }
        return moves;
    }

    // -------------------------------------------------------------------------
    // Game
    // -------------------------------------------------------------------------

    @Override
    public TicTacToe copy()
    {
        TicTacToe copy = new TicTacToe();
        copy.crosses = crosses;
        copy.noughts = noughts;
        return copy;
    }

    @Override
    public int getCurPlayer()
    {
        // X moves on even turns (9, 7, 5, ... empty cells), O on odd turns
        return (emptyCells() + 1) % 2;
    }

    @Override
    public List<String> getMoves()
    {
        List<Integer> indices = legalMoveIndices();
        List<String> moves = new ArrayList<>();
        for (int i = 0; i < indices.size(); i++)
            moves.add(String.valueOf(indices.get(i)));
        return List.copyOf(moves);
    }

    @Override
    public int getNumMoves()
    {
        return isWin() ? 0 : emptyCells();
    }

    @Override
    public int getNumPlayers()
    {
        return 2;
    }

    @Override
    public void makeMove(int move)
    {
        List<Integer> indices = legalMoveIndices();
        if (move < 0 || move >= indices.size())
            throw new IllegalArgumentException("Illegal move: " + move);
        setCurrentBitboard(currentBitboard() | (1 << indices.get(move)));
        notifyMoveObservers();
    }

    @Override
    public String getName()
    {
        return "Tic-tac-toe";
    }

    @Override
    public void reset()
    {
        crosses = noughts = 0;
    }

    @Override
    public Optional<GameResult[]> getOutcome()
    {
        if (getNumMoves() != 0)
            return Optional.empty();
        if (checkWin(crosses))
            return Optional.of(new GameResult[] { GameResult.WIN, GameResult.LOSS });
        if (checkWin(noughts))
            return Optional.of(new GameResult[] { GameResult.LOSS, GameResult.WIN });
        return Optional.of(new GameResult[] { GameResult.DRAW, GameResult.DRAW });
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof TicTacToe)) return false;
        TicTacToe other = (TicTacToe) obj;
        return crosses == other.crosses && noughts == other.noughts;
    }

    @Override
    public int hashCode()
    {
        return 31 * crosses + noughts;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (!isOver()) {
            sb.append("Player: ").append(getCurPlayer()).append("\n");
            sb.append("Moves:  ").append(Arrays.toString(legalMoveIndices().toArray())).append("\n");
        } else {
            sb.append("Game over\n");
        }
        sb.append("\n");
        for (int i = 0; i < CELLS; i++) {
            if ((crosses & (1 << i)) != 0)
                sb.append(" X ");
            else if ((noughts & (1 << i)) != 0)
                sb.append(" O ");
            else
                sb.append(" - ");
            if (i % SIZE == SIZE - 1)
                sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args)
    {
        System.out.println(SpeedTest.gameSpeed(new TicTacToe(), 10));
    }
}
