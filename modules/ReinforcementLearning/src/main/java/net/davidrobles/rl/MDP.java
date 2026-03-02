package net.davidrobles.rl;

import java.util.Collection;
import java.util.Map;

public interface MDP<S, A>
{
    // Returns the initial state of the MDP. It can be stochastic.
    S getStartState();

    // Returns a list of the possible actions that the agent can take in the given state.
    // Can return an empty list if we are in a terminal state.
    Collection<A> getActions(S state);

    // Returns a list of all the states.
    Collection<S> getStates();

    // returns a map (nextState, probability) representing the states reachable from 'state'
    // by taking 'action' along with their transition probabilities
    // Not available in reinforcement learning.
    Map<S, Double> getTransitions(S state, A action);

    // Returns the reward of being in the given state, taking the given action,
    // and moving to the given next state. Not available in reinforcement learning.
    double getReward(S state, A action, S nextState);

    // Returns true if the current state is a terminal state.  By convention,
    // a terminal state has zero future rewards.  Sometimes the terminal state(s)
    // may have no possible actions.  It is also common to think of the terminal
    // state as having a self-loop action 'pass' with zero reward; the formulations
    // are equivalent.
    boolean isTerminal(S state);
}
