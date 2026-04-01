package net.davidrobles.rl.valuefunctions;

/**
 * A state value function V(s).
 *
 * <p>Estimates the expected cumulative discounted return from a given state when following a fixed
 * policy π (V^π) or the optimal policy (V*). Used by prediction algorithms such as TD(0) and TD(λ),
 * and by model-based planners such as value iteration and policy iteration.
 *
 * @param <S> the type of the states
 */
public interface VFunction<S> {
    /**
     * Returns the estimated value of the given state.
     *
     * @param state the state to evaluate
     * @return the estimated cumulative discounted return from {@code state}
     */
    double getValue(S state);
}
