package net.davidrobles.mauler.core;

import net.davidrobles.mauler.core.util.Console;
import net.davidrobles.mauler.players.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Runs a series of {@link Match}es between two players and aggregates the results.
 *
 * <p>Matches are executed in parallel using a thread pool sized to the number of
 * available processors. The starter alternates every game (game 0: player 0 starts,
 * game 1: player 1 starts, etc.) to give both players equal first-move advantage.
 *
 * <p>All statistics ({@link #getWins}, {@link #getDraws}, etc.) are only available
 * after {@link #run()} has completed.
 *
 * @param <GAME> the game type
 */
public class Series<GAME extends Game<GAME>>
{
    private final GAME game;
    private final int nGames;
    private final List<Player<GAME>> players;
    private final int timeout;
    private final Outcome[] outcomes;
    private boolean finished = false;
    private boolean verbose = true;

    /**
     * Creates a series with no per-move time limit.
     *
     * @param game    the game to play
     * @param nGames  number of matches to run
     * @param players the two players
     * @throws IllegalArgumentException if the number of players doesn't match the game
     */
    public Series(GAME game, int nGames, List<Player<GAME>> players)
    {
        this(game, nGames, players, -1);
    }

    /**
     * Creates a series with a per-move time limit.
     *
     * @param game    the game to play
     * @param nGames  number of matches to run
     * @param players the two players
     * @param timeout per-move time limit in milliseconds (must be positive)
     * @throws IllegalArgumentException if the number of players doesn't match the game, or timeout is not positive
     */
    public Series(GAME game, int nGames, List<Player<GAME>> players, int timeout)
    {
        if (game.getNumPlayers() != players.size())
            throw new IllegalArgumentException("Game requires " + game.getNumPlayers() + " players, got " + players.size());
        if (timeout != -1 && timeout <= 0)
            throw new IllegalArgumentException("timeout must be positive, got: " + timeout);

        this.game = game;
        this.nGames = nGames;
        this.players = players;
        this.timeout = timeout;
        this.outcomes = new Outcome[nGames];
    }

    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    // -------------------------------------------------------------------------
    // Run
    // -------------------------------------------------------------------------

    /**
     * Runs all matches in parallel and blocks until all are complete.
     */
    public void run()
    {
        int nProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nProcessors);

        if (verbose)
            printHeader(nProcessors);

        Collection<Match<GAME>> matches = new ArrayList<>();
        for (int i = 0; i < nGames; i++)
        {
            int starter = i % 2;
            matches.add(timeout > 0
                    ? new Match<>(game, players, starter, timeout)
                    : new Match<>(game, players, starter));
        }

        try
        {
            List<Future<Outcome[]>> futures = executor.invokeAll(matches);
            executor.shutdown();

            int i = 0;
            for (Future<Outcome[]> future : futures)
                outcomes[i++] = future.get()[0];
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Series interrupted", e);
        }
        catch (ExecutionException e)
        {
            throw new RuntimeException("Match execution failed", e.getCause());
        }

        finished = true;

        if (verbose)
            printFooter();
    }

    // -------------------------------------------------------------------------
    // Statistics
    // -------------------------------------------------------------------------

    public List<Player<GAME>> getPlayers()
    {
        return players;
    }

    public Outcome[] getOutcomes()
    {
        checkFinished();
        return outcomes;
    }

    public int getNumGames()
    {
        return outcomes.length;
    }

    public int getWins(int player)
    {
        checkFinished();
        return count(player == 0 ? Outcome.WIN : Outcome.LOSS);
    }

    public int getLosses(int player)
    {
        checkFinished();
        return count(player == 0 ? Outcome.LOSS : Outcome.WIN);
    }

    public int getDraws()
    {
        checkFinished();
        return count(Outcome.DRAW);
    }

    public double getWinsAvg(int player)
    {
        checkFinished();
        return getWins(player) / (double) outcomes.length;
    }

    public double getLossesAvg(int player)
    {
        checkFinished();
        return getLosses(player) / (double) outcomes.length;
    }

    public double getDrawsAvg()
    {
        checkFinished();
        return getDraws() / (double) outcomes.length;
    }

    public double getStdError(int player)
    {
        checkFinished();
        double avg = getWinsAvg(player);
        return Math.sqrt((avg * (1 - avg)) / outcomes.length);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void checkFinished()
    {
        if (!finished)
            throw new IllegalStateException("Series has not been run yet — call run() first");
    }

    private int count(Outcome outcome)
    {
        int n = 0;
        for (Outcome o : outcomes)
            if (outcome == o) n++;
        return n;
    }

    private void printHeader(int nProcessors)
    {
        Console.header("Series — " + game.getName());
        System.out.println("  " + Console.dim("Processors  ") + nProcessors);
        System.out.println("  " + Console.dim("Matches     ") + String.format("%,d", nGames));
        System.out.println("  " + Console.dim("Player 1    ") + players.get(0));
        System.out.println("  " + Console.dim("Player 2    ") + players.get(1));
        if (timeout > 0)
            System.out.println("  " + Console.dim("Timeout     ") + timeout + " ms");
        System.out.println();
    }

    private void printFooter()
    {
        Console.header("Results — " + game.getName());
        System.out.println("  " + Console.green(String.format("%-12s %,d  (%.1f%%)", "Wins P1", getWins(0), getWinsAvg(0) * 100)));
        System.out.println("  " + Console.blue(String.format("%-12s %,d  (%.1f%%)", "Wins P2", getWins(1), getWinsAvg(1) * 100)));
        System.out.println("  " + Console.yellow(String.format("%-12s %,d  (%.1f%%)", "Draws", getDraws(), getDrawsAvg() * 100)));
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%14s: %s%n",   "Game",       game.getName()));
        sb.append(String.format("%14s: %,d%n",  "Matches",    nGames));
        sb.append(String.format("%14s: %s%n",   "Player 1",   players.get(0)));
        sb.append(String.format("%14s: %s%n",   "Player 2",   players.get(1)));
        if (timeout > 0)
            sb.append(String.format("%14s: %d ms%n", "Timeout", timeout));
        if (finished) {
            sb.append(String.format("%14s: %,d (%.2f%%)%n", "Wins P1", getWins(0), getWinsAvg(0) * 100));
            sb.append(String.format("%14s: %,d (%.2f%%)%n", "Wins P2", getWins(1), getWinsAvg(1) * 100));
            sb.append(String.format("%14s: %,d (%.2f%%)%n", "Draws",   getDraws(), getDrawsAvg() * 100));
        }
        return sb.toString();
    }
}
