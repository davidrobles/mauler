package net.davidrobles.rl.policies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A deterministic tabular policy that stores an explicit state → action mapping.
 *
 * <p>Used by model-based algorithms (policy iteration) which compute an optimal policy and store it
 * as a lookup table.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class TabularPolicy<S, A> implements Policy<S, A>, StochasticPolicy<S, A> {
    private final Map<S, A> map = new HashMap<>();

    public A getAction(S state) {
        return map.get(state);
    }

    public void setAction(S state, A action) {
        map.put(state, action);
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        return map.get(state);
    }

    @Override
    public double logProbability(S state, A action, List<A> actions) {
        return map.get(state).equals(action) ? 0.0 : Double.NEGATIVE_INFINITY;
    }
}
