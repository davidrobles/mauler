package net.davidrobles.rl.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.davidrobles.rl.ObservableQAgent;
import net.davidrobles.rl.QPair;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.TabularQFunction;

/**
 * On-policy tabular SARSA(λ) with accumulating eligibility traces.
 *
 * <p>Extends SARSA with eligibility traces that assign credit to recently visited state-action
 * pairs. Setting λ=0 recovers one-step SARSA; λ=1 approximates Monte Carlo updates.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class TabularSARSALambda<S, A> implements ObservableQAgent<S, A> {
    private final Policy<S, A> policy;
    private final double alpha;
    private final double gamma;
    private final double lambda;
    private final TabularQFunction<S, A> table;
    // Pre-selected next action for SARSA on-policy coupling.
    private A nextAction = null;
    private final Map<QPair<S, A>, Double> traces = new HashMap<>();
    private final List<QFunctionObserver<S, A>> qFunctionObservers = new ArrayList<>();

    /**
     * @param table the Q-function to update (shared with the behavior policy)
     * @param policy the behavior policy used for action selection
     * @param alpha learning rate
     * @param gamma discount factor
     * @param lambda eligibility-trace decay rate (0 = SARSA(0), 1 = Monte Carlo)
     */
    public TabularSARSALambda(
            TabularQFunction<S, A> table,
            Policy<S, A> policy,
            double alpha,
            double gamma,
            double lambda) {
        if (alpha <= 0 || alpha > 1) throw new IllegalArgumentException("alpha must be in (0, 1]");
        if (gamma < 0 || gamma > 1) throw new IllegalArgumentException("gamma must be in [0, 1]");
        if (lambda < 0 || lambda > 1)
            throw new IllegalArgumentException("lambda must be in [0, 1]");
        this.table = table;
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

        double tdError = result.reward + gamma * nextQ - table.getValue(state, action);

        // Accumulating trace: e(s,a) += 1
        QPair<S, A> sa = new QPair<>(state, action);
        traces.merge(sa, 1.0, Double::sum);

        for (Map.Entry<QPair<S, A>, Double> entry : traces.entrySet()) {
            QPair<S, A> key = entry.getKey();
            table.setValue(
                    key,
                    table.getValue(key.state(), key.action()) + alpha * tdError * entry.getValue());
            entry.setValue(gamma * lambda * entry.getValue());
        }

        if (result.done) traces.clear();
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
