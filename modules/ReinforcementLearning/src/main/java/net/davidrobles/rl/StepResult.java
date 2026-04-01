package net.davidrobles.rl;

/**
 * The result of a single environment step, as returned by {@link Environment#step(Object)}.
 *
 * <p>Mirrors the {@code (observation, reward, terminated)} tuple from OpenAI Gym's {@code step()}
 * convention.
 *
 * @param nextState the state reached after the action was applied
 * @param reward the reward signal received for this transition
 * @param done {@code true} if {@code nextState} is a terminal state (episode over)
 * @param <S> the state (observation) type
 */
public record StepResult<S>(S nextState, double reward, boolean done) {}
