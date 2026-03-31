package net.davidrobles.rl.algorithms;

import java.util.ArrayList;
import java.util.List;
import net.davidrobles.rl.Agent;
import net.davidrobles.rl.QPair;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularQFunction;

public class TabularSARSA<S, A> implements Agent<S, A> {
    private RLPolicy<S, A> policy;
    private double alpha;
    private double gamma;
    private TabularQFunction<S, A> table = new TabularQFunction<S, A>();
    // Pre-selected next action so that selectAction and update share the same (S, A, R, S', A').
    private A nextAction = null;
    private List<QFunctionObserver<S, A>> qFunctionObservers =
            new ArrayList<QFunctionObserver<S, A>>();

    public TabularSARSA(RLPolicy<S, A> policy, double alpha, double gamma) {
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        // If update() pre-selected the next action, use it (SARSA on-policy coupling).
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

        double newValue =
                table.getValue(state, action)
                        + alpha * (result.reward + gamma * nextQ - table.getValue(state, action));
        table.setValue(new QPair<S, A>(state, action), newValue);
        notifyValueFunctionUpdate();
    }

    public void notifyValueFunctionUpdate() {
        for (QFunctionObserver<S, A> observer : qFunctionObservers)
            observer.qFunctionUpdated(table);
    }

    public void addQFunctionObserver(QFunctionObserver<S, A> observer) {
        qFunctionObservers.add(observer);
    }
}
