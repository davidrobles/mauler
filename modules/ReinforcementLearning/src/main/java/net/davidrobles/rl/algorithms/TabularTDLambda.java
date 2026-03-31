package net.davidrobles.rl.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.davidrobles.rl.Environment;
import net.davidrobles.rl.Learner;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;

public class TabularTDLambda<S, A> implements Learner {
    private Environment<S, A> env;
    private RLPolicy<S, A> policy;
    private double alpha;
    private double gamma;
    private double lambda;
    private int numEpisodes;
    private int currentEpisode = 0;
    private TabularVFunction<S> table;
    private Map<S, Double> traces = new HashMap<S, Double>();
    private List<VFunctionObserver<S>> valueFuncObservers = new ArrayList<VFunctionObserver<S>>();

    public TabularTDLambda(
            Environment<S, A> env,
            RLPolicy<S, A> policy,
            double alpha,
            double gamma,
            double lambda,
            int numEpisodes) {
        this.env = env;
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
        this.lambda = lambda;
        this.numEpisodes = numEpisodes;
        this.table = new TabularVFunction<S>();
    }

    public void episode() {
        System.out.println("Episode " + currentEpisode);
        env.reset();

        while (!env.getActions(env.getCurrentState()).isEmpty()) {
            step();
            notifyValueFunctionUpdate();
        }
    }

    public void step() {
        S currentState = env.getCurrentState();
        A action = policy.getAction(env, table);
        StepResult<S> result = env.step(action);
        double tdError =
                result.reward
                        + gamma * table.getValue(result.nextState)
                        - table.getValue(currentState);

        if (traces.containsKey(currentState))
            traces.put(currentState, traces.get(currentState) + 1);
        else traces.put(currentState, 0.0);

        for (S state : traces.keySet()) {
            table.setValue(state, table.getValue(state) + alpha * tdError * traces.get(state));
            traces.put(state, gamma * lambda * traces.get(state));
        }

        notifyValueFunctionUpdate();
    }

    public void notifyValueFunctionUpdate() {
        for (VFunctionObserver<S> observer : valueFuncObservers)
            observer.valueFunctionChanged(table);
    }

    @Override
    public void learn() {
        for (; currentEpisode < numEpisodes; currentEpisode++) episode();
    }

    public void addVFunctionObserver(VFunctionObserver<S> observer) {
        valueFuncObservers.add(observer);
    }
}
