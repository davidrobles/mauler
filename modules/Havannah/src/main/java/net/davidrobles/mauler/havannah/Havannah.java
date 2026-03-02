package net.davidrobles.mauler.havannah;

import net.davidrobles.mauler.core.AbstractGame;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Outcome;

import java.util.*;

public class Havannah extends AbstractGame implements Game<Havannah>
{
    private int ply, size;
    private CellWrapper[] cells;
    public static final char BLACK_STONE = '\u25C9', WHITE_STONE = '\u25CE';
    private static Random rnd = new Random();
    @SuppressWarnings("unchecked")
    private Map<Set<Integer>, Short>[] connectionsMap = (Map<Set<Integer>, Short>[]) new HashMap[2];
    private int[][] adjs;
    private short[] bits;
    private HCell[][] board;
    private Outcome[] wins;

    private Havannah() {  }

    public Havannah(int size)
    {
        this.size = size;
        HavannahUtil.initHavannah(size);
        cells = HavannahUtil.getCells(size);
        initConnectionsSets();
        adjs = HavannahUtil.getAdjacencies(size);
        board = HavannahUtil.getBoard(size);
        bits = HavannahUtil.getBits(size);
        wins = HavannahUtil.getWins(size);
    }

    public int getBoardLength()
    {
        return size * 2 - 1;
    }

    private void initConnectionsSets()
    {
        connectionsMap[0] = new HashMap<Set<Integer>, Short>();
        connectionsMap[1] = new HashMap<Set<Integer>, Short>();
    }

    public int getSize()
    {
        return size;
    }

    private void randomizeBoard()
    {
        for (int i = 0; i < cells.length; i++)
            cells[i].type = Cell.values()[rnd.nextInt(3)];
    }
   
    private int emptyCells()
    {
        return legalMoves().size();
    }

    private List<Integer> legalMoves()
    {
        List<Integer> moves = new ArrayList<Integer>();

        for (int i = 0; i < cells.length; i++)
            if (cells[i].type == Cell.EMPTY)
                moves.add(i);

        return moves;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Current player: " + getCurPlayer() +  "\n");
        builder.append("Black sets: " + connectionsMap[0].size() + "\n");

        for (Set<Integer> sets : connectionsMap[0].keySet())
            builder.append(Arrays.toString(sets.toArray()));

        builder.append("\n");
        builder.append("White sets: " + connectionsMap[1].size() + "\n");

        for (Set<Integer> sets : connectionsMap[1].keySet())
            builder.append(Arrays.toString(sets.toArray()));

        builder.append("\n");
        builder.append("Ply: " + ply + "\n");
        builder.append("To move: " + getCurPlayer() + " (" + Cell.values()[getCurPlayer()] + ")\n");
        builder.append("Legal moves: " + legalMoves().size() + "\n\n");
        builder.append(" ");

        HCell[][] board = HavannahUtil.getBoard(size);

        for (int col = 1; col <= board.length; col++)
            builder.append((col >= 10 ? " " : "  ") + col);

        builder.append("\n");

        for (int row = 0; row < board.length; row++)
        {
            builder.append((char) ('a' + row) + " ");

            for (int col = 0; col < board[row].length; col++)
            {
                CellWrapper cell = cells[board[row][col].index];

                if (board[row][col].type == Cell.ILLEGAL)
                    builder.append("   ");
                else if (cell.type == Cell.BLACK)
                    builder.append(" " + BLACK_STONE + " ");
                else if (cell.type == Cell.WHITE)
                    builder.append(" " + WHITE_STONE + " ");
                else if (cell.type == Cell.EMPTY)
                    builder.append(" - ");
            }

            builder.append("\n");
        }

        return builder.toString();
    }

    public int getOppPlayer()
    {
        return ply % 2 == 0 ? 1 : 0;
    }

    ////////////////////
    // Game Interface //
    ////////////////////

