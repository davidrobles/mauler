package net.davidrobles.mauler.othello.ef.ntuples;

import net.davidrobles.mauler.othello.LinearEF;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.players.PlayersUtil;

// TODO: write code to convert this to Simon's format

/**
 * An N-tuple system.
 */
public class NTupleSystem implements LinearEF<Othello>
{
    protected final NTuple[] nTuples;

    private static final double WIN  =  1.0,
                                LOSS = -1.0,
                                DRAW =  0.0;

    public NTupleSystem(NTuple[] nTuples)
    {
        this.nTuples = nTuples;
    }

    public NTuple[] getNTuples()
    {
        return nTuples;
    }

    public void reset()
    {
        for (NTuple nTuple : nTuples)
            nTuple.reset();
    }

    public void setNTuple(int index, NTuple nTuple)
    {
        nTuples[index] = nTuple;
    }

    /** Returns the number of weights in all the n-tuples. */
    public int getNumWeights()
    {
        int count = 0;

        for (NTuple nTuple : nTuples)
            count += nTuple.getNumWeights();

        return count;
    }

    /** Calculates the average length of the N-tuples of this system. */
    public double getLengthAvg()
    {
        int total = 0;

        for (NTuple nTuple : nTuples)
            total += nTuple.getTuplesLength();

        return total / (double) nTuples.length;
    }

    /** Returns a deep copy of the N-tuple system. */
    public NTupleSystem copy()
    {
        NTuple[] nTuplesCopy = new NTuple[nTuples.length];

        for (int i = 0; i < nTuplesCopy.length; i++)
            nTuplesCopy[i] = nTuples[i].copy();

        return new NTupleSystem(nTuplesCopy);
    }

    public String getInfo()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Weights: %d\n", getNumWeights()));
        builder.append(String.format("No. N-tuples: %d\n", nTuples.length));
        builder.append(String.format("Length average: %.1f\n", getLengthAvg()));
        return builder.toString();
    }

    public String toCoolBoard()
    {
        StringBuilder builder = new StringBuilder();

        for (NTuple nTuple : nTuples)
            builder.append(nTuple.toBoardStr() + "\n");

        return builder.toString();
    }

    //////////////
    // LinearEF //
    //////////////

    @Override
    public void updateWeights(Othello othello, double tdError)
    {
        for (NTuple nTuple : nTuples)
            nTuple.updateWeights(othello, tdError);
    }

    //////////////
    // EvalFunc //
    //////////////

    @Override
    public double eval(Othello othello, int player)
    {
        if (othello.isOver())
            return PlayersUtil.utility(othello, player, WIN, LOSS, DRAW);

        double value = 0;

        for (NTuple nTuple : nTuples)
            value += nTuple.eval(othello, player);

        value = player == 0 ? value : -value;

        return Math.tanh(value);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return String.format("<NTS numTuples: %d, numWeights: %d>", nTuples.length, getNumWeights());
    }
}
