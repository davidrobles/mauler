package net.davidrobles.mauler.players.mc;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.UtilFunc;
import net.davidrobles.util.DRUtil;

import java.util.Random;

/**
 * Flat (one-ply) Monte Carlo player.
 *
 * <p>For each legal move, a number of random rollouts are run to completion
 * and scored with a {@link UtilFunc}. The move with the highest cumulative
 * score is returned.
 *
 * <p>Two operational modes are supported:
 * <ul>
 *   <li><b>Simulation-count</b> — construct with {@code nSims}; distributes
 *       simulations round-robin across all moves, then picks the best.</li>
 *   <li><b>Time-based</b> — construct without {@code nSims}; use
 *       {@link #move(Game, int)} with a millisecond budget. Runs full sweeps
 *       over all moves until time expires.</li>
 * </ul>
 *
 * @param <GAME> the game type
 */
public class MonteCarlo<GAME extends Game<GAME>> implements Player<GAME>
{
    protected final UtilFunc<GAME> utilFunc = new UtilFunc<>();
    protected final Random rand;
    private final int nSims;

    /**
     * Creates a time-based player backed by a default (unseeded) {@link Random}.
     * Use {@link #move(Game, int)} — calling {@link #move(Game)} will throw.
     */
    public MonteCarlo()
    {
        this(new Random());
    }

    /**
     * Creates a time-based player backed by the given {@link Random}.
     * Use {@link #move(Game, int)} — calling {@link #move(Game)} will throw.
     *
     * @param rand the random source for rollouts
     */
    public MonteCarlo(Random rand)
    {
        this(-1, rand);
    }

    /**
     * Creates a simulation-count player backed by a default (unseeded) {@link Random}.
     *
     * @param nSims total number of rollouts to run per move call (must be positive)
     */
    public MonteCarlo(int nSims)
    {
        this(nSims, new Random());
    }

    /**
     * Creates a simulation-count player backed by the given {@link Random}.
     *
     * @param nSims total number of rollouts to run per move call (must be positive)
     * @param rand  the random source for rollouts
     * @throws IllegalArgumentException if {@code nSims} is not positive
     */
    public MonteCarlo(int nSims, Random rand)
    {
        if (nSims != -1 && nSims <= 0)
            throw new IllegalArgumentException("nSims must be positive, got: " + nSims);

        this.nSims = nSims;
        this.rand  = rand;
    }

    // -------------------------------------------------------------------------
    // Player
    // -------------------------------------------------------------------------

    @Override
    public boolean isDeterministic()
    {
        return false;
    }

    /**
     * Selects a move using the configured number of rollouts, distributed
     * round-robin across all legal moves.
     *
     * @throws IllegalStateException if this instance was not constructed with {@code nSims}
     */
    @Override
    public int move(GAME game)
    {
        if (nSims < 0)
            throw new IllegalStateException(
                    "move(game) requires nSims — use move(game, timeout) or construct with nSims");

        int numMoves = game.getNumMoves();
        if (numMoves == 1)
            return 0;

        int player = game.getCurPlayer();
        double[] scores = new double[numMoves];

        for (int i = 0; i < nSims; i++)
        {
            int move = i % numMoves;
            scores[move] += simulate(game, move, player);
        }

        return DRUtil.argMax(scores);
    }

    /**
     * Selects a move by running rollouts until the time budget is exhausted.
     * Each sweep simulates every legal move once before repeating.
     */
    @Override
    public int move(GAME game, int timeout)
    {
        int numMoves = game.getNumMoves();
        if (numMoves == 1)
            return 0;

        int player = game.getCurPlayer();
        double[] scores = new double[numMoves];
        long timeDue = System.currentTimeMillis() + timeout;

        while (System.currentTimeMillis() < timeDue)
        {
            for (int move = 0; move < numMoves; move++)
                scores[move] += simulate(game, move, player);
        }

        return DRUtil.argMax(scores);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Plays {@code move} on a copy of {@code game}, then runs a random rollout
     * to completion and returns the score for {@code player}.
     */
    private double simulate(GAME game, int move, int player)
    {
        GAME copy = game.copy();
        copy.makeMove(move);

        while (!copy.isOver())
            copy.makeMove(rand.nextInt(copy.getNumMoves()));

        return utilFunc.eval(copy, player);
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return nSims < 0
                ? "<MonteCarlo>"
                : String.format("<MonteCarlo nSims=%d>", nSims);
    }
}
