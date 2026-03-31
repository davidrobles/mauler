package net.davidrobles.rl;

import java.util.List;

/**
 * A Reinforcement Learning agent. Encapsulates the update rule only — it does not own an
 * environment or episode loop. Use {@link RLLoop} to drive training.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface Agent<S, A> {
    /**
     * Selects an action to take in the given state.
     *
     * @param state the current state
     * @param actions the list of available actions (non-empty)
     * @return the selected action
     */
    A selectAction(S state, List<A> actions);

    /**
     * Updates the agent's internal state (e.g. value function) after one transition.
     *
     * @param state the state before the action
     * @param action the action taken
     * @param result the step result from the environment
     * @param nextActions available actions in the next state; empty if the episode is done
     */
    void update(S state, A action, StepResult<S> result, List<A> nextActions);
}
