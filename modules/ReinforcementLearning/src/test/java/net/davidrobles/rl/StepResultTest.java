package net.davidrobles.rl;

import static org.junit.Assert.*;

import org.junit.Test;

public class StepResultTest {

    // -------------------------------------------------------------------------
    // Accessor methods (record components are exposed as methods, not fields)
    // -------------------------------------------------------------------------

    @Test
    public void accessors() {
        StepResult<String> result = new StepResult<>("next", 2.5, true);
        assertEquals("next", result.nextState());
        assertEquals(2.5, result.reward(), 1e-9);
        assertTrue(result.done());
    }

    @Test
    public void nonTerminalResult() {
        StepResult<Integer> result = new StepResult<>(42, 0.0, false);
        assertEquals(Integer.valueOf(42), result.nextState());
        assertEquals(0.0, result.reward(), 1e-9);
        assertFalse(result.done());
    }

    // -------------------------------------------------------------------------
    // Record equality and hashCode
    // -------------------------------------------------------------------------

    @Test
    public void equalResultsAreEqual() {
        StepResult<String> a = new StepResult<>("s", 1.0, false);
        StepResult<String> b = new StepResult<>("s", 1.0, false);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void differentNextStateMeansNotEqual() {
        assertNotEquals(new StepResult<>("s0", 1.0, false), new StepResult<>("s1", 1.0, false));
    }

    @Test
    public void differentRewardMeansNotEqual() {
        assertNotEquals(new StepResult<>("s0", 1.0, false), new StepResult<>("s0", 2.0, false));
    }

    @Test
    public void differentDoneMeansNotEqual() {
        assertNotEquals(new StepResult<>("s0", 1.0, false), new StepResult<>("s0", 1.0, true));
    }

    @Test
    public void toStringContainsComponents() {
        StepResult<String> r = new StepResult<>("goal", 3.0, true);
        String s = r.toString();
        assertTrue(s.contains("goal"));
        assertTrue(s.contains("3.0"));
    }
}
