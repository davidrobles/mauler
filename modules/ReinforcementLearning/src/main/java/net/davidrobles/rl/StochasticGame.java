package net.davidrobles.rl;

import java.util.Map;
import java.util.Set;

public interface StochasticGame<S, A>
{
    int getNumberOfAgents();
    Set<A> getActions(S state, int agent);
    Set<S> getStates();
    Map<S, Double> getTransitions(S state, A action);
    double getReward(S state, A action, S nextState, int agent);
    boolean isTerminal();
}

abstract class MarkovGame<S, A> implements StochasticGame<S, A>
{
    public int getNumberOfAgents()
    {
        return 2;
    }
}