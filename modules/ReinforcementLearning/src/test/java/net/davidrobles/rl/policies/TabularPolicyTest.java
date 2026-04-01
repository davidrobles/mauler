package net.davidrobles.rl.policies;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class TabularPolicyTest {

    private TabularPolicy<String, String> policy;

    @Before
    public void setUp() {
        policy = new TabularPolicy<>();
    }

    // -------------------------------------------------------------------------
    // getAction / setAction / selectAction
    // -------------------------------------------------------------------------

    @Test
    public void getActionReturnsSetAction() {
        policy.setAction("s0", "a1");
        assertEquals("a1", policy.getAction("s0"));
    }

    @Test
    public void selectActionReturnsSetAction() {
        policy.setAction("s0", "a2");
        assertEquals("a2", policy.selectAction("s0", List.of("a0", "a1", "a2")));
    }

    @Test
    public void setActionOverwrites() {
        policy.setAction("s0", "a0");
        policy.setAction("s0", "a1");
        assertEquals("a1", policy.getAction("s0"));
        assertEquals("a1", policy.selectAction("s0", List.of("a0", "a1")));
    }

    @Test
    public void differentStatesAreIndependent() {
        policy.setAction("s0", "up");
        policy.setAction("s1", "down");
        assertEquals("up", policy.selectAction("s0", List.of("up", "down")));
        assertEquals("down", policy.selectAction("s1", List.of("up", "down")));
    }

    @Test
    public void unmappedStateReturnsNull() {
        // TabularPolicy returns null for states not in the map.
        assertNull(policy.getAction("unknown"));
        assertNull(policy.selectAction("unknown", List.of("a0")));
    }

    // -------------------------------------------------------------------------
    // logProbability
    // -------------------------------------------------------------------------

    @Test
    public void logProbabilityForStoredActionIsZero() {
        policy.setAction("s0", "a1");
        assertEquals(0.0, policy.logProbability("s0", "a1", List.of("a0", "a1")), 1e-9);
    }

    @Test
    public void logProbabilityForOtherActionIsNegativeInfinity() {
        policy.setAction("s0", "a1");
        assertEquals(
                Double.NEGATIVE_INFINITY,
                policy.logProbability("s0", "a0", List.of("a0", "a1")),
                1e-9);
    }
}
