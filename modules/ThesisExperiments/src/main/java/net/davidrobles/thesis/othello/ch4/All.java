package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.RoundRobin;
import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.ef.ntuples.NTUtil;
import net.davidrobles.mauler.othello.ef.ntuples.NTupleSystem;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.mauler.players.EpsilonGreedy;
import net.davidrobles.mauler.players.mcts.UCT;
import net.davidrobles.mauler.players.mcts.enh.UCTPrior;

import java.util.ArrayList;
import java.util.List;

public class All
{
    static void allExperiments()
    {
        int nGames = 50;
        int timeout = 50;
        double c = 0.5;
        int nInit = 100;

        // Value functions
        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        NTupleSystem logistello = NTUtil.load("logistello11-130000-0.822");

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playerNames = new ArrayList<String>();

        // UCT
        players.add(new UCT<Othello>(c));
        playerNames.add("UCT");

//        // UCT + Non Random Default Policy
//        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(wpc, 0.1), c));
//        playerNames.add("UCT+NR");

//        UCT + Non Random Default Policy
//        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(heuristic, 0.9), c));
//        playerNames.add("UCT-0.9");

//        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(heuristic, 0.99), c));
//        playerNames.add("UCT-0.99");
//
//        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(heuristic, 0.999), c));
//        playerNames.add("UCT-0.999");
//
//        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(heuristic, 1.0), c));
//        playerNames.add("UCT-1.0");

        // UCT + Prior Knowledge
//        players.add(new UCTPrior<Othello>(c, logistello, 100));
//        playerNames.add("UCT+100");
//
//        players.add(new UCTPrior<Othello>(c, logistello, 500));
//        playerNames.add("UCT+500");

        players.add(new UCTPrior<Othello>(c, logistello, 1000));
        playerNames.add("UCT+1000+Log");

        players.add(new UCTPrior<Othello>(c, wpc, 1000));
        playerNames.add("UCT+1000+WPC");

//
//        players.add(new UCTPrior<Othello>(c, heuristic, 5000));
//        playerNames.add("UCT+5000");

        // UCT + Non Random Default Policy + Prior Knowledge
//        GreedyPlayer<Othello> greedy = new GreedyPlayer<Othello>(logistello);
//        players.add(new UCTPrior<Othello>(greedy, c, logistello, nInit));
//        playerNames.add("UCT+NR+PK");

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames, timeout);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

    static void test()
    {
        int nGames = 100;
        double c = 0.5;
        WPC heuristic = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(heuristic, 0.0), c));
        players.add(new UCT<Othello>(new EpsilonGreedy<Othello>(heuristic, 1.0), c));

        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, 50);
        series.run();
    }

    static void myDR()
    {
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playerNames = new ArrayList<String>();

        // NTS-RS
        players.add(new EpsilonGreedy<Othello>( OthelloVF.NTS_RS, 0.1));
        playerNames.add("NTS-RS");

        // WPC-SYM
//        players.add(new EpsilonGreedy<Othello>(WPC_SYM, 0.1));
//        playerNames.add("WPC-SYM");

//        // NTS-RND
//        players.add(new EpsilonGreedy<Othello>(NTS_RND, 0.1));
//        playerNames.add("NTS-RND");

//        // NTS-LOG
        players.add(new EpsilonGreedy<Othello>(OthelloVF.NTS_LOG, 0.1));
        playerNames.add("NTS-LOG");

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), 10000, players, playerNames);
        roundRobin.run();
        System.out.println();
        System.out.println(roundRobin.toLatexTable());
    }

    public static void main(String[] args)
    {
        myDR();
    }
}
