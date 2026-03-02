package net.davidrobles.mauler.othello.ef;

import net.davidrobles.mauler.players.Evaluator;

import java.util.Iterator;
import java.util.TreeSet;

public class EvaluatorFitness<T extends Evaluator> implements Comparable<EvaluatorFitness<T>>
{
    private double fitness;
    private T evalFunc;

    public EvaluatorFitness(T evalFunc, double fitness)
    {
        this.evalFunc = evalFunc;
        this.fitness = fitness;
    }

    public T getEvaluator()
    {
        return evalFunc;
    }

    public double getFitness()
    {
        return fitness;
    }

    public static <T extends Evaluator> void printTop(TreeSet<EvaluatorFitness<T>> set, int howMany)
    {
        Iterator<EvaluatorFitness<T>> iter = set.iterator();
        System.out.println("Top evaluation functions so far: ");

        for (int i = 0; i < howMany && iter.hasNext(); i++)
            System.out.println(iter.next());
    }


    ////////////////
    // Comparable //
    ////////////////

    @Override
    public int compareTo(EvaluatorFitness<T> other)
    {
        if (getFitness() > other.getFitness())
            return -1;

        if (getFitness() < other.getFitness())
            return 1;

        return 0;
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return "Fitness: " + fitness;
    }
}
