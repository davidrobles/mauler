package net.davidrobles.rl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A simple deterministic chain MDP used by planning tests.
 *
 * <pre>
 *   0 --STEP--> 1 --STEP--> 2 (terminal)
 *        r=0         r=1.0
 * </pre>
 *
 * <p>State 2 is terminal and is intentionally excluded from {@link #getStates()} so that {@code
 * ValueIteration} and {@code PolicyIteration} treat it as an absorbing state with V=0.
 */
public class ChainMDP implements MDP<Integer, String> {

    public static final String STEP = "STEP";

    @Override
    public Integer getStartState() {
        return 0;
    }

    @Override
    public Collection<String> getActions(Integer state) {
        if (state == 2) return List.of();
        return List.of(STEP);
    }

    @Override
    public Collection<Integer> getStates() {
        // Terminal state 2 is excluded; its default table value of 0.0 is correct.
        return List.of(0, 1);
    }

    @Override
    public Map<Integer, Double> getTransitions(Integer state, String action) {
        if (!STEP.equals(action)) return Map.of();
        return switch (state) {
            case 0 -> Map.of(1, 1.0);
            case 1 -> Map.of(2, 1.0);
            default -> Map.of();
        };
    }

    @Override
    public double getReward(Integer state, String action, Integer nextState) {
        if (state == 1 && nextState == 2) return 1.0;
        return 0.0;
    }

    @Override
    public boolean isTerminal(Integer state) {
        return state == 2;
    }
}
