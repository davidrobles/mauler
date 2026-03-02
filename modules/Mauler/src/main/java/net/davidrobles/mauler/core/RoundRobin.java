package net.davidrobles.mauler.core;

//import com.google.common.base.Stopwatch;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.util.DRMarkdown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoundRobin<GAME extends Game<GAME>>
{
    protected GAME game;
    protected List<Player<GAME>> players;
    protected List<String> playerNames;
    protected int nGames;
    protected boolean finished = false;
    protected Outcome[][][] outcomes;
    protected double[][] averages;
    protected double[][] stdErrors;
    private String caption = "FILL";
//    private Stopwatch stopwatch = new Stopwatch();
    private boolean verbose = true;
    private int timeout = -1;

    public RoundRobin(GAME game, int nGames, List<Player<GAME>> players)
    {
        this.game = game;
        this.players = players;
        this.nGames = nGames;
        this.outcomes = new Outcome[players.size()][players.size()][nGames];
        this.averages = new double[players.size()][players.size() + 1];
        this.stdErrors = new double[players.size()][players.size() + 1];
        this.playerNames = new ArrayList<String>();

        for (int i = 0; i < players.size(); i++)
             playerNames.add(players.get(i).toString());
    }

    public RoundRobin(GAME game, int nGames, List<Player<GAME>> players, int timeout)
    {
        this(game, nGames, players);

        if (timeout <= 0)
            throw new IllegalArgumentException();

        this.timeout = timeout;
    }

    public RoundRobin(GAME game, int nGames, List<Player<GAME>> players, List<String> playerNames)
    {
        this(game, nGames, players);
        this.playerNames = playerNames;
    }

    public RoundRobin(GAME game, int nGames, List<Player<GAME>> players, List<String> playerNames, int timeout)
    {
        this(game, nGames, players, playerNames);

        if (timeout <= 0)
            throw new IllegalArgumentException();

        this.timeout = timeout;
    }

    //////////////////////
    // Abstract methods //
    //////////////////////

//    protected abstract Series<GAME> createSeries(List<Player<GAME>> players);
//    protected abstract void toStringExtra(StringBuilder builder);

    //////////////////////
    // Instance methods //
    //////////////////////

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    public void run()
    {
        if (verbose) {
            DRMarkdown.printH1("Starting Round Robin");
            int nSeries = (players.size() / 2) * (players.size() - 1);
            System.out.println("Series to play: " + nSeries);
            System.out.println(this);
        }

        Collection<Series<GAME>> series = new ArrayList<Series<GAME>>();

        for (int p1 = 0; p1 < players.size(); p1++)
        {
            for (int p2 = p1 + 1; p2 < players.size(); p2++)
            {
                List<Player<GAME>> seriesPlayers = new ArrayList<Player<GAME>>();
                seriesPlayers.add(players.get(p1));
                seriesPlayers.add(players.get(p2));

                if (timeout <= 0)
                    series.add(new Series<GAME>(game, nGames, seriesPlayers));
                else
                    series.add(new Series<GAME>(game, nGames, seriesPlayers, timeout));
            }
        }

//        stopwatch.start();

        for (Series<GAME> s : series) {
            try {
                s.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Series<GAME> s : series)
        {
            List<Player<GAME>> thePlayers = s.getPlayers();

            int player1Index = players.indexOf(thePlayers.get(0));
            int player2Index = players.indexOf(thePlayers.get(1));

            Outcome[] outs = s.getOutcomes();

            stdErrors[player1Index][player2Index] = s.getStdError(0);
            stdErrors[player2Index][player1Index] = s.getStdError(1);
            averages[player1Index][player2Index] = s.getWinsAvg(0);
            averages[player2Index][player1Index] = s.getWinsAvg(1);

            for (int i = 0; i < outs.length; i++)
            {
                Outcome theOut = outs[i];
                outcomes[player1Index][player2Index][i] = theOut;

                if (theOut == Outcome.WIN)
                    outcomes[player2Index][player1Index][i] = Outcome.LOSS;
                else if (theOut == Outcome.LOSS)
                    outcomes[player2Index][player1Index][i] = Outcome.WIN;
                else
                    outcomes[player2Index][player1Index][i] = Outcome.DRAW;
            }
        }

        calculateAverages();
        finished = true;

        if (verbose) {
            DRMarkdown.printH1("Finished Round Robin");
            System.out.println(this);
            System.out.println(toFormattedTable());
        }
    }

    private void calculateAverages()
    {
        for (int p1 = 0; p1 < players.size(); p1++)
        {
            double total = 0;

            for (int p2 = 0; p2 < players.size(); p2++)
                if (p1 != p2)
                    total += averages[p1][p2];

            averages[p1][players.size()] = total / (players.size() - 1);
            double avg = averages[p1][players.size()];
            stdErrors[p1][players.size()] = Math.sqrt((avg * (1 - avg)) / ((players.size() - 1) * nGames));
        }
    }

    public String toFormattedTable()
    {
        int maxPlayerLength = -1;

        for (String player : playerNames)
        {
            int length = player.length();

            if (length > maxPlayerLength)
                maxPlayerLength = length;
        }

        maxPlayerLength += 4;

        if (maxPlayerLength < 16)
            maxPlayerLength = 16;

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < maxPlayerLength * (players.size() + 2); i++)
            builder.append("-");

        builder.append("\n");

        builder.append(String.format("%" + maxPlayerLength + "s", "")); // col headers

        for (String player : playerNames)
            builder.append(String.format("%" + maxPlayerLength + "s", player));

        builder.append(String.format("%" + maxPlayerLength + "s", "Avg."));

        builder.append("\n");

        for (int i = 0; i < maxPlayerLength * (players.size() + 2); i++)
            builder.append("-");

        builder.append("\n");

        // rows
        for (int p1 = 0; p1 < players.size(); p1++)
        {
            builder.append(String.format("%" + maxPlayerLength + "s", playerNames.get(p1)));

            for (int p2 = 0; p2 <= players.size(); p2++)
            {
                if (p1 == p2)
                    builder.append(String.format("%" + maxPlayerLength + "s", "--"));
                else {
                    String str = String.format("%6.1f (%.1f)", averages[p1][p2] * 100, stdErrors[p1][p2] * 100);
                    builder.append(String.format("%" + maxPlayerLength + "s", str));
                }
            }

            builder.append("\n");
        }

        for (int i = 0; i < maxPlayerLength * (players.size() + 2); i++)
            builder.append("-");

        builder.append("\n");

        return builder.toString();
    }

    public String toLatexTable()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("\\begin{table*}[ht]\n");
        builder.append("\\centering\n");
        builder.append("\\scriptsize\n");
        builder.append("\\begin{tabular}{l");

        for (Player<GAME> ignored : players)
            builder.append("r");

        builder.append("r}\n");
        builder.append("\\toprule\n");
        builder.append("Player");

        for (String player : playerNames)
            builder.append(" & " + player);

        builder.append(" & Avg. \\\\\n");
        builder.append("\\midrule\n");

        for (int p1 = 0; p1 < players.size(); p1++)
        {
            builder.append(playerNames.get(p1));

            for (int p2 = 0; p2 < players.size() + 1; p2++)
            {
                if (p1 == p2)
                    builder.append(" & ---");
                else
                    builder.append(String.format(" & %.1f (%.1f)", averages[p1][p2] * 100, stdErrors[p1][p2] * 100));
            }

            builder.append(" \\\\\n");
        }

        builder.append("\\bottomrule\n");
        builder.append("\\end{tabular}\n");
        builder.append("\\caption{" + caption + "}\n");
        builder.append("\\label{tab:FILLsdsfsdfs}\n");
        builder.append("\\end{table*}\n");

        return builder.toString();
    }

    public String toCSV()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Player");

        for (String player : playerNames)
            builder.append("," + player);

        builder.append(",Avg. \n");

        for (int p1 = 0; p1 < players.size(); p1++)
        {
            builder.append(playerNames.get(p1));

            for (int p2 = 0; p2 < players.size() + 1; p2++)
            {
                if (p1 == p2)
                    builder.append(",-");
                else
                    builder.append(String.format(",%.6f", averages[p1][p2]));
            }

            builder.append("\n");
        }

        return builder.toString();
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%14s: %s\n", "Game", game.getName()));
        builder.append(String.format("%14s: %s\n", "No. matches", nGames));

        if (timeout > 0)
            builder.append(String.format("%14s: %s\n", "Timeout", timeout));

        builder.append(String.format("%14s: %s\n", "Players", players.get(0)));

        for (int i = 1; i < players.size(); i++)
            builder.append(String.format("%14s  %s\n", "", players.get(i)));

//        if (finished)
//            builder.append(String.format("%14s: %s\n", "Elapsed time", stopwatch));

        return builder.toString();
    }
}
