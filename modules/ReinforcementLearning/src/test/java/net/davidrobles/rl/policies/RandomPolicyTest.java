package net.davidrobles.rl.policies;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.Test;

public class RandomPolicyTest {

    private static final double EPS = 1e-9;

    @Test
    public void singleActionIsAlwaysSelected() {
        RandomPolicy<String, String> policy = new RandomPolicy<>(new Random(0));
        for (int i = 0; i < 10; i++) {
            assertEquals("only", policy.selectAction("s", List.of("only")));
        }
    }

    @Test
    public void returnsOnlyActionsFromGivenList() {
        RandomPolicy<String, String> policy = new RandomPolicy<>(new Random(42));
        List<String> actions = List.of("a0", "a1", "a2");
        Set<String> observed = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String chosen = policy.selectAction("s", actions);
            observed.add(chosen);
            assertTrue(actions.contains(chosen));
        }
        // With 100 draws from 3 actions the probability of missing any is negligible
        assertEquals(3, observed.size());
    }

    @Test
    public void logProbabilityForSingleAction() {
        RandomPolicy<String, String> policy = new RandomPolicy<>(new Random(0));
        // log(1/1) = 0
        assertEquals(0.0, policy.logProbability("s", "a0", List.of("a0")), EPS);
    }

    @Test
    public void logProbabilityForTwoActions() {
        RandomPolicy<String, String> policy = new RandomPolicy<>(new Random(0));
        double expected = -Math.log(2);
        assertEquals(expected, policy.logProbability("s", "a0", List.of("a0", "a1")), EPS);
        assertEquals(expected, policy.logProbability("s", "a1", List.of("a0", "a1")), EPS);
    }

    @Test
    public void logProbabilityForFourActions() {
        RandomPolicy<String, String> policy = new RandomPolicy<>(new Random(0));
        double expected = -Math.log(4);
        List<String> actions = List.of("a0", "a1", "a2", "a3");
        for (String a : actions) {
            assertEquals(expected, policy.logProbability("s", a, actions), EPS);
        }
    }

    @Test
    public void logProbabilityIsNegative() {
        RandomPolicy<String, String> policy = new RandomPolicy<>(new Random(0));
        assertTrue(policy.logProbability("s", "a0", List.of("a0", "a1")) < 0);
    }

    // Lifecycle hooks are no-ops; verify they don't throw.
    @Test
    public void lifecycleHooksDoNotThrow() {
        RandomPolicy<String, String> policy = new RandomPolicy<>(new Random(0));
        policy.reset();
        policy.onStep(1);
        policy.onEpisodeEnd(0);
        policy.setTrainingMode(false);
    }
}
