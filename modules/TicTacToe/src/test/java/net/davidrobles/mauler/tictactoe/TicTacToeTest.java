package net.davidrobles.mauler.tictactoe;

import net.davidrobles.mauler.core.GameResult;
import net.davidrobles.mauler.core.GameTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Full test suite for {@link TicTacToe}.
 *
 * <p>Inherited from {@link GameTest}: testGameFinishes, testCopy, testHashCode,
 * testNumLegalMovesEqualsListMoves.
 *
 * <p>Board layout (cell indices, row-major):
 * <pre>
 *   0 | 1 | 2
 *  ---+---+---
 *   3 | 4 | 5
 *  ---+---+---
 *   6 | 7 | 8
 * </pre>
 *
 * <p>Move semantics: {@code makeMove(i)} places on the {@code i}-th empty cell
 * (0-indexed, ascending cell order).
 */
public class TicTacToeTest extends GameTest<TicTacToe>
{
    @Before
    public void init()
    {
        game = new TicTacToe();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Applies a sequence of move indices to {@code game}. */
    private void play(int... moves)
    {
        for (int m : moves)
            game.makeMove(m);
    }

    /**
     * Drives X to win via the top row (cells 0, 1, 2); O occupies cells 3, 4.
     *
     * <pre>
     *  X | X | X
     * ---+---+---
     *  O | O | -
     * ---+---+---
     *  - | - | -
     * </pre>
     */
    private void playXWinsTopRow()
    {
        play(0,   // X → cell 0;  empty: [1..8]
             2,   // O → cell 3;  empty: [1,2,3,4,5,6,7,8]
             0,   // X → cell 1;  empty: [1,2,4,5,6,7,8]
             1,   // O → cell 4;  empty: [2,4,5,6,7,8]
             0);  // X → cell 2  → X wins
    }

    /**
     * Drives X to win via the main diagonal (cells 0, 4, 8); O occupies cells 1, 2.
     *
     * <pre>
     *  X | O | O
     * ---+---+---
     *  - | X | -
     * ---+---+---
     *  - | - | X
     * </pre>
     */
    private void playXWinsDiagonal()
    {
        play(0,   // X → cell 0;  empty: [1..8]
             0,   // O → cell 1;  empty: [2..8]
             2,   // X → cell 4;  empty: [2,3,4,5,6,7,8]
             0,   // O → cell 2;  empty: [2,3,5,6,7,8]
             4);  // X → cell 8;  empty: [3,5,6,7,8]  → X wins
    }

    /**
     * Drives O to win via center column (cells 1, 4, 7); X occupies cells 0, 2, 3.
     *
     * <pre>
     *  X | O | X
     * ---+---+---
     *  X | O | -
     * ---+---+---
     *  - | O | -
     * </pre>
     */
    private void playOWinsCenterColumn()
    {
        play(0,   // X → cell 0;  empty: [1..8]
             0,   // O → cell 1;  empty: [2..8]
             0,   // X → cell 2;  empty: [3..8]
             1,   // O → cell 4;  empty: [3,4,5,6,7,8]
             0,   // X → cell 3;  empty: [3,5,6,7,8]
             2);  // O → cell 7;  empty: [5,6,7,8]  → O wins
    }

    /**
     * Plays a full board that ends in a draw.
     *
     * <pre>
     *  X | O | X
     * ---+---+---
     *  O | X | X
     * ---+---+---
     *  O | X | O
     * </pre>
     *
     * X: cells 0, 2, 4, 5, 7 — no winning line.
     * O: cells 1, 3, 6, 8 — no winning line.
     */
    private void playDraw()
    {
        play(0,   // X → cell 0
             0,   // O → cell 1
             0,   // X → cell 2
             0,   // O → cell 3
             0,   // X → cell 4;  (verify: X=0,2,4 → 21, no win)
             1,   // O → cell 6;  empty: [5,6,7,8]
             0,   // X → cell 5;  empty: [5,7,8]
             1,   // O → cell 8;  empty: [7,8]
             0);  // X → cell 7;  empty: [7]  → board full, draw
    }

    // -------------------------------------------------------------------------
    // Initial state
    // -------------------------------------------------------------------------

    @Test
    public void initialBoardIsAllEmpty()
    {
        for (int i = 0; i < TicTacToe.CELLS; i++)
            assertEquals(TicTacToe.Cell.EMPTY, game.getCell(i));
    }

    @Test
    public void initialNumMovesIsNine()
    {
        assertEquals(9, game.getNumMoves());
    }

    @Test
    public void initialMovesListHasNineEntries()
    {
        assertEquals(9, game.getMoves().size());
    }

    @Test
    public void initialPlayerIsZero()
    {
        assertEquals(0, game.getCurPlayer());
    }

    @Test
    public void initialGameIsNotOver()
    {
        assertFalse(game.isOver());
    }

    @Test
    public void initialOutcomeIsAbsent()
    {
        assertFalse(game.getOutcome().isPresent());
    }

    @Test
    public void initialGetNumPlayersIsTwo()
    {
        assertEquals(2, game.getNumPlayers());
    }

    @Test
    public void getNameReturnsTicTacToe()
    {
        assertEquals("Tic-tac-toe", game.getName());
    }

    // -------------------------------------------------------------------------
    // getMoves() content
    // -------------------------------------------------------------------------

    @Test
    public void initialMovesListContainsAllCellIndices()
    {
        List<String> moves = game.getMoves();
        for (int i = 0; i < TicTacToe.CELLS; i++)
            assertTrue("expected cell " + i + " in initial moves", moves.contains(String.valueOf(i)));
    }

    @Test
    public void movesListExcludesOccupiedCell()
    {
        game.makeMove(0); // X occupies cell 0 (first empty = lowest index)
        assertFalse(game.getMoves().contains("0"));
    }

    @Test
    public void movesListShrinksByOneEachMove()
    {
        int expected = TicTacToe.CELLS;
        while (!game.isOver()) {
            assertEquals(expected, game.getMoves().size());
            game.makeMove(0);
            expected--;
        }
    }

    // -------------------------------------------------------------------------
    // getCurPlayer() / alternation
    // -------------------------------------------------------------------------

    @Test
    public void playerAlternatesAfterEachMove()
    {
        int expected = 0;
        while (!game.isOver()) {
            assertEquals(expected, game.getCurPlayer());
            game.makeMove(0);
            expected = 1 - expected;
        }
    }

    // -------------------------------------------------------------------------
    // Cell state
    // -------------------------------------------------------------------------

    @Test
    public void makeMoveSetsCrossForPlayerZero()
    {
        game.makeMove(0); // X → cell 0
        assertEquals(TicTacToe.Cell.CROSS, game.getCell(0));
    }

    @Test
    public void makeMoveSetNoughtForPlayerOne()
    {
        game.makeMove(0); // X → cell 0
        game.makeMove(0); // O → cell 1 (next empty)
        assertEquals(TicTacToe.Cell.NOUGHT, game.getCell(1));
    }

    @Test
    public void unplayedCellRemainsEmpty()
    {
        game.makeMove(0); // X → cell 0
        for (int i = 1; i < TicTacToe.CELLS; i++)
            assertEquals(TicTacToe.Cell.EMPTY, game.getCell(i));
    }

    @Test
    public void getCellRowColMatchesCellIndex()
    {
        game.makeMove(0); // X at cell 0 = (0,0);  empty: [1..8]
        game.makeMove(7); // O at cell 8 = (2,2);  empty: [1..8], idx 7 → cell 8

        for (int row = 0; row < TicTacToe.SIZE; row++)
            for (int col = 0; col < TicTacToe.SIZE; col++)
                assertEquals(game.getCell(TicTacToe.SIZE * row + col), game.getCell(row, col));
    }

    @Test
    public void getBoardMatchesGetCell()
    {
        game.makeMove(0); // place something so the board is not trivially empty
        TicTacToe.Cell[] board = game.getBoard();
        for (int i = 0; i < TicTacToe.CELLS; i++)
            assertEquals(game.getCell(i), board[i]);
    }

    // -------------------------------------------------------------------------
    // Winning conditions
    // -------------------------------------------------------------------------

    @Test
    public void xWinsTopRow()
    {
        playXWinsTopRow();

        assertTrue(game.isOver());
        GameResult[] outcome = game.getOutcome().orElseThrow();
        assertEquals(GameResult.WIN,  outcome[0]);
        assertEquals(GameResult.LOSS, outcome[1]);
    }

    @Test
    public void xWinsDiagonal()
    {
        playXWinsDiagonal();

        assertTrue(game.isOver());
        GameResult[] outcome = game.getOutcome().orElseThrow();
        assertEquals(GameResult.WIN,  outcome[0]);
        assertEquals(GameResult.LOSS, outcome[1]);
    }

    @Test
    public void oWinsCenterColumn()
    {
        playOWinsCenterColumn();

        assertTrue(game.isOver());
        GameResult[] outcome = game.getOutcome().orElseThrow();
        assertEquals(GameResult.LOSS, outcome[0]);
        assertEquals(GameResult.WIN,  outcome[1]);
    }

    @Test
    public void draw()
    {
        playDraw();

        assertTrue(game.isOver());
        GameResult[] outcome = game.getOutcome().orElseThrow();
        assertEquals(GameResult.DRAW, outcome[0]);
        assertEquals(GameResult.DRAW, outcome[1]);
    }

    // -------------------------------------------------------------------------
    // Game-over state
    // -------------------------------------------------------------------------

    @Test
    public void noMovesAfterWin()
    {
        playXWinsTopRow();
        assertEquals(0, game.getNumMoves());
        assertEquals(0, game.getMoves().size());
    }

    @Test
    public void noMovesAfterDraw()
    {
        playDraw();
        assertEquals(0, game.getNumMoves());
        assertEquals(0, game.getMoves().size());
    }

    @Test
    public void outcomeAbsentDuringPlay()
    {
        // Verify outcome stays absent until the game is actually over
        for (int i = 0; i < 4; i++) {
            assertFalse(game.getOutcome().isPresent());
            game.makeMove(0);
        }
    }

    // -------------------------------------------------------------------------
    // reset()
    // -------------------------------------------------------------------------

    @Test
    public void resetRestoresInitialState()
    {
        playXWinsTopRow();
        assertTrue(game.isOver());

        game.reset();

        assertFalse(game.isOver());
        assertEquals(0, game.getCurPlayer());
        assertEquals(9, game.getNumMoves());
        for (int i = 0; i < TicTacToe.CELLS; i++)
            assertEquals(TicTacToe.Cell.EMPTY, game.getCell(i));
    }

    // -------------------------------------------------------------------------
    // copy() independence
    // -------------------------------------------------------------------------

    @Test
    public void copyIsIndependentFromOriginal()
    {
        game.makeMove(0);      // X at cell 0
        TicTacToe copy = game.copy();
        copy.makeMove(0);      // O at cell 1 (on copy only)

        assertEquals(TicTacToe.Cell.CROSS,  game.getCell(0));
        assertEquals(TicTacToe.Cell.EMPTY,  game.getCell(1)); // original unaffected
        assertEquals(TicTacToe.Cell.CROSS,  copy.getCell(0));
        assertEquals(TicTacToe.Cell.NOUGHT, copy.getCell(1));
    }

    @Test
    public void originalIsIndependentFromCopy()
    {
        TicTacToe copy = game.copy();
        copy.makeMove(0); // X at cell 0 on copy

        assertEquals(TicTacToe.Cell.EMPTY, game.getCell(0)); // original unaffected
        assertEquals(9, game.getNumMoves());
    }

    // -------------------------------------------------------------------------
    // equals() / hashCode()
    // -------------------------------------------------------------------------

    @Test
    public void equalGamesHaveSameHashCode()
    {
        game.makeMove(0);
        TicTacToe other = game.copy();
        assertEquals(game, other);
        assertEquals(game.hashCode(), other.hashCode());
    }

    @Test
    public void differentGamesAreNotEqual()
    {
        TicTacToe other = new TicTacToe();
        game.makeMove(0);
        assertNotEquals(game, other);
    }

    // -------------------------------------------------------------------------
    // makeMove() validation
    // -------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void makeMoveNegativeIndexThrows()
    {
        game.makeMove(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void makeMoveOutOfRangeThrows()
    {
        game.makeMove(game.getNumMoves());
    }

    @Test(expected = IllegalArgumentException.class)
    public void makeMoveWhenGameOverThrows()
    {
        playXWinsTopRow();
        game.makeMove(0);
    }
}
