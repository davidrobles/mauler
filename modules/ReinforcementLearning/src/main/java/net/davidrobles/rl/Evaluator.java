package net.davidrobles.rl;

import java.util.List;

/**
 * An agent that evaluates a fixed policy by estimating a value function, but does not learn to act.
 *
 * <p>Evaluators observe transitions and update their value estimates, but delegate action selection
 * entirely to an external policy. The {@link #observe} method is the primary hook; {@link
 * Agent#update} is provided as a default that ignores the action and next-action arguments, which
 * are irrelevant for prediction algorithms.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public interface Evaluator<S, A> extends Agent<S, A> {
    /**
     * Observes a transition and updates the value estimate for {@code state}.
     *
     * @param state the state before the transition
     * @param result the result of the transition (reward, next state, done flag)
     */
    void observe(S state, StepResult<S> result);

    @Override
    default void update(S state, A action, StepResult<S> result, List<A> nextActions) {
        observe(state, result);
    }
}
