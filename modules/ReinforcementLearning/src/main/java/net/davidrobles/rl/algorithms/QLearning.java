package net.davidrobles.rl.algorithms;

import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularQFunction;
import net.davidrobles.rl.Learner;
import net.davidrobles.rl.QPair;
import net.davidrobles.rl.RLEnv;

import java.util.ArrayList;
import java.util.List;

public class QLearning<S, A> implements Learner
{
    private RLEnv<S, A> env;
    private RLPolicy<S, A> policy;
    private double alpha;                       // learning rate
    private double gamma;                       // discount factor
    private int numEpisodes;
    private int currentEpisode = 0;
    private TabularQFunction<S, A> table = new TabularQFunction<S, A>();;
    private List<QFunctionObserver<S, A>> qFunctionObservers = new ArrayList<QFunctionObserver<S, A>>();

    public QLearning(RLEnv<S, A> env, RLPolicy<S, A> policy, double alpha,
                     double gamma, int numEpisodes)
    {
        this.env = env;
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
        this.numEpisodes = numEpisodes;
    }

    public void episode()
    {
        System.out.println("Episode " + currentEpisode);
        env.reset();

        while (!env.getPossibleActions(env.getCurrentState()).isEmpty())
        {
            step();
            notifyValueFunctionUpdate();
        }
    }

    public void notifyQFunctionUpdate()
    {
        for (QFunctionObserver<S, A> observer : qFunctionObservers)
            observer.qFunctionUpdated(table);
    }

    public void addQFunctionObserver(QFunctionObserver<S, A> observer)
    {
        this.qFunctionObservers.add(observer);
    }

    private void step()
    {
        S state = env.getCurrentState();
        A action = policy.getAction(env, table);
        double reward = env.performAction(action);
        S nextState = env.getCurrentState();
        double nextStateNextActionValue = Double.NEGATIVE_INFINITY;

        if (env.getPossibleActions(env.getCurrentState()).isEmpty())
        {
            nextStateNextActionValue = 0;
        }
        else
        {
            for (A nextAction : env.getPossibleActions(nextState))
            {
                double tempValue = table.getValue(nextState, nextAction);

                if (tempValue > nextStateNextActionValue)
                    nextStateNextActionValue = tempValue;
            }
        }

        double updateValue = reward + (gamma * nextStateNextActionValue) - table.getValue(state, action);
        double newValue = table.getValue(state, action) + (alpha * updateValue);
        table.setValue(new QPair<S, A>(state, action), newValue);
        notifyQFunctionUpdate();
    }

    public void notifyValueFunctionUpdate()
    {
        for (QFunctionObserver<S, A> observer : qFunctionObservers)
            observer.qFunctionUpdated(table);
    }

    @Override
    public void learn()
    {
        for ( ; currentEpisode < numEpisodes; currentEpisode++)
            episode();
    }
}
