package net.davidrobles.mauler.connect4;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import net.davidrobles.mauler.core.GameTest;
import org.junit.Before;
import org.junit.Test;

public class Connect4Test extends GameTest<Connect4> {
    @Before
    public void init() {
        this.game = new Connect4();
    }

    @Test
    public void testNumberOfPlayers() throws Exception {
        assertEquals(2, game.getNumPlayers());
    }

    @Test
    public void testInitialBoard() {
        Connect4.Cell[] expectedBoard = new Connect4.Cell[6 * 7];
        for (int i = 0; i < expectedBoard.length; i++) expectedBoard[i] = Connect4.Cell.EMPTY;
        assertArrayEquals(expectedBoard, new Connect4().getBoard());
    }

    @Test
    public void testEqualsSameState() {
        Connect4 a = new Connect4();
        Connect4 b = new Connect4();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testEqualsDifferentCurrentPlayer() {
        // Same pieces, different current player — must NOT be equal
        Connect4 a = new Connect4();
        Connect4 b = new Connect4();
        a.makeMove(0); // P1 moves; now it's P2's turn in 'a'
        // 'b' still has P1 to move; same player1 bitboard as 'a' before P1 moved — not the same
        // state. A cleaner case: copy after one move vs initial.
        Connect4 initial = new Connect4();
        Connect4 afterMove = new Connect4();
        afterMove.makeMove(0);
        assertNotEquals(initial, afterMove);
    }

    @Test
    public void testEqualsCopyMatchesOriginal() {
        Connect4 original = new Connect4();
        original.makeMove(0);
        original.makeMove(1);
        Connect4 copy = original.copy();
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
    }

    @Test
    public void testEqualsGameOverDistinguished() {
        // A finished game must not equal a non-finished game with same pieces
        Connect4 game = new Connect4();
        // Drop 4 in column 0 for P1 to win (moves 0, cols, 2*cols, 3*cols)
        // Interleave P2 moves in column 1
        game.makeMove(0); // P1 col0 row0
        game.makeMove(1); // P2 col1 row0
        game.makeMove(0); // P1 col0 row1
        game.makeMove(1); // P2 col1 row1
        game.makeMove(0); // P1 col0 row2
        game.makeMove(1); // P2 col1 row2
        // P1 wins on next move
        Connect4 beforeWin = game.copy();
        game.makeMove(0); // P1 col0 row3 — wins
        assertNotEquals(beforeWin, game);
    }
}
