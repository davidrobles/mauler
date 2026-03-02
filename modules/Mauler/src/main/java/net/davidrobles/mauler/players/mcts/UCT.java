package net.davidrobles.mauler.players.mcts;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Strategy;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.players.mcts.tree.UCB1;

/**
 * Upper Confidence Bounds for Trees (UCT).
 *
 * <p>Specialises {@link MCTS} with the {@link UCB1} tree policy and a
 * uniformly random rollout policy. Constructors without a {@code defPolicy}
 * argument use {@link RandPlayer} as the default rollout policy.
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
        super(new UCB1<>(c), new RandPlayer<>());
    }

    /**
     * Creates a simulation-count UCT instance with a random rollout policy.
     *
     * @param c     UCB1 exploration constant
     * @param nSims number of simulations per move
     */
    public UCT(double c, int nSims)
    {
        super(new UCB1<>(c), new RandPlayer<>(), nSims);
    }

    /**
     * Creates a time-based UCT instance with a custom rollout policy.
     *
     * @param defPolicy the rollout (default) policy
     * @param c         UCB1 exploration constant
     */
    public UCT(Strategy<GAME> defPolicy, double c)
    {
        super(new UCB1<>(c), defPolicy);
    }

    /**
     * Creates a simulation-count UCT instance with a custom rollout policy.
     *
     * @param defPolicy the rollout (default) policy
     * @param c         UCB1 exploration constant
     * @param nSims     number of simulations per move
     */
    public UCT(Strategy<GAME> defPolicy, double c, int nSims)
    {
        super(new UCB1<>(c), defPolicy, nSims);
    }
}
