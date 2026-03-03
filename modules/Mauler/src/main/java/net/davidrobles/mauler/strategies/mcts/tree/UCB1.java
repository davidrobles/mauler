package net.davidrobles.mauler.strategies.mcts.tree;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;

import java.util.Random;

public class UCB1<GAME extends Game<GAME>> implements SelectionPolicy<GAME>
{
    private final double c;
    private final Random rng;

    public UCB1(double c, Random rng)
    {
        this.c = c;
        this.rng = rng;
    }

    public UCB1(double c)
    {
        this(c, new Random());
    }

    //////////////////////
    // SelectionPolicy  //
    //////////////////////

    @Override
    public int move(MCTSNode<GAME> node)
    {
        int bestMove = -1;
        double bestValue = Double.NEGATIVE_INFINITY;
        double parentVisits = 0;

        for (int move = 0; move < node.getGame().getNumMoves(); move++)
            parentVisits += node.getActionVisits(move);

        for (int move = 0; move < node.getGame().getNumMoves(); move++)
        {
            double value;

            // ensures that each arm is selected once before further exploration
            if (node.getActionVisits(move) == 0)
            {
                int bias = rng.nextInt(1000) + 10;
                value = 100000000 - bias;
            }
            else
            {
                double exploitation = -node.getActionValue(move);
                double exploration  = c * Math.sqrt(Math.log(parentVisits) / (double) node.getActionVisits(move));
                value = exploitation + exploration;
            }

            if (value > bestValue)
            {
                bestMove  = move;
                bestValue = value;
            }
        }
        return bestMove;
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return String.format("<UCB1 c: %.2f>", c);
    }
}
