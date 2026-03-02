package net.davidrobles.thesis.othello.ch7;

import net.davidrobles.mauler.core.RoundRobin;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.players.EpsilonGreedy;
import net.davidrobles.mauler.players.mcts.UCT;
import net.davidrobles.thesis.othello.ch4.PriorKnowledgeExp;

import java.util.*;

import static net.davidrobles.thesis.othello.ch4.OthelloVF.*;

public class MCTSReplacingDefault
{

//        /**
//         * MCTS
//         * vs
//         * MCTS with Prior Knowledge and Non-Random Default Policy
//         */
//        static void MCTSvsMCTSPriorNonRandom()
//        {
//            int nGames = 50;
//            int timeout = 1000;
//            double c = 0.5;
//            int qInit = 100;
//            double epsilon = 0.01;
//
//            WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
//            List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//            players.add(new UCT<Othello>(c));
//            players.add(new MCTSPrior<Othello>(new UCB1<Othello>(c), new EpsilonGreedy<Othello>(wpc, epsilon), wpc, qInit));
//
//            Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
//            series.run();
//        }

    /**
     * Round robin tournament between MCTS players with Prior Knowledge
     * and Non-Random Default Policy using different exploration.
     */
    static void roundRobinMCTSPriorNonRandom()
    {
        System.out.println("spaguetti");

        int nGames = 100;
        double c = 0.5;

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//        players.add(new UCT<Othello>(Rc));
        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(WPC_SYM, 0.1), c));
        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(NTS_EVO, 0.1), c));
        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(NTS_LOG, 0.1), c));

        List<String> playerNames = new ArrayList<String>();
        playerNames.add("UCT");
//        playerNames.add("WPC-SYM");
        playerNames.add("NTS-EVO");
        playerNames.add("NTS_LOG");

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames, 1000);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

//        /**
//         * MCTS
//         * vs
//         * MCTS with Non-Random Default Policy
//         */
//        static void MCTSvsMCTSNonRandom()
//        {
//            int nGames = 250;
//            int timeout = 1000;
//            double c = 0.5;
//
//            WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
//            List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//            players.add(new UCT<Othello>(c));
//            players.add(new MCTS<Othello>(new UCB1<Othello>(c), new GreedyPlayer<Othello>(wpc)));
//
//            Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
//            series.run();
//        }
//
//        /**
//         * MCTS
//         * vs
//         * MCTS with Prior Knowledge
//         */
//        static void MCTSvsMCTSPrior()
//        {
//            int nGames = 250;
//            int timeout = 1000;
//            double c = 0.5;
//            int initQVisits = 100;
//
//            WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
//            List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//            players.add(new UCT<Othello>(c));
//            players.add(new UCTPrior<Othello>(c, wpc, initQVisits));
//
//            Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
//            series.run();
//        }
//
//        static void roundRobinBetweenPriorKnowMCTSPlayersWithSims()
//        {
//            int nGames = 250;
//            int nSims = 500;
//            double c = 0.5;
//            int[] initQVisits = { 10, 50, 100, 250, 500 };
//
//            WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
//            List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//            List<String> playerNames = new ArrayList<String>();
//
//            for (int i = 0; i < initQVisits.length; i++)
//            {
//                players.add(new UCTPrior<Othello>(c, wpc, initQVisits[i], nSims));
//                playerNames.add("QVisits = " + initQVisits[i]);
//            }
//
//            RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames);
//            roundRobin.run();
//            System.out.println(roundRobin.toLatexTable());
//        }
//
//        static void roundRobinBetweenPriorVsAllWithTime()
//        {
//            int nGames = 20;
//            int timeout = 250;
//            double c = 0.5;
//            int initQVisits = 50;
//
//            WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
//            List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//            List<String> playerNames = new ArrayList<String>();
//
//            players.add(new MonteCarlo<Othello>());
//            playerNames.add("MonteCarlo");
//            players.add(new UCT<Othello>(c));
//            playerNames.add("UCT");
//            players.add(new UCTPrior<Othello>(c, wpc, initQVisits));
//            playerNames.add("UCTPrior");
//            players.add(new UCTNoRollout<Othello>(c, wpc));
//            playerNames.add("UCTNoRollout");
//
//            RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames, timeout);
//            roundRobin.run();
//            System.out.println(roundRobin.toLatexTable());
//        }

    public static void main(String[] args)
    {
//        roundRobinMCTSPriorNonRandom();

//        MCTS_WPC.MCTSvsMCTS_WPCEpsilonValues();


//        roundRobinMCTSPriorNonRandom();

        PriorKnowledgeExp.roundRobinBetweenPriorKnowMCTSPlayersWithSims();
    }
}
