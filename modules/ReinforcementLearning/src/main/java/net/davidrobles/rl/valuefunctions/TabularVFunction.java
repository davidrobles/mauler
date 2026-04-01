package net.davidrobles.rl.valuefunctions;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabular (lookup table) implementation of a trainable state value function.
 *
 * @param <S> the type of the states
 */
public class TabularVFunction<S> implements TrainableVFunction<S> {
    private final double alpha;
    private Map<S, Double> stateValues = new HashMap<>();

    /** Creates a TabularVFunction for direct value assignment (e.g. DP planning). */
    public TabularVFunction() {
        this.alpha = 1.0;
    }

    /**
     * @param alpha learning rate in (0, 1]
     */
    public TabularVFunction(double alpha) {
        if (alpha <= 0 || alpha > 1) throw new IllegalArgumentException("alpha must be in (0, 1]");
        this.alpha = alpha;
    }

    @Override
    public double getValue(S state) {
        return stateValues.getOrDefault(state, 0.0);
    }

    @Override
    public void update(S state, double tdTarget) {
        double current = getValue(state);
        setValue(state, current + alpha * (tdTarget - current));
    }

    /** Directly sets V(state) — for use by planning algorithms and tests. */
    public void setValue(S state, double value) {
        stateValues.put(state, value);
    }
}
