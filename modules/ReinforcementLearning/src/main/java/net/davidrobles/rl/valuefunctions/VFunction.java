package net.davidrobles.rl.valuefunctions;

/**
 * A state value function.
 */
public interface VFunction<S>
{
    /**
     * Returns the estimated value of the given state.
     */
    public double getValue(S state);
}
