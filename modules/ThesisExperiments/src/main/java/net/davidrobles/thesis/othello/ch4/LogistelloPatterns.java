package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.OthelloUtil;
import net.davidrobles.mauler.othello.TD0;
import net.davidrobles.mauler.othello.ef.ntuples.NTUtil;
import net.davidrobles.mauler.othello.ef.ntuples.NTupleSystem;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.mauler.players.DRPlot;
import net.davidrobles.mauler.players.EpsilonGreedy;

import java.util.ArrayList;
import java.util.List;

public class LogistelloPatterns
{
    /**
     * Patterns from Michael Buro's paper:
     * "Improving heuristic mini-max search by supervised learning".
     */
    public static final long[] PATTERNS =
    {
            16909320, // diag4
            4328785936L, // diag5
            1108169199648L, // diag6
            283691315109952L, // diag7
            72624976668147840L, // diag8
            65280L, // hor./vert.2
            16711680L, // hor./vert.3
            4278190080L, // hor./vert.4
            17151L, // edge+2X
            7967L, // 2x5-corner
            460551L // 3x3-corner
    };

    static void learnNTS()
    {
        // NTS to learn
        NTupleSystem nts = NTUtil.generateRandomNTupleSystem(10, 6);
//        NTupleSystem nts = new NTupleSystem(NTUtil.createNTuples(PATTERNS));

        // TD(0) settings
        int episodes = 5000;
        double learningRate = 0.001;
        double discountFactor = 1.0;
        double epsilon = 0.1;
        int interval = 100;
        int games = 1000;

        // Info
        System.out.println("==========================================");
        System.out.println(" Learning the weights of a NTS with TD(0)");
        System.out.println("==========================================\n");
        System.out.println("Episodes:        " + episodes);
        System.out.println("Learning rate:   " + learningRate);
        System.out.println("Discount factor: " + discountFactor);
        System.out.println("Epsilon:         " + epsilon);
        System.out.println("Interval:        " + interval);
        System.out.println("Games to play:   " + games);

        // Evaluation functions to test against
        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        NTupleSystem logistello = NTUtil.load("logistello11-130000-0.822");
//        NTupleSystem prevNTS = nts.copy();

        // Learning algorithm
        TD0<Othello> td0 = new TD0<Othello>(new Othello(), nts, episodes, learningRate, discountFactor, epsilon);

        // Plot
        Object[] rowNames = { "Random", "DR-WPC", "Logistello" };
        Object[] colNames = new Integer[(episodes / interval) + 1];

        for (int i = 0; i < colNames.length; i++)
            colNames[i] = i * interval;

        colNames[colNames.length - 1] = episodes;

        DRPlot plot = new DRPlot(rowNames, colNames);
        int i = 0;

        for (int episode = 0; episode <= episodes; episode++)
        {
            td0.iteration();

            if (episode % interval == 0)
            {
                System.out.println("\n---------------------");
                System.out.println(" Episode:  " + episode );
                System.out.println("---------------------\n");

                List<Player<Othello>> players;
                Series<Othello> series;

                // Series against previous
//                players.add(new EpsilonGreedy<Othello>(nts, 0.1));
//                players.add(new EpsilonGreedy<Othello>(prevNTS, 0.1));
//                series.setVerbose(false);
//                series.run();
//                System.out.println("vs prev: " + series.getWinsAvg(0));

                // Series against Random
                players = new ArrayList<Player<Othello>>();
                players.add(new EpsilonGreedy<Othello>(nts, 0.0));
                players.add(new RandPlayer<Othello>());
                series = new Series<Othello>(new Othello(), games, players);
                series.setVerbose(false);
                series.run();
                System.out.println("vs Random: " + series.getWinsAvg(0));
                plot.setData(0, i, series.getWinsAvg(0));

                // Series against best WPC
                players = new ArrayList<Player<Othello>>();
                players.add(new EpsilonGreedy<Othello>(nts, 0.1));
                players.add(new EpsilonGreedy<Othello>(wpc, 0.1));
                series = new Series<Othello>(new Othello(), games, players);
                series.setVerbose(false);
                series.run();
                System.out.println("vs WPC: " + series.getWinsAvg(0));
                plot.setData(1, i, series.getWinsAvg(0));

                // Series against best WPC
                players = new ArrayList<Player<Othello>>();
                players.add(new EpsilonGreedy<Othello>(nts, 0.1));
                players.add(new EpsilonGreedy<Othello>(logistello, 0.1));
                series = new Series<Othello>(new Othello(), games, players);
                series.setVerbose(false);
                series.run();
                System.out.println("vs Logistello: " + series.getWinsAvg(0));
                plot.setData(2, i, series.getWinsAvg(0));

                // Series against MCTS
//                players = new ArrayList<Player<Othello>>();
//                players.add(new EpsilonGreedy<Othello>(nts, 0.1));
//                players.add(new UCT<Othello>(0.5, 200));
//                series = new Series<Othello>(new Othello(), mauler, players);
//                series.setVerbose(false);
//                series.run();
//                System.out.println("vs MCTS: " + series.getWinsAvg(0));
//                plot.setData(2, i++, series.getWinsAvg(0));

//                prevNTS = nts.copy();
                i++;
                System.out.println("------------------------");

                // Save N-tuple system
                NTUtil.save(nts, "turing-" + episode + "-" + String.format("%.3f", series.getWinsAvg(0)));
            }
        }

        System.out.println(plot);
    }

    static void printLogistelloPatterns()
    {
        for (int i = 0; i < PATTERNS.length; i++)
            OthelloUtil.printBitBoard(PATTERNS[i]);
    }

    public static void main(String[] args)
    {
        learnNTS();
    }
}
