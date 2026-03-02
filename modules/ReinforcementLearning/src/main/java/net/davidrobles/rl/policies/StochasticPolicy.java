package net.davidrobles.rl.policies;

interface StochasticPolicy<S, A>
{
    /**
     * Returns the probability of taking the given action in the given state.
     */
    double getProbability(S state, A action);
}
