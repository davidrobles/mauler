package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.mauler.players.EpsilonGreedy;
import net.davidrobles.mauler.players.mcts.MCTS;
import net.davidrobles.mauler.players.mcts.UCT;
import net.davidrobles.mauler.players.mcts.enh.UCTPrior;
import net.davidrobles.mauler.players.mcts.tree.UCB1;

import java.util.ArrayList;
import java.util.List;

public class SimsPerTime
{
    /**
     * Counts the number of simulations per second performed by UCT.
     */
    public static void uctNumSims()
    {
        Othello othello = new Othello();
        double c = 0.5;
        UCT<Othello> player = new UCT<Othello>(c);
        double total = 0;
        double runs = 50;
        int timeout = 1000;

        for (int i = 0; i < runs; i++)
        {
            System.out.println("run: " + i);
            othello.reset();
            int sims = player.move(othello, timeout);
            total += sims;
        }

        System.out.println("Sims count: " + (total / runs));
    }

    public static void MCTSWithWPC()
    {
        Othello othello = new Othello();
        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        double c = 0.5;
        double epsilon = 0.0;
        EpsilonGreedy<Othello> epsilonGreedy = new EpsilonGreedy<Othello>(wpc, epsilon);
        MCTS<Othello> player = new MCTS<Othello>(new UCB1<Othello>(c), epsilonGreedy);
        double total = 0;
        double runs = 50;
        int timeout = 1000;

        for (int i = 0; i < runs; i++)
        {
            System.out.println("run: " + i);
            othello.reset();
            int sims = player.move(othello, timeout);
            total += sims;
        }

        System.out.println("Sims count: " + (total / runs));
    }

    /**
     * Counts the number of simulations performed by MCTS and MCTSPrior.
     */
    static void MCTSvsMCTSPriorNum()
    {
        int nRuns = 100;
        double c = 0.5;
        int initQVisits = 100;
        int timeout = 1000;

        Othello othello = new Othello();
        WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();

        players.add(new UCTPrior<Othello>(c, wpc, initQVisits));
        players.add(new UCT<Othello>(c));

        for (Player<Othello> player : players)
        {
            System.out.println("Player: " + player);
            int total = 0;

            for (int run = 0; run < nRuns; run++)
            {
                if (run % 5 == 0)
                    System.out.println("\tRun: " + run);

                othello.reset();
                total += player.move(othello, timeout);
            }

            System.out.println("Sims count: " + (total / nRuns));
        }
    }

    public static void main(String[] args)
    {
        uctNumSims();
    }
}
