package net.davidrobles.rl.algorithms;

import net.davidrobles.rl.Learner;
import net.davidrobles.rl.QPair;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularQFunction;
import net.davidrobles.rl.RLEnv;

import java.util.ArrayList;
import java.util.List;

public class TabularSARSA<S, A> implements Learner
{
    private RLEnv<S, A> env;
    private RLPolicy<S, A> policy;
    private double alpha;                       // learning rate
    private double gamma;                       // discount factor
    private int numEpisodes;
    private int currentEpisode = 0;
    private TabularQFunction<S, A> table;
    private List<QFunctionObserver<S, A>> qFunctionObservers = new ArrayList<QFunctionObserver<S, A>>();

    public TabularSARSA(RLEnv<S, A> env, RLPolicy<S, A> policy, double alpha,
                      double gamma, int numEpisodes)
    {
        this.env = env;
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
        this.numEpisodes = numEpisodes;
        this.table = new TabularQFunction<S, A>();
    }

    A action;

    public void episode()
    {
        System.out.println("Episode " + currentEpisode);
        env.reset();
        action = policy.getAction(env, table);

        while (!env.getPossibleActions(env.getCurrentState()).isEmpty())
        {
            step();
            notifyValueFunctionUpdate();
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void step()
    {
        S state = env.getCurrentState();
        double reward = env.performAction(action);
        S nextState = env.getCurrentState();
        A nextAction = null;
        double nextStateNextActionValue;

        if (env.getPossibleActions(env.getCurrentState()).isEmpty()) {
            nextStateNextActionValue = 0;
        } else {
            nextAction = policy.getAction(env, table);
            nextStateNextActionValue = table.getValue(nextState, nextAction);
        }

        double updateValue = reward + (gamma * nextStateNextActionValue) - table.getValue(state, action);
        double newValue = table.getValue(state, action) + (alpha * updateValue);
        table.setValue(new QPair<S, A>(state, action), newValue);
        action = nextAction;
        notifyValueFunctionUpdate();
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

    public void addQFunctionObserver(QFunctionObserver<S, A> observer)
    {
        qFunctionObservers.add(observer);
    }
}
