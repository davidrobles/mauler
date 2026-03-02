package net.davidrobles.mauler.havannah;

import net.davidrobles.mauler.core.Outcome;

import java.util.*;

public class HavannahUtil
{
    private static final int MIN = 4, MAX = 10;
    private static HCell[][] cells = new HCell[MAX - MIN][];
    private static HCell[][][] boards = new HCell[MAX - MIN][][]; // 4, 5, 6, 7, 8, 9, 10
    @SuppressWarnings("unchecked")
    private static int[][][] adjs = new int[MAX - MIN][][];
    private static short[][] bits = new short[MAX - MIN][];
    private static Outcome[][] wins = new Outcome[MAX - MIN][(int)(Math.pow(2, 12))];

    public static void initHavannah(int size)
    {
        if (size < MIN && size > MAX)
            throw new IllegalArgumentException();

        // init board
        if (boards[size - MIN] == null)
        {
            initBoard(size);
            initLookupTable(size);
            initAdjs(size);
            initBits(size);
            initWins(size);
        }
    }

    private static void initWins(int size)
    {
        int index = size - MIN;

        for (int i = 0; i < wins[index].length; i++)
        {
            int count = 0;

            // corners

            for (int j = 0; j < 6; j++)
                if (((1 << j) & i) != 0)
                    count++;

            if (count > 1) {
                wins[index][i] = Outcome.WIN;
                continue;
            }

            // edges

            count = 0;

            for (int j = 6; j < 12; j++)
                if (((1 << j) & i) != 0)
                    count++;

            if (count > 2) {
                wins[index][i] = Outcome.WIN;
                continue;
            }

            // TODO: fix this
//            wins[index][i] = Outcome.NA;
        }
    }

    private static void initBits(int size)
    {
        int index = size - MIN;
        bits[index] = new short[cells[index].length];

        // fill with 0's
        for (int i = 0; i < bits[index].length; i++)
            bits[index][i] = 0;

        // 0
        bits[index][bits[index].length - 1] = 1;
        // 1
        bits[index][(bits[index].length / 2) + size - 1] = 1 << 1;
        // 2
        bits[index][size - 1] = 1 << 2;
        // 3
        bits[index][0] = 1 << 3;
        // 4
        bits[index][(bits[index].length / 2) - size + 1] = 1 << 4;
        // 5
        bits[index][bits[index].length - size] = 1 << 5;

        HCell[][] board = getBoard(size);

        // 6
        for (int row = board.length - 2; row > board.length - size; row--)
            bits[index][board[row][board.length - 1].index] = 1 << 6;

        // 7
        for (int row = 1; row < size - 1; row++)
            bits[index][board[row][size - 1 + row].index] = 1 << 7;

        // 8
        for (int col = 1; col < size - 1; col++)
            bits[index][board[0][col].index] = 1 << 8;

        // 9
        for (int row = 1; row < size - 1; row++)
            bits[index][board[row][0].index] = 1 << 9;

        // 10
        for (int row = 1; row < size - 1; row++)
            bits[index][board[size - 1 + row][row].index] = 1 << 10;

        // 11
        for (int col = board.length - size + 1; col < board.length - 1; col++)
            bits[index][board[board.length - 1][col].index] = 1 << 11;
    }

    private static void initBoard(int size)
    {
        HCell[][] newBoard = new HCell[size * 2 - 1][size * 2 - 1];

        // empty
        for (int row = 0; row < newBoard.length; row++)
            for (int col = 0; col < newBoard[0].length; col++)
                newBoard[row][col] = new HCell(row, col, Cell.EMPTY);

        // illegal
        for (int row = 0; row < size - 1; row++) {
            for (int col = 0; col < size - 1 - row; col++) {
                newBoard[size + col + row][col].type = Cell.ILLEGAL;
                newBoard[col][size + col + row].type = Cell.ILLEGAL;
            }
        }

        boards[size - MIN] = newBoard;
    }

    private static void initLookupTable(int size)
    {
        cells[size - MIN] = new HCell[getNumCells(size)];
        HCell[][] board = boards[size - MIN];
        int index = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col].type != Cell.ILLEGAL) {
                    board[row][col].index = index;
                    cells[size - MIN][index++] = board[row][col];
                }
            }
        }
    }

    public static int getNumCells(int size)
    {
        return (3 * size) * (size - 1) + 1; //        return (3 * size * size) - (3 * size) + 1;
    }

    private static void initAdjs(int size)
    {
        int index = size - MIN;
        adjs[index] = new int[cells[index].length][];

        for (int i = 0; i < cells[index].length; i++)
        {
            int row = cells[index][i].row;
            int col = cells[index][i].col;
            List<Integer> adjList = new ArrayList<Integer>();
            HCell[][] board = boards[index];

            // Add to North
            if (row > 0 && board[row - 1][col].type != Cell.ILLEGAL)
                adjList.add(board[row - 1][col].index);

            // Add to Left
            if (col > 0 && board[row][col - 1].type != Cell.ILLEGAL)
                adjList.add(board[row][col - 1].index);

            // Add to South
            if (row < board.length - 1 && board[row + 1][col].type != Cell.ILLEGAL)
                adjList.add(board[row + 1][col].index);

            // Add to Right
            if (col < board[0].length - 1 && board[row][col + 1].type != Cell.ILLEGAL)
                adjList.add(board[row][col + 1].index);

            // Add to South Right
            if (row < board.length - 1 && col < board[0].length - 1 && board[row + 1][col + 1].type != Cell.ILLEGAL)
                adjList.add(board[row + 1][col + 1].index);

            // Add to North Left
            if (row > 0 && col > 0 && board[row - 1][col - 1].type != Cell.ILLEGAL)
                adjList.add(board[row - 1][col - 1].index);

            int[] adjArray = new int[adjList.size()];

            for (int j = 0; j < adjList.size(); j++)
                adjArray[j] = adjList.get(j);

            adjs[index][i] = adjArray;
        }
    }

    public static CellWrapper[] getCells(int size)
    {
        CellWrapper[] cellsArray = new CellWrapper[getNumCells(size)];

        for (int i = 0; i < cellsArray.length; i++) {
            HCell cell = cells[size - MIN][i];
            cellsArray[i] = new CellWrapper(cell, cell.type);
        }

        return cellsArray;
    }

    public static short[] getBits(int size)
    {
        return bits[size - MIN];
    }

    public static int[][] getAdjacencies(int size)
    {
        return adjs[size - MIN];
    }

    public static HCell[][] getBoard(int size)
    {
        return boards[size - MIN];
    }
    
    public static Outcome[] getWins(int size)
    {
        return wins[size - MIN];
    }
}
