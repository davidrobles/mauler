package net.davidrobles.mauler.othello.ef;

import net.davidrobles.mauler.players.EvalFunc;

import java.util.Iterator;
import java.util.TreeSet;

public class EvalFuncFitness<T extends EvalFunc> implements Comparable<EvalFuncFitness<T>>
{
    private double fitness;
    private T evalFunc;

    public EvalFuncFitness(T evalFunc, double fitness)
    {
        this.evalFunc = evalFunc;
        this.fitness = fitness;
    }

    public T getEvalFunc()
    {
        return evalFunc;
    }

    public double getFitness()
    {
        return fitness;
    }

    public static <T extends EvalFunc> void printTop(TreeSet<EvalFuncFitness<T>> set, int howMany)
    {
        Iterator<EvalFuncFitness<T>> iter = set.iterator();
        System.out.println("Top evaluation functions so far: ");

        for (int i = 0; i < howMany && iter.hasNext(); i++)
            System.out.println(iter.next());
    }


    ////////////////
    // Comparable //
    ////////////////

    @Override
    public int compareTo(EvalFuncFitness<T> other)
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
