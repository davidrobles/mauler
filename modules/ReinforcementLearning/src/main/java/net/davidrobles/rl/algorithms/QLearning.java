package net.davidrobles.rl.algorithms;

import java.util.ArrayList;
import java.util.List;
import net.davidrobles.rl.Agent;
import net.davidrobles.rl.QPair;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularQFunction;

public class QLearning<S, A> implements Agent<S, A> {
    private RLPolicy<S, A> policy;
    private double alpha;
    private double gamma;
    private TabularQFunction<S, A> table = new TabularQFunction<S, A>();
    private List<QFunctionObserver<S, A>> qFunctionObservers =
            new ArrayList<QFunctionObserver<S, A>>();

    public QLearning(RLPolicy<S, A> policy, double alpha, double gamma) {
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        return policy.getAction(state, actions, table);
    }

    @Override
    public void update(S state, A action, StepResult<S> result, List<A> nextActions) {
        double maxNextQ = 0.0;

        if (!nextActions.isEmpty()) {
            maxNextQ = Double.NEGATIVE_INFINITY;
            for (A nextAction : nextActions) {
                double v = table.getValue(result.nextState, nextAction);
                if (v > maxNextQ) maxNextQ = v;
            }
        }

        double newValue =
                table.getValue(state, action)
                        + alpha
                                * (result.reward
                                        + gamma * maxNextQ
                                        - table.getValue(state, action));
        table.setValue(new QPair<S, A>(state, action), newValue);
        notifyQFunctionUpdate();
    }

    public void notifyQFunctionUpdate() {
        for (QFunctionObserver<S, A> observer : qFunctionObservers)
            observer.qFunctionUpdated(table);
    }

    public void addQFunctionObserver(QFunctionObserver<S, A> observer) {
        qFunctionObservers.add(observer);
    }
}
