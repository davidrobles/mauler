package net.davidrobles.rl.policies;

import java.util.List;

/**
 * A stochastic policy that can report the log-probability of selecting a given action.
 *
 * <p>Log-probability is required by policy-gradient algorithms (REINFORCE, PPO, A2C) to compute the
 * policy-gradient loss: ∇ log π(a|s) · G.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface StochasticPolicy<S, A> extends Policy<S, A> {
    /**
     * Returns the natural log of the probability of selecting {@code action} in {@code state}.
     *
     * @param state the current state
     * @param action the action whose probability is queried
     * @param actions the full list of available actions (defines the support of the distribution)
     * @return log π(action | state)
     */
    double logProbability(S state, A action, List<A> actions);
}
