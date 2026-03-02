package net.davidrobles.mauler.othello.ef.wpc;

import net.davidrobles.mauler.othello.LinearEF;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.OthelloUtil;
import net.davidrobles.mauler.players.PlayersUtil;

/**
 * A weighted piece counter.
 */
public class WPC implements LinearEF<Othello>
{
    private double[] weights;

    /** The value of the squares with a disc from the player in turn. */
    public static final double BLACK_VALUE =  1.0;

    /** The value of the squares with a disc from the player not in turn. */
    public static final double WHITE_VALUE = -1.0;

    /** The value of empty squares. */
    public static final double EMPTY_VALUE =  0.0;

    private static final double WIN  =  1.0;
    private static final double LOSS = -1.0;
    private static final double DRAW =  0.0;

    /**
     * Creates a weighted piece counter (WPC). By default it creates
     * a symmetric WPC with zeroed weights.
     */
    public WPC()
    {
        this(WPCType.SYM);
    }

    /**
     * Creates a weighted piece counter (WPC) initialised to zero.
     */
    public WPC(WPCType type)
    {
        weights = WPCUtil.getZeroWeights(type);
    }

    /**
     * Creates a weighted piece counter (WPC) from the given weights.
     * The length of the weights must be 10 for symmetric or 64 for asymmetric.
     */
    public WPC(double[] weights)
    {
        setWeights(weights);
    }

    /**
     * Returns the type of the WPC. If the array of weights has a length
     * of 10 it is considered symmetric, if not is asymmetric (64).
     */
    public WPCType getType()
    {
        return weights.length == WPCType.SYM.getSize() ? WPCType.SYM : WPCType.ASYM;
    }

    public double[] getWeights()
    {
        return weights;
    }

    public WPC copy()
    {
        double[] weightsCopy = new double[weights.length];

        for (int i = 0; i < weightsCopy.length; i++)
            weightsCopy[i] = weights[i];

        return new WPC(weightsCopy);
    }

    public void setWeights(double[] weights)
    {
        if (weights.length == WPCType.SYM.getSize())
            this.weights = new double[WPCType.SYM.getSize()];
        else if (weights.length == WPCType.ASYM.getSize())
            this.weights = new double[WPCType.ASYM.getSize()];
        else
            throw new IllegalArgumentException(
                    "The size of the array of weights must be 10 (symmetric) or 64 (asymmetric).");

        System.arraycopy(weights, 0, this.weights, 0, weights.length);
    }

    /** Returns a formatted table with the results of the round robin tournament. */
    public String getFormattedWeights()
    {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < Othello.NUM_SQUARES; i++)
        {
            builder.append(String.format("%15f", weights[getType() == WPCType.SYM ? OthelloUtil.SYMMETRY_MAP[i] : i]));

            if (i % Othello.SIZE == Othello.SIZE - 1)
                builder.append("\n");
        }

        return builder.toString();
    }

    /**  Constructs a latex table of the results of the round robin tournament. */
    public String getLatexTable()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("\\begin{table}[!t]\n");
        builder.append("\\centering\n");
        builder.append("\\scriptsize\n");
        builder.append("\\begin{tabular}{|r|r|r|r|r|r|r|r|r|}\n");
        builder.append("\\hline\n");
        builder.append("");

        for (int i = 0; i < 8; i++)
            builder.append(" & \\multicolumn{1}{|c|}{" + (char) ('A' + i)  + "}");

        builder.append(" \\\\ \\hline \n");

        for (int i = 0; i < 64; i++)
        {
            if (i % 8 == 0)
                builder.append((i / 8 + 1) + " & ");

            if (getType() == WPCType.SYM)
                builder.append(String.format("%+.4f", weights[OthelloUtil.SYMMETRY_MAP[i]]));
            else
                builder.append(String.format("%+.4f", weights[i]));

            if (i % 8 == 7)
                builder.append(" \\\\\n");
            else
                builder.append(" & ");
        }

        builder.append("\\hline\n");
        builder.append("\\end{tabular}\n");
        builder.append("\\caption{FILL}\n");
        builder.append("\\label{tab:FILL}\n");
        builder.append("\\end{table}\n");
        return builder.toString();
    }

    public double[] getFeatures(Othello game)
    {
        double[] values = new double[Othello.NUM_SQUARES];

        for (int cellIndex = 0; cellIndex < values.length; cellIndex++)
        {
            Othello.Square cell = game.getSquare(cellIndex);

            if (cell == Othello.Square.EMPTY)
                values[cellIndex] = EMPTY_VALUE;
            else if (cell == Othello.Square.BLACK)
                values[cellIndex] = BLACK_VALUE;
            else
                values[cellIndex] = WHITE_VALUE;
        }

        return values;
    }

    ////////////////////////////////
    // Linear Evaluation Function //
    ////////////////////////////////

    public void updateWeights(Othello othello, double tdError)
    {
        double[] features = getFeatures(othello);

        if (getType() == WPCType.SYM)
            for (int squareIx = 0; squareIx < Othello.NUM_SQUARES; squareIx++)
                weights[OthelloUtil.SYMMETRY_MAP[squareIx]] += tdError * features[squareIx];
        else
            for (int squareIx = 0; squareIx < Othello.NUM_SQUARES; squareIx++)
                weights[squareIx] += tdError * features[squareIx];
    }

    /////////////////////////
    // Evaluation Function //
    /////////////////////////

    @Override
    public double eval(Othello othello, int player)
    {
        if (othello.isOver())
            return PlayersUtil.utility(othello, player, WIN, LOSS, DRAW);

        double boardValue = 0;
        final boolean sym = getType() == WPCType.SYM;

        for (int squareIndex = 0; squareIndex < Othello.NUM_SQUARES; squareIndex++)
        {
            Othello.Square square = othello.getSquare(squareIndex);

            if (square == Othello.Square.EMPTY)
                continue;

            // index of the square in the symmetric map
            int symIndex = sym ? OthelloUtil.SYMMETRY_MAP[squareIndex] : squareIndex;
            boardValue += weights[symIndex] * (square == Othello.Square.BLACK ? BLACK_VALUE : WHITE_VALUE);
        }

        boardValue = player == 0 ? boardValue : -boardValue;

        return Math.tanh(boardValue);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return String.format("<WPC type: %s>", getType());
    }
}
