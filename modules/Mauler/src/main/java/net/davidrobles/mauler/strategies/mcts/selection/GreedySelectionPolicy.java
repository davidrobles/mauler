package net.davidrobles.mauler.strategies.mcts.selection;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;

import java.util.Random;

public class GreedySelectionPolicy<GAME extends Game<GAME>> implements SelectionPolicy<GAME>
{
    private Random rng;

    public GreedySelectionPolicy(Random rng)
    {
        this.rng = rng;
    }

    //////////////////////
    // SelectionPolicy  //
    //////////////////////

    @Override
    public int move(MCTSNode<GAME> node)
    {
        int bestMove = -1;
        double bestValue = Double.NEGATIVE_INFINITY;

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
                value = -node.getActionValue(move);
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
        return "<Greedy>";
    }
}
