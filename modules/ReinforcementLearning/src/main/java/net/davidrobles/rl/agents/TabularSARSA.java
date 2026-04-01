package net.davidrobles.rl.agents;

import java.util.ArrayList;
import java.util.List;
import net.davidrobles.rl.ObservableQAgent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularQFunction;

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
public class TabularSARSA<S, A> implements ObservableQAgent<S, A> {
    private final Policy<S, A> policy;
    private final double alpha;
    private final double gamma;
    private final TabularQFunction<S, A> table;
    // Pre-selected next action to maintain the on-policy (S, A, R, S', A') SARSA coupling.
    private A nextAction = null;
    private final List<QFunctionObserver<S, A>> qFunctionObservers = new ArrayList<>();

    /**
     * @param table the Q-function to update (shared with the behavior policy)
     * @param policy the behavior policy used for action selection
     * @param alpha learning rate
     * @param gamma discount factor
     */
    public TabularSARSA(
            TabularQFunction<S, A> table, Policy<S, A> policy, double alpha, double gamma) {
        if (alpha <= 0 || alpha > 1) throw new IllegalArgumentException("alpha must be in (0, 1]");
        if (gamma < 0 || gamma > 1) throw new IllegalArgumentException("gamma must be in [0, 1]");
        this.table = table;
        this.policy = policy;
        this.alpha = alpha;
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

        if (result.done || nextActions.isEmpty()) {
            nextQ = 0.0;
            nextAction = null;
        } else {
            nextAction = policy.selectAction(result.nextState, nextActions);
            nextQ = table.getValue(result.nextState, nextAction);
        }

        double currentQ = table.getValue(state, action);
        table.setValue(
                state, action, currentQ + alpha * (result.reward + gamma * nextQ - currentQ));
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
