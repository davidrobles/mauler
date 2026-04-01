package net.davidrobles.rl.valuefunctions;

import java.util.HashMap;
import java.util.Map;

/** Tabular (lookup table) implementation of a trainable state-action value function. */
public class TabularQFunction<S, A> implements TrainableQFunction<S, A> {
    private final double alpha;
    private Map<QPair<S, A>, Double> actionValues = new HashMap<>();

    /**
     * @param alpha learning rate in (0, 1]
     */
    public TabularQFunction(double alpha) {
        if (alpha <= 0 || alpha > 1) throw new IllegalArgumentException("alpha must be in (0, 1]");
        this.alpha = alpha;
    }

    @Override
    public double getValue(S state, A action) {
        return actionValues.getOrDefault(new QPair<>(state, action), 0.0);
    }

    @Override
    public void update(S state, A action, double tdTarget) {
        double current = getValue(state, action);
        setValue(state, action, current + alpha * (tdTarget - current));
    }

    /** Directly sets Q(state, action) — for use by planning algorithms and tests. */
    public void setValue(S state, A action, double value) {
        actionValues.put(new QPair<>(state, action), value);
    }
}
