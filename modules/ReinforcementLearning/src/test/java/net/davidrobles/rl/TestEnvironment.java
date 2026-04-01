package net.davidrobles.rl;

import java.util.List;

/**
 * A two-step deterministic environment for model-free tests.
 *
 * <pre>
 *   state 0 --go--> state 1 (reward=0, done=false)
 *   state 1 --go--> state 2 (reward=1, done=true)
 * </pre>
 *
 * <p>Call {@link #reset()} to restart from state 0.
 */
public class TestEnvironment implements Environment<Integer, String> {

    public static final String GO = "go";

    private int current = 0;

    @Override
    public Integer getCurrentState() {
        return current;
    }

    @Override
    public List<String> getActions(Integer state) {
        if (state >= 2) return List.of();
        return List.of(GO);
    }

    @Override
    public StepResult<Integer> step(String action) {
        int next = current + 1;
        double reward = (current == 1) ? 1.0 : 0.0;
        boolean done = (next >= 2);
        current = next;
        return new StepResult<>(next, reward, done);
    }

    @Override
    public Integer reset() {
        current = 0;
        return 0;
    }
}
