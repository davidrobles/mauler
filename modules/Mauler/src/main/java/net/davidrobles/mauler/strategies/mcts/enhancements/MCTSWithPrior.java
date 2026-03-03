package net.davidrobles.mauler.strategies.mcts.enhancements;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.mcts.MCTS;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;
import net.davidrobles.mauler.strategies.mcts.selection.SelectionPolicy;

/**
 * MCTS with prior knowledge initialization.
 *
 * <p>Extends the standard MCTS expansion step: when a node is first visited,
 * it is seeded with a prior evaluation score and {@code initQVisits} virtual
 * simulations before any real rollouts are counted. This biases the parent's
 * UCB1 selection toward promising nodes (high prior) and away from poor ones
 * (low prior) during early search.
 *
 * <p>The prior's influence decays as real simulations accumulate. After
 * {@code n} real simulations through the node, the running mean value is:
 * <pre>
 *   Q(s) ≈ (priorValue * initQVisits + sum(outcomes)) / (initQVisits + n)
 * </pre>
 * A larger {@code initQVisits} makes the prior persist longer; a smaller value
 * lets real simulation results take over sooner.
 *
 * <p>References:
 * <ul>
 *   <li>Coulom (2006). "Efficient Selectivity and Backup Operators in
 *       Monte-Carlo Tree Search." <em>CG 2006</em>, LNCS 4630:72&ndash;83.
 *       §5 introduces virtual wins/losses — seeding nodes with a prior count
 *       — which is the technique implemented here.
 *   <li>Gelly &amp; Silver (2007). "Combining Online and Offline Knowledge in UCT."
 *       <em>ICML 2007</em>. Uses an offline value function to bias UCT node
 *       selection; closely related to prior knowledge initialization.
 *   <li>Browne et al. (2012). "A Survey of Monte Carlo Tree Search Methods."
 *       <em>IEEE TCIAIG</em>, 4(1):1&ndash;43. §6.1 surveys prior knowledge
 *       techniques, including value initialization and virtual visits.
 * </ul>
 *
 * @param <GAME> the game type
 * @see UCTPrior
 */
public class MCTSPrior<GAME extends Game<GAME>> extends MCTS<GAME>
{
    private final Evaluator<GAME> priorEF;
    private final int             initQVisits;

    /**
     * Creates a time-based MCTSPrior instance (use {@link #move(Game, int)}).
     *
     * @param selectionPolicy the UCB1 or other selection policy
     * @param rolloutPolicy   the rollout (default) policy
     * @param priorEF         evaluator used to seed each newly expanded node
     * @param initQVisits     number of virtual simulations the prior represents;
     *                        must be &ge; 1
     * @throws IllegalArgumentException if {@code initQVisits} is less than 1
     */
    public MCTSPrior(SelectionPolicy<GAME> selectionPolicy, Strategy<GAME> rolloutPolicy,
                     Evaluator<GAME> priorEF, int initQVisits)
    {
        super(selectionPolicy, rolloutPolicy);
        if (initQVisits < 1)
            throw new IllegalArgumentException("initQVisits must be >= 1, got: " + initQVisits);
        this.priorEF     = priorEF;
        this.initQVisits = initQVisits;
    }

    /**
     * Creates a simulation-count MCTSPrior instance.
     *
     * @param selectionPolicy the UCB1 or other selection policy
     * @param rolloutPolicy   the rollout (default) policy
     * @param priorEF         evaluator used to seed each newly expanded node
     * @param initQVisits     number of virtual simulations the prior represents;
     *                        must be &ge; 1
     * @param nSims           number of simulations per move
     * @throws IllegalArgumentException if {@code initQVisits} is less than 1
     */
    public MCTSPrior(SelectionPolicy<GAME> selectionPolicy, Strategy<GAME> rolloutPolicy,
                     Evaluator<GAME> priorEF, int initQVisits, int nSims)
    {
        super(selectionPolicy, rolloutPolicy, nSims);
        if (initQVisits < 1)
            throw new IllegalArgumentException("initQVisits must be >= 1, got: " + initQVisits);
        this.priorEF     = priorEF;
        this.initQVisits = initQVisits;
    }

    /**
     * Expands {@code node} and seeds it with prior knowledge.
     *
     * <p>After creating the node's children, the node itself (which is a child
     * of the previously selected node) is initialized with:
     * <ul>
     *   <li>A value from {@code priorEF}, from the current player's perspective.
     *   <li>{@code initQVisits} virtual visits, as if that many simulations
     *       had already returned the prior value.
     * </ul>
     * The parent's UCB1 formula will use these seeded statistics when deciding
     * whether to revisit this node, giving the prior a head start over
     * unvisited siblings.
     */
    @Override
    protected void expand(MCTSNode<GAME> node)
    {
        node.expand();
        node.setValue(priorEF.evaluate(node.getGame(), node.getGame().getCurPlayer()));
        node.setVisits(initQVisits);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        if (nSims > 0)
            return String.format("<MCTSPrior selectionPolicy=%s rolloutPolicy=%s priorEF=%s initQVisits=%d nSims=%d>",
                    selectionPolicy, rolloutPolicy, priorEF, initQVisits, nSims);
        else
            return String.format("<MCTSPrior selectionPolicy=%s rolloutPolicy=%s priorEF=%s initQVisits=%d>",
                    selectionPolicy, rolloutPolicy, priorEF, initQVisits);
    }
}
