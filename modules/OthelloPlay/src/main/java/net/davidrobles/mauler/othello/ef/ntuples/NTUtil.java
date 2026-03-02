package net.davidrobles.mauler.othello.ef.ntuples;

import com.google.gson.Gson;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.OthelloUtil;

import java.io.*;
import java.util.*;

/**
 * Utility methods used by N-tuple systems.
 */
public class NTUtil
{
    // TODO: fix names black white
    private static final String DIR_PATH = "OthelloSB/resources/ntuples/";

    /** The value of a square with a black disc. */
    private static final int BLACK_VALUE = 2;

    /** The value of a square with no disc. */
    private static final int EMPTY_VALUE = 1;

    /** The value of a square with a white disc. */
    private static final int WHITE_VALUE = 0;

    public static final Random RNG = new Random();

    public static void randomiseWeights(double[] weights)
    {
        Random rng = new Random();

        for (int i = 0; i < weights.length; i++)
            weights[i] = ((2 * rng.nextDouble()) - 1) / 8;
    }

    /**
     * Returns a list of tuples by applying all the possible
     * transformations to the given tuple.
     */
    public static int[][] expandTuple(int[] tuple)
    {
        Set<int[]> expansions = new HashSet<int[]>();
        expansions.add(tuple);

        for (NTTransform trans : NTTransform.values())
            expansions.add(expand(tuple, trans));

        return walksSetToMultiArray(expansions);
    }

    /** Applies a transformation to an N-tuple and returns the modified N-tuple. */
    public static int[] expand(int[] tuple, NTTransform transformation)
    {
        int[] expandedTuple = new int[tuple.length];

        for (int i = 0; i < expandedTuple.length; i++)
            expandedTuple[i] = transformation.transform(tuple[i]);

        return expandedTuple;
    }

    /**
     * Performs a random walk and returns the visited squares.
     * The same square can be visited more than once, but added
     * to the visited squares only during the first visit.
     * @param steps number of steps taken during the random walk
     * @return the indices of the squares of the random walk
     */
    public static int[] generateRandomWalk(int steps)
    {
        List<Integer> walkList = new ArrayList<Integer>();
        long current = randomSquareBitboard();

        for (int i = 1; i < steps; i++)
        {
            current = moveCellBitBoardRandomly(current);
            int cellIndex = Long.numberOfTrailingZeros(current);

            if (!walkList.contains(cellIndex))
                walkList.add(cellIndex);
        }

        return intListToArray(walkList);
    }

    /** Returns a bitboard with one of the 64 squares on. */
    public static long randomSquareBitboard()
    {
        return 1L << RNG.nextInt(Othello.NUM_SQUARES);
    }

    /**
     * Returns a new square bitboard by taking a random step
     * on the given squareBitboard.
     */
    public static long moveCellBitBoardRandomly(long squareBitboard)
    {
        OthelloUtil.Direction[] dirs = OthelloUtil.legals(squareBitboard);
        return dirs[RNG.nextInt(dirs.length)].shift(squareBitboard);
    }

    /**
     * Performs a random walk, creates a tuple of the visited squares,
     * creates all the expansions of that tuple and returns an
     * NTuple with the walk and the expansions.
     */
    public static NTuple generateNTuple(int steps)
    {
        int[] tuple = generateRandomWalk(steps);
        int[][] tuples = expandTuple(tuple);
        return new NTuple(tuples);
    }

    /** Returns an array of NTuple's from the given tuples. */
    public static NTuple[] createNTuples(int[][] tuples)
    {
        NTuple[] nTuples = new NTuple[tuples.length];

        for (int i = 0; i < tuples.length; i++)
            nTuples[i] = new NTuple(tuples[i]);

        return nTuples;
    }

    /**
     * Returns an array of {@link NTuple} from the given <code>bitTuples</code>.
     * Ready to be used to create an {@link NTupleSystem}.
     */
    public static NTuple[] createNTuples(long[] bitTuples)
    {
        int[][] newPatterns = new int[bitTuples.length][];

        for (int i = 0; i < bitTuples.length; i++)
        {
            int[] cells = new int[Long.bitCount(bitTuples[i])];

            for (int j = 0, count = 0; j < Othello.NUM_SQUARES; j++)
                if (((1L << j) & bitTuples[i]) != 0L)
                    cells[count++] = j;

            newPatterns[i] = cells;
        }

        return createNTuples(newPatterns);
    }

