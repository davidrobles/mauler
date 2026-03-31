package net.davidrobles.rl.policies;

import java.util.List;
import java.util.Random;
import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.VFunction;

/**
 * Uniform random policy.
 *
 * @param <S> the type of the state
 * @param <A> the type of the action
 */
public class RandomPolicy<S, A> implements RLPolicy<S, A> {
    private final Random rng;

    public RandomPolicy(Random rng) {
        this.rng = rng;
    }

    @Override
    public A getAction(S state, List<A> actions, QFunction<S, A> qFunc) {
        return actions.get(rng.nextInt(actions.size()));
    }

    @Override
    public A getAction(S state, List<A> actions, VFunction<S> vFunc) {
        return actions.get(rng.nextInt(actions.size()));
    }
}
