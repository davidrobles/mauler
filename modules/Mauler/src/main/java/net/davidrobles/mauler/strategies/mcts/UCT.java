package net.davidrobles.mauler.strategies.mcts;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.strategies.mcts.selection.UCB1;

/**
 * Upper Confidence Bounds for Trees (UCT).
 *
 * <p>Specialises {@link MCTS} with the {@link UCB1} tree policy and a
 * uniformly random rollout policy. Constructors without a {@code rolloutPolicy}
 * argument use {@link RandomStrategy} as the default rollout policy.
 *
 * <p>Two operational modes are supported:
 * <ul>
 *   <li>Simulation-count — pass {@code nSims}; {@link #move(Game)} runs exactly
 *       that many simulations.</li>
 *   <li>Time-based — omit {@code nSims}; use {@link #move(Game, int)} with a
 *       millisecond budget.</li>
 * </ul>
 */
public class UCT<GAME extends Game<GAME>> extends MCTS<GAME>
{
    /**
     * Creates a time-based UCT instance with a random rollout policy.
     *
     * @param c UCB1 exploration constant
     */
    public UCT(double c)
    {
        super(new UCB1<>(c), new RandomStrategy<>());
    }

    /**
     * Creates a simulation-count UCT instance with a random rollout policy.
     *
     * @param c     UCB1 exploration constant
     * @param nSims number of simulations per move
     */
    public UCT(double c, int nSims)
    {
        super(new UCB1<>(c), new RandomStrategy<>(), nSims);
    }

    /**
     * Creates a time-based UCT instance with a custom rollout policy.
     *
     * @param rolloutPolicy the rollout (default) policy
     * @param c         UCB1 exploration constant
     */
    public UCT(Strategy<GAME> rolloutPolicy, double c)
    {
        super(new UCB1<>(c), rolloutPolicy);
    }

    /**
     * Creates a simulation-count UCT instance with a custom rollout policy.
     *
     * @param rolloutPolicy the rollout (default) policy
     * @param c         UCB1 exploration constant
     * @param nSims     number of simulations per move
     */
    public UCT(Strategy<GAME> rolloutPolicy, double c, int nSims)
    {
        super(new UCB1<>(c), rolloutPolicy, nSims);
    }
}
