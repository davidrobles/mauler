package net.davidrobles.rl.valuefunctions;

/**
 * A state-action value function Q(s, a).
 *
 * <p>Estimates the expected cumulative discounted return from a given state after taking a given
 * action and then following a fixed policy π (Q^π) or the optimal policy (Q*). Used by control
 * algorithms such as Q-Learning and SARSA, and by policies such as {@link
 * net.davidrobles.rl.policies.GreedyPolicy} and {@link net.davidrobles.rl.policies.EpsilonGreedy}.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface QFunction<S, A> {
    /**
     * Returns the estimated value of the given state-action pair.
     *
     * @param state the state observed
     * @param action the action taken
     * @return the estimated cumulative discounted return from {@code state} after taking {@code
     *     action}
     */
    double getValue(S state, A action);
}
