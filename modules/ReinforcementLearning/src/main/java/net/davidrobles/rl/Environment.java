package net.davidrobles.rl;

import java.util.List;

/**
 * A Reinforcement Learning environment.
 *
 * <p>Wraps the transition dynamics and reward function exposed to an agent. Implementations
 * typically delegate to an {@link MDP}, but the interface can also represent simulators or
 * real-world systems where the full MDP model is not available.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface Environment<S, A> {
    /** Returns the current state of the environment. */
    S getCurrentState();

    /**
     * Returns the actions available to the agent in the given state.
     *
     * <p>Returns an empty list when {@code state} is a terminal state (episode over). Callers
     * should treat an empty result the same as {@link StepResult#done()} being {@code true}.
     *
     * @param state the state to query
     * @return an unmodifiable list of legal actions; empty iff {@code state} is terminal
     */
    List<A> getActions(S state);

    /**
     * Applies {@code action} to the environment, advances the internal state, and returns a {@link
     * StepResult} containing the next state, the reward signal, and whether the episode has ended.
     */
    StepResult<S> step(A action);

    /** Resets the environment to the start state and returns it. */
    S reset();
}
