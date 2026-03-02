package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.TD0;
import net.davidrobles.mauler.othello.ef.EvalFuncFitness;
import net.davidrobles.mauler.players.EpsilonGreedy;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCType;

import java.util.*;

public class WPCLearning
{
    // this version of the algorithm is run against a particular player, in this
    // case the random player
    static void learnWPCWithTD0()
    {
        // td0 settings
        WPCType type = WPCType.SYM;
        int episodes = 50000;
        double learningRate = 0.001;
        double discountFactor = 1.0;
        double epsilon = 0.1;
        int interval = 50;
        int games = 10000;

        // to save the best WPC's
        TreeSet<EvalFuncFitness<WPC>> set = new TreeSet<EvalFuncFitness<WPC>>();

        WPC wpc = new WPC(type);
        TD0<Othello> td0 = new TD0<Othello>(new Othello(), wpc, episodes, learningRate, discountFactor, epsilon);

        // players
        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(new EpsilonGreedy<Othello>(wpc, 0.0));
        players.add(new RandPlayer<Othello>());

        for (int episode = 0; episode <= episode; episode++)
        {
            td0.iteration();

            if (episode % interval == 0)
            {
                Series<Othello> series = new Series<Othello>(new Othello(), games, players);
                series.setVerbose(false);
                series.run();
                System.out.println("Episode:  " + episode);
                System.out.println("Win rate: " + series.getWinsAvg(0));
                set.add(new EvalFuncFitness<WPC>(wpc.copy(), series.getWinsAvg(0)));
                EvalFuncFitness.printTop(set, 5);
                System.out.println("------------------------");
            }
        }
    }

    // this version of the algorithm is run against a particular player, in this
    // case the random player
    static void learnWPCWithTD0PlayingAgainstSelf()
    {
        // td0 settings
        WPCType type = WPCType.SYM;
        int episodes = 50000;
        double learningRate = 0.001;
        double discountFactor = 1.0;
        double epsilon = 0.1;
        int interval = 100;
        int games = 10000;

        WPC wpc = new WPC(type);
        WPC prevWPC = wpc.copy();
        TD0<Othello> td0 = new TD0<Othello>(new Othello(), wpc, episodes, learningRate, discountFactor, epsilon);

        for (int episode = 1; episode <= episode; episode++)
        {
            td0.iteration();

            if (episode % interval == 0)
            {
                // players
                List<Player<Othello>> players = new ArrayList<Player<Othello>>();
                players.add(new EpsilonGreedy<Othello>(prevWPC, 0.1));
                players.add(new EpsilonGreedy<Othello>(wpc, 0.1));
                Series<Othello> series = new Series<Othello>(new Othello(), games, players);
                series.setVerbose(false);
                series.run();
                System.out.println("Episode:  " + episode);
                System.out.println("Win rate: " + (series.getWinsAvg(1) - series.getWinsAvg(0)));
                System.out.println(wpc.getFormattedWeights());
                System.out.println("------------------------");
                prevWPC = wpc.copy();
            }
        }
    }

    public static void main(String[] args)
    {
        learnWPCWithTD0();
    }
}
