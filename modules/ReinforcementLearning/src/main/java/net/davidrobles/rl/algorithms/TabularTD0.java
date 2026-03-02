package net.davidrobles.rl.algorithms;

import net.davidrobles.rl.Learner;
import net.davidrobles.rl.RLEnv;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularVFunction;

import java.util.ArrayList;
import java.util.List;

public class TabularTD0<S, A> implements Learner
{
    /**
     * A RL environment.
     */
    private RLEnv<S, A> env;

    /**
     * Behaviour policy.
     */
    private RLPolicy<S, A> policy;

    /**
     * Learning rate.
     */
    private double alpha;

    /**
     * Discount factor.
     */
    private double gamma;

    /**
     * Number of episodes.
     */
    private int numEp;

    /**
     * Current episode.
     */
    private int curEp = 0;

    /**
     * State value lookup table.
     */
    private TabularVFunction<S> table = new TabularVFunction<S>();


    private List<VFunctionObserver<S>> valueFuncObservers = new ArrayList<VFunctionObserver<S>>();

    public TabularTD0(RLEnv<S, A> env, RLPolicy<S, A> policy, double alpha, double gamma, int numEp)
    {
        this.env = env;
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
        this.numEp = numEp;
    }

    public void episode()
    {
        System.out.println("Episode " + curEp);
        env.reset();

        while (!env.getPossibleActions(env.getCurrentState()).isEmpty())
        {
            step();
            notifyValueFunctionUpdate();
        }
    }

    public void step()
    {
        A action = policy.getAction(env, table);
        S currentState = env.getCurrentState();
        double reward = env.performAction(action);
        S nextState = env.getCurrentState();
        double newValue = table.getValue(currentState)
                        + (alpha * (reward + (gamma * table.getValue(nextState))
                        - table.getValue(currentState)));
        table.setValue(currentState, newValue);
    }

    public void notifyValueFunctionUpdate()
    {
        for (VFunctionObserver<S> observer : valueFuncObservers)
            observer.valueFunctionChanged(table);
    }

    public void addVFunctionObserver(VFunctionObserver<S> observer)
    {
        valueFuncObservers.add(observer);
    }

    @Override
    public void learn()
    {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for ( ; curEp < numEp; curEp++)
            episode();
    }
}
