package net.davidrobles.mauler.othello.ef.ntuples;

import com.google.gson.Gson;
import net.davidrobles.mauler.othello.LinearEF;
import net.davidrobles.mauler.othello.Othello;

import java.util.*;

/**
 * An N-Tuple is a sequence of N squares on the Othello board.
 */
public class NTuple implements LinearEF<Othello>
{
    private int[][] tuples;
    private double[] weights;

    /** If true, Math.tanh is applied before evaluating the game. */
    public boolean applyTanh = false;

    /**
     * Creates an N-tuple with the given sample points. It automatically
     * creates all the expansions for the given tuple.
     */
    public NTuple(int[] tuple)
    {
        this(NTUtil.expandTuple(tuple));
    }

    /**
     * Creates an N-tuple with the given tuples. The tuples are usually
     * the expansions of one of the tuples, but it is not a requirement.
     */
    public NTuple(int[][] tuples)
    {
        this.tuples = tuples;
        weights = new double[(int) Math.pow(Othello.Square.values().length, getTuplesLength())];
//        NTUtil.randomiseWeights(weights);
    }

    public int[][] getTuples()
    {
        return tuples;
    }

    /** Returns a deep copy of this N-tuple */
    public NTuple copy()
    {
        int[][] tuplesCopy = new int[tuples.length][tuples[0].length];

        for (int i = 0; i < tuplesCopy.length; i++)
            for (int j = 0; j < tuplesCopy[i].length; j++)
                tuplesCopy[i][j] = tuples[i][j];

        return new NTuple(tuplesCopy);
    }

    /**  Returns the number of squares of the tuple. All the tuples must have the same length. */
    public int getTuplesLength()
    {
        return tuples[0].length;
    }

    /** Returns the number of weights in the N-tuple. */
    public int getNumWeights()
    {
        return weights.length;
    }

    /** Returns the index in the vector of weights for the given sample points. */
    private int address(int[] nTuple, int[] board)
    {
        int address = 0;

        for (int square : nTuple)
        {
            address *= Othello.Square.values().length;
            address += board[square];
        }

        return address;
    }

    public String toBoardStr()
    {
        StringBuilder builder = new StringBuilder();
        Set<Integer> discs = new HashSet<Integer>();

        for (int row = 0; row < tuples.length; row++)
            for (int col = 0; col < tuples[row].length; col++)
                discs.add(tuples[row][col]);

        List<Integer> list = new ArrayList<Integer>();
        list.addAll(discs);
        Collections.sort(list);
        System.out.println(Arrays.toString(list.toArray()));

        for (int disc = 0; disc < Othello.NUM_SQUARES; disc++)
        {
            if (list.contains(disc))
                builder.append("p");
            else
                builder.append("-");

            if (disc % Othello.SIZE == 7)
                builder.append("\n");
        }

        return builder.toString();
    }

    public void reset()
    {
        for (int i = 0; i < weights.length; i++)
            weights[i] = 0;
    }

    //////////////
    // LinearEF //
    //////////////

    @Override
    public void updateWeights(Othello othello, double tdError)
    {
        int[] board = NTUtil.boardToNTupleValues(othello);

        for (int[] tuple : tuples)
            weights[address(tuple, board)] += tdError;
    }

    //////////////
    // EvalFunc //
    //////////////

    @Override
    public double eval(Othello othello, int player)
    {
        double value = 0;
        int[] board = NTUtil.boardToNTupleValues(othello);

        for (int[] tuple : tuples)
            value += weights[address(tuple, board)];

        return applyTanh ? Math.tanh(value) : value;
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }

    public static void main(String[] args)
    {
        int[] array = { 1 };
        NTuple nTuple = new NTuple(array);
        System.out.println(nTuple.weights.length);
    }
}
