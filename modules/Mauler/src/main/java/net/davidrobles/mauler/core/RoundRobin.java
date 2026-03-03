package net.davidrobles.mauler.core;

import net.davidrobles.util.Console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * A round-robin tournament in which every pair of players plays a {@link Series} of games.
 *
 * <p>For {@code n} players, {@code n*(n-1)/2} series are run (one per unique pair). Each series
 * alternates who starts each game to neutralize first-move advantage. Results are aggregated
 * into a win-rate matrix: {@code averages[i][j]} is player {@code i}'s win rate against player {@code j}.
 * The last column ({@code averages[i][n]}) holds player {@code i}'s overall average win rate.
 *
 * <p>Results can be exported as a formatted text table ({@link #toFormattedTable()}),
 * LaTeX ({@link #toLatexTable()}), or CSV ({@link #toCSV()}).
 *
 * @param <GAME> the game type
 */
public class RoundRobin<GAME extends Game<GAME>>
{
    private final Supplier<GAME> gameFactory;
    private final String gameName;
    private final List<Strategy<GAME>> players;
    private final List<String> playerNames;
    private final int nGames;
    private final int timeout;
    private final GameResult[][][] outcomes;
    private final double[][] averages;
    private final double[][] stdErrors;
    private String caption = "";
    private boolean finished = false;
    private boolean verbose = true;

    /**
     * Creates a round-robin tournament with no per-move time limit.
     *
     * @param gameFactory supplier that produces a fresh game instance for each match
     * @param nGames      number of games per series (per player pair)
     * @param players     the participating players
     */
    public RoundRobin(Supplier<GAME> gameFactory, int nGames, List<Strategy<GAME>> players)
    {
        this(gameFactory, nGames, players, defaultNames(players), -1);
    }

    /**
     * Creates a round-robin tournament with a per-move time limit.
     *
     * @param gameFactory supplier that produces a fresh game instance for each match
     * @param nGames      number of games per series
     * @param players     the participating players
     * @param timeout     per-move time limit in milliseconds (must be positive)
     */
    public RoundRobin(Supplier<GAME> gameFactory, int nGames, List<Strategy<GAME>> players, int timeout)
    {
        this(gameFactory, nGames, players, defaultNames(players), timeout);
    }

    /**
     * Creates a round-robin tournament with custom player display names.
     *
     * @param gameFactory supplier that produces a fresh game instance for each match
     * @param nGames      number of games per series
     * @param players     the participating players
     * @param playerNames display names for each player (parallel to {@code players})
     */
    public RoundRobin(Supplier<GAME> gameFactory, int nGames, List<Strategy<GAME>> players, List<String> playerNames)
    {
        this(gameFactory, nGames, players, playerNames, -1);
    }

    /**
     * Creates a round-robin tournament with custom player names and a per-move time limit.
     *
     * @param gameFactory supplier that produces a fresh game instance for each match
     * @param nGames      number of games per series
     * @param players     the participating players
     * @param playerNames display names for each player
     * @param timeout     per-move time limit in milliseconds, or {@code -1} for no limit
     */
    public RoundRobin(Supplier<GAME> gameFactory, int nGames, List<Strategy<GAME>> players, List<String> playerNames, int timeout)
    {
        if (timeout != -1 && timeout <= 0)
            throw new IllegalArgumentException("timeout must be positive, got: " + timeout);

        this.gameFactory = gameFactory;
        this.gameName = gameFactory.get().getName();
        this.nGames = nGames;
        this.players = players;
        this.playerNames = playerNames;
        this.timeout = timeout;
        this.outcomes = new GameResult[players.size()][players.size()][nGames];
        this.averages = new double[players.size()][players.size() + 1];
        this.stdErrors = new double[players.size()][players.size() + 1];
    }

    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    // -------------------------------------------------------------------------
    // Run
    // -------------------------------------------------------------------------

    /**
     * Runs all series and blocks until complete.
     */
    public void run()
    {
        int n = players.size();
        int nSeries = n * (n - 1) / 2;

        if (verbose)
        {
            Console.header("Round Robin — " + gameName);
            System.out.println("  " + Console.dim("Series      ") + nSeries);
            System.out.println("  " + Console.dim("Games each  ") + String.format("%,d", nGames));
            if (timeout > 0)
                System.out.println("  " + Console.dim("Timeout     ") + timeout + " ms");
            System.out.println();
        }

        Collection<Series<GAME>> allSeries = new ArrayList<>();

        for (int p1 = 0; p1 < n; p1++)
        {
            for (int p2 = p1 + 1; p2 < n; p2++)
            {
                List<Strategy<GAME>> pair = new ArrayList<>();
                pair.add(players.get(p1));
                pair.add(players.get(p2));

                Series<GAME> s = timeout > 0
                        ? new Series<>(gameFactory, nGames, pair, timeout)
                        : new Series<>(gameFactory, nGames, pair);

                s.setVerbose(false);
                allSeries.add(s);
            }
        }

        for (Series<GAME> s : allSeries)
            s.run();

        for (Series<GAME> s : allSeries)
            recordResults(s);

        calculateAverages();
        finished = true;

        if (verbose)
        {
            Console.header("Results — " + gameName);
            System.out.println(toFormattedTable());
        }
    }

    // -------------------------------------------------------------------------
    // Results
    // -------------------------------------------------------------------------

    /**
     * Returns the formatted win-rate table. Requires {@link #run()} to have completed.
     */
    public String toFormattedTable()
    {
        checkFinished();

        int col = maxPlayerNameLength() + 4;
        if (col < 16) col = 16;

        String divider = "─".repeat(col * (players.size() + 2)) + "\n";
        StringBuilder sb = new StringBuilder();

        sb.append(divider);
        sb.append(String.format("%" + col + "s", ""));
        for (String name : playerNames)
            sb.append(String.format("%" + col + "s", name));
        sb.append(String.format("%" + col + "s", "Avg.")).append("\n");
        sb.append(divider);

        for (int p1 = 0; p1 < players.size(); p1++)
        {
            sb.append(String.format("%" + col + "s", playerNames.get(p1)));
            for (int p2 = 0; p2 <= players.size(); p2++)
            {
                if (p1 == p2)
                    sb.append(String.format("%" + col + "s", "--"));
                else
                    sb.append(String.format("%" + col + "s",
                            String.format("%6.1f (%.1f)", averages[p1][p2] * 100, stdErrors[p1][p2] * 100)));
            }
            sb.append("\n");
        }

        sb.append(divider);
        return sb.toString();
    }

    /**
     * Returns a LaTeX table of the results. Requires {@link #run()} to have completed.
     */
    public String toLatexTable()
    {
        checkFinished();

        StringBuilder sb = new StringBuilder();
        sb.append("\\begin{table*}[ht]\n");
        sb.append("\\centering\n");
        sb.append("\\scriptsize\n");
        sb.append("\\begin{tabular}{l");

        for (int i = 0; i <= players.size(); i++)
            sb.append("r");

        sb.append("}\n\\toprule\nPlayer");

        for (String name : playerNames)
            sb.append(" & ").append(name);

        sb.append(" & Avg. \\\\\n\\midrule\n");

        for (int p1 = 0; p1 < players.size(); p1++)
        {
            sb.append(playerNames.get(p1));
            for (int p2 = 0; p2 <= players.size(); p2++)
            {
                if (p1 == p2)
                    sb.append(" & ---");
                else
                    sb.append(String.format(" & %.1f (%.1f)", averages[p1][p2] * 100, stdErrors[p1][p2] * 100));
            }
            sb.append(" \\\\\n");
        }

        sb.append("\\bottomrule\n\\end{tabular}\n");
        if (!caption.isEmpty())
            sb.append("\\caption{").append(caption).append("}\n");
        sb.append("\\end{table*}\n");

        return sb.toString();
    }

    /**
     * Returns a CSV of the results. Requires {@link #run()} to have completed.
     */
    public String toCSV()
    {
        checkFinished();

        StringBuilder sb = new StringBuilder("Player");

        for (String name : playerNames)
            sb.append(",").append(name);

        sb.append(",Avg.\n");

        for (int p1 = 0; p1 < players.size(); p1++)
        {
            sb.append(playerNames.get(p1));
            for (int p2 = 0; p2 <= players.size(); p2++)
            {
                if (p1 == p2)
                    sb.append(",-");
                else
                    sb.append(String.format(",%.6f", averages[p1][p2]));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void recordResults(Series<GAME> s)
    {
        List<Strategy<GAME>> pair = s.getStrategies();
        int p1 = players.indexOf(pair.get(0));
        int p2 = players.indexOf(pair.get(1));

        averages[p1][p2] = s.getWinsAvg(0);
        averages[p2][p1] = s.getWinsAvg(1);
        stdErrors[p1][p2] = s.getStdError(0);
        stdErrors[p2][p1] = s.getStdError(1);

        GameResult[] outs = s.getOutcomes();
        for (int i = 0; i < outs.length; i++)
        {
            outcomes[p1][p2][i] = outs[i];
            outcomes[p2][p1][i] = outs[i].flip();
        }
    }

    private void calculateAverages()
    {
        int n = players.size();
        for (int p1 = 0; p1 < n; p1++)
        {
            double total = 0;
            for (int p2 = 0; p2 < n; p2++)
                if (p1 != p2) total += averages[p1][p2];

            double avg = total / (n - 1);
            averages[p1][n] = avg;
            stdErrors[p1][n] = Math.sqrt((avg * (1 - avg)) / ((n - 1) * nGames));
        }
    }

    private void checkFinished()
    {
        if (!finished)
            throw new IllegalStateException("RoundRobin has not been run yet — call run() first");
    }

    private int maxPlayerNameLength()
    {
        int max = 0;
        for (String name : playerNames)
            if (name.length() > max) max = name.length();
        return max;
    }

    private static <GAME extends Game<GAME>> List<String> defaultNames(List<Strategy<GAME>> players)
    {
        List<String> names = new ArrayList<>();
        for (Strategy<GAME> p : players)
            names.add(p.toString());
        return names;
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%14s: %s%n",  "Game",    gameName));
        sb.append(String.format("%14s: %,d%n", "Matches", nGames));
        if (timeout > 0)
            sb.append(String.format("%14s: %d ms%n", "Timeout", timeout));
        sb.append(String.format("%14s: %s%n", "Players", players.get(0)));
        for (int i = 1; i < players.size(); i++)
            sb.append(String.format("%14s  %s%n", "", players.get(i)));
        return sb.toString();
    }
}
