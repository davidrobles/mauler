package net.davidrobles.rl.valuefunctions;

import static org.junit.Assert.*;

import org.junit.Test;

public class TabularQFunctionTest {

    private static final double EPS = 1e-9;

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    @Test
    public void defaultValueIsZero() {
        TabularQFunction<String, String> q = new TabularQFunction<>(0.5);
        assertEquals(0.0, q.getValue("s0", "a0"), EPS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void alphaZeroIsRejected() {
        new TabularQFunction<String, String>(0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void alphaNegativeIsRejected() {
        new TabularQFunction<String, String>(-0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void alphaAboveOneIsRejected() {
        new TabularQFunction<String, String>(1.1);
    }

    @Test
    public void alphaOneIsAccepted() {
        assertNotNull(new TabularQFunction<String, String>(1.0));
    }

    // -------------------------------------------------------------------------
    // setValue / getValue
    // -------------------------------------------------------------------------

    @Test
    public void setValueAndGetValue() {
        TabularQFunction<String, String> q = new TabularQFunction<>(0.5);
        q.setValue("s0", "a0", 3.14);
        assertEquals(3.14, q.getValue("s0", "a0"), EPS);
    }

    @Test
    public void setValueOverwrites() {
        TabularQFunction<String, String> q = new TabularQFunction<>(0.5);
        q.setValue("s0", "a0", 1.0);
        q.setValue("s0", "a0", 9.0);
        assertEquals(9.0, q.getValue("s0", "a0"), EPS);
    }

    @Test
    public void differentStateActionPairsAreIndependent() {
        TabularQFunction<String, String> q = new TabularQFunction<>(0.5);
        q.setValue("s0", "a0", 1.0);
        q.setValue("s0", "a1", 2.0);
        q.setValue("s1", "a0", 3.0);
        assertEquals(1.0, q.getValue("s0", "a0"), EPS);
        assertEquals(2.0, q.getValue("s0", "a1"), EPS);
        assertEquals(3.0, q.getValue("s1", "a0"), EPS);
        assertEquals(0.0, q.getValue("s1", "a1"), EPS); // never set
    }

    // -------------------------------------------------------------------------
    // update — Q(s,a) ← Q(s,a) + α * (target − Q(s,a))
    // -------------------------------------------------------------------------

    @Test
    public void updateFromZeroWithAlphaHalf() {
        TabularQFunction<String, String> q = new TabularQFunction<>(0.5);
        // Q(s0,a0)=0, target=2  →  new Q = 0 + 0.5*(2-0) = 1.0
        q.update("s0", "a0", 2.0);
        assertEquals(1.0, q.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updateFromNonZeroValue() {
        TabularQFunction<String, String> q = new TabularQFunction<>(0.5);
        q.setValue("s0", "a0", 1.0);
        // Q=1, target=3  →  new Q = 1 + 0.5*(3-1) = 2.0
        q.update("s0", "a0", 3.0);
        assertEquals(2.0, q.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updateWithAlphaOneConvergesInOneStep() {
        TabularQFunction<String, String> q = new TabularQFunction<>(1.0);
        q.setValue("s0", "a0", 0.5);
        q.update("s0", "a0", 7.0);
        assertEquals(7.0, q.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updateDoesNotAffectOtherPairs() {
        TabularQFunction<String, String> q = new TabularQFunction<>(0.5);
        q.setValue("s0", "a1", 9.0);
        q.update("s0", "a0", 2.0);
        assertEquals(9.0, q.getValue("s0", "a1"), EPS);
    }

    @Test
    public void repeatedUpdatesConvergeToTarget() {
        TabularQFunction<String, String> q = new TabularQFunction<>(0.1);
        double target = 5.0;
        for (int i = 0; i < 200; i++) {
            q.update("s0", "a0", target);
        }
        assertEquals(target, q.getValue("s0", "a0"), 0.01);
    }
}
