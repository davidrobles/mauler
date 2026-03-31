package net.davidrobles.rl.algorithms;

import java.util.ArrayList;
import java.util.List;
import net.davidrobles.rl.Agent;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;

public class TabularTD0<S, A> implements Agent<S, A> {
    private RLPolicy<S, A> policy;
    private double alpha;
    private double gamma;
    private TabularVFunction<S> table = new TabularVFunction<S>();
    private List<VFunctionObserver<S>> valueFuncObservers = new ArrayList<VFunctionObserver<S>>();

    public TabularTD0(RLPolicy<S, A> policy, double alpha, double gamma) {
        this.policy = policy;
        this.alpha = alpha;
        this.gamma = gamma;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        return policy.getAction(state, actions, table);
    }

    @Override
    public void update(S state, A action, StepResult<S> result, List<A> nextActions) {
        double newValue =
                table.getValue(state)
                        + alpha
                                * (result.reward
                                        + gamma * table.getValue(result.nextState)
                                        - table.getValue(state));
        table.setValue(state, newValue);
        notifyValueFunctionUpdate();
    }

    public void notifyValueFunctionUpdate() {
        for (VFunctionObserver<S> observer : valueFuncObservers)
            observer.valueFunctionChanged(table);
    }

    public void addVFunctionObserver(VFunctionObserver<S> observer) {
        valueFuncObservers.add(observer);
    }
}
