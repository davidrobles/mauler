package net.davidrobles.rl.valuefunctions;

import net.davidrobles.rl.QPair;

/**
 * A state-action value function.
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface QFunction<S, A>
{
    /**
     * Returns the value of the given state-action pair.
     */
    public double getValue(S state, A action);


    /**
     * Returns the value of the given state-action pair.
     */
    public double getValue(QPair<S, A> qPair);
}
