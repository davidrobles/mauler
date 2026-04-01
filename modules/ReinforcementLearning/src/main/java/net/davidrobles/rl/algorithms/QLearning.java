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
    private final RLPolicy<S, A> policy;
    private final double alpha;
    private final double gamma;
    private final TabularQFunction<S, A> table;
    private final List<QFunctionObserver<S, A>> qFunctionObservers = new ArrayList<>();

    /**
     * @param table the Q-function to update (shared with the behavior policy)
     * @param policy the behavior policy used for action selection
     * @param alpha learning rate
     * @param gamma discount factor
     */
    public QLearning(
            TabularQFunction<S, A> table, RLPolicy<S, A> policy, double alpha, double gamma) {
        this.table = table;
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        return policy.selectAction(state, actions);
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
        table.setValue(new QPair<>(state, action), newValue);
        notifyQFunctionUpdate();
    }

    public void addQFunctionObserver(QFunctionObserver<S, A> observer) {
        qFunctionObservers.add(observer);
    }

    private void notifyQFunctionUpdate() {
        for (QFunctionObserver<S, A> observer : qFunctionObservers)
            observer.qFunctionUpdated(table);
    }
}
