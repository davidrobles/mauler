package net.davidrobles.thesis.othello.ch4;

//import com.google.gson.Gson;
import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.TD0;
import net.davidrobles.mauler.othello.ef.EvaluatorFitness;
import net.davidrobles.mauler.othello.ef.ntuples.NTUtil;
import net.davidrobles.mauler.othello.ef.ntuples.NTupleSystem;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCType;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.mauler.strategies.greedy.EpsilonGreedyStrategy;
import net.davidrobles.mauler.strategies.greedy.GreedyStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class NTSLearning
{
    // TODO: What about a learner that tests the learning after n number of time steps against some particular player?

    static void learnWPCWithTD0()
    {
        // td0 settings
        WPCType type = WPCType.SYM;
        int episodes = 50000;
        double learningRate = 0.001;
        double discountFactor = 1.0;
        double epsilon = 0.1;
        int interval = 5;
        int games = 10000;

        // to save the best WPC's
        TreeSet<EvaluatorFitness<WPC>> set = new TreeSet<EvaluatorFitness<WPC>>();

        WPC wpc = new WPC(type);
        TD0<Othello> td0 = new TD0<Othello>(Othello::new, wpc, episodes, learningRate, discountFactor, epsilon);

        // players
        List<Strategy<Othello>> players = new ArrayList<Strategy<Othello>>();
        players.add(new EpsilonGreedyStrategy<Othello>(wpc, 0.0));
        players.add(new RandomStrategy<Othello>());

        for (int episode = 0; episode <= episode; episode++)
        {
            td0.iteration();

            if (episode % interval == 0)
            {
                Series<Othello> series = new Series<>(Othello::new, games, players);
                series.setVerbose(false);
                series.run();
                System.out.println("Episode:  " + episode);
                System.out.println("Win rate: " + series.getWinsAvg(0));
                set.add(new EvaluatorFitness<WPC>(wpc.copy(), series.getWinsAvg(0)));
                EvaluatorFitness.printTop(set, 5);
                System.out.println("------------------------");
            }
        }
    }

    static void learnNTSWithTD0PlayingAgainstSelf()
    {
        // td0 settings
//        int episodes = 500000;
//        double learningRate = 0.001;
//        double discountFactor = 1.0;
//        double epsilon = 0.1;
//        int interval = 5000;
//        int mauler = 1000;

        int episodes = 5000;
        double learningRate = 0.001;
        double discountFactor = 1.0;
        double epsilon = 0.1;
        int interval = 1000;
        int games = 100;

        // Evaluation functions
//        NTupleSystem nts = NTUtil.load("hola9-0.3470");
//        NTupleSystem nts = NTUtil.load("buenero-56");
//        nts.reset();
        NTupleSystem nts = NTUtil.generateRandomNTupleSystem(20, 6);
//        NTupleSystem nts = new NTupleSystem(NTUtil.createNTuples(LogistelloPatterns.PATTERNS));
//        nts.reset();
//        NTupleSystem logistello = NTUtil.load("logistello11-130000-0.822");

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));

        NTupleSystem prevNTS = nts.copy();
        TD0<Othello> td0 = new TD0<Othello>(Othello::new, nts, episodes, learningRate, discountFactor, epsilon);

        for (int episode = 1; episode <= episodes; episode++)
        {
            td0.iteration();

            if (episode % interval == 0)
            {
                System.out.println("Episode:  " + episode);

                List<Strategy<Othello>> players;
                Series<Othello> series;

                // Series against previous
//                players = new ArrayList<Strategy<Othello>>();
//                players.add(new EpsilonGreedyStrategy<Othello>(nts, 0.1));
//                players.add(new EpsilonGreedyStrategy<Othello>(prevNTS, 0.1));
//                series = new Series<Othello>(new Othello(), mauler, players);
//                series.setVerbose(false);
//                series.run();
//                System.out.println("vs prev: " + (series.getWinsAvg(0) - series.getWinsAvg(1)));

                // Series against best WPC
//                players = new ArrayList<Strategy<Othello>>();
//                players.add(new EpsilonGreedyStrategy<Othello>(nts, 0.1));
//                players.add(new EpsilonGreedyStrategy<Othello>(wpc, 0.1));
//                series = new Series<Othello>(new Othello(), mauler, players);
//                series.setVerbose(false);
//                series.run();
//                System.out.println("vs WPC: " + series.getWinsAvg(0));

//                // Series against MC
//                players = new ArrayList<Strategy<Othello>>();
//                players.add(new EpsilonGreedyStrategy<Othello>(nts, 0.0));
//                players.add(new MonteCarlo<Othello>(200));
//                series = new Series<Othello>(new Othello(), mauler, players);
//                series.setVerbose(false);
//                series.run();
//                System.out.println("vs MC: " + series.getWinsAvg(0));

                // Series against Random
                players = new ArrayList<Strategy<Othello>>();
                players.add(new GreedyStrategy<Othello>(nts));
                players.add(new RandomStrategy<Othello>());
                System.out.println("size: " + players.size());
                series = new Series<>(Othello::new, games, players);
                series.setVerbose(false);
                series.run();
                System.out.println("vs Random: " + series.getWinsAvg(0));

                // Series against Logistello
//                players = new ArrayList<Strategy<Othello>>();
//                players.add(new EpsilonGreedyStrategy<Othello>(nts, 0.1));
//                players.add(new EpsilonGreedyStrategy<Othello>(logistello, 0.1));
//                series = new Series<Othello>(new Othello(), mauler, players);
//                series.setVerbose(false);
//                series.run();
//                System.out.println("vs Logistello: " + series.getWinsAvg(0));

                String filename = String.format("test-%d-%.4f", episode, series.getWinsAvg(0));
                NTUtil.save(nts, filename);

//                Gson gson = new Gson();
//                System.out.println(gson.toJson(nts, NTupleSystem.class));
//                System.out.println("------------------------");
//                System.out.println();
//                prevNTS = nts.copy();
            }
        }
    }

    public static void main(String[] args)
    {
        learnNTSWithTD0PlayingAgainstSelf();
    }
}
