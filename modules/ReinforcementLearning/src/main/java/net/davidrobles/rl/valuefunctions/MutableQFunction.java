package net.davidrobles.rl.valuefunctions;

/**
 * A mutable state-action value function that supports both reads and writes.
 *
 * <p>Extends the read-only {@link QFunction} with a {@code setValue} method, enabling tabular and
 * linear function approximation implementations to be used interchangeably by learning algorithms.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface MutableQFunction<S, A> extends QFunction<S, A> {
    void setValue(S state, A action, double value);
}
