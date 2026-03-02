package net.davidrobles.mauler.connect4;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.GameResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Connect4 implements Game<Connect4>
{
    public enum Cell { P1, P2, EMPTY }
    private int current;
    private long player1, player2, legal;
    private boolean gameOver = false;
    private GameResult[] outcomes;
    private int rows, cols, cellsCount, n;
    private List<Long> moves = new ArrayList<Long>();
    private static final long LEFT_MASK = -4432676798594L, RIGHT_MASK = -283691315109953L,
            UP_MASK = -128L, DOWN_MASK = -4363686772737L;

    public Connect4()
    {
        this(6, 7, 4);
    }

    public Connect4(int rows, int cols, int length)
    {
        cellsCount = rows * cols;

        if (cellsCount > 64)
            throw new IllegalArgumentException();

        this.rows = rows;
        this.cols = cols;
        this.n = length;

        reset();
    }

    // returns the type of the cell
    public Cell getCell(int cellIndex)
    {
        // black stone in the cell
        if ((player1 & (1L << cellIndex)) != 0L)
            return Cell.P1;

        // white stone in the cell
        if ((player2 & (1L << cellIndex)) != 0L)
            return Cell.P2;

        // no stones in the cell
        return Cell.EMPTY;
    }

    public Cell getCell(int row, int col)
    {
        return getCell(rows * row + cols * col);
    }

    public Cell[] getBoard()
    {
        Cell[] boardArray = new Cell[cellsCount];

        for (int i = 0; i < cellsCount; i++)
            boardArray[i] = getCell(i);

        return boardArray;
    }

    public List<Long> getBitMoves()
    {
        if (moves.isEmpty())
            for (int i = 0; i < cellsCount; i++)
                if ((legal & (1L << i)) != 0L)
                    moves.add(1L << i);

        return moves;
    }

    private long getCurrentPlayerBitboard()
    {
        return current == 0 ? player1 : player2;
    }

    private void setCurrentPlayerBitboard(long bitboard)
    {
        if (current == 0)
            player1 = bitboard;
        else
            player2 = bitboard;
    }

    public boolean checkWin(long bitboard, long moveBitboard)
    {
        long current = moveBitboard;
        int howMany = 1;

        // down

        while (true)
        {
            current = (current << cols) & UP_MASK;

            if ((current & bitboard) == 0L)
                break;

            if (++howMany == n)
                return true;
        }

        ////////////////////////////////////////////////////////////////////////

        // down left

        current = moveBitboard;
        howMany = 1;

        while (true)
        {
            current = (current << (cols - 1L)) & UP_MASK & RIGHT_MASK;

            if ((current & bitboard) == 0L)
                break;

            if (++howMany == n)
                return true;
        }

        // up right

        current = moveBitboard;

        while (true)
        {
            current = (current >> (cols - 1L)) & LEFT_MASK & DOWN_MASK;

            if ((current & bitboard) == 0L)
                break;

            if (++howMany == n)
                return true;
        }

        ////////////////////////////////////////////////////////////////////////

        // down right

        current = moveBitboard;
        howMany = 1;

        while (true)
        {
            current = (current << (cols + 1L)) & UP_MASK & LEFT_MASK;

            if ((current & bitboard) == 0L)
                break;

            if (++howMany == n)
                return true;
        }

        // up left

        current = moveBitboard;

        while (true)
        {
            current = (current >> (cols + 1L)) & RIGHT_MASK & DOWN_MASK;

            if ((current & bitboard) == 0L)
                break;

            if (++howMany == n)
                return true;
        }

        ////////////////////////////////////////////////////////////////

        // horizontal

        current = moveBitboard;
        howMany = 1;

        // left

        while (true)
        {
            current = (current >> 1L) & RIGHT_MASK;

            if ((current & bitboard) == 0L)
                break;

            if (++howMany == n)
                return true;
        }

        // right

        current = moveBitboard;

        while (true)
        {
            current = (current << 1L) & LEFT_MASK;

            if ((current & bitboard) == 0L)
                break;

            if (++howMany == n)
                return true;
        }


        return false;
    }


    //////////
    // Game //
    //////////

    @Override
    public Connect4 copy()
    {
        Connect4 connect4 = new Connect4();
        connect4.current = current;
        connect4.gameOver = gameOver;
        connect4.outcomes = gameOver ? new GameResult[] { outcomes[0], outcomes[1] } : null;
        connect4.player1 = player1;
        connect4.player2 = player2;
        connect4.legal = legal;

        return connect4;
    }

    @Override
    public int getCurPlayer()
    {
        return current;
    }

    @Override
    public int getNumPlayers()
    {
        return 2;
    }

    @Override
    public boolean isOver()
    {
        return gameOver;
    }

    @Override
    public int getNumMoves()
    {
        return Long.bitCount(legal);
    }

    @Override
    public List<String> getMoves() {
        List<String> moves = new ArrayList<>();
        for (int i = 0; i < getBitMoves().size(); i++)
            moves.add(String.valueOf(i));
        return List.copyOf(moves);
    }

    @Override
    public Optional<GameResult[]> getOutcome()
    {
        return gameOver ? Optional.of(outcomes) : Optional.empty();
    }

    @Override
    public void makeMove(int move)
    {
        if (move < 0 || move >= getBitMoves().size())
            throw new IllegalArgumentException("Illegal move!");

        long moveBitboard = getBitMoves().get(move);
        setCurrentPlayerBitboard(getCurrentPlayerBitboard() | moveBitboard);
        legal &= ~moveBitboard; // remove the move made
        moves.clear();

        if (checkWin(getCurrentPlayerBitboard(), moveBitboard))
        {
            outcomes = new GameResult[2];
            outcomes[getCurPlayer()] = GameResult.WIN;
            outcomes[(getCurPlayer() + 1) % 2] = GameResult.LOSS;
            gameOver = true;
            legal = 0L;
        }
        else
        {
            legal |= (moveBitboard >> cols); // add the new legal move

            if (Long.bitCount(legal) == 0)
            {
                outcomes = new GameResult[] { GameResult.DRAW, GameResult.DRAW };
                gameOver = true;
            }
        }

        current = (current + 1) % 2;
    }

    @Override
    public Connect4 newInstance()
    {
        return new Connect4();
    }

    @Override
    public void reset()
    {
        current = 0;
        player1 = 0L;
        player2 = 0L;
        legal = 4363686772736L;
        gameOver = false;
        outcomes = null;
        moves.clear();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(("Current player: " + getCurPlayer() + "\n"));
        builder.append("\n");

        for (long i = 0; i < cellsCount; i++)
        {
            if ((player1 & (1L << i)) != 0L)
                builder.append(" \u25C9 ");
            else if ((player2 & (1L << i)) != 0L)
                builder.append(" \u25CE ");
            else if ((legal & (1L << i)) != 0L)
                builder.append(" x ");
            else
                builder.append(" - ");

            if (i % cols == cols - 1)
                builder.append("\n");
        }

        return builder.toString();
    }

    ////////////
    // Object //
    ////////////


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connect4 connect4 = (Connect4) o;

        if (player1 != connect4.player1) return false;
        if (player2 != connect4.player2) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (player1 ^ (player1 >>> 32));
        result = 31 * result + (int) (player2 ^ (player2 >>> 32));
        return result;
    }

    @Override
    public String getName()
    {
        return "Connect-4";
    }

    ///////////////
    // DEBUGGING //
    ///////////////

    public void printBitBoard(Long b)
    {
        for (long i = 0; i < cellsCount; i++)
        {
            if ((b & (1L << i)) != 0)
                System.out.print(" X ");
            else
                System.out.print(" - ");

            if (i % cols == cols - 1)
                System.out.println();
        }

        System.out.println("\n");
    }
}
