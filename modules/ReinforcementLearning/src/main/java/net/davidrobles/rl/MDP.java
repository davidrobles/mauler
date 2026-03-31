package net.davidrobles.rl;

import java.util.Collection;
import java.util.Map;

/**
 * A Markov Decision Process (MDP) defined by a tuple (S, A, T, R, γ).
 *
 * <p>This interface represents the full model of the environment and is used by model-based
 * algorithms such as {@code ValueIteration} and {@code PolicyIteration}. Model-free algorithms
 * (Q-learning, SARSA, TD) interact with the environment through {@link Environment} instead.
 *
 * @param <S> the state type
 * @param <A> the action type
 */
public interface MDP<S, A> {

    /**
     * Returns the initial state. May be fixed or sampled from a start-state distribution; either
     * way a single concrete state is returned.
     */
    S getStartState();

    /**
     * Returns the actions available in {@code state}. Returns an empty collection for terminal
     * states.
     */
    Collection<A> getActions(S state);

    /** Returns all states in the MDP. */
    Collection<S> getStates();

    /**
     * Returns the transition distribution T(s, a) as a map from successor state to probability.
     * Probabilities must sum to 1. Returns an empty map for terminal states.
     */
    Map<S, Double> getTransitions(S state, A action);

    /**
     * Returns the reward R(s, a, s') for transitioning from {@code state} to {@code nextState} via
     * {@code action}.
     */
    double getReward(S state, A action, S nextState);

    /** Returns {@code true} if {@code state} is a terminal (absorbing) state. */
    boolean isTerminal(S state);
}
