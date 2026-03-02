package net.davidrobles.rl;

import java.util.HashSet;
import java.util.Set;

/**
 * This class consists exclusively of static methods that operate on {@link MDP}'s.
 *
 * @author David Robles
 */
public class MDPUtil
{
    public static <S, A> Set<S> getNextStates(MDP<S, A> mdp, S state)
    {
        Set<S> nextStates = new HashSet<S>();

        for (A action : mdp.getActions(state))
            nextStates.addAll(mdp.getTransitions(state, action).keySet());

        return nextStates;
    }
}
