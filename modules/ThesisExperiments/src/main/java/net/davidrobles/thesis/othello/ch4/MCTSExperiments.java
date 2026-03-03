package net.davidrobles.thesis.othello.ch4;

//import com.google.common.base.Stopwatch;
import net.davidrobles.mauler.core.RoundRobin;
import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.util.DRPlot;
import net.davidrobles.mauler.strategies.greedy.GreedyStrategy;
import net.davidrobles.mauler.strategies.mc.MonteCarlo;
import net.davidrobles.mauler.strategies.mcts.MCTS;
import net.davidrobles.mauler.strategies.mcts.tree.SelectionPolicy;
import net.davidrobles.mauler.strategies.mcts.tree.UCB1;
import net.davidrobles.util.DRMarkdown;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTSExperiments
{
    private static void MCTSvsMC()
    {
        Random rng = new Random();

        // configuration
        double c = 0.5;
        int nSims = 5000;

        // Tree policies
        SelectionPolicy<Othello> treePolicy = new UCB1<Othello>(c, rng);

        // Default policies
        Strategy<Othello> defaultPolicy = new RandomStrategy<Othello>(rng);

        // Players
        List<Strategy<Othello>> players = new ArrayList<Strategy<Othello>>();
        MCTS<Othello> mcts = new MCTS<Othello>(treePolicy, defaultPolicy, nSims);
        MonteCarlo<Othello> mc = new MonteCarlo<Othello>(nSims);
        players.add(mcts);
        players.add(mc);

        int nGames = 50;

        Series<Othello> series = new Series<>(Othello::new, nGames, players);
        series.run();
    }

    private static void MCTSvsMC_Time()
    {
        Random rng = new Random();

        // configuration
        double c = 0.5;
        int timeout = 302;

        // Tree policies
        SelectionPolicy<Othello> treePolicy = new UCB1<Othello>(c, rng);

        // Default policies
        Strategy<Othello> defaultPolicy = new RandomStrategy<Othello>(rng);

        // Players
        List<Strategy<Othello>> players = new ArrayList<Strategy<Othello>>();
        MCTS<Othello> mcts = new MCTS<Othello>(treePolicy, defaultPolicy);
        MonteCarlo<Othello> mc = new MonteCarlo<Othello>();
        players.add(mcts);
        players.add(mc);

        int nGames = 100;

        Series<Othello> series = new Series<>(Othello::new, nGames, players, timeout);
        series.run();
    }

    public static void testParallelMC()
    {
//        Othello othello = new Othello();
//        UtilityFunction<Othello> utilFunc = new UtilityFunction<Othello>();
//        MCTime<Othello> timedMC = new MCTime<Othello>(utilFunc);
//        RandomPlayer<Othello> randomPlayer = new RandomPlayer<Othello>();
//        ParTimedMC<Othello> parTimedMC = new ParTimedMC<Othello>(utilFunc);
//        List<Strategy<Othello>> players = new ArrayList<Strategy<Othello>>();
//        players.add(timedMC);
//        players.add(randomPlayer);
//
//        Series<Othello> series = new Series<Othello>(new Othello(), 100, players);
//        System.out.println(series.call());
    }

    public static void testSeries()
    {
        RandomStrategy<Othello> randomPlayer = new RandomStrategy<Othello>();
        List<Strategy<Othello>> players = new ArrayList<Strategy<Othello>>();
        players.add(randomPlayer);
        players.add(randomPlayer);

        int nGames = 250;
        Series<Othello> series = new Series<>(Othello::new, nGames, players);
        series.run();
        System.out.println(series);
    }

    public static void testRoundRobin()
    {
        List<Strategy<Othello>> players = new ArrayList<Strategy<Othello>>();
        List<String> playerNames = new ArrayList<String>();
        players.add(new RandomStrategy<Othello>());
        playerNames.add("Rnd");
        players.add(new GreedyStrategy<Othello>(new WPC(WPCUtil.load("dr-sym-6462"))));
        playerNames.add("Greedy");
//        players.add(new GreedyStrategy<Othello>(new WPC(WPCUtil.load("dr-sym-6172"))));
//        playerNames.add("Greedy");
        players.add(new MonteCarlo<Othello>(100));
        playerNames.add("MC");
        players.add(new MCTS<Othello>(new UCB1<Othello>(0.5), new RandomStrategy<Othello>(), 100));
        playerNames.add("MCTS");

        int nGames = 100;
        RoundRobin<Othello> roundRobin = new RoundRobin<>(Othello::new, nGames, players, playerNames);
        String caption = String.format("Round Robin Tournament (Winning rates over %d mauler).", nGames);
        roundRobin.setCaption(caption);
        roundRobin.run();
        DRMarkdown.printH2("Results");
        System.out.println(roundRobin.toFormattedTable());
        DRMarkdown.printH2("Latex");
        System.out.println(roundRobin.toLatexTable());
        DRMarkdown.printH2("CSV");
        System.out.println(roundRobin.toCSV());
    }

    public static void hello()
    {
        List<Strategy<Othello>> players = new ArrayList<Strategy<Othello>>();
        players.add(new MonteCarlo<Othello>(100));
        players.add(new MonteCarlo<Othello>(100));

        int nGames = 20;
        Series<Othello> series = new Series<>(Othello::new, nGames, players);
//        series.call();
    }

    public static void yeap()
    {
        List<Strategy<Othello>> players = new ArrayList<Strategy<Othello>>();
        players.add(new MonteCarlo<Othello>(500));
        players.add(new MonteCarlo<Othello>(500));

        int nGames = 20;
//        Stopwatch stopwatch = new Stopwatch();
//        stopwatch.start();

        for (int i = 0; i < nGames; i++)
        {
            Othello othello = new Othello();

            while (!othello.isOver())
                othello.makeMove(players.get(othello.getCurPlayer()).move(othello));
        }

//        stopwatch.stop();
//        System.out.println(stopwatch);
    }

    private static Integer[] getRanges(int start, int end, int steps)
    {
        int interval = (end - start) / steps;
        Integer[] nSims = new Integer[steps + 1];

        nSims[0] = start;
        nSims[steps] = end;

        for (int i = 1; i <= steps; i++)
            nSims[i] = nSims[i - 1] + interval;

        return nSims;
    }

    public static void mcVsRandom()
    {
        // configuration
        int nGames = 1000;

//        Integer[] nSims = { 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100 };
//        Integer[] nSims = { 1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
//        Integer[] nSims = { 200 };

        int start = 1;
        int end = 250;
        int steps = 25;
        Integer[] nSims = getRanges(start, end, steps);

        Object[] rowNames = { "MC", "MCTS" };

        DRPlot plot = new DRPlot(rowNames, nSims);

        for (int run = 0; run < nSims.length; run++)
        {
            System.out.println("Run " + run + "\n");
            System.out.println();

            // MC
            MonteCarlo<Othello> mcSims = new MonteCarlo<Othello>(nSims[run]);

            // MCTS
            double c = 0.5;
            SelectionPolicy<Othello> treePolicy = new UCB1<Othello>(c);
            MCTS<Othello> mctsSims = new MCTS<Othello>(treePolicy, new RandomStrategy<Othello>(), nSims[run]);

            // Players evaluated
            List<Strategy<Othello>> testedPlayers = new ArrayList<Strategy<Othello>>();
            testedPlayers.add(mcSims);
            testedPlayers.add(mctsSims);

            for (int i = 0; i < testedPlayers.size(); i++)
            {
                List<Strategy<Othello>> seriesPlayers = new ArrayList<Strategy<Othello>>();
                seriesPlayers.add(testedPlayers.get(i));
                seriesPlayers.add(new RandomStrategy<Othello>());

                // Series
                Series<Othello> series = new Series<>(Othello::new, nGames, seriesPlayers);
                series.run();

                // Plot
                plot.setData(i, run, series.getWinsAvg(0));
            }
        }

        System.out.println(plot);
    }

    public void ratioNumSimsMCvsMCTS()
    {
        // Players
        MCTS<Othello> mcts = new MCTS<Othello>(new UCB1<Othello>(0.5), new RandomStrategy<Othello>());
        MonteCarlo<Othello> mc = new MonteCarlo<Othello>();
        mcts.move(new Othello(), 1000);
        mc.move(new Othello(), 1000);


    }

    public static void main(String[] args)
    {
//       MCTSvsMC_Time();

//        MCTSWithWPC();

//        System.out.println("Number of processors: " + Runtime.getRuntime().availableProcessors());
//        MCTSvsMCPlotSims();
//        MCTSvsMC();
//        mcVsRandom();

//        int mb = 1024 * 1024;
//        Games.play();
//        hello();

//        MCTSWithWPC();
//        testParallelMC();

//        testRoundRobin();

//        System.out.println(Runtime.getRuntime().availableProcessors());
//        System.out.println(Runtime.getRuntime().maxMemory() / mb);
//        System.out.println(Runtime.getRuntime().totalMemory() / mb);
    }
}
