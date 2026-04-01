package net.davidrobles.rl.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.davidrobles.rl.ObservableVAgent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.MutableVFunction;
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
    private final double alpha;
    private final double gamma;
    private final MutableVFunction<S> table;
    private final List<VFunctionObserver<S>> valueFuncObservers = new ArrayList<>();

    /**
     * @param table the V-function to evaluate and update (shared with the caller)
     * @param policy the behavior policy used for action selection
     * @param alpha learning rate
     * @param gamma discount factor
     */
    public TabularTD0(MutableVFunction<S> table, Policy<S, A> policy, double alpha, double gamma) {
        if (alpha <= 0 || alpha > 1) throw new IllegalArgumentException("alpha must be in (0, 1]");
        if (gamma < 0 || gamma > 1) throw new IllegalArgumentException("gamma must be in [0, 1]");
        this.table = Objects.requireNonNull(table, "table must not be null");
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
        this.alpha = alpha;
        this.gamma = gamma;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        return policy.selectAction(state, actions);
    }

    @Override
    public void update(S state, A action, StepResult<S> result, List<A> nextActions) {
        double currentV = table.getValue(state);
        double nextV = result.done ? 0.0 : table.getValue(result.nextState);
        table.setValue(state, currentV + alpha * (result.reward + gamma * nextV - currentV));
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
