package net.davidrobles.thesis.othello.ch5;

import net.davidrobles.mauler.connect4.Connect4;
import net.davidrobles.mauler.core.RoundRobin;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.players.mcts.UCT;

import java.util.ArrayList;
import java.util.List;

public class MctsExploration {

    public static void c4Exploration() {
        Connect4 c4 = new Connect4();
        int numGames = 100;
        int numMctsSims = 10000;
        List<Player<Connect4>> players = new ArrayList<Player<Connect4>>();
        List<String> playerNames = new ArrayList<String>();
        players.add(new UCT<Connect4>(0.6, numMctsSims));
        playerNames.add("C = 0.6");
        players.add(new UCT<Connect4>(0.8, numMctsSims));
        playerNames.add("C = 0.8");
//        players.add(new UCT<Connect4>(0.9, numMctsSims));
//        playerNames.add("C = 0.9");
        players.add(new UCT<Connect4>(1.0, numMctsSims));
        playerNames.add("C = 1.0");
//        players.add(new UCT<Connect4>(1.1, numMctsSims));
//        playerNames.add("C = 1.1");
        players.add(new UCT<Connect4>(1.2, numMctsSims));
        playerNames.add("C = 1.2");
        players.add(new UCT<Connect4>(1.4, numMctsSims));
        playerNames.add("C = 1.4");
        RoundRobin<Connect4> roundRobin = new RoundRobin<Connect4>(c4, numGames, players, playerNames);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

    public static void mctsRadialTreesExplorationConstants() {
        Othello othello = new Othello();
        int numGames = 100;
        int numMctsSims = 10000;
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playerNames = new ArrayList<String>();
        players.add(new UCT<Othello>(0.2, numMctsSims));
        playerNames.add("C = 0.2");
        players.add(new UCT<Othello>(5.0, numMctsSims));
        playerNames.add("C = 5.0");
        players.add(new UCT<Othello>(10.0, numMctsSims));
        playerNames.add("C = 10.0");
        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(othello, numGames, players, playerNames);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

    public static void main(String[] args) {
        c4Exploration();
//        GamesUtil.playRandomGame(new Connect4());
//        Connect4 c4 = new Connect4();
//        System.out.println(c4);
    }
}
