package net.davidrobles.thesis.othello.ch4.ga;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.othello.TD0;
import net.davidrobles.mauler.othello.ef.ntuples.NTUtil;
import net.davidrobles.mauler.othello.ef.ntuples.NTuple;
import net.davidrobles.mauler.othello.ef.ntuples.NTupleSystem;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;
import net.davidrobles.mauler.players.EpsilonGreedy;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GANTuples
{
    static Random rnd = new Random();
    static int populationSize = 32;
    static int numTuples = 20;
    static int walkLength = 10;
    static int generations = 100;
    static double mutProb = 0.01;
    static double crossProb = 0.80;

    static void GA()
    {
        List<ScoredNTS> population = initPopulation();

        for (int gen = 0; gen < generations; gen++)
        {
            System.out.println("Latest!");
            System.out.println("================");
            System.out.println(" Generation " + gen);
            System.out.println("================\n");

            trainTD0(population);
            calcExternalFitness(population);
            Collections.sort(population);
            printPopulation(population);
            saveBest(population, gen);
            calcInternalFitness(population);
            double[] fitnesses = FitProp.createFitnessesArray(population);
            List<ScoredNTS> newPopulation = new ArrayList<ScoredNTS>();

            for (int j = 0; j < populationSize; j += 2)
            {
                NTupleSystem parent1 = select(population, fitnesses);
                NTupleSystem parent2 = select(population, fitnesses);
                NTupleSystem[] offspring = reproduce(parent1, parent2);
                mutation(offspring[0]);
                mutation(offspring[1]);
                newPopulation.add(new ScoredNTS(offspring[0]));
                newPopulation.add(new ScoredNTS(offspring[1]));
            }

            population = newPopulation;
        }
    }

    static void mutation(NTupleSystem nts)
    {
        for (int i = 0; i < nts.getNTuples().length; i++)
        {
            if (rnd.nextDouble() < mutProb)
            {
                NTuple newNTuple = NTUtil.generateNTuple(walkLength);
                nts.setNTuple(i, newNTuple);
            }
        }
    }

    // the given population should be sorted
    static void saveBest(List<ScoredNTS> population, int generation)
    {
        ScoredNTS bestNTS = population.get(0);
        String filename = String.format("hola%d-%.4f", generation, bestNTS.getFitness());
        NTUtil.save(bestNTS.getNTupleSystem(), filename);
    }

    /**
     * Seed initial population?
     */
    static List<ScoredNTS> initPopulation()
    {
        List<ScoredNTS> population = new ArrayList<ScoredNTS>();
        population.add(new ScoredNTS(getLogistello(walkLength)));

        for (int i = 1; i < populationSize; i++)
        {
            NTupleSystem nts = NTUtil.generateRandomNTupleSystem(numTuples, walkLength);
            population.add(new ScoredNTS(nts));
        }

        return population;
    }

    private static NTupleSystem getLogistello(int size)
    {
        NTupleSystem logistello = NTUtil.load("logistello11-130000-0.822");
        NTuple[] newNTuples = new NTuple[size];

        for (int i = 0; i < size; i++)
        {
            if (i < logistello.getNTuples().length)
                newNTuples[i] = logistello.getNTuples()[i].copy();
            else
                newNTuples[i] = NTUtil.generateNTuple(walkLength);
        }

        return new NTupleSystem(newNTuples);
    }

    private static void trainTD0(List<ScoredNTS> population)
    {
        System.out.print("Training the N-tuple systems using TD(0)... ");

        int nProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nProcessors);
        Collection<TrainNTSTask> tasks = new ArrayList<TrainNTSTask>();

        for (int i = 0; i < population.size(); i++)
        {
            ScoredNTS scoredNTS = population.get(i);
            NTupleSystem nts = scoredNTS.getNTupleSystem();
            tasks.add(new TrainNTSTask(nts));
        }

        try {
            executor.invokeAll(tasks);
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Done!");
    }

    static class TrainNTSTask implements Callable<Void>
    {
        static final int episodes = 2500;
        static final double learningRate = 0.001;
        static final double discountFactor = 1.0;
        static final double epsilon = 0.1;
        private NTupleSystem nts;

        public TrainNTSTask(NTupleSystem nts)
        {
            this.nts = nts;
        }

        @Override
        public Void call() throws Exception
        {
            TD0<Othello> td0 = new TD0<Othello>(new Othello(), nts, episodes, learningRate, discountFactor, epsilon);
            td0.learn();
            System.out.println("Completed");
            return null;
        }
    }

    static WPC wpc = new WPC(WPCUtil.load("dr-sym-6462"));

    private static void calcExternalFitness(List<ScoredNTS> population)
    {
        NTupleSystem logistello = NTUtil.load("logistello11-130000-0.822");
        System.out.print("Calculating External Fitnesses... ");
        int nGames = 1000;

        for (int i = 0; i < population.size(); i++)
        {
            ScoredNTS scoredNTS = population.get(i);
            NTupleSystem nts = scoredNTS.getNTupleSystem();

            // rest
            List<Player<Othello>> players = new ArrayList<Player<Othello>>();
            players.add(new EpsilonGreedy<Othello>(nts, 0.1));
            players.add(new EpsilonGreedy<Othello>(logistello, 0.1));

            Series<Othello> series = new Series<Othello>(new Othello(), nGames, players);
            series.run();

            double winsAvg = series.getWinsAvg(0);
            scoredNTS.setFitness(winsAvg);
        }

        System.out.println("Done!");
    }

    public static NTupleSystem[] reproduce(NTupleSystem a, NTupleSystem b)
    {
        if (rnd.nextDouble() > crossProb)
            return new NTupleSystem[] { a.copy(), b.copy() };

        int length = a.getNTuples().length;
        NTuple[][] nTuples = new NTuple[2][length];
        int c = rnd.nextInt(length);

        for (int i = 0; i < length; i++)
        {
            if (i < c) {
                nTuples[0][i] = a.getNTuples()[i].copy();
                nTuples[1][i] = b.getNTuples()[i].copy();
            }
            else {
                nTuples[0][i] = b.getNTuples()[i].copy();
                nTuples[1][i] = a.getNTuples()[i].copy();
            }
        }

        NTupleSystem[] offspring = { new NTupleSystem(nTuples[0]), new NTupleSystem(nTuples[1]) };

        return offspring;
    }

    static NTupleSystem select(List<ScoredNTS> population, double[] fitnesses)
    {
        double value = FitProp.genRandFit(fitnesses);
        int indIndex = FitProp.fitnessProportionateSelection(value, fitnesses);

        return population.get(indIndex).getNTupleSystem();
    }

    static void calcInternalFitness(List<ScoredNTS> population)
    {
        System.out.print("Calculating Internal Fitnesses... ");
        FitProp.singleEliminationTournament(population);
        System.out.println("Done!");
    }

    static void printTop10(List<ScoredNTS> population)
    {
        System.out.println("Top 10:\n");

        for (int i = 0; i < 10; i++)
            System.out.format("%3d. %.4f \n", i + 1, population.get(i).getFitness());

        System.out.println();
    }

    static void printPopulation(List<ScoredNTS> population)
    {
        System.out.println("Population sorted by fitness:\n");

        for (int i = 0; i < population.size(); i++)
            System.out.format("%3d. %.4f \n", i + 1, population.get(i).getFitness());

        System.out.println();
    }

    public static void main(String[] args)
    {
        GA();
    }
}
