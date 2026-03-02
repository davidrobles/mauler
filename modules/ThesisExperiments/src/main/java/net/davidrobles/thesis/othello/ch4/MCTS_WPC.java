package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.Outcome;
import net.davidrobles.mauler.core.RoundRobin;
import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.mauler.players.EpsilonGreedy;
import net.davidrobles.mauler.players.GreedyPlayer;
import net.davidrobles.mauler.players.UtilFunc;
import net.davidrobles.mauler.players.mcts.MCTS;
import net.davidrobles.mauler.players.mcts.UCT;
import net.davidrobles.mauler.players.mcts.tree.TreePolicy;
import net.davidrobles.mauler.players.mcts.tree.UCB1;

import java.util.ArrayList;
import java.util.List;

import static net.davidrobles.thesis.othello.ch4.OthelloVF.*;

public class MCTS_WPC
{
    public static void evaluateUtilityFunctionsInMCTS()
    {
        Othello othello = new Othello();
        double c = 0.5;
        int nGames = 250;
        int sims = 2500;

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playersNames = new ArrayList<String>();

        TreePolicy<Othello> treePolicy = new UCB1<Othello>(c);
        RandPlayer<Othello> randPlayer = new RandPlayer<Othello>();
        MCTS<Othello> mcts = new MCTS<Othello>(treePolicy, randPlayer, sims);
        mcts.setUtilFunc(new UtilFunc<Othello>(1.0, 0.0, 0.5));
        players.add(mcts);
        playersNames.add("u=[1,0.0,0.5]");

        TreePolicy<Othello> treePolicy2 = new UCB1<Othello>(c);
        RandPlayer<Othello> randPlayer2 = new RandPlayer<Othello>();
        MCTS<Othello> mcts2 = new MCTS<Othello>(treePolicy2, randPlayer2, sims);
        players.add(mcts2);
        playersNames.add("u=[1,-1,0]");

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(othello, nGames, players, playersNames);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

    // Evaluates MCTS algorithms with different C values
    public static void MCTSDifferentCValues()
    {
        Othello othello = new Othello();
        double[] cValues = { 0.3, 0.4, 0.5, 0.6, 0.7 };
        int nGames = 250;
        int sims = 2500;

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playersNames = new ArrayList<String>();

        for (int i = 0; i < cValues.length; i++)
        {
            TreePolicy<Othello> treePolicy = new UCB1<Othello>(cValues[i]);
            RandPlayer<Othello> randPlayer = new RandPlayer<Othello>();
            MCTS<Othello> mcts = new MCTS<Othello>(treePolicy, randPlayer, sims);
            players.add(mcts);
            playersNames.add("c=" + cValues[i]);
        }

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(othello, nGames, players, playersNames);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

    // This experiment will find out which epsilon value is better for the replacement of
    // the random player in the default policy
    public static void MCTSvsMCTS_WPCEpsilonValues()
    {
        System.out.println("with evo");
        Othello othello = new Othello();
        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        double[] epsilonValues = { 0.0, 0.1, 0.25, 0.5, 1.0};
        double c = 0.5;
        int nGames = 50;
        int timeout = 250;
        int nSims = 1500;

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playersNames = new ArrayList<String>();

        for (int i = 0; i < epsilonValues.length; i++)
        {
            EpsilonGreedy<Othello> eGreedyWPC = new EpsilonGreedy<Othello>(NTS_EVO, epsilonValues[i]);
            MCTS<Othello> mcts = new MCTS<Othello>(new UCB1<Othello>(c), eGreedyWPC);
            players.add(mcts);
            playersNames.add("e=" + epsilonValues[i]);
        }

        RoundRobin<Othello> rr = new RoundRobin<Othello>(othello, nGames, players, playersNames, timeout);
        rr.run();
        System.out.println(rr.toLatexTable());
    }

    public static void MCTS_vs_MCTS_WPC_Sims()
    {
        int nSims = 3000;
        double c = 0.5;
        int nGames = 50;
        double epsilon = 0.0;
        TreePolicy<Othello> treePolicy = new UCB1<Othello>(c);
        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        EpsilonGreedy<Othello> epsilonGreedy = new EpsilonGreedy<Othello>(wpc, epsilon);
        MCTS<Othello> stdMCTS = new MCTS<Othello>(treePolicy, new RandPlayer<Othello>(), nSims);
        MCTS<Othello> wpcMCTS = new MCTS<Othello>(treePolicy, epsilonGreedy, nSims);
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(stdMCTS);
        players.add(wpcMCTS);
        Series<Othello> untimedSeries = new Series<Othello>(new Othello(), nGames, players);
        untimedSeries.run();
    }

    public static void MCTS_vs_MCTS_WPC_Time()
    {
        double c = 0.5;
        int nGames = 100;
        int timeout = 1000;
        TreePolicy<Othello> treePolicy = new UCB1<Othello>(c);
        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
//        MCTSTime<Othello> stdMCTS = new MCTSTime<Othello>(treePolicy, new RandPlayer<Othello>());
        MCTS<Othello> stdMCTS = new MCTS<Othello>(treePolicy, new EpsilonGreedy<Othello>(wpc, 1.0));
        MCTS<Othello> wpcMCTS = new MCTS<Othello>(treePolicy, new EpsilonGreedy<Othello>(wpc, 0.1));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(stdMCTS);
        players.add(wpcMCTS);

        Series<Othello> timedSeries = new Series<Othello>(new Othello(), nGames, players, timeout);
        timedSeries.run();
    }

    public static void fuckingTest()
    {
        double c = 0.5;
        TreePolicy<Othello> treePolicy = new UCB1<Othello>(c);
        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        MCTS<Othello> p1 = new MCTS<Othello>(treePolicy, new EpsilonGreedy<Othello>(wpc, 1.0));
        MCTS<Othello> p2 = new MCTS<Othello>(treePolicy, new EpsilonGreedy<Othello>(wpc, 0.1));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(p1);
        players.add(p2);

        Othello othello = new Othello();
        int nGames = 100;
        int wins = 0;
        int timeout = 50;

        for (int i = 0; i < nGames; i++)
        {
            othello.reset();
            int starter = i % 2;

            while (!othello.isOver())
            {
                int curPlayer = (othello.getCurPlayer() + starter) % 2;
//                int curPlayer = othello.getCurPlayer();
                othello.makeMove(players.get(curPlayer).move(othello, timeout));
            }

            if (othello.getOutcome()[starter] == Outcome.WIN)
                wins++;
        }

        System.out.println("Wins: " + wins);
    }

    static void MCTSNonRandomGreedyVsMCTSNonRandomEpsilonGreedy()
    {
        int nGames = 50;
        int timeout = 1000;
        double c = 0.5;
        double epsilon = 0.01;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new UCT<Othello>(new GreedyPlayer<Othello>(wpc), c));
        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(wpc, epsilon), c));

        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
        series.run();
    }
}
