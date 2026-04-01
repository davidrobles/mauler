package net.davidrobles.rl.valuefunctions;

/**
 * A mutable state value function that supports both reads and writes.
 *
 * <p>Extends the read-only {@link VFunction} with a {@code setValue} method, enabling tabular and
 * linear function approximation implementations to be used interchangeably by learning algorithms.
 *
 * @param <S> the type of the states
 */
public interface MutableVFunction<S> extends VFunction<S> {
    void setValue(S state, double value);
}
