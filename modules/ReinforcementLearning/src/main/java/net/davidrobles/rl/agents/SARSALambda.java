package net.davidrobles.rl.agents;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.davidrobles.rl.ObservableQAgent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import net.davidrobles.rl.valuefunctions.QPair;
import net.davidrobles.rl.valuefunctions.TrainableQFunction;

/**
 * On-policy tabular SARSA(λ) with accumulating eligibility traces.
 *
 * <p>Extends SARSA with eligibility traces that assign credit to recently visited state-action
 * pairs. Setting λ=0 recovers one-step SARSA; λ=1 approximates Monte Carlo updates.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class SARSALambda<S, A> implements ObservableQAgent<S, A> {
    private final Policy<S, A> policy;
    private final double gamma;
    private final double lambda;
    private final TrainableQFunction<S, A> table;
    // Pre-selected next action for SARSA on-policy coupling.
    private A nextAction = null;
    private final Map<QPair<S, A>, Double> traces = new HashMap<>();
    private final Set<QFunctionObserver<S, A>> qFunctionObservers = new LinkedHashSet<>();

    /**
     * @param table the Q-function to update (shared with the behavior policy); owns the learning
     *     rate
     * @param policy the behavior policy used for action selection
     * @param gamma discount factor
     * @param lambda eligibility-trace decay rate (0 = SARSA(0), 1 = Monte Carlo)
     */
    public SARSALambda(
            TrainableQFunction<S, A> table, Policy<S, A> policy, double gamma, double lambda) {
        if (gamma < 0 || gamma > 1) throw new IllegalArgumentException("gamma must be in [0, 1]");
        if (lambda < 0 || lambda > 1)
            throw new IllegalArgumentException("lambda must be in [0, 1]");
        this.table = Objects.requireNonNull(table, "table must not be null");
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
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

        if (result.done() || nextActions.isEmpty()) {
            nextQ = 0.0;
            nextAction = null;
        } else {
            nextAction = policy.selectAction(result.nextState(), nextActions);
            nextQ = table.getValue(result.nextState(), nextAction);
        }

        double tdError = result.reward() + gamma * nextQ - table.getValue(state, action);

        // Accumulating trace: e(s,a) += 1
        traces.merge(new QPair<>(state, action), 1.0, Double::sum);

        for (Map.Entry<QPair<S, A>, Double> entry : traces.entrySet()) {
            QPair<S, A> key = entry.getKey();
            double currentQ = table.getValue(key.state(), key.action());
            table.update(key.state(), key.action(), currentQ + tdError * entry.getValue());
            entry.setValue(gamma * lambda * entry.getValue());
        }

        if (result.done()) traces.clear();
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
