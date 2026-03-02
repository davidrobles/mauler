package net.davidrobles.rl.policies;

import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.RLEnv;
import net.davidrobles.rl.valuefunctions.VFunction;

import java.util.List;
import java.util.Random;

/**
 * Uniform random policy.
 * @param <S> the type of the state
 * @param <A> the type of the action
 */
public class RandomPolicy<S, A> implements RLPolicy<S, A>
{
    private final Random rng;

    public RandomPolicy(Random rng)
    {
        this.rng = rng;
    }

    public A getRandomAction(RLEnv<S, A> env)
    {
        List<A> actions = env.getPossibleActions(env.getCurrentState());
        return actions.get(rng.nextInt(actions.size()));
    }

    @Override
    public A getAction(RLEnv<S, A> env, QFunction<S, A> saqFunction)
    {
        return getRandomAction(env);
    }

    @Override
    public A getAction(RLEnv<S, A> env, VFunction<S> svFunction)
    {
        return getRandomAction(env);
    }
}
