package net.davidrobles.rl.valuefunctions;

/**
 * A state-action pair, used as a key in tabular Q-function lookups.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public record QPair<S, A>(S state, A action) {}
