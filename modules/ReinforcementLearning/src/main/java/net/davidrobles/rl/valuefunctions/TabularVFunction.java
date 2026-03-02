package net.davidrobles.rl.valuefunctions;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabular (lookup table) implementation of a state value function.
 *
 * @param <S> the type of the states
 */
public class TabularVFunction<S> implements VFunction<S>
{
    private Map<S, Double> stateValues = new HashMap<S, Double>();

    @Override
    public double getValue(S state)
    {
        return stateValues.containsKey(state) ? stateValues.get(state) : 0;
    }

    public void setValue(S state, double value)
    {
        stateValues.put(state, value);
    }
}
