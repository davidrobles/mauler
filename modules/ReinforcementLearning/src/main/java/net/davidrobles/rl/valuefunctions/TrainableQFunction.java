package net.davidrobles.rl.valuefunctions;

/**
 * A Q-function that can be updated towards a TD target.
 *
 * <p>The caller (learning algorithm) is responsible for computing the TD target; the implementation
 * decides how to update its parameters (e.g. table lookup, linear weights, neural network).
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface TrainableQFunction<S, A> extends QFunction<S, A> {
    /**
     * Updates the function towards the given TD target for the state-action pair (state, action).
     *
     * @param state the state observed
     * @param action the action taken
     * @param tdTarget the TD target (e.g. {@code r + γ * max_a' Q(s', a')} for Q-Learning)
     */
    void update(S state, A action, double tdTarget);
}
