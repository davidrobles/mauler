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
 * On-policy tabular SARSA (Rummery &amp; Niranjan, 1994).
 *
 * <p>The update target uses the action actually taken in the next state (S, A, R, S', A'), making
 * this an on-policy algorithm. The next action is pre-selected during {@link #update} and reused in
 * the following {@link #selectAction} call to maintain the coupling.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class SARSA<S, A> implements ObservableQAgent<S, A> {
    private final Policy<S, A> policy;
    private final double gamma;
    private final TrainableQFunction<S, A> table;
    // Pre-selected next action to maintain the on-policy (S, A, R, S', A') SARSA coupling.
    private A nextAction = null;
    private final Set<QFunctionObserver<S, A>> qFunctionObservers = new LinkedHashSet<>();

    /**
     * @param table the Q-function to update (shared with the behavior policy); owns the learning
     *     rate
     * @param policy the behavior policy used for action selection
     * @param gamma discount factor
     */
    public SARSA(TrainableQFunction<S, A> table, Policy<S, A> policy, double gamma) {
        if (gamma < 0 || gamma > 1) throw new IllegalArgumentException("gamma must be in [0, 1]");
        this.table = Objects.requireNonNull(table, "table must not be null");
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
        this.gamma = gamma;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        // Use the action pre-selected by update() to honour the SARSA (S,A,R,S',A') coupling.
        if (nextAction != null) {
            A a = nextAction;
            nextAction = null;
            return a;
        }
        return policy.selectAction(state, actions);
    }

    @Override
    public void update(S state, A action, StepResult<S> result, List<A> nextActions) {
        double nextQ;

        if (result.done() || nextActions.isEmpty()) {
            nextQ = 0.0;
            nextAction = null;
        } else {
            nextAction = policy.selectAction(result.nextState(), nextActions);
            nextQ = table.getValue(result.nextState(), nextAction);
        }

        table.update(state, action, result.reward() + gamma * nextQ);
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
