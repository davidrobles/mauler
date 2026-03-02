package net.davidrobles.mauler.tictactoe;

import net.davidrobles.mauler.core.GameTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TicTacToeTest extends GameTest<TicTacToe> {

    @Before
    public void init() {
        this.game = new TicTacToe();
    }

    @Test
    public void testNumberOfPlayers() throws Exception {
        assertEquals(2, game.getNumPlayers());
    }

    @Test
    public void testInitialBoard() {
        TicTacToe.Cell[] expectedBoard = new TicTacToe.Cell[9];
        for (int i = 0; i < expectedBoard.length; i++)
            expectedBoard[i] = TicTacToe.Cell.EMPTY;
        assertArrayEquals(expectedBoard, new TicTacToe().getBoard());
    }
}
