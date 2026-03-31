package net.davidrobles.rl.policies;

import java.util.List;
import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.VFunction;

/**
 * A Reinforcement Learning Policy.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface RLPolicy<S, A> {
    A getAction(S state, List<A> actions, QFunction<S, A> qFunc);

    A getAction(S state, List<A> actions, VFunction<S> vFunc);
}
