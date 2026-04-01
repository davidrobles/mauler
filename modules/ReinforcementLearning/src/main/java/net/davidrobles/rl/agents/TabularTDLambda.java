package net.davidrobles.rl.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.davidrobles.rl.Agent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;

/**
 * Tabular TD(λ) for on-policy state value prediction with eligibility traces.
 *
 * <p>Extends TD(0) with accumulating eligibility traces that spread credit across recently visited
 * states. Setting λ=0 recovers TD(0); λ=1 approximates Monte Carlo updates.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class TabularTDLambda<S, A> implements Agent<S, A> {
    private final Policy<S, A> policy;
    private final double alpha;
    private final double gamma;
    private final double lambda;
    private final TabularVFunction<S> table;
    private final Map<S, Double> traces = new HashMap<>();
    private final List<VFunctionObserver<S>> valueFuncObservers = new ArrayList<>();

    /**
     * @param table the V-function to evaluate and update (shared with the caller)
     * @param policy the behavior policy used for action selection
     * @param alpha learning rate
     * @param gamma discount factor
     * @param lambda eligibility-trace decay rate (0 = TD(0), 1 = Monte Carlo)
     */
    public TabularTDLambda(
            TabularVFunction<S> table,
            Policy<S, A> policy,
            double alpha,
            double gamma,
            double lambda) {
        this.table = table;
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
        this.lambda = lambda;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        return policy.selectAction(state, actions);
    }

    @Override
    public void update(S state, A action, StepResult<S> result, List<A> nextActions) {
        double tdError =
                result.reward + gamma * table.getValue(result.nextState) - table.getValue(state);

        // Accumulating trace: e(s) += 1
        traces.merge(state, 1.0, Double::sum);

        for (Map.Entry<S, Double> entry : traces.entrySet()) {
            table.setValue(
                    entry.getKey(),
                    table.getValue(entry.getKey()) + alpha * tdError * entry.getValue());
            entry.setValue(gamma * lambda * entry.getValue());
        }

        if (result.done) traces.clear();
        notifyValueFunctionUpdate();
    }

    public void addVFunctionObserver(VFunctionObserver<S> observer) {
        valueFuncObservers.add(observer);
    }

    private void notifyValueFunctionUpdate() {
        for (VFunctionObserver<S> observer : valueFuncObservers)
            observer.valueFunctionUpdated(table);
    }
}
