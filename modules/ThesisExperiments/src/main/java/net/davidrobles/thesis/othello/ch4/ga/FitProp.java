package net.davidrobles.thesis.othello.ch4.ga;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.players.EpsilonGreedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FitProp
{
    private static Random rnd = new Random();

    public static double genRandFit(double[] fitnesses)
    {
        return rnd.nextDouble() * fitnesses[fitnesses.length - 1];
    }

    public static int fitnessProportionateSelection(double value, double[] fitnesses)
    {
        if (value < 0 || value > fitnesses[fitnesses.length - 1])
            throw new IllegalArgumentException("Number out of range.");

        for (int i = 1; i < fitnesses.length; i++)
            if (fitnesses[i - 1] < value && value <= fitnesses[i])
                return i;

        return 0;
    }

    public static double[] createFitnessesArray(List<ScoredNTS> scoredPopulation)
    {
        double[] per = new double[scoredPopulation.size()];
        per[0] = scoredPopulation.get(0).getFitness();

        for (int i = 1; i < per.length; i++)
            per[i] = per[i - 1] + scoredPopulation.get(i).getFitness();

        return per;
    }

    public static double logOfBase(int base, int num)
    {
        return Math.log(num) / Math.log(base);
    }

    public static void singleEliminationTournament(List<ScoredNTS> population)
    {
        int nGames = 1000;
        double epsilon = 0.1;
        List<ScoredNTS> R = new ArrayList<ScoredNTS>(population);
        Collections.shuffle(R);
        int nRounds = (int) logOfBase(2, population.size());

        for (int i = 0; i < nRounds; i++)
        {
            List<ScoredNTS> Q = new ArrayList<ScoredNTS>(R);
            R.clear();
            int le = Q.size();
//            System.out.println("Round " + le);

            for (int j = 0; j < le; j += 2)
            {
//                System.out.println("\tMatch " + j);
                ScoredNTS nts1 = Q.get(j);
                nts1.setFitness(nts1.getFitness() + 1);
                ScoredNTS nts2 = Q.get(j + 1);
                nts2.setFitness(nts2.getFitness() + 1);

                List<Player<Othello>> players = new ArrayList<Player<Othello>>();
                players.add(new EpsilonGreedy<Othello>(nts1.getNTupleSystem(), epsilon));
                players.add(new EpsilonGreedy<Othello>(nts2.getNTupleSystem(), epsilon));

                Series<Othello> series = new Series<Othello>(new Othello(), nGames, players);
                series.run();

                if (series.getWinsAvg(0) >= series.getWinsAvg(1))
                    R.add(nts1);
                else
                    R.add(nts2);
            }
        }
    }
}
