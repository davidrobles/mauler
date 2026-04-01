package net.davidrobles.rl.valuefunctions;

import java.util.HashMap;
import java.util.Map;
import net.davidrobles.rl.QPair;

/** Tabular (lookup table) implementation of a state-action value function. */
public class TabularQFunction<S, A> implements QFunction<S, A> {
    private Map<QPair<S, A>, Double> actionValues = new HashMap<>();

    @Override
    public double getValue(S state, A action) {
        return getValue(new QPair<>(state, action));
    }

    private double getValue(QPair<S, A> qPair) {
        return actionValues.containsKey(qPair) ? actionValues.get(qPair) : 0;
    }

    public void setValue(QPair<S, A> qPair, double value) {
        actionValues.put(qPair, value);
    }

    public void setValue(S state, A action, double value) {
        actionValues.put(new QPair<>(state, action), value);
    }
}
