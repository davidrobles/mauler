package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.players.DRPlot;
import net.davidrobles.mauler.players.UtilFunc;
import net.davidrobles.mauler.players.mc.MonteCarlo;
import net.davidrobles.mauler.players.mcts.MCTS;
import net.davidrobles.mauler.players.mcts.tree.TreePolicy;
import net.davidrobles.mauler.players.mcts.tree.UCB1;
import net.davidrobles.util.DRMarkdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MCvsMCTS
{
    private static void MCTSvsMCPlotTime()
    {
        int initTime = 50;
        int endTime = 1000;
        int steps = 15;
        int nGames = 50;

        MCTSvsMCPlotTime(initTime, endTime, steps, nGames);
    }

    private static void MCTSvsMCPlotTime(int start, int end, int steps, int nGames)
    {
        DRMarkdown.printH1("MCTS vs MC as tim per move is increased");
        Random rng = new Random();

        // configuration
        double c = 0.5;

        // Tree policies
        TreePolicy<Othello> treePolicy = new UCB1<Othello>(c, rng);

        // Default policies
        Player<Othello> defaultPolicy = new RandPlayer<Othello>(rng);
        String[] rowNames = { "MCTS", "MC" };

        int interval = (end - start) / steps;
        Integer[] nTimes = new Integer[steps + 1];

        nTimes[0] = start;
        nTimes[steps] = end;

        for (int i = 1; i <= steps; i++)
            nTimes[i] = nTimes[i - 1] + interval;

        System.out.println(Arrays.toString(nTimes));
        System.out.println("Num mauler: " + nGames);

        DRPlot plot = new DRPlot(rowNames, nTimes);

        for (int run = 0; run < nTimes.length; run++)
        {
            System.out.println("Run:  " + run);
            System.out.println("Timeout: " + nTimes[run]);

            // Players
            List<Player<Othello>> players = new ArrayList<Player<Othello>>();
            MCTS<Othello> mcts = new MCTS<Othello>(treePolicy, defaultPolicy);
            players.add(mcts);
            MonteCarlo<Othello> mc = new MonteCarlo<Othello>();
            players.add(mc);

            Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, nTimes[run]);
            series.run();

            for (int player = 0; player < players.size(); player++)
                plot.setData(player, run, series.getWinsAvg(player));
        }

        System.out.println(plot);
    }

    private static void MCTSvsMCPlot()
    {
        int initSims = 50;
        int endSims = 5000;
        int steps = 15;
        int nGames = 100;

        MCTSvsMCPlotSims(initSims, endSims, steps, nGames);
    }

    private static void MCTSvsMCPlotSims(int start, int end, int steps, int nGames)
    {
        DRMarkdown.printH1("MCTS vs MC as number of simulations incrase");
        Random rng = new Random();

        // configuration
        double c = 0.5;

        // Tree policies
        TreePolicy<Othello> treePolicy = new UCB1<Othello>(c, rng);

        // Default policies
        Player<Othello> defaultPolicy = new RandPlayer<Othello>(rng);
        UtilFunc<Othello> utilFunc = new UtilFunc<Othello>();
        String[] rowNames = { "MC", "MCTS" };

        int interval = (end - start) / steps;
        Integer[] nSims = new Integer[steps + 1];

        nSims[0] = start;
        nSims[steps] = end;

        for (int i = 1; i <= steps; i++)
            nSims[i] = nSims[i - 1] + interval;

        System.out.println(Arrays.toString(nSims));
        System.out.println("Num mauler: " + nGames);

        DRPlot plot = new DRPlot(rowNames, nSims);

        for (int run = 0; run < nSims.length; run++)
        {
            System.out.println("Run:  " + run);
            System.out.println("No. sims:  ");

            // Players
            List<Player<Othello>> players = new ArrayList<Player<Othello>>();
            MonteCarlo<Othello> mc = new MonteCarlo<Othello>(nSims[run]);
            MCTS<Othello> mcts = new MCTS<Othello>(treePolicy, defaultPolicy, nSims[run]);
            players.add(mc);
            players.add(mcts);

            Series<Othello> series = new Series<Othello>(new Othello(), nGames, players);
            series.run();

            for (int player = 0; player < players.size(); player++)
                plot.setData(player, run, series.getWinsAvg(player));
        }

        System.out.println(plot);
    }

    public static void main(String[] args)
    {
        MCTSvsMCPlotTime();
    }
}
