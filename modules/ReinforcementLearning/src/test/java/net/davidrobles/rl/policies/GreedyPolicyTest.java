package net.davidrobles.rl.policies;

import static org.junit.Assert.*;

import java.util.List;
import net.davidrobles.rl.valuefunctions.TabularQFunction;
import org.junit.Before;
import org.junit.Test;

public class GreedyPolicyTest {

    private TabularQFunction<String, String> q;
    private GreedyPolicy<String, String> policy;

    @Before
    public void setUp() {
        q = new TabularQFunction<>(0.5);
        policy = new GreedyPolicy<>(q);
    }

    @Test
    public void selectsActionWithHighestQValue() {
        q.setValue("s0", "a0", 1.0);
        q.setValue("s0", "a1", 3.0);
        q.setValue("s0", "a2", 2.0);
        assertEquals("a1", policy.selectAction("s0", List.of("a0", "a1", "a2")));
    }

    @Test
    public void singleActionIsAlwaysSelected() {
        q.setValue("s0", "a0", -100.0);
        assertEquals("a0", policy.selectAction("s0", List.of("a0")));
    }

    @Test
    public void allZeroQValuesSelectsFirstAction() {
        // Default Q=0 for all → first action in list wins (strict greater-than comparison)
        String selected = policy.selectAction("s0", List.of("a0", "a1", "a2"));
        assertEquals("a0", selected);
    }

    @Test
    public void reflectsUpdatedQValues() {
        q.setValue("s0", "a0", 5.0);
        assertEquals("a0", policy.selectAction("s0", List.of("a0", "a1")));

        // Update table so a1 is now best
        q.setValue("s0", "a1", 10.0);
        assertEquals("a1", policy.selectAction("s0", List.of("a0", "a1")));
    }

    @Test
    public void negativeQValuesSelectsLeastNegative() {
        q.setValue("s0", "a0", -5.0);
        q.setValue("s0", "a1", -1.0);
        assertEquals("a1", policy.selectAction("s0", List.of("a0", "a1")));
    }

    // Lifecycle hooks are no-ops by default; verify they don't throw.
    @Test
    public void lifecycleHooksDoNotThrow() {
        policy.reset();
        policy.onStep(1);
        policy.onEpisodeEnd(0);
        policy.setTrainingMode(false);
    }
}
