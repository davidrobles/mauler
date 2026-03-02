package net.davidrobles.mauler.connect4;

import net.davidrobles.mauler.core.GameTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Connect4Test extends GameTest<Connect4>
{
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
        for (int i = 0; i < expectedBoard.length; i++)
            expectedBoard[i] = Connect4.Cell.EMPTY;
        assertArrayEquals(expectedBoard, new Connect4().getBoard());
    }
}
