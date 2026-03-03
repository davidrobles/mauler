package net.davidrobles.mauler.strategies.mcts.selection;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;

import java.util.Random;

/**
 * A purely greedy selection policy: always descends into the child with the
 * highest mean value Q(s,a), with no exploration bonus.
 *
 * <p>Equivalent to {@link UCB1} with c&nbsp;=&nbsp;0. Useful as a baseline
 * and in contexts where the tree is already well-explored, but tends to
 * under-explore and converge prematurely in practice.
 *
 * <p>Any unvisited arm is treated as having infinite value, and one is chosen
 * uniformly at random among them via reservoir sampling.
 *
 * <p>References:
 * <ul>
 *   <li>Browne et al. (2012). "A Survey of Monte Carlo Tree Search Methods."
 *       <em>IEEE TCIAIG</em>, 4(1):1&ndash;43. §3.2 discusses the exploration
 *       constant and notes that c&nbsp;=&nbsp;0 reduces UCT to pure exploitation.
 * </ul>
 */
public class GreedySelectionPolicy<GAME extends Game<GAME>> implements SelectionPolicy<GAME>
{
    private final Random rng;

    public GreedySelectionPolicy(Random rng)
    {
        this.rng = rng;
    }

    public GreedySelectionPolicy()
    {
        this(new Random());
    }

    //////////////////////
    // SelectionPolicy  //
    //////////////////////

    @Override
    public int move(MCTSNode<GAME> node)
    {
        int unvisited = randomUnvisited(node);
        return unvisited != -1 ? unvisited : bestGreedy(node);
    }

    /**
     * Returns a uniformly random unvisited arm, or -1 if all arms have been visited.
     * Uses reservoir sampling for uniform selection in a single O(k) pass.
     */
    private int randomUnvisited(MCTSNode<GAME> node)
    {
        int selected = -1;
        int count    = 0;

        for (int move = 0; move < node.getGame().getNumMoves(); move++)
        {
            if (node.getActionVisits(move) == 0)
            {
                count++;
                if (rng.nextInt(count) == 0)
                    selected = move;
            }
        }

        return selected;
    }

    /** Returns the arm with the highest mean value (pure exploitation, no exploration term). */
    private int bestGreedy(MCTSNode<GAME> node)
    {
        int    bestMove  = 0;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (int move = 0; move < node.getGame().getNumMoves(); move++)
        {
            // Negate: getActionValue returns Q from the child's (opponent's) perspective;
            // the zero-sum property means the current player's value is its negation.
            double value = -node.getActionValue(move);

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
        return "<GreedySelection>";
    }
}
