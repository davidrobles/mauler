package net.davidrobles.rl.algorithms;

import net.davidrobles.rl.Learner;
import net.davidrobles.rl.RLEnv;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularVFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabularTDLambda<S, A> implements Learner
{
    private RLEnv<S, A> env;
    private RLPolicy<S, A> policy;
    private double alpha;
    private double gamma;
    private double lambda;
    private int numEpisodes;
    private int currentEpisode = 0;
    private TabularVFunction<S> table;
    private Map<S, Double> traces = new HashMap<S, Double>();
    private List<VFunctionObserver<S>> valueFuncObservers = new ArrayList<VFunctionObserver<S>>();

    public TabularTDLambda(RLEnv<S, A> env, RLPolicy<S, A> policy,
                    double alpha, double gamma, double lambda, int numEpisodes)
    {
        this.env = env;
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
        this.lambda = lambda;
        this.numEpisodes = numEpisodes;
        this.table = new TabularVFunction<S>();
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

    public void step()
    {
        A action = policy.getAction(env, table);
        S currentState = env.getCurrentState();
        double reward = env.performAction(action);
        S nextState = env.getCurrentState();
        double tdError = reward + gamma * table.getValue(nextState) - table.getValue(currentState);

        if (traces.containsKey(currentState))
            traces.put(currentState, traces.get(currentState) + 1);
        else
            traces.put(currentState, 0.0);

        for (S state : traces.keySet())
        {
            table.setValue(state, table.getValue(state) + alpha * tdError * traces.get(state));
            traces.put(state, gamma * lambda * traces.get(state));
        }

        notifyValueFunctionUpdate();

    }

    public void notifyValueFunctionUpdate()
    {
        for (VFunctionObserver<S> observer : valueFuncObservers)
            observer.valueFunctionChanged(table);
    }

    @Override
    public void learn()
    {
        for ( ; currentEpisode < numEpisodes; currentEpisode++)
            episode();
    }

    public void addVFunctionObserver(VFunctionObserver<S> observer)
    {
        valueFuncObservers.add(observer);
    }
}
