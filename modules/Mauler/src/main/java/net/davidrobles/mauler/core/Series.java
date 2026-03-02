package net.davidrobles.mauler.core;

//import com.google.common.base.Stopwatch;
import net.davidrobles.mauler.players.Player;

import java.util.*;
import java.util.concurrent.*;

public class Series<GAME extends Game<GAME>>
{
    protected GAME game;
    private int nGames;
    protected List<Player<GAME>> players;
    private Outcome[] outcomes;
//    private Stopwatch stopwatch = new Stopwatch();
    private boolean finished = false;
    private boolean verbose = true;
    private int timeout = -1;

    public Series(GAME game, int nGames, List<Player<GAME>> players)
    {
        if (game.getNumPlayers() != players.size())
            throw new IllegalArgumentException("This is played by " + game.getNumPlayers() + " players!");

        this.game = game;
        this.nGames = nGames;
        this.players = players;
        this.outcomes = new Outcome[nGames];
    }

    public Series(GAME game, int nGames, List<Player<GAME>> players, int timeout)
    {
        if (game.getNumPlayers() != players.size())
            throw new IllegalArgumentException("This is played by " + game.getNumPlayers() + " players!");

        if (timeout <= 0)
            throw new IllegalArgumentException();

        this.game = game;
        this.nGames = nGames;
        this.players = players;
        this.outcomes = new Outcome[nGames];
        this.timeout = timeout;
    }

    //////////////////////
    // Instance methods //
    //////////////////////

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    private int count(Outcome outcome)
    {
        if (!finished)
            throw new UnsupportedOperationException();

        int count = 0;

        for (Outcome o : outcomes)
            if (outcome == o)
                count++;

        return count;
    }

    public List<Player<GAME>> getPlayers()
    {
        return players;
    }

    public Outcome[] getOutcomes()
    {
        if (!finished)
            throw new UnsupportedOperationException();

        return outcomes;
    }

    public double getStdError(int player)
    {
        if (!finished)
            throw new UnsupportedOperationException();

        double avg = getWinsAvg(player);
        return Math.sqrt((avg * (1 - avg)) / outcomes.length);
    }

    public int getWins(int player)
    {
        if (!finished)
            throw new UnsupportedOperationException();

        return count(player == 0 ? Outcome.WIN : Outcome.LOSS);
    }

    public double getWinsAvg(int player)
    {
        if (!finished)
            throw new UnsupportedOperationException();

        return getWins(player) / (double) outcomes.length;
    }

    public int getLosses(int player)
    {
        return count(player == 1 ? Outcome.WIN : Outcome.LOSS);
    }

    public double getLossesAvg(int player)
    {
        if (!finished)
            throw new UnsupportedOperationException();

        return getLosses(player) / (double) outcomes.length;
    }

    public int getDraws()
    {
        if (!finished)
            throw new UnsupportedOperationException();

        return count(Outcome.DRAW);
    }

    public double getDrawsAvg()
    {
        if (!finished)
            throw new UnsupportedOperationException();

        return getDraws() / (double) outcomes.length;
    }

    public int getNumGames()
    {
        return outcomes.length;
    }

    public String getElapsedTime()
    {
        if (!finished)
            throw new UnsupportedOperationException();

//        return stopwatch.toString();
        return null;
    }

    public void run()
    {
        int nProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nProcessors);
        Collection<Match<GAME>> matches = new ArrayList<Match<GAME>>();
        StringBuilder builder = new StringBuilder();

        if (verbose)
        {
            builder.append("=================\n");
            builder.append(" Starting Series\n");
            builder.append("=================\n\n");
            builder.append(String.format("%14s: %s\n", "No. processors", Runtime.getRuntime().availableProcessors()));
            builder.append(this);
            System.out.println(builder);
        }

        // create callable objects
        for (int i = 0; i < nGames; i++)
        {
            int starter = i % 2;

            if (timeout > 0)
                matches.add(new Match<GAME>(game, players, starter, timeout));
            else
                matches.add(new Match<GAME>(game, players, starter));
        }

//        stopwatch.start();

        try
        {
            List<Future<Outcome[]>> futures = executor.invokeAll(matches);
            executor.shutdown();
//            stopwatch.stop();
            int outcome = 0;

            for (Future<Outcome[]> future : futures)
                outcomes[outcome++] = future.get()[0];
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        finished = true;

        if (verbose)
        {
            builder = new StringBuilder();
            builder.append("=================\n");
            builder.append(" Finished Series\n");
            builder.append("=================\n\n");
            builder.append(this);
            System.out.println(builder);
        }
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%14s: %s\n", "Game", game.getName()));
        builder.append(String.format("%14s: %,d\n", "No. matches", nGames));
        builder.append(String.format("%14s: %s\n", "Players", players.get(0)));
        builder.append(String.format("%14s  %s\n", "", players.get(1)));

        if (timeout > 0)
            builder.append(String.format("%14s: %s\n", "Timeout", timeout));

        if (finished) {
            builder.append(String.format("%14s: %,d (%.2f%%)\n", "Wins 1", getWins(0), getWinsAvg(0) * 100));
            builder.append(String.format("%14s: %,d (%.2f%%)\n", "Wins 2", getWins(1), getWinsAvg(1) * 100));
            builder.append(String.format("%14s: %,d (%.2f%%)\n", "Draws", getDraws(), getDrawsAvg() * 100));
//            builder.append(String.format("%14s: %s\n", "Elapsed time", stopwatch));
        }

        return builder.toString();
    }
}
