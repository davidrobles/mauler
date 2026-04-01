package net.davidrobles.rl.agents;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.davidrobles.rl.ObservableQAgent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.TrainableQFunction;

/**
 * Off-policy tabular Q-Learning (Watkins, 1989).
 *
 * <p>The update target is the greedy (max) action value over the next state, making this an
 * off-policy algorithm: the behavior policy used for exploration can differ from the implicit
 * greedy target policy.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class QLearning<S, A> implements ObservableQAgent<S, A> {
    private final Policy<S, A> policy;
    private final double gamma;
    private final TrainableQFunction<S, A> table;
    private final Set<QFunctionObserver<S, A>> qFunctionObservers = new LinkedHashSet<>();

    /**
     * @param table the Q-function to update (shared with the behavior policy); owns the learning
     *     rate
     * @param policy the behavior policy used for action selection
     * @param gamma discount factor
     */
    public QLearning(TrainableQFunction<S, A> table, Policy<S, A> policy, double gamma) {
        if (gamma < 0 || gamma > 1) throw new IllegalArgumentException("gamma must be in [0, 1]");
        this.table = Objects.requireNonNull(table, "table must not be null");
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
        this.gamma = gamma;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        return policy.selectAction(state, actions);
    }

    @Override
    public void update(S state, A action, StepResult<S> result, List<A> nextActions) {
        double maxNextQ = 0.0;

        if (!result.done() && !nextActions.isEmpty()) {
            maxNextQ = Double.NEGATIVE_INFINITY;
            for (A nextAction : nextActions) {
                double v = table.getValue(result.nextState(), nextAction);
                if (v > maxNextQ) maxNextQ = v;
            }
        }

        table.update(state, action, result.reward() + gamma * maxNextQ);
        notifyQFunctionUpdate();
    }

    @Override
    public void addQFunctionObserver(QFunctionObserver<S, A> observer) {
        qFunctionObservers.add(observer);
    }

    private void notifyQFunctionUpdate() {
        for (QFunctionObserver<S, A> observer : qFunctionObservers)
            observer.qFunctionUpdated(table);
    }
}
