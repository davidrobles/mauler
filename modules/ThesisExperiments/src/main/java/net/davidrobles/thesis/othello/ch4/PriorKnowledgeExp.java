package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.RoundRobin;
import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.mauler.players.EpsilonGreedy;
import net.davidrobles.mauler.players.GreedyPlayer;
import net.davidrobles.mauler.players.mc.MonteCarlo;
import net.davidrobles.mauler.players.mcts.MCTS;
import net.davidrobles.mauler.players.mcts.UCT;
import net.davidrobles.mauler.players.mcts.enh.MCTSPrior;
import net.davidrobles.mauler.players.mcts.enh.UCTNoRollout;
import net.davidrobles.mauler.players.mcts.enh.UCTPrior;
import net.davidrobles.mauler.players.mcts.tree.UCB1;

import java.util.ArrayList;
import java.util.List;

/**
 * Experiments of MCTS players with added prior knowledge.
 */
public class PriorKnowledgeExp
{
    /**
     * MCTS
     * vs
     * MCTS with Prior Knowledge and Non-Random Default Policy
     */
    static void MCTSvsMCTSPriorNonRandom()
    {
        int nGames = 50;
        int timeout = 1000;
        double c = 0.5;
        int qInit = 100;
        double epsilon = 0.01;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new UCT<Othello>(c));
        players.add(new MCTSPrior<Othello>(new UCB1<Othello>(c), new EpsilonGreedy<Othello>(wpc, epsilon), wpc, qInit));

        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
        series.run();
    }

    /**
     * Round robin tournament between MCTS players with Prior Knowledge
     * and Non-Random Default Policy using different exploration.
     */
    static void roundRobinMCTSPriorNonRandom()
    {
        int nGames = 250;
        int nSims = 1000;
        double c = 0.5;
        int nInit = 100;

        double[] epsilons = { 0.0, 0.1, 0.2, 0.3 };

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playerNames = new ArrayList<String>();

        for (int i = 0; i < epsilons.length; i++)
        {
            EpsilonGreedy<Othello> eGreedy = new EpsilonGreedy<Othello>(wpc, epsilons[i]);
            players.add(new MCTSPrior<Othello>(new UCB1<Othello>(c), eGreedy, wpc, nInit, nSims));
            playerNames.add("e = " + epsilons[i]);
        }

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

    /**
     * MCTS
     * vs
     * MCTS with Non-Random Default Policy
     */
    static void MCTSvsMCTSNonRandom()
    {
        int nGames = 250;
        int timeout = 1000;
        double c = 0.5;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new UCT<Othello>(c));
        players.add(new MCTS<Othello>(new UCB1<Othello>(c), new GreedyPlayer<Othello>(wpc)));

        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
        series.run();
    }

    /**
     * MCTS
     * vs
     * MCTS with Prior Knowledge
     */
    static void MCTSvsMCTSPrior()
    {
        int nGames = 250;
        int timeout = 1000;
        double c = 0.5;
        int initQVisits = 100;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new UCT<Othello>(c));
        players.add(new UCTPrior<Othello>(c, wpc, initQVisits));

        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
        series.run();
    }

    public static void roundRobinBetweenPriorKnowMCTSPlayersWithSims()
    {
        System.out.println("evo");

        int nGames = 100;
        int nSims = 10;
        double c = 0.5;
        int timeout = 1000;
        int[] initQVisits = { 10, 25, 50, 75, 100 };

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playerNames = new ArrayList<String>();

//        for (int i = 0; i < initQVisits.length; i++)
//        {
//            players.add(new UCTPrior<Othello>(c, wpc, initQVisits[i]));
//            playerNames.add("QVisits = " + initQVisits[i]);
//        }

//        players.add(new UCTPrior<Othello>(c, wpc, 100));
//        playerNames.add("WPC");

//        players.add(new UCTPrior<Othello>(c, NTS_LOG, 100));
//        playerNames.add("NTS_LOG");

//        players.add(new UCT<Othello>(c));
//        playerNames.add("UCT");

//        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(wpc, 0.1), c));
//        playerNames.add("NR");

        players.add(new UCTPrior<Othello>(c, OthelloVF.NTS_LOG, 100));
        playerNames.add("NTS_LOG");

        players.add(new UCTPrior<Othello>(c, OthelloVF.NTS_EVO, 100));
        playerNames.add("NTS_LOG");

//        players.add(new UCTPrior<Othello>(new EpsilonGreedy<Othello>(wpc, 0.1), c, NTS_LOG, 100));
//        playerNames.add("BOTH");

//        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(NTS_LOG, 0.1), c));
//        playerNames.add("NR");

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames, timeout);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

    static void roundRobinBetweenPriorVsAllWithTime()
    {
        int nGames = 20;
        int timeout = 250;
        double c = 0.5;
        int initQVisits = 50;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playerNames = new ArrayList<String>();

        players.add(new MonteCarlo<Othello>());
        playerNames.add("MonteCarlo");
        players.add(new UCT<Othello>(c));
        playerNames.add("UCT");
        players.add(new UCTPrior<Othello>(c, wpc, initQVisits));
        playerNames.add("UCTPrior");
        players.add(new UCTNoRollout<Othello>(c, wpc));
        playerNames.add("UCTNoRollout");

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames, timeout);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

    public static void main(String[] args)
    {
        roundRobinMCTSPriorNonRandom();
    }
}
