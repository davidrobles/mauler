package net.davidrobles.mauler.strategies.mcts.enhancements;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.mcts.selection.UCB1;

/**
 * Convenience subclass of {@link MCTSNoRollout} that uses UCB1 as the selection policy.
 *
 * <p>Equivalent to constructing {@code new MCTSNoRollout<>(new UCB1<>(c), evalFunc)}.
 *
 * @param <GAME> the game type
 *
 * @see MCTSNoRollout
 * @see UCB1
 */
public class UCTNoRollout<GAME extends Game<GAME>> extends MCTSNoRollout<GAME>
{
    /**
     * Creates a time-based instance (use {@link #move(Game, int)}).
     *
     * @param c        the UCB1 exploration constant
     * @param evalFunc the evaluation function used in place of a rollout
     */
    public UCTNoRollout(double c, Evaluator<GAME> evalFunc)
    {
        super(new UCB1<>(c), evalFunc);
    }

    /**
     * Creates a simulation-count instance.
     *
     * @param c        the UCB1 exploration constant
     * @param evalFunc the evaluation function used in place of a rollout
     * @param nSims    number of simulations per move
     */
    public UCTNoRollout(double c, Evaluator<GAME> evalFunc, int nSims)
    {
        super(new UCB1<>(c), evalFunc, nSims);
    }
}
