package net.davidrobles.mauler.players.mcts.tree;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.mcts.MCTSNode;

import java.util.Random;

public class GreedyTreePolicy<GAME extends Game<GAME>> implements TreePolicy<GAME>
{
    private Random rng;

    public GreedyTreePolicy(Random rng)
    {
        this.rng = rng;
    }

    /////////////////
    // Tree Policy //
    /////////////////

    @Override
    public int move(MCTSNode<GAME> node, int player)
    {
        int bestMove = -1;
        boolean max = node.getGame().getCurPlayer() == player;
        double bestValue = max ? Integer.MIN_VALUE : Double.MAX_VALUE;

        for (int move = 0; move < node.getGame().getNumMoves(); move++)
        {
            double value;

            // ensures that each arm is selected once before further exploration
            if (node.getActionCount(move) == 0)
            {
                int bias = rng.nextInt(1000) + 10;
                value = max ? (100000000 - bias) : (-100000000 + bias);
            } else {
                value = max ? node.getActionValue(move) : -node.getActionValue(move);
            }

            if (max) { // max
                if (value > bestValue) {
                    bestMove = move;
                    bestValue = value;
                }
            } else if (value < bestValue) { // min
                bestMove = move;
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
