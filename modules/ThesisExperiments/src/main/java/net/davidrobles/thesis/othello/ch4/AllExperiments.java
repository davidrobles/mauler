package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.RoundRobin;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.mauler.players.GreedyPlayer;
import net.davidrobles.mauler.players.mc.MonteCarlo;
import net.davidrobles.mauler.players.mcts.UCT;
import net.davidrobles.mauler.players.mcts.enh.UCTPrior;

import java.util.ArrayList;
import java.util.List;

public class AllExperiments
{
    static void allExperiments()
    {
        int nGames = 20;
        int timeout = 100;
        double c = 0.5;
        int nInit = 100;

        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        List<String> playerNames = new ArrayList<String>();

        // Monte Carlo
        players.add(new MonteCarlo<Othello>());
        playerNames.add("MC");

        // UCT
        players.add(new UCT<Othello>(c));
        playerNames.add("UCT");

        // UCT + Non Random Default Policy
        players.add(new UCT<Othello>(new GreedyPlayer<Othello>(wpc), c));
        playerNames.add("UCT+NR");

        // UCT + Prior Knowledge
        players.add(new UCTPrior<Othello>(c, wpc, nInit));
        playerNames.add("UCT+PK");

        // UCT + Non Random Default Policy + Prior Knowledge
        GreedyPlayer<Othello> greedy = new GreedyPlayer<Othello>(wpc);
        players.add(new UCTPrior<Othello>(greedy, c, wpc, nInit));
        playerNames.add("UCT+NR+PK");

        RoundRobin<Othello> roundRobin = new RoundRobin<Othello>(new Othello(), nGames, players, playerNames, timeout);
        roundRobin.run();
        System.out.println(roundRobin.toLatexTable());
    }

    public static void main(String[] args)
    {
        allExperiments();
    }
}
