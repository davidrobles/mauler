package net.davidrobles.mauler.core;


import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * A single game between two players, returning the {@link GameResult} for each player.
 *
 * <p>Implements {@link Callable} so matches can be submitted to an {@link java.util.concurrent.ExecutorService}
 * for parallel tournament execution.
 *
 * <p>When both players are deterministic, a 10% random-move injection is applied to avoid
 * the same game being played every time (which would make multi-game series meaningless).
 *
 * <p>The {@code starter} parameter controls which player moves first: {@code 0} means
 * {@code players.get(0)} starts, {@code 1} means {@code players.get(1)} starts. Outcomes
 * are always returned from {@code players.get(0)}'s perspective regardless of who started.
 *
 * @param <GAME> the game type
 */
public class Match<GAME extends Game<GAME>> implements Callable<GameResult[]>
{
    private static final double RANDOM_MOVE_PROBABILITY = 0.1;

    private final Supplier<GAME> gameFactory;
    private final List<Strategy<GAME>> players;
    private final int starter;
    private final int timeout;
    private final boolean injectRandomMoves;
    private final Random rnd = new Random();

    /**
     * Creates a match with a per-move time limit.
     *
     * @param gameFactory supplier that produces a fresh game instance for each call
     * @param players     the two players; index 0 is player one, index 1 is player two
     * @param starter     {@code 0} or {@code 1} — which player moves first
     * @param timeout     per-move time limit in milliseconds (must be positive)
     * @throws IllegalArgumentException if {@code timeout} is not positive
     */
    public Match(Supplier<GAME> gameFactory, List<Strategy<GAME>> players, int starter, int timeout)
    {
        if (timeout <= 0)
            throw new IllegalArgumentException("timeout must be positive, got: " + timeout);
        if (players.size() < 2)
            throw new IllegalArgumentException("at least 2 players required");

        this.gameFactory = gameFactory;
        this.players = players;
        this.starter = starter;
        this.timeout = timeout;
        this.injectRandomMoves = players.get(0).isDeterministic() && players.get(1).isDeterministic();
    }

    /**
     * Creates a match with no time limit.
     *
     * @param gameFactory supplier that produces a fresh game instance for each call
     * @param players     the two players
     * @param starter     {@code 0} or {@code 1} — which player moves first
     */
    public Match(Supplier<GAME> gameFactory, List<Strategy<GAME>> players, int starter)
    {
        if (players.size() < 2)
            throw new IllegalArgumentException("at least 2 players required");

        this.gameFactory = gameFactory;
        this.players = players;
        this.starter = starter;
        this.timeout = -1;
        this.injectRandomMoves = players.get(0).isDeterministic() && players.get(1).isDeterministic();
    }

    // -------------------------------------------------------------------------
    // Callable
    // -------------------------------------------------------------------------

    @Override
    public GameResult[] call()
    {
        GAME g = gameFactory.get();

        while (!g.isOver())
            g.makeMove(selectMove(g));

        GameResult[] outcomes = g.getOutcome().orElseThrow();

        if (starter == 1)
            outcomes = new GameResult[] { outcomes[1], outcomes[0] };

        return outcomes;
    }

    private int selectMove(GAME g)
    {
        if (injectRandomMoves && rnd.nextDouble() < RANDOM_MOVE_PROBABILITY)
            return rnd.nextInt(g.getNumMoves());

        int playerToMove = (g.getCurPlayer() + starter) % 2;
        Strategy<GAME> player = players.get(playerToMove);

        return timeout > 0 ? player.move(g, timeout) : player.move(g);
    }
}
