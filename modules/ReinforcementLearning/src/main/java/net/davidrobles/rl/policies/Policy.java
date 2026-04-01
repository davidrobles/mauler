package net.davidrobles.rl.policies;

import java.util.List;

/**
 * A Reinforcement Learning policy: maps a state and its available actions to a chosen action.
 *
 * <p>Any value function the policy relies on (e.g. Q-function for greedy selection) is bound at
 * construction time, not passed on every call. Because tabular value functions are mutable
 * references, the policy always sees the latest estimates as the algorithm updates them.
 *
 * <p>Policies may maintain internal state across steps (e.g. an ε-decay schedule). Override {@link
 * #update(int)} to react to the running step count provided by the training loop.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface Policy<S, A> {
    /**
     * Selects an action for the given state.
     *
     * @param state the current state
     * @param actions the list of available actions (non-empty)
     * @return the selected action
     */
    A selectAction(S state, List<A> actions);

    /**
     * Notifies the policy that one step has completed. Override to implement schedules such as
     * linear ε-decay or temperature annealing. The default implementation is a no-op.
     *
     * @param totalSteps running total of environment steps taken so far
     */
    default void update(int totalSteps) {}
}
