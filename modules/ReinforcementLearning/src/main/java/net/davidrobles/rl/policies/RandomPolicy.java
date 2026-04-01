package net.davidrobles.rl.policies;

import java.util.List;
import java.util.Random;

/**
 * Uniform random policy. Selects each available action with equal probability.
 *
 * <p>Implements {@link StochasticPolicy}: log π(a|s) = −log(|actions|).
 *
 * @param <S> the type of the state
 * @param <A> the type of the action
 */
public class RandomPolicy<S, A> implements StochasticPolicy<S, A> {
    private final Random rng;

    public RandomPolicy(Random rng) {
        this.rng = rng;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        return actions.get(rng.nextInt(actions.size()));
    }

    @Override
    public double logProbability(S state, A action, List<A> actions) {
        return -Math.log(actions.size());
    }
}
