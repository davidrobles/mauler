package net.davidrobles.rl;

import java.util.List;

/**
 * A Reinforcement Learning environment. Usually an implementation of this interface
 * will use an {@link MDP}, but it can be used internally only.
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface RLEnv<S, A>
{
    /**
     * Returns the current state in the environment.
     */
    S getCurrentState();

    /**
     * Returns a list of the possible actions that the agent can take in the given state.
     * Can return the empty list if we are in a terminal state.
     */
    List<A> getPossibleActions(S state);

    /**
     * Performs an action on the environment that changes its internal state.
     * @return the reward.
     */
    double performAction(A Action);

    /**
     * Resets the current state to the start state.
     */
    void reset();

    /**
     * Has the environment entered a terminal
     * state? This means there are no successors
     */
    boolean isTerminal();
}
