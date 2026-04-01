package net.davidrobles.rl.valuefunctions;

/**
 * A V-function that can be updated towards a TD target.
 *
 * <p>The caller (learning algorithm) is responsible for computing the TD target; the implementation
 * decides how to update its parameters (e.g. table lookup, linear weights, neural network).
 *
 * @param <S> the type of the states
 */
public interface TrainableVFunction<S> extends VFunction<S> {
    /**
     * Updates the function towards the given TD target for the given state.
     *
     * @param state the state observed
     * @param tdTarget the TD target (e.g. {@code r + γ * V(s')} for TD(0))
     */
    void update(S state, double tdTarget);
}
