package net.davidrobles.rl.algorithms;

import net.davidrobles.rl.MDP;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularVFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValueIteration<S, A>
{
    private MDP<S, A> mdp;
    private TabularVFunction<S> table = new TabularVFunction<S>();
    private double theta;    // A small positive number used as a termination condition
    private double gamma;    // Discount factor
    private List<VFunctionObserver<S>> observers = new ArrayList<VFunctionObserver<S>>();

    public ValueIteration(MDP<S, A> mdp, double theta, double gamma)
    {
        this.mdp = mdp;
        this.theta = theta;
        this.gamma = gamma;
    }

    public void notifyValueFunctionUpdate()
    {
        for (VFunctionObserver<S> observer : observers)
            observer.valueFunctionChanged(table);
    }

    public void addVFunctionObserver(VFunctionObserver<S> observer)
    {
        observers.add(observer);
    }

    public void learn()
    {
        double delta;

        System.out.println("Value Iteration started...");

        do
        {
            delta = 0;

            for (S state : mdp.getStates())
            {
                double oldStateValue = table.getValue(state);
                double newStateValue = Double.NEGATIVE_INFINITY;

                for (A action : mdp.getActions(state))
                {
                    double tot = 0;
                    Map<S, Double> nextTransitions = mdp.getTransitions(state, action);

                    for (S nextState : nextTransitions.keySet())
                    {
                        double probability = nextTransitions.get(nextState);
                        double reward = mdp.getReward(state, action, nextState);
                        double nextStateValue = table.getValue(nextState);
                        tot += probability * (reward + (gamma * nextStateValue));
                    }

                    if (tot > newStateValue)
                        newStateValue = tot;
                }

                table.setValue(state, newStateValue);
                delta = Math.max(delta, Math.abs(oldStateValue - table.getValue(state)));
                notifyValueFunctionUpdate();
            }

            System.out.printf("%.4f (delta) < %.4f (theta) ?????\n", delta, theta);

        } while (delta > theta);

        System.out.println("DP Value Iteration finished.");
    }
}
