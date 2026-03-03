package net.davidrobles.mauler.strategies.mcts.enhancements;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.strategies.mcts.selection.UCB1;

/**
 * UCT with prior knowledge initialization.
 *
 * <p>Specializes {@link MCTSPrior} with UCB1 as the selection policy. Constructors
 * without a {@code rolloutPolicy} argument use {@link RandomStrategy} as the default.
 *
 * @param <GAME> the game type
 * @see MCTSPrior
 */
public class UCTPrior<GAME extends Game<GAME>> extends MCTSPrior<GAME>
{
    /**
     * Creates a time-based UCTPrior with a random rollout policy.
     *
     * @param c           UCB1 exploration constant
     * @param priorEF     evaluator used to seed each newly expanded node
     * @param initQVisits number of virtual simulations the prior represents
     */
    public UCTPrior(double c, Evaluator<GAME> priorEF, int initQVisits)
    {
        super(new UCB1<>(c), new RandomStrategy<>(), priorEF, initQVisits);
    }

    /**
     * Creates a simulation-count UCTPrior with a random rollout policy.
     *
     * @param c           UCB1 exploration constant
     * @param priorEF     evaluator used to seed each newly expanded node
     * @param initQVisits number of virtual simulations the prior represents
     * @param nSims       number of simulations per move
     */
    public UCTPrior(double c, Evaluator<GAME> priorEF, int initQVisits, int nSims)
    {
        super(new UCB1<>(c), new RandomStrategy<>(), priorEF, initQVisits, nSims);
    }

    /**
     * Creates a time-based UCTPrior with a custom rollout policy.
     *
     * @param rolloutPolicy the rollout (default) policy
     * @param c             UCB1 exploration constant
     * @param priorEF       evaluator used to seed each newly expanded node
     * @param initQVisits   number of virtual simulations the prior represents
     */
    public UCTPrior(Strategy<GAME> rolloutPolicy, double c, Evaluator<GAME> priorEF, int initQVisits)
    {
        super(new UCB1<>(c), rolloutPolicy, priorEF, initQVisits);
    }

    /**
     * Creates a simulation-count UCTPrior with a custom rollout policy.
     *
     * @param rolloutPolicy the rollout (default) policy
     * @param c             UCB1 exploration constant
     * @param priorEF       evaluator used to seed each newly expanded node
     * @param initQVisits   number of virtual simulations the prior represents
     * @param nSims         number of simulations per move
     */
    public UCTPrior(Strategy<GAME> rolloutPolicy, double c, Evaluator<GAME> priorEF, int initQVisits, int nSims)
    {
        super(new UCB1<>(c), rolloutPolicy, priorEF, initQVisits, nSims);
    }
}
