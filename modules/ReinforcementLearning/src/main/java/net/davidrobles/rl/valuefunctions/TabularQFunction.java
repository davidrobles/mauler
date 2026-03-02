package net.davidrobles.rl.valuefunctions;

import net.davidrobles.rl.QPair;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabular (lookup table) implementation of a state-sction value function.
 */
public class TabularQFunction<S, A> implements QFunction<S, A>
{
    private Map<QPair, Double> actionValues = new HashMap<QPair, Double>();

    @Override
    public double getValue(S state, A action)
    {
        return getValue(new QPair<S, A>(state, action));
    }

    @Override
    public double getValue(QPair qPair)
    {
        return actionValues.containsKey(qPair) ? actionValues.get(qPair) : 0;
    }

    public void setValue(QPair qPair, double value)
    {
        actionValues.put(qPair, value);
    }

    public void setValue(S state, A action, double value)
    {
        setValue(new QPair<S, A>(state, action), value);
    }
}
