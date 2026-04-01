package net.davidrobles.rl.policies;

import java.util.List;
import net.davidrobles.rl.valuefunctions.QFunction;

/**
 * Always selects the action with the highest Q-value (greedy w.r.t. the Q-function).
 *
 * <p>The Q-function is bound at construction. Because {@link
 * net.davidrobles.rl.valuefunctions.TabularQFunction} is mutable, the policy always reflects the
 * latest estimates produced by the learning algorithm.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class GreedyPolicy<S, A> implements Policy<S, A> {
    private final QFunction<S, A> qFunc;

    public GreedyPolicy(QFunction<S, A> qFunc) {
        this.qFunc = qFunc;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
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
}
