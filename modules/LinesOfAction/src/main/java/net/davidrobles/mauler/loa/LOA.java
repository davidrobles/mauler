package net.davidrobles.mauler.loa;

import net.davidrobles.mauler.core.AbstractGame;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Outcome;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LOA extends AbstractGame implements Game<LOA>
{
    public enum Cell { BLACK, WHITE, EMPTY }
    private boolean gameOver;
    private int current;
    private long black, white;
    private Outcome[] outcomes;

    public static final int SIDE_SIZE = 8;
    public static final int N_CELLS = 64;

    long[] rows = new long[] {
        255L,
        65280L,
        16711680L,
        4278190080L,
        1095216660480L,
        280375465082880L,
        71776119061217280L,
        -72057594037927936L
    };

    long[] cols = new long[] {
        72340172838076673L,
        144680345676153346L,
        289360691352306692L,
        578721382704613384L,
        1157442765409226768L,
        2314885530818453536L,
        4629771061636907072L,
        -9187201950435737472L
    };

    // /
    long[] diagRight = new long[] {
        1L,
        258L,
        66052L,
        16909320L,
        4328785936L,
        1108169199648L,
        283691315109952L,
        72624976668147840L,
        145249953336295424L,
        290499906672525312L,
        580999813328273408L,
        1161999622361579520L,
        2323998145211531264L,
        4647714815446351872L,
        -9223372036854775808L
    };

    // \
    long[] diagLeft = new long[] {
        128L,
        32832L,
        8405024L,
        2151686160L,
        550831656968L,
        141012904183812L,
        36099303471055874L,
        -9205322385119247871L,
        4620710844295151872L,
        2310355422147575808L,
        1155177711073755136L,
        577588855528488960L,
        288794425616760832L,
        144396663052566528L,
        72057594037927936L
    };

    int[] rowsCount = new int[8];
    int[] colsCount = new int[8];
    int[] diagLeftCount = new int[15];
    int[] diagRightCount = new int[15];

    public LOA() {
        reset();
    }

    /**
     * Returns an array of 64 cells representing each cell of the board.
     * @return an array of 64 cells representing each cell of the board
     */
    public Cell[] getBoard() {
        Cell[] board = new Cell[N_CELLS];
        for (int i = 0; i < N_CELLS; i++)
            board[i] =  getCell(i);
        return board;
    }

    /**
     * Returns the type of the cell for the given cell index. The index goes from 0 to 63.
     * 0 representing the top left cell, and 63 the bottom right cell.
     * @param cellIndex the index of the cell
     * @return the type of the cell for the given cell index
     */
    public Cell getCell(int cellIndex) {
        if ((black & (1L << cellIndex)) != 0L)
            return Cell.BLACK;
        if ((white & (1L << cellIndex)) != 0L)
            return Cell.WHITE;
        return Cell.EMPTY;
    }

    /**
     * Returns the type of the cell for the given cell location. Rows order goes from top to bottom,
     * with 0 being the top row and 7 the bottom row. Columns order goes from left to right, with
     * 0 being the first column and 7 being the last column.
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the type of the cell for the given cell location
     */
    public Cell getCell(int row, int col) {
        return getCell(LOA.SIDE_SIZE * row + col);
    }

    /**
     * Returns a bitboard with the pieces of the player in turn. For example, if the player to move is 0 (X's),
     * it will return a bitboard (64 bit long), with 1's where there are black pieces, and 0's where there are
     * either empty cells or white pieces.<br /><br />
     * The following board:<br /><br />
     * - X - X - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - 0 - <br />
     * - - - 0 - - - - <br />
     * - - - - - - - - <br />
     * - 0 - - 0 - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br /><br />
     * would return:<br /><br />
     * 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00001010
     *
     * @return a bitboard with the pieces of the player in turn
     */
    private long getCurrentBitboard() {
        return current == 0 ? black : white;
    }

    /**
     * Returns a bitboard with the pieces of the player NOT in turn. For example, if the player to move is 0 (X's),
     * it will return a bitboard (64 bit long), with 1's where there are white pieces, and 0's where there are
     * either empty cells or black pieces.<br /><br />
     * The following board:<br /><br />
     * - X - X - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - 0 - <br />
     * - - - 0 - - - - <br />
     * - - - - - - - - <br />
     * - 0 - - 0 - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br /><br />
     * would return:<br /><br />
     * 00000000 00000000 00010010 00000000 00001000 01000000 00000000 00000000
     *
     * @return a bitboard with the pieces of the player NOT in turn
     */
    private long getOpponentBitboard() {
        return current == 0 ? white : black;
    }


    /**
     * Sets the bitboard of the player in turn. It must be a long representing all the pieces of the player in turn.
     * @param bitboard the new bitboard
     */
    private void setCurrentBitboard(long bitboard) {
        if (current == 0)
            black = bitboard;
        else
            white = bitboard;
    }

    /**
     * Sets the bitboard of the player NOT in turn. It must be a long representing
     * all the pieces of the player NOT in turn.
     * @param bitboard the new bitboard
     */
    private void setOpponentBitboard(long bitboard) {
        if (current == 0)
            white = bitboard;
        else
            black = bitboard;
    }

    /**
     * Counts the number of checkers for each of the lines.
     */
    private void countCheckers() {
        for (int i = 0; i < SIDE_SIZE; i++) {
            rowsCount[i] = Long.bitCount((black | white) & rows[i]);
            colsCount[i] = Long.bitCount((black | white) & cols[i]);
        }
        for (int i = 0; i < 15; i++) {
            diagLeftCount[i] = Long.bitCount((black | white) & diagLeft[i]);
            diagRightCount[i] = Long.bitCount((black | white) & diagRight[i]);
        }
    }

    List<long[]> moves = new ArrayList<long[]>();

    private void calculateLegalMoves() {
        moves.clear();
        countCheckers();
        long curBitboard = getCurrentBitboard();
        long oppBitboard = getOpponentBitboard();

        for (int i = 0; i < N_CELLS; i++)
        {
            long cur = (curBitboard & (1L << i));

            if (cur != 0L)
            {
                int row = i / SIDE_SIZE;
                int col = i % SIDE_SIZE;
                int diagRightIndex = row + col;
                int diagLeftIndex = 7 - col + row;

                // up
                int count = colsCount[col];
                long candidateMove = (cur >> (SIDE_SIZE * count));

                if (candidateMove != 0L && ((candidateMove & curBitboard) == 0L))
                {
                    boolean test = true;
                    long tmp = cur;

                    for (int j = 0; j < count - 1; j++)
                    {
                        tmp = (tmp >> SIDE_SIZE);

                        if ((tmp & oppBitboard) != 0L)
                        {
                            test = false;
                            break;
                        }
                    }

                    if (test)
                        moves.add(new long[] { cur, candidateMove });
                }

                // down
                candidateMove = (cur << (SIDE_SIZE * count));

                if (candidateMove != 0L && ((candidateMove & curBitboard) == 0L))
                {
                    boolean test = true;
                    long tmp = cur;

                    for (int j = 0; j < count - 1; j++)
                    {
                        tmp = (tmp << SIDE_SIZE);

                        if ((tmp & oppBitboard) != 0L)
                        {
                            test = false;
                            break;
                        }
                    }

                    if (test)
                        moves.add(new long[] { cur, candidateMove });
                }

                // left
                count = rowsCount[row];
                candidateMove = (cur >> count) & rows[row];

                if (candidateMove != 0L && ((candidateMove & curBitboard) == 0L))
                {
                    boolean test = true;
                    long tmp = cur;

                    for (int j = 0; j < count - 1; j++)
                    {
                        tmp = (tmp >> 1);

                        if ((tmp & oppBitboard) != 0L)
                        {
                            test = false;
                            break;
                        }
                    }

                    if (test)
                        moves.add(new long[] { cur, candidateMove });
                }

                // right
                count = rowsCount[row];
                candidateMove = (cur << count) & rows[row];

                if (candidateMove != 0L && ((candidateMove & curBitboard) == 0L))
                {
                    boolean test = true;
                    long tmp = cur;

                    for (int j = 0; j < count - 1; j++)
                    {
                        tmp = (tmp << 1);

                        if ((tmp & oppBitboard) != 0L)
                        {
                            test = false;
                            break;
                        }
                    }

                    if (test)
                        moves.add(new long[] { cur, candidateMove });
                }

                // top right
                count = diagRightCount[diagRightIndex];
                candidateMove = (cur >> (SIDE_SIZE * count - count)) & diagRight[diagRightIndex];

                if (candidateMove != 0L && ((candidateMove & curBitboard) == 0L))
                {
                    boolean test = true;
                    long tmp = cur;

                    for (int j = 0; j < count - 1; j++)
                    {
                        tmp = (tmp >> (SIDE_SIZE - 1));

                        if ((tmp & oppBitboard) != 0L)
                        {
                            test = false;
                            break;
                        }
                    }

                    if (test)
                        moves.add(new long[] { cur, candidateMove });
                }

                // top left
                count = diagLeftCount[diagLeftIndex];
                candidateMove = (cur >> (SIDE_SIZE * count + count)) & diagLeft[diagLeftIndex];

                if (candidateMove != 0L && ((candidateMove & curBitboard) == 0L))
                {
                    boolean test = true;
                    long tmp = cur;

                    for (int j = 0; j < count - 1; j++)
                    {
                        tmp = (tmp >> (SIDE_SIZE + 1));

                        if ((tmp & oppBitboard) != 0L)
                        {
                            test = false;
                            break;
                        }
                    }

                    if (test)
                        moves.add(new long[] { cur, candidateMove });
                }

                // down right
                count = diagLeftCount[diagLeftIndex];
                candidateMove = (cur << (SIDE_SIZE * count + count)) & diagLeft[diagLeftIndex];

                if (candidateMove != 0L && ((candidateMove & curBitboard) == 0L))
                {
                    boolean test = true;
                    long tmp = cur;

                    for (int j = 0; j < count - 1; j++)
                    {
                        tmp = (tmp << (SIDE_SIZE + 1));

                        if ((tmp & oppBitboard) != 0L)
                        {
                            test = false;
                            break;
                        }
                    }

                    if (test)
                        moves.add(new long[] { cur, candidateMove });
                }

                // down left
                count = diagRightCount[diagRightIndex];
                candidateMove = (cur << (SIDE_SIZE * count - count)) & diagRight[diagRightIndex];

                if (candidateMove != 0L && ((candidateMove & curBitboard) == 0L))
                {
                    boolean test = true;
                    long tmp = cur;

                    for (int j = 0; j < count - 1; j++)
                    {
                        tmp = (tmp << (SIDE_SIZE - 1));

                        if ((tmp & oppBitboard) != 0L)
                        {
                            test = false;
                            break;
                        }
                    }

                    if (test)
                        moves.add(new long[] { cur, candidateMove });
                }
            }
        }
    }

    private void printEntry(Map.Entry<Long, List<Long>> entry)
    {
        long current = entry.getKey();
        long legal = 0L;

        for (Long move : entry.getValue())
            legal |= move;

        StringBuilder builder = new StringBuilder();

        builder.append("   ");

        for (int i = 0; i < SIDE_SIZE; i++)
            builder.append((" " + ((char) ('A' + i)) + " "));

        builder.append("\n");

        for (int i = 0; i < N_CELLS; i++)
        {
            if (i % SIDE_SIZE == 0)
                builder.append((" " + (i / SIDE_SIZE + 1) + " "));

            if ((current & (1L << i)) != 0)
                builder.append(" \u25C9 ");
            else if ((legal & (1L << i)) != 0)
                builder.append(" x ");
            else
                builder.append(" - ");

            if (i % SIDE_SIZE == SIDE_SIZE - 1)
                builder.append((" " + (i / SIDE_SIZE + 1) + " \n"));
        }

        builder.append("   ");

        for (int i = 0; i < SIDE_SIZE; i++)
            builder.append((" " + (char) ('A' + i) + " "));

        builder.append("\n");

        System.out.println(builder);

//        return builder.toString();
    }

    /**
     * Returns the index of the given move. The move is given in a bitboard representation.<br /><br />
     * Example:<br /><br />
     * if the bitboard is:<br /><br />
     * - - - - - - - - <br />
     * - X - - - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br /><br />
     * 00000000 00000000 00000000 00000000 00000000 00000000 00000010 00000000<br /><br />
     * this method will return:<br /><br />
     * 9, which is the index of the cell
     * @param bitboard a bitboard with only one bit ON, which represents one cell
     * @return the index of the given move
     */
    private int cellBitToIndex(Long bitboard)
    {
        return Long.numberOfTrailingZeros(bitboard);
    }

    int count;

    private int countFlood(long bitboard)
    {
        count = 0;

        for (int i = 0; i < N_CELLS; i++)
        {
            long move = (1L << i);

            if ((move & bitboard) != 0L)
            {
                floodFill(bitboard, move, 0L);
                break;
            }
        }

        return count;
    }

    private void floodFill(long bitboard, long node, long visited)
    {
        if (((node & visited) != 0L) || (bitboard & node) == 0L)
            return;

        visited |= node;
        count++;

//        // up
        floodFill(bitboard, node >> SIDE_SIZE, visited);

        // down
        floodFill(bitboard, node << SIDE_SIZE, visited);

        // right
        floodFill(bitboard, node << 1, visited);

        // left
        floodFill(bitboard, node >> 1, visited);

        // up right
        floodFill(bitboard, node >> 7, visited);

        // up left
        floodFill(bitboard, node >> 9, visited);

        // down right
        floodFill(bitboard, node << 9, visited);

        // down left
        floodFill(bitboard, node << 7, visited);
    }

    private boolean checkWin(long bitboard)
    {
//        int countFlood = countFlood(bitboard);
//        int bitCount = Long.bitCount(bitboard);
//
//        if (countFlood == bitCount)
//        {
//            System.out.println("Count flood: " + countFlood);
//            System.out.println("Bit count:   " + bitCount);
//            return true;
//        }

//        return false;
        return countFlood(bitboard) == Long.bitCount(bitboard);
    }

    private void changePlayer()
    {
        current = (current + 1) % 2;
    }

    /**
     * Returns the index of the player NOT in turn.
     * @return the index of the player NOT in turn
     */
    public int getOppositePlayer()
    {
        return (current + 1) % 2;
    }

    /**
     * Returns a python-style string representation of the array of all the legal moves.<br /><br />
     * For example: <br /><br />
     * [a5-e3, b3-c5, d6-a2]
     * @return a string of the legal moves
     */
    private String getMovesString()
    {
        if (moves.isEmpty())
            return "[]";

        StringBuilder builder = new StringBuilder("[");

        for (String str : getMoves())
            builder.append((str + ", "));

        return builder.substring(0, builder.length() - 2) + "]";
    }

    /**
     * Returns the number of checkers of the given player.
     * @param playerIndex the index of the player
     * @return the number of checkers of the given player
     */
    public int getCount(int playerIndex)
    {
        return playerIndex == 0 ? Long.bitCount(black) : Long.bitCount(white);
    }

    /**
     * Returns the coordinates of the source cell of the given move.<br /><br />
     * Example: <br /><br />
     * "a8-c8" => (row: 0, col: 0)<br />
     * "a5-g5" => (row: 3, col: 0)<br />
     * "e3-d5" => (row: 5, col: 4)<br /><br />
     * @param moveStr a string representation of a move
     * @return the coordinates of the source cell of the given move.
     */
    public Point srcMoveToPoint(String moveStr)
    {
        int row = 8 - Integer.valueOf(String.valueOf(moveStr.charAt(1)));
        int col = getCol(moveStr.charAt(0));
        return new Point(col, row);
    }

    /**
     * Returns the coordinates of the target cell of the given move.<br /><br />
     * Example: <br /><br />
     * "a8-c8" => (row: 0, col: 2)<br />
     * "a5-g5" => (row: 3, col: 6)<br />
     * "e3-d5" => (row: 3, col: 3)<br /><br />
     * @param moveStr a string representation of a move
     * @return the coordinates of the target cell of the given move.
     */
    public Point dstMoveToPoint(String moveStr)
    {
        int row = 8 - Integer.valueOf(String.valueOf(moveStr.charAt(4)));
        int col = getCol(moveStr.charAt(3));
        return new Point(col, row);
    }

    /**
     * Returns the index of the column represented by the given character.<br /><br />
     * Example:<br /><br />
     * 'a' => 0<br />
     * 'd' => 3<br /><br />
     * @param letter the character of the column
     * @return the index of the column
     */
    private int getCol(char letter)
    {
        for (int i = 0; i < SIDE_SIZE; i++)
            if ((char) ('a' + (i)) == letter)
                return i;

        return -838448;
    }

    /**
     * Returns a collection of the legal moves for the given cell.
     * For example: filterMoves(3, 2) could return a Collection ["c5-c3", "c5-c7", "c5-a5"]
     * @param row the row of the cell
     * @param col the col of the cell
     * @return a collection of the legal moves for the given cell
     */
    public Collection<String> filterMoves(int row, int col)
    {
        List<String> newMoves = new ArrayList<String>();

        for (String move : getMoves())
            if (cellToString(row, col).equals(move.substring(0, 2)))
                newMoves.add(move);

        return newMoves;
    }

    ////////////
    // Player //
    ////////////

    @Override
    public LOA copy()
    {
        LOA newLOA = new LOA();
        newLOA.current = current;
        newLOA.black = black;
        newLOA.white = white;
        newLOA.gameOver = gameOver;

        for (long[] move : moves)
            newLOA.moves.add(new long[] { move[0], move[1] });

        return newLOA;
    }

    @Override
    public int getCurPlayer()
    {
        return current;
    }

    @Override
    public String[] getMoves()
    {
        List<String> legalMoves = new ArrayList<String>();

        for (long[] move : moves)
            legalMoves.add(cellToString(cellBitToIndex(move[0])) + "-" + cellToString(cellBitToIndex(move[1])));

        return legalMoves.toArray(new String[legalMoves.size()]);
    }

    @Override
    public int getNumMoves()
    {
        return moves.size();
    }

    @Override
    public int getNumPlayers()
    {
        return 2;
    }

    @Override
    public String getName()
    {
        return "Lines of Action";
    }

    @Override
    public Outcome[] getOutcome()
    {
        return outcomes;
    }

    @Override
    public boolean isOver()
    {
        return gameOver;
    }

    @Override
    public void makeMove(int move)
    {
        long[] curMove = moves.get(move);
        setCurrentBitboard((getCurrentBitboard() & ~curMove[0]) | curMove[1]);
        setOpponentBitboard(getOpponentBitboard() & ~curMove[1]);

        if (checkWin(getCurrentBitboard()) && checkWin(getOpponentBitboard()))
        {
            gameOver = true;
            outcomes[0] = Outcome.DRAW;
            outcomes[1] = Outcome.DRAW;
        }
        else if (checkWin(getCurrentBitboard()))
        {
            gameOver = true;
            outcomes[getCurPlayer()] = Outcome.WIN;
            outcomes[getOppositePlayer()] = Outcome.LOSS;
        }
        else if (checkWin(getOpponentBitboard()))
        {
            gameOver = true;
            outcomes[getCurPlayer()] = Outcome.LOSS;
            outcomes[getOppositePlayer()] = Outcome.WIN;
        }

        moves.clear();
        changePlayer();

        if (!gameOver)
        {
            calculateLegalMoves();

            if (moves.isEmpty())
            {
                changePlayer();
                calculateLegalMoves();

                if (moves.isEmpty())
                {
                    gameOver = true;
                    outcomes[0] = Outcome.DRAW;
                    outcomes[1] = Outcome.DRAW;
                }
                else
                {
                    moves.clear();
                    moves.add(null);
                }

                changePlayer();
            }
        }
    }

    @Override
    public LOA newInstance()
    {
        return new LOA();
    }

    @Override
    public void reset()
    {
        current = 0;
        black = 9079256848778920062L;
        white = 36452665219186944L;
        gameOver = false;
        outcomes = new Outcome[] { Outcome.NA, Outcome.NA };
        calculateLegalMoves();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Legal moves: %s\n", getMovesString()));
        builder.append(String.format("Blacks count: %d\n", Long.bitCount(black)));
        builder.append(String.format("Whites count: %d\n\n", Long.bitCount(white)));

//        if (legal == PASS)
//            builder.append(String.format("Moves count: 1 (Pass)\n"));
//        else
//            builder.append(String.format("Moves count: %d\n\n", Long.bitCount(legal)));

        builder.append("   ");

        for (int i = 0; i < SIDE_SIZE; i++)
            builder.append((" " + ((char) ('a' + i)) + " "));

        builder.append("\n");

        for (int i = 0; i < N_CELLS; i++)
        {
            if (i % SIDE_SIZE == 0)
                builder.append((" " + (8 - (i / SIDE_SIZE)) + " "));

            if ((black & (1L << i)) != 0)
                builder.append(" \u25C9 ");
            else if ((white & (1L << i)) != 0)
                builder.append(" \u25CE ");
            else
                builder.append(" - ");

            if (i % SIDE_SIZE == SIDE_SIZE - 1)
                builder.append((" " + (8 - (i / SIDE_SIZE)) + " \n"));
        }

        builder.append("   ");

        for (int i = 0; i < SIDE_SIZE; i++)
            builder.append((" " + (char) ('a' + i) + " "));

        builder.append("\n");

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

        LOA loa = (LOA) o;

        if (black != loa.black) return false;
        if (current != loa.current) return false;
        if (gameOver != loa.gameOver) return false;
        if (white != loa.white) return false;

        return true;
    }

    /**
     * Resets the state of the game. It resets both bitboards: black and white.<br /><br />
     * Black bitboard to:<br /><br />
     * - X X X X X X - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br />
     * - - - - - - - - <br />
     * - X X X X X X - <br /><br />
     * 01111110 00000000 00000000 00000000 00000000 00000000 00000000 01111110<br /><br />
     * and the white bitboard to:<br /><br />
     * - - - - - - - - <br />
     * 0 - - - - - - 0 <br />
     * 0 - - - - - - 0 <br />
     * 0 - - - - - - 0 <br />
     * 0 - - - - - - 0 <br />
     * 0 - - - - - - 0 <br />
     * 0 - - - - - - 0 <br />
     * - - - - - - - - <br /><br />
     * 00000000 10000001 10000001 10000001 10000001 10000001 10000001 00000000<br /><br />
     */
    public void setInitialBoard()
    {
        int[] blackInit = new int[] { 1, 2, 3, 4, 5, 6, 57, 58, 59, 60, 61, 62 };

        for (int blackIndex : blackInit)
            black |= (1L << blackIndex);

        int[] whiteInit = new int[] { 8, 16, 24, 32, 40, 48, 15, 23, 31, 39, 47, 55 };

        for (int whiteIndex : whiteInit)
            white |= (1L << whiteIndex);
    }

    /////////////////////
    // UTILITY METHODS //
    /////////////////////

    /**
     * Converts the given cell to a String representation.<br /><br />
     * Examples:<br /><br />
     * 0  => 'a8'<br />
     * 8  => 'a7'<br />
     * 13 => 'f7'<br />
     * 63 => 'h1'<br />
     * @param cellIndex the index of the cell to convert
     * @return the string representation of the given cell
     */
    public static String cellToString(int cellIndex)
    {
        return String.format("%c%d", (char) ('a' + cellIndex % SIDE_SIZE), (8 - (cellIndex / SIDE_SIZE)));
    }

    /**
     * Converts the coordinates of a cell to a String representation.<br /><br />
     * Examples:<br /><br />
     * (0, 0) => 'a8'<br />
     * (2, 4) => 'e6'
     * @param row the row of the cell to convert
     * @param col the column of the cell to convert
     * @return the string representation of the given cell
     */
    public static String cellToString(int row, int col)
    {
        return cellToString(cellCoordsToIndex(row, col));
    }

    /**
     * Converts the coordinates of a cell of the board to an index representing the same cell in the board.
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the index of the given cell
     */
    private static int cellCoordsToIndex(int row, int col)
    {
        return SIDE_SIZE * row + col;
    }

    ///////////////
    // DEBUGGING //
    ///////////////

    /**
     * Displays the given bitboard with ON bits as X's and OFF bits as -'s.
     * @param bitboard the bitboard to be displayed
     */
    public static void printBitBoard(Long bitboard)
    {
        for (long i = 0; i < N_CELLS; i++)
        {
            if ((bitboard & (1L << i)) != 0)
                System.out.print(" X ");
            else
                System.out.print(" - ");

            if (i % SIDE_SIZE == SIDE_SIZE - 1)
                System.out.println();
        }

        System.out.println("\n");
    }

    public static void main(String[] args)
    {
        Random rng = new Random();
        LOA loa = new LOA();
        System.out.println(loa);

        while (!loa.isOver())
        {
            loa.makeMove(rng.nextInt(loa.getNumMoves()));
            System.out.println(loa);
        }

        System.out.println(Arrays.toString(loa.getOutcome()));
    }
}
