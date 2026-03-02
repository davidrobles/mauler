package net.davidrobles.mauler.loa;

import net.davidrobles.mauler.core.GameTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LOATest extends GameTest<LOA>
{
    @Before
    public void initialiase()
    {
        this.game = new LOA();
    }

    @Test
    public void testInitialBoard()
    {
        LOA.Cell[] expectedBoard = new LOA.Cell[64];

        for (int i = 0; i < expectedBoard.length; i++)
            expectedBoard[i] = LOA.Cell.EMPTY;

        for (int black : new int[] {1, 2, 3, 4, 5, 6, 57, 58, 59, 60, 61, 62})
            expectedBoard[black] = LOA.Cell.BLACK;

        for (int white : new int[] {8, 16, 24, 32, 40, 48, 15, 23, 31, 39, 47, 55})
            expectedBoard[white] = LOA.Cell.WHITE;

        assertArrayEquals(expectedBoard, new LOA().getBoard());
    }


}
