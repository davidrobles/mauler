package net.davidrobles.rl.agents;

import java.util.ArrayList;
import java.util.List;
import net.davidrobles.rl.ObservableQAgent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.MutableQFunction;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;

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
public class TabularQLearning<S, A> implements ObservableQAgent<S, A> {
    private final Policy<S, A> policy;
    private final double alpha;
    private final double gamma;
    private final MutableQFunction<S, A> table;
    private final List<QFunctionObserver<S, A>> qFunctionObservers = new ArrayList<>();

    /**
     * @param table the Q-function to update (shared with the behavior policy)
     * @param policy the behavior policy used for action selection
     * @param alpha learning rate
     * @param gamma discount factor
     */
    public TabularQLearning(
            MutableQFunction<S, A> table, Policy<S, A> policy, double alpha, double gamma) {
        if (alpha <= 0 || alpha > 1) throw new IllegalArgumentException("alpha must be in (0, 1]");
        if (gamma < 0 || gamma > 1) throw new IllegalArgumentException("gamma must be in [0, 1]");
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

        if (!result.done && !nextActions.isEmpty()) {
            maxNextQ = Double.NEGATIVE_INFINITY;
            for (A nextAction : nextActions) {
                double v = table.getValue(result.nextState, nextAction);
                if (v > maxNextQ) maxNextQ = v;
            }
        }

        double currentQ = table.getValue(state, action);
        table.setValue(
                state, action, currentQ + alpha * (result.reward + gamma * maxNextQ - currentQ));
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