    public static NTupleSystem generateRandomNTupleSystem(int numNTuples, int steps)
    {
        return new NTupleSystem(generateNTuples(numNTuples, steps));
    }

    public static NTuple[] generateNTuples(int numNTuples, int steps)
    {
        NTuple[] nTuples = new NTuple[numNTuples];

        for (int i = 0; i < numNTuples; i++)
            nTuples[i] = generateNTuple(steps);

        return nTuples;
    }

    public static int[] boardToNTupleValues(Othello othello)
    {
        Othello.Square[] cells = othello.getBoard();
        int[] board = new int[Othello.NUM_SQUARES];

        for (int i = 0; i < cells.length; i++)
        {
            Othello.Square cell = cells[i];

            if (cell == Othello.Square.BLACK)
                board[i] = BLACK_VALUE;
            else if (cell == Othello.Square.EMPTY)
                board[i] = EMPTY_VALUE;
            else if (cell == Othello.Square.WHITE)
                board[i] = WHITE_VALUE;
        }

        return board;
    }

    /////////////////
    // PERSISTENCE //
    /////////////////

    /** Loads a saved N-tuple system from the filename of a JSON file. */
    public static NTupleSystem load(String filename)
    {
        return load(new File(DIR_PATH + filename + ".json"));
    }

    /** Loads a saved N-tuple system from a JSON file. */
    public static NTupleSystem load(File file)
    {
        Gson gson = new Gson();
        Reader reader = null;

        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return gson.fromJson(reader, NTupleSystem.class);
    }

    /** Saves an N-tuple system to a file */
    public static void save(NTupleSystem nts, String filename)
    {
        Gson gson = new Gson();

        try {
            FileWriter writer = new FileWriter(DIR_PATH + filename + ".json");
            writer.append(gson.toJson(nts));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////////////////////
    // HELPER METHODS //
    ////////////////////

    private static int[][] walksSetToMultiArray(Set<int[]> walks)
    {
        int i = 0;
        int[][] expansionsArray = new int[walks.size()][];

        for (int[] expansion : walks)
            expansionsArray[i++] = expansion;

        return expansionsArray;
    }

    /**
     * Returns an array of integers from a {@link java.util.List} of
     * integers.
     * @param list a list of integers
     * @return an array of integers
     */
    private static int[] intListToArray(List<Integer> list)
    {
        int[] newArray = new int[list.size()];

        for (int i = 0; i < newArray.length; i++)
            newArray[i] = list.get(i);

        return newArray;
    }

    /////////////
    // TESTING //
    /////////////

    /**
     * Prints the squares on the Othello board of the given tuple.
     * Printed to the standard output.
     */
    public static void printWalk(int[] tuple)
    {
        long walksBitboard = 0L;

        for (Integer cellIndex : tuple)
            walksBitboard |= 1L << cellIndex;

        OthelloUtil.printBitBoard(walksBitboard);
    }

    /** Prints all the n-tuples of the given N-tuple system. */
    public static void printNTupleSystem(NTupleSystem nts)
    {
        for (NTuple nTuple : nts.getNTuples())
        {
            for (int[] tuple : nTuple.getTuples())
                NTUtil.printWalk(tuple);

            System.out.println("---------------");
        }
    }

    public static void printWeightsPerLength()
    {
        System.out.format("%10s %10s\n", "Length", "No. weights");

        for (int i = 1; i <= 10; i++)
            System.out.format("%10s %10s\n", i, Math.pow(3, i));
    }

    private static void testWalks()
    {
        int[] walk = NTUtil.generateRandomWalk(6);
        System.out.println("Expansions\n");
        int[][] expansions = NTUtil.expandTuple(walk);

        for (int[] expansion : expansions) {
            System.out.println(Arrays.toString(expansion));
            NTUtil.printWalk(expansion);
        }
    }

    public static void main(String[] args)
    {
        testWalks();
    }
}
