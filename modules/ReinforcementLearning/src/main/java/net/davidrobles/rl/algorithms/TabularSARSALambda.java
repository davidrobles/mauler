package net.davidrobles.rl.algorithms;

import net.davidrobles.rl.QPair;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularQFunction;
import net.davidrobles.rl.Learner;
import net.davidrobles.rl.RLEnv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabularSARSALambda<S, A> implements Learner
{
    private RLEnv<S, A> env;
    private RLPolicy<S, A> policy;
    private double alpha;                       // learning rate
    private double gamma;                       // discount factor
    private int numEpisodes;
    private double lambda;
    private int currentEpisode = 0;
    private TabularQFunction<S, A> table = new TabularQFunction<S, A>();;
    private A action;
    private Map<QPair<S, A>, Double> traces = new HashMap<QPair<S, A>, Double>();
    private List<QFunctionObserver<S, A>> qFunctionObservers = new ArrayList<QFunctionObserver<S, A>>();

    public TabularSARSALambda(RLEnv<S, A> env, RLPolicy<S, A> policy, double alpha,
                              double gamma, double lambda, int numEpisodes)
    {
        this.env = env;
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
        this.lambda = lambda;
        this.numEpisodes = numEpisodes;
    }

    @Override
    public void learn()
    {
        for ( ; currentEpisode < numEpisodes; currentEpisode++)
            episode();
    }

    public void step()
    {
        S state = env.getCurrentState();
        double reward = env.performAction(action);
        S nextState = env.getCurrentState();
        A nextAction = null;

        if (env.getPossibleActions(env.getCurrentState()).isEmpty()) {
//            System.out.println("here");
    //                    nextStateNextActionValue = 0;
        } else {
            nextAction = policy.getAction(env, table);
    //                    nextStateNextActionValue = qFunction.getValue(nextState, nextAction);
        }

        double tdError = reward + gamma * table.getValue(nextState, nextAction)
                - table.getValue(state, action);

        QPair<S, A> newQPair = new QPair<S, A>(state, action);

        if (traces.containsKey(newQPair)) {
            traces.put(newQPair, traces.get(newQPair) + 1);
        } else {
            traces.put(newQPair, 0.0);
        }

        for (QPair<S, A> qPair : traces.keySet()) {
            table.setValue(qPair, table.getValue(qPair) + alpha * tdError * traces.get(qPair));
            traces.put(qPair, gamma * lambda * traces.get(qPair));
        }

        action = nextAction;
    }

    public void episode()
    {
        System.out.println("Episode " + currentEpisode);
        env.reset();
        action = policy.getAction(env, table);

        while (!env.getPossibleActions(env.getCurrentState()).isEmpty())
        {
            step();
            notifyValueFunctionUpdate();
        }
    }

    public void addQFunctionObserver(QFunctionObserver<S, A> observer)
    {
        qFunctionObservers.add(observer);
    }

    public void notifyValueFunctionUpdate()
    {
        for (QFunctionObserver<S, A> observer : qFunctionObservers)
            observer.qFunctionUpdated(table);
    }
}
