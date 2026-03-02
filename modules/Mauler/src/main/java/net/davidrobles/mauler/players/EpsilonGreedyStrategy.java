package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;

import java.util.Random;

/**
 * A player that acts greedily with probability {@code (1 - ε)} and uniformly at
 * random with probability {@code ε}.
 *
 * <p>Special cases:
 * <ul>
 *   <li>{@code ε = 0.0} — fully greedy (deterministic); equivalent to {@link GreedyStrategy}</li>
 *   <li>{@code ε = 1.0} — fully random; equivalent to {@link RandomStrategy}</li>
 * </ul>
 *
 * <p>Commonly used as a rollout policy inside MCTS to inject heuristic knowledge
 * while keeping some exploration. The seeded-{@link Random} constructor enables
 * reproducible experiments.
 *
 * @param <GAME> the game type
 */
public class EpsilonGreedyStrategy<GAME extends Game<GAME>> implements Strategy<GAME>
{
    protected final Evaluator<GAME> evalFunc;
    protected final double epsilon;
    protected final Random rng;

    /**
     * Creates an ε-greedy player with the given evaluation function and exploration rate.
     *
     * @param evalFunc the evaluation function used for greedy move selection
     * @param epsilon  exploration probability in {@code [0.0, 1.0]}
     * @param rng      the random source used for exploration decisions
     * @throws IllegalArgumentException if {@code epsilon} is outside {@code [0.0, 1.0]}
     */
    public EpsilonGreedyStrategy(Evaluator<GAME> evalFunc, double epsilon, Random rng)
    {
        if (epsilon < 0.0 || epsilon > 1.0)
            throw new IllegalArgumentException("epsilon must be in [0.0, 1.0], got: " + epsilon);

        this.evalFunc = evalFunc;
        this.epsilon  = epsilon;
        this.rng      = rng;
    }

    /**
     * Creates an ε-greedy player backed by a default (unseeded) {@link Random}.
     *
     * @param evalFunc the evaluation function used for greedy move selection
     * @param epsilon  exploration probability in {@code [0.0, 1.0]}
     */
    public EpsilonGreedyStrategy(Evaluator<GAME> evalFunc, double epsilon)
    {
        this(evalFunc, epsilon, new Random());
    }

    /** Returns the exploration probability ε. */
    public double getEpsilon()
    {
        return epsilon;
    }

    // -------------------------------------------------------------------------
    // Strategy
    // -------------------------------------------------------------------------

    /** Returns {@code true} when {@code ε = 0} (fully greedy, no randomness). */
    @Override
    public boolean isDeterministic()
    {
        return epsilon == 0.0;
    }

    @Override
    public int move(GAME game)
    {
        return PlayersUtil.epsilonGreedyMove(game, evalFunc, epsilon, rng);
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return String.format("<\u03B5-Greedy evalFunc=%s \u03B5=%.2f>", evalFunc, epsilon);
    }
}
