package net.davidrobles.mauler.strategies;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;

import java.util.Random;

/**
 * A player that selects moves uniformly at random.
 *
 * <p>Useful as a baseline opponent, a rollout policy in Monte Carlo search,
 * and in tests where reproducible behaviour is needed (pass a seeded
 * {@link Random} to the constructor).
 *
 * @param <GAME> the game type
 */
public class RandomStrategy<GAME extends Game<GAME>> implements Strategy<GAME>
{
    private final Random rng;

    /** Creates a player backed by a default (unseeded) {@link Random}. */
    public RandomStrategy()
    {
        this(new Random());
    }

    /**
     * Creates a player backed by the given {@link Random}.
     *
     * @param rng the random source to use for move selection
     */
    public RandomStrategy(Random rng)
    {
        this.rng = rng;
    }

    // -------------------------------------------------------------------------
    // Strategy
    // -------------------------------------------------------------------------

    @Override
    public boolean isDeterministic()
    {
        return false;
    }

    @Override
    public int move(GAME game)
    {
        return rng.nextInt(game.getNumMoves());
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "<Random>";
    }
}
