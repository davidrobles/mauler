package net.davidrobles.rl.policies;

import net.davidrobles.rl.RLEnv;
import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.VFunction;

/**
 * A Reinforcement Learning Policy.
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface RLPolicy<S, A>
{
    A getAction(RLEnv<S, A> env, QFunction<S, A> qFunc);

    A getAction(RLEnv<S, A> env, VFunction<S> vFunc);
}
