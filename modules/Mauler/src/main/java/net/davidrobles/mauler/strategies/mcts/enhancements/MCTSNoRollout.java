package net.davidrobles.mauler.strategies.mcts.enhancements;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.mcts.MCTS;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;
import net.davidrobles.mauler.strategies.mcts.selection.SelectionPolicy;

/**
 * MCTS variant that replaces the random rollout phase with a static evaluation function.
 *
 * <p>Standard MCTS plays out a complete random game (the "default policy") to estimate the
 * value of a leaf node. This class instead calls an {@link Evaluator} directly at the leaf,
 * which is faster per simulation and can incorporate domain knowledge without requiring
 * a full playout.
 *
 * <p>The evaluator is called from the perspective of the current player at the leaf node
 * ({@link Game#getCurPlayer()}). The evaluator's return value must follow the same sign
 * convention as rollout outcomes: positive values favor the leaf's current player.
 *
 * <p>The trade-off relative to random rollouts:
 * <ul>
 *   <li>More simulations per unit time (no playout needed)</li>
 *   <li>Value estimates are only as good as the evaluation function</li>
 *   <li>A weak evaluator can be worse than a random rollout</li>
 * </ul>
 *
 * @param <GAME> the game type
 *
 * @see <a href="https://doi.org/10.1109/TCIAIG.2012.2200891">Browne et al. (2012)
 *      "A Survey of Monte Carlo Tree Search Methods", §4.7 (Evaluation Functions)</a>
 * @see <a href="https://doi.org/10.1007/978-3-540-75538-8_7">Gelly &amp; Silver (2007)
 *      "Combining Online and Offline Knowledge in UCT"</a>
 */
public class MCTSNoRollout<GAME extends Game<GAME>> extends MCTS<GAME>
{
    private final Evaluator<GAME> evalFunc;

    /**
     * Creates a time-based instance (use {@link #move(Game, int)}).
     *
     * @param selectionPolicy the tree selection policy
     * @param evalFunc        the evaluation function used in place of a rollout;
     *                        must return higher values for states that are better
     *                        for the player passed as its second argument
     */
    public MCTSNoRollout(SelectionPolicy<GAME> selectionPolicy, Evaluator<GAME> evalFunc)
    {
        super(selectionPolicy, null);
        this.evalFunc = evalFunc;
    }

    /**
     * Creates a simulation-count instance.
     *
     * @param selectionPolicy the tree selection policy
     * @param evalFunc        the evaluation function used in place of a rollout
     * @param nSims           number of simulations per move
     */
    public MCTSNoRollout(SelectionPolicy<GAME> selectionPolicy, Evaluator<GAME> evalFunc, int nSims)
    {
        super(selectionPolicy, null, nSims);
        this.evalFunc = evalFunc;
    }

    //////////////////
    // AbstractMCTS //
    //////////////////

    /**
     * Scores the leaf node using the evaluation function instead of a random playout.
     *
     * @param node the leaf node to evaluate
     * @return the evaluation score from the perspective of {@code node}'s current player
     */
    @Override
    protected double rollout(MCTSNode<GAME> node)
    {
        return evalFunc.evaluate(node.getGame(), node.getGame().getCurPlayer());
    }

    /**
     * Returns a new {@code MCTSNoRollout} with the same configuration.
     *
     * <p>Overrides {@link MCTS#copy()} to avoid returning a plain {@code MCTS} with a
     * {@code null} rollout policy, which would crash when {@code rollout()} is invoked.
     */
    @Override
    public MCTS<GAME> copy()
    {
        return new MCTSNoRollout<>(selectionPolicy, evalFunc, nSims);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        if (nSims > 0)
            return String.format("<MCTSNoRollout selectionPolicy=%s evalFunc=%s nSims=%d>",
                    selectionPolicy, evalFunc, nSims);
        else
            return String.format("<MCTSNoRollout selectionPolicy=%s evalFunc=%s>",
                    selectionPolicy, evalFunc);
    }
}
