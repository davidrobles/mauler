package net.davidrobles.rl.policies;

import java.util.List;
import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.VFunction;

public class GreedyPolicy<S, A> implements RLPolicy<S, A> {
    @Override
    public A getAction(S state, List<A> actions, QFunction<S, A> qFunc) {
        A bestAction = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (A action : actions) {
            double value = qFunc.getValue(state, action);

            if (value > bestValue) {
                bestAction = action;
                bestValue = value;
            }
        }

        return bestAction;
    }

    /**
     * Not supported: selecting the greedy action requires knowing next-state values, which needs a
     * model. Use a Q-function overload or a model-based policy instead.
     */
    @Override
    public A getAction(S state, List<A> actions, VFunction<S> vFunc) {
        throw new UnsupportedOperationException(
                "Greedy action selection from a V-function requires a model.");
    }
}
