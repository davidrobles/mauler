package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

import java.util.Random;

/**
 * An Epsilon Greedy player takes greedy moves with (1 - epsilon) probability,
 * and random moves otherwise.
 */
public class EpsilonGreedy<GAME extends Game<GAME>> implements Player<GAME>
{
    protected final double epsilon;
    protected final EvalFunc<GAME> evalFunc;
    protected final Random rng;

    public EpsilonGreedy(EvalFunc<GAME> evalFunc, double epsilon, Random rng)
    {
        if (epsilon < 0.0 || epsilon > 1.0)
            throw new IllegalArgumentException();

        this.evalFunc = evalFunc;
        this.epsilon = epsilon;
        this.rng = rng;
    }

    public EpsilonGreedy(EvalFunc<GAME> evalFunc, double epsilon)
    {
        this(evalFunc, epsilon, new Random());
    }

    public double getEpsilon()
    {
        return epsilon;
    }

    ////////////
    // Player //
    ////////////

    @Override
    public boolean isDeterministic()
    {
        return epsilon == 0;
    }

    @Override
    public int move(GAME game)
    {
        return PlayersUtil.epsilonGreedyMove(game, evalFunc, epsilon, rng);
    }

    @Override
    public int move(GAME game, int timeout)
    {
        return move(game);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return String.format("<\u03B5-greedy evalFunc: %s, \u03B5: %.2f>", evalFunc, epsilon);
    }
}
