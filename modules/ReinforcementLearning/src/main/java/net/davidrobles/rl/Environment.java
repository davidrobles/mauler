package net.davidrobles.rl;

import java.util.List;

/**
 * A Reinforcement Learning environment. Usually an implementation of this interface will use an
 * {@link MDP}, but it can be used internally only.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface Environment<S, A> {
    /** Returns the current state in the environment. */
    S getCurrentState();

    /**
     * Returns a list of the possible actions that the agent can take in the given state. Can return
     * the empty list if we are in a terminal state.
     */
    List<A> getPossibleActions(S state);

    /**
     * Applies {@code action} to the environment, advances the internal state, and returns a {@link
     * StepResult} containing the next state, the reward signal, and whether the episode has ended.
     */
    StepResult<S> step(A action);

    /** Resets the environment to the start state and returns it. */
    S reset();

    /** Has the environment entered a terminal state? This means there are no successors */
    boolean isTerminal();
}
