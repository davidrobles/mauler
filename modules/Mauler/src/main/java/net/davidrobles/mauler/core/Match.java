package net.davidrobles.mauler.core;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * A single game between two strategies, returning the {@link GameResult} for each player.
 *
 * <p>Implements {@link Callable} so matches can be submitted to an {@link java.util.concurrent.ExecutorService}
 * for parallel tournament execution.
 *
 * <p>The {@code starter} parameter controls which strategy moves first: {@code 0} means
 * {@code strategies.get(0)} starts, {@code 1} means {@code strategies.get(1)} starts. Outcomes
 * are always returned from {@code strategies.get(0)}'s perspective regardless of who started.
 *
 * <p>Each strategy receives a defensive copy of the game state so it cannot mutate
 * the live game.
 *
 * @param <GAME> the game type
 */
public class Match<GAME extends Game<GAME>> implements Callable<GameResult[]>
{
    private final Supplier<GAME> gameFactory;
    private final List<Strategy<GAME>> players;
    private final int starter;
    private final int timeout;

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
        int playerToMove = (g.getCurPlayer() + starter) % 2;
        Strategy<GAME> strategy = players.get(playerToMove);
        GAME copy = g.copy();

        return timeout > 0 ? strategy.move(copy, timeout) : strategy.move(copy);
    }
}
