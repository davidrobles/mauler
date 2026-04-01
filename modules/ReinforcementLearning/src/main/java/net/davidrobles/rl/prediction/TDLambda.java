package net.davidrobles.rl.prediction;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.davidrobles.rl.Evaluator;
import net.davidrobles.rl.ObservableVAgent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.TrainableVFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;

/**
 * TD(λ) for on-policy state value prediction with eligibility traces.
 *
 * <p>Extends TD(0) with accumulating eligibility traces that spread credit across recently visited
 * states. Setting λ=0 recovers TD(0); λ=1 approximates Monte Carlo updates.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class TDLambda<S, A> implements Evaluator<S, A>, ObservableVAgent<S, A> {
    private final Policy<S, A> policy;
    private final double gamma;
    private final double lambda;
    private final TrainableVFunction<S> table;
    private final Map<S, Double> traces = new HashMap<>();
    private final Set<VFunctionObserver<S>> valueFuncObservers = new LinkedHashSet<>();

    /**
     * @param table the V-function to evaluate and update (shared with the caller); owns the
     *     learning rate
     * @param policy the behavior policy used for action selection
     * @param gamma discount factor
     * @param lambda eligibility-trace decay rate (0 = TD(0), 1 = Monte Carlo)
     */
    public TDLambda(TrainableVFunction<S> table, Policy<S, A> policy, double gamma, double lambda) {
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
        return policy.selectAction(state, actions);
    }

    @Override
    public void observe(S state, StepResult<S> result) {
        double nextV = result.done() ? 0.0 : table.getValue(result.nextState());
        double tdError = result.reward() + gamma * nextV - table.getValue(state);

        // Accumulating trace: e(s) += 1
        traces.merge(state, 1.0, Double::sum);

        for (Map.Entry<S, Double> entry : traces.entrySet()) {
            S s = entry.getKey();
            table.update(s, table.getValue(s) + tdError * entry.getValue());
            entry.setValue(gamma * lambda * entry.getValue());
        }

        if (result.done()) traces.clear();
        notifyValueFunctionUpdate();
    }

    @Override
    public void addVFunctionObserver(VFunctionObserver<S> observer) {
        valueFuncObservers.add(observer);
    }

    private void notifyValueFunctionUpdate() {
        for (VFunctionObserver<S> observer : valueFuncObservers)
            observer.valueFunctionUpdated(table);
    }
}
