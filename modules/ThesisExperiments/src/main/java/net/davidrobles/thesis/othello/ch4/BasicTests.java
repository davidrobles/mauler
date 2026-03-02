package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.Match;
import net.davidrobles.mauler.core.RoundRobin;
import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.ef.ntuples.NTUtil;
import net.davidrobles.mauler.othello.ef.ntuples.NTupleSystem;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.mauler.players.mc.MonteCarlo;
import net.davidrobles.mauler.players.mcts.UCT;
import net.davidrobles.mauler.players.minimax.AlphaBeta;

import java.util.ArrayList;
import java.util.List;

public class BasicTests
{
    static void testMatch()
    {
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new RandPlayer<Othello>());
        players.add(new RandPlayer<Othello>());
        Match<Othello> match = new Match<Othello>(new Othello(), players, 0);
        match.call();
        System.out.println(match);
    }

    static void testSeriesWithRandomPlayers()
    {
        int nGames = 50000;

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new RandPlayer<Othello>());
        players.add(new RandPlayer<Othello>());
        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players);
        series.run();
    }

    static void testSeriesRandomVsMCWithSims()
    {
        int nGames = 100;
        int nSims = 100;

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new RandPlayer<Othello>());
        players.add(new MonteCarlo<Othello>(nSims));
        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players);
        series.run();
    }

    static void testSeriesRandomVsMCWithTime()
    {
        int nGames = 100;
        int timeout = 20;

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new RandPlayer<Othello>());
        players.add(new MonteCarlo<Othello>());
        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
        series.run();
    }

//    static void testSeriesRandomVsAlphaBetaWithMaxDepth()
//    {
//        int nGames = 10;
//        int maxDepth = 7;
//        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
//
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//        players.add(new RandPlayer<Othello>());
//        players.add(new AlphaBeta<Othello>(wpc, maxDepth));
//
//        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players);
//        series.run();
//    }

    static void testSeriesRandomVsAlphaBetaWithMaxDepth()
    {
        int nGames = 10;
        int maxDepth = 6;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        NTupleSystem nts = NTUtil.load("logistello-150000-0.996");

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new RandPlayer<Othello>());
        players.add(new AlphaBeta<Othello>(nts, maxDepth));

        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players);
        series.run();
    }

    static void testSeriesRandomVsAlphaBetaWithTime()
    {
        int nGames = 10;
        int timeout = 500;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        NTupleSystem nts = NTUtil.load("logistello-150000-0.996");

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new RandPlayer<Othello>());
        players.add(new AlphaBeta<Othello>(nts));

        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
        series.run();
    }

    static void testSeriesMCVsMCTSWithSims()
    {
        int nGames = 100;
        int nSims = 200;
        double c = 0.5;

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new MonteCarlo<Othello>(nSims));
        players.add(new UCT<Othello>(c, nSims));
        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players);
        series.run();
    }

    static void testSeriesMCVsMCTSWithTime()
    {
        int nGames = 100;
        int timeout = 20;
        double c = 0.5;
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new MonteCarlo<Othello>());
        players.add(new UCT<Othello>(c));
        Series<Othello> series = new Series<Othello>(new Othello(), nGames, players, timeout);
        series.run();
    }

    static void testRoundRobinWithSims()
    {
        int nGames = 100;
        int nSims = 100;
        double c = 0.5;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        NTupleSystem nts = NTUtil.load("logistello-150000-0.996");

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new RandPlayer<Othello>());
        players.add(new MonteCarlo<Othello>(nSims));
        players.add(new UCT<Othello>(c, nSims));
        players.add(new AlphaBeta<Othello>(nts, 6));

        List<String> playerNames = new ArrayList<String>();
        playerNames.add("Random");
        playerNames.add("MonteCarlo");
        playerNames.add("UCT");
        playerNames.add("Alpha-Beta");

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames);
        roundRobin.run();
    }

    static void testRoundRobinWithTime()
    {
        int nGames = 20;
        int timeout = 100;
        double c = 0.5;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playerNames = new ArrayList<String>();

        playerNames.add("Random");
        players.add(new RandPlayer<Othello>());

        playerNames.add("MonteCarlo");
        players.add(new MonteCarlo<Othello>());

        playerNames.add("UCT");
        players.add(new UCT<Othello>(c));

        playerNames.add("Alpha-Beta");
        players.add(new AlphaBeta<Othello>(wpc, 6));

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames, timeout);
        roundRobin.run();
    }

    public static void main(String[] args)
    {
        testSeriesRandomVsAlphaBetaWithMaxDepth();
    }
}
