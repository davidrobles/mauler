package net.davidrobles.rl.valuefunctions;

/**
 * A state-action value function.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface QFunction<S, A> {
    /** Returns the estimated value of the given state-action pair. */
    double getValue(S state, A action);
}
