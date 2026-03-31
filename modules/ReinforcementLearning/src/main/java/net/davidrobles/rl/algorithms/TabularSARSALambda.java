package net.davidrobles.rl.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.davidrobles.rl.Agent;
import net.davidrobles.rl.QPair;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularQFunction;

public class TabularSARSALambda<S, A> implements Agent<S, A> {
    private RLPolicy<S, A> policy;
    private double alpha;
    private double gamma;
    private double lambda;
    private TabularQFunction<S, A> table = new TabularQFunction<S, A>();
    // Pre-selected next action for SARSA on-policy coupling.
    private A nextAction = null;
    private Map<QPair<S, A>, Double> traces = new HashMap<QPair<S, A>, Double>();
    private List<QFunctionObserver<S, A>> qFunctionObservers =
            new ArrayList<QFunctionObserver<S, A>>();

    public TabularSARSALambda(RLPolicy<S, A> policy, double alpha, double gamma, double lambda) {
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
        this.lambda = lambda;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        if (nextAction != null) {
            A a = nextAction;
            nextAction = null;
            return a;
        }
        return policy.getAction(state, actions, table);
    }

    @Override
    public void update(S state, A action, StepResult<S> result, List<A> nextActions) {
        double nextQ;

        if (result.done || nextActions.isEmpty()) {
            nextQ = 0.0;
            nextAction = null;
        } else {
            nextAction = policy.getAction(result.nextState, nextActions, table);
            nextQ = table.getValue(result.nextState, nextAction);
        }

        double tdError = result.reward + gamma * nextQ - table.getValue(state, action);

        // Accumulating trace: e(s,a) += 1
        QPair<S, A> sa = new QPair<S, A>(state, action);
        traces.merge(sa, 1.0, Double::sum);

        for (Map.Entry<QPair<S, A>, Double> entry : traces.entrySet()) {
            table.setValue(
                    entry.getKey(),
                    table.getValue(entry.getKey()) + alpha * tdError * entry.getValue());
            entry.setValue(gamma * lambda * entry.getValue());
        }

        if (result.done) traces.clear();
        notifyValueFunctionUpdate();
    }

    public void addQFunctionObserver(QFunctionObserver<S, A> observer) {
        qFunctionObservers.add(observer);
    }

    public void notifyValueFunctionUpdate() {
        for (QFunctionObserver<S, A> observer : qFunctionObservers)
            observer.qFunctionUpdated(table);
    }
}
