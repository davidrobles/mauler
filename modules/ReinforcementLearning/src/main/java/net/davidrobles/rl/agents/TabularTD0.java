package net.davidrobles.rl.agents;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.davidrobles.rl.ObservableVAgent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.TrainableVFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;

/**
 * Tabular TD(0) for on-policy state value prediction.
 *
 * <p>Estimates the value function V^π for a fixed policy π using one-step temporal-difference
 * updates. Action selection is fully delegated to the provided policy.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class TabularTD0<S, A> implements ObservableVAgent<S, A> {
    private final Policy<S, A> policy;
    private final double gamma;
    private final TrainableVFunction<S> table;
    private final Set<VFunctionObserver<S>> valueFuncObservers = new LinkedHashSet<>();

    /**
     * @param table the V-function to evaluate and update (shared with the caller); owns the
     *     learning rate
     * @param policy the behavior policy used for action selection
     * @param gamma discount factor
     */
    public TabularTD0(TrainableVFunction<S> table, Policy<S, A> policy, double gamma) {
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
        double nextV = result.done ? 0.0 : table.getValue(result.nextState);
        table.update(state, result.reward + gamma * nextV);
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