    @Override
    public Havannah copy()
    {
        Havannah newHavannah = new Havannah(size);
        newHavannah.ply = ply;
        newHavannah.size = size;
        newHavannah.adjs = adjs;
        newHavannah.board = board;
        newHavannah.bits = bits;
        newHavannah.wins = wins;
        newHavannah.cells = new CellWrapper[cells.length];
        for (int i = 0; i < cells.length; i++)
            newHavannah.cells[i] = cells[i].copy();
        for (int i = 0; i < connectionsMap.length; i++) {
            newHavannah.connectionsMap[i] = new HashMap<Set<Integer>, Short>();
            for (Map.Entry<Set<Integer>, Short> entry : connectionsMap[i].entrySet()) {
                Set<Integer> newSet = new HashSet<Integer>(entry.getKey().size());
                for (Integer integer : entry.getKey())
                    newSet.add(integer);
                newHavannah.connectionsMap[i].put(newSet, entry.getValue());
            }
        }
        return newHavannah;
    }

    @Override
    public int getCurPlayer()
    {
        return ply % 2 == 0 ? 0 : 1;
    }

    @Override
    public String[] getMoves()
    {
        String[] moveStr = new String[getNumMoves()];

        for (int i = 0; i < moveStr.length; i++)
            moveStr[i] = "testing";

        return moveStr;
    }

    @Override
    public int getNumMoves()
    {
        return emptyCells();
    }

    @Override
    public String getName()
    {
        return "Havannah";
    }

    @Override
    public int getNumPlayers()
    {
        return 2;
    }

    @Override
    public Outcome[] getOutcome()
    {
        return new Outcome[0];
    }

    @Override
    public boolean isOver()
    {
        if (checkWin())
            return true;

        return emptyCells() == 0;
    }

    private void setCell(int cellIndex, Cell type)
    {
        cells[cellIndex].type = type;
    }

    public boolean checkWin()
    {
        for (Map.Entry<Set<Integer>, Short> entry : connectionsMap[getOppPlayer()].entrySet())
            if (wins[entry.getValue()] == Outcome.WIN)
                return true;

        return false;
    }

    public static void printBitsets(short s)
    {
        for (int i = 0; i < 12; i++)
            if (((1 << i) & s) != 0)
                System.out.print(1);
            else
                System.out.print(0);

        System.out.println();
    }
    
    @Override
    public void makeMove(int moveIndex)
    {
        int cellIndex = legalMoves().get(moveIndex); // TODO: check that move is legal
        setCell(cellIndex, Cell.values()[getCurPlayer()]); // TODO: temp fix
        Set<Integer> singletonSet = new HashSet<Integer>();
        singletonSet.add(cellIndex);
        short value = bits[cellIndex];
        int[][] adjs = HavannahUtil.getAdjacencies(size);

        for (int adj : adjs[cellIndex])
        {
            for (Iterator<Map.Entry<Set<Integer>,Short>> iterator = connectionsMap[getCurPlayer()].entrySet().iterator(); iterator.hasNext(); )
            {
                Map.Entry<Set<Integer>, Short> integerSet = iterator.next();

                if (integerSet.getKey().contains(adj))
                {
                    singletonSet.addAll(integerSet.getKey());
                    value |= integerSet.getValue();
                    iterator.remove();
                }
            }
        }

        connectionsMap[getCurPlayer()].put(singletonSet, value);
        ply++;
    }

    @Override
    public Havannah newInstance()
    {
        return new Havannah(size);
    }

    @Override
    public void reset()
    {
        ply = 0;
        initConnectionsSets();
        cells = HavannahUtil.getCells(size);
    }

    public static int numCells(int sideSize)
    {
        return 3 * sideSize * sideSize - 3 * sideSize + 1;
    }

    public Cell getCell(int row, int col)
    {
        HCell[][] board = HavannahUtil.getBoard(size);
        
        if (board[row][col].type == Cell.ILLEGAL)
            return Cell.ILLEGAL;
        
        return cells[board[row][col].index].type;
    }

    public static void main(String[] args)
    {
        Havannah havannah = new Havannah(4);
        System.out.println(havannah);

//        while (!havannah.isOver())
//        {
//            havannah.makeMove(rnd.nextInt(havannah.getNumMoves()));
//            System.out.println(havannah);
//        }
    }
}

