package net.davidrobles.rl;

/**
 * The result of a single environment step, as returned by {@link Environment#step(Object)}.
 *
 * <p>Mirrors the {@code (observation, reward, terminated)} tuple from OpenAI Gym's {@code step()}
 * convention.
 *
 * @param <S> the state (observation) type
 */
public class StepResult<S> {
    /** The state reached after the action was applied. */
    public final S nextState;

    /** The reward signal received for this transition. */
    public final double reward;

    /** {@code true} if {@code nextState} is a terminal state (episode over). */
    public final boolean done;

    public StepResult(S nextState, double reward, boolean done) {
        this.nextState = nextState;
        this.reward = reward;
        this.done = done;
    }
}
