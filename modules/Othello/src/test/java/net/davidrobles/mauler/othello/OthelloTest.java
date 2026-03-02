package net.davidrobles.mauler.othello;

import net.davidrobles.mauler.core.GameTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class OthelloTest extends GameTest<Othello>
{
    @Before
    public void init()
    {
        this.game = new Othello();
    }

    @Test
    public void testNumberOfPlayers() throws Exception
    {
        assertEquals(2, game.getNumPlayers());
    }

    @Test
    public void testInitialBoard()
    {
        Othello.Square[] board = createBoard();
        initBoard(board);
        assertArrayEquals(board, new Othello().getBoard());
    }

    private Othello.Square[] createBoard()
    {
        Othello.Square[] board = new Othello.Square[Othello.NUM_SQUARES];

        for (int i = 0; i < Othello.NUM_SQUARES; i++)
            board[i] = Othello.Square.EMPTY;

        return board;
    }

    private void initBoard(Othello.Square[] board)
    {
        board[27] = Othello.Square.WHITE;
        board[28] = Othello.Square.BLACK;
        board[35] = Othello.Square.BLACK;
        board[36] = Othello.Square.WHITE;
    }
}
