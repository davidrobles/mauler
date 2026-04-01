package net.davidrobles.rl.policies;

import java.util.List;

/**
 * A Reinforcement Learning policy: maps a state and its available actions to a chosen action.
 *
 * <p>Any value function the policy relies on (e.g. Q-function for greedy selection) is bound at
 * construction time, not passed on every call. Because tabular value functions are mutable
 * references, the policy always sees the latest estimates as the algorithm updates them.
 *
 * <p>Policies may maintain internal state across steps or episodes (e.g. an ε-decay schedule,
 * recurrent hidden state). Override the lifecycle hooks to react to training progress:
 *
 * <ul>
 *   <li>{@link #reset()} — called at the start of each episode
 *   <li>{@link #onStep(int)} — called after each environment step
 *   <li>{@link #onEpisodeEnd(int)} — called at the end of each episode
 *   <li>{@link #setTrainingMode(boolean)} — switch between exploration (train) and greedy (eval)
 * </ul>
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
     * Resets any internal per-episode state (e.g. recurrent hidden state, eligibility traces).
     * Called by {@link net.davidrobles.rl.RLLoop} at the start of each episode.
     */
    default void reset() {}

    /**
     * Called after each environment step with the running total of steps taken. Override to
     * implement per-step schedules (e.g. linear ε-decay over total steps).
     *
     * @param totalSteps running total of environment steps taken so far
     */
    default void onStep(int totalSteps) {}

    /**
     * Called at the end of each episode. Override to implement per-episode schedules (e.g. decaying
     * ε by episode, annealing temperature).
     *
     * @param episode the episode number just completed (0-indexed)
     */
    default void onEpisodeEnd(int episode) {}

    /**
     * Switches the policy between training mode (exploration enabled) and evaluation mode
     * (deterministic / greedy). Default implementation is a no-op.
     *
     * @param training {@code true} to enable exploration, {@code false} for greedy evaluation
     */
    default void setTrainingMode(boolean training) {}
}
