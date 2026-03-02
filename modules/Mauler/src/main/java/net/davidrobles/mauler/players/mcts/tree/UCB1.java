package net.davidrobles.mauler.players.mcts.tree;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.mcts.MCTSNode;

import java.util.Random;

public class UCB1<GAME extends Game<GAME>> implements TreePolicy<GAME>
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

    /////////////////
    // Tree Policy //
    /////////////////

    @Override
    public int move(MCTSNode<GAME> node, int player)
    {
        int bestMove = -1;
        boolean max = node.getGame().getCurPlayer() == player;
        double bestValue = max ? -Double.MAX_VALUE : Double.MAX_VALUE;
        double nb = 0;

        for (int move = 0; move < node.getGame().getNumMoves(); move++)
            nb += node.getActionCount(move);

        for (int move = 0; move < node.getGame().getNumMoves(); move++)
        {
            double value = 0;

            // ensures that each arm is selected once before further exploration
            if (node.getActionCount(move) == 0)
            {
                int bias = rng.nextInt(1000) + 10;
                value = max ? (100000000 - bias) : (-100000000 + bias);
            }
            else
            {
                double exploitation = node.getActionValue(move);
                double exploration = c * Math.sqrt(Math.log(nb) / (double) node.getActionCount(move));
                value += exploitation;
                value += max ? exploration : -exploration;
            }

            if (max)
            {
                if (value > bestValue) {
                    bestMove = move;
                    bestValue = value;
                }
            }
            else if (value < bestValue) { // min
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
        return String.format("<UCB1 c: %.2f>", c);
    }
}
