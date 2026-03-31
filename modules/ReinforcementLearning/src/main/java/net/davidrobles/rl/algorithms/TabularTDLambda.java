package net.davidrobles.rl.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.davidrobles.rl.Agent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;

public class TabularTDLambda<S, A> implements Agent<S, A> {
    private RLPolicy<S, A> policy;
    private double alpha;
    private double gamma;
    private double lambda;
    private TabularVFunction<S> table = new TabularVFunction<S>();
    private Map<S, Double> traces = new HashMap<S, Double>();
    private List<VFunctionObserver<S>> valueFuncObservers = new ArrayList<VFunctionObserver<S>>();

    public TabularTDLambda(RLPolicy<S, A> policy, double alpha, double gamma, double lambda) {
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
        this.lambda = lambda;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        return policy.getAction(state, actions, table);
    }

    @Override
    public void update(S state, A action, StepResult<S> result, List<A> nextActions) {
        double tdError =
                result.reward + gamma * table.getValue(result.nextState) - table.getValue(state);

        // Accumulating trace: e(s) += 1
        traces.merge(state, 1.0, Double::sum);

        for (Map.Entry<S, Double> entry : traces.entrySet()) {
            table.setValue(
                    entry.getKey(),
                    table.getValue(entry.getKey()) + alpha * tdError * entry.getValue());
            entry.setValue(gamma * lambda * entry.getValue());
        }

        if (result.done) traces.clear();
        notifyValueFunctionUpdate();
    }

    public void notifyValueFunctionUpdate() {
        for (VFunctionObserver<S> observer : valueFuncObservers)
            observer.valueFunctionChanged(table);
    }

    public void addVFunctionObserver(VFunctionObserver<S> observer) {
        valueFuncObservers.add(observer);
    }
}
