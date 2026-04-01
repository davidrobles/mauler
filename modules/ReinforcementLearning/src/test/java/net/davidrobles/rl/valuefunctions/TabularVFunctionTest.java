package net.davidrobles.rl.valuefunctions;

import static org.junit.Assert.*;

import org.junit.Test;

public class TabularVFunctionTest {

    private static final double EPS = 1e-9;

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    @Test
    public void defaultValueIsZero() {
        TabularVFunction<String> v = new TabularVFunction<>(0.5);
        assertEquals(0.0, v.getValue("unseen"), EPS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void alphaZeroIsRejected() {
        new TabularVFunction<String>(0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void alphaNegativeIsRejected() {
        new TabularVFunction<String>(-0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void alphaAboveOneIsRejected() {
        new TabularVFunction<String>(1.1);
    }

    @Test
    public void alphaOneIsAccepted() {
        TabularVFunction<String> v = new TabularVFunction<>(1.0);
        assertNotNull(v);
    }

    // -------------------------------------------------------------------------
    // setValue / getValue
    // -------------------------------------------------------------------------

    @Test
    public void setValueAndGetValue() {
        TabularVFunction<String> v = new TabularVFunction<>(0.5);
        v.setValue("s0", 3.14);
        assertEquals(3.14, v.getValue("s0"), EPS);
    }

    @Test
    public void setValueOverwrites() {
        TabularVFunction<String> v = new TabularVFunction<>(0.5);
        v.setValue("s0", 1.0);
        v.setValue("s0", 2.0);
        assertEquals(2.0, v.getValue("s0"), EPS);
    }

    @Test
    public void setValueDoesNotAffectOtherStates() {
        TabularVFunction<String> v = new TabularVFunction<>(0.5);
        v.setValue("s0", 5.0);
        assertEquals(0.0, v.getValue("s1"), EPS);
    }

    // -------------------------------------------------------------------------
    // update — TD step: V(s) ← V(s) + α * (target − V(s))
    // -------------------------------------------------------------------------

    @Test
    public void updateFromZeroWithAlphaHalf() {
        TabularVFunction<String> v = new TabularVFunction<>(0.5);
        // V(s0)=0, target=2.0  →  new V(s0) = 0 + 0.5*(2-0) = 1.0
        v.update("s0", 2.0);
        assertEquals(1.0, v.getValue("s0"), EPS);
    }

    @Test
    public void updateFromNonZeroValue() {
        TabularVFunction<String> v = new TabularVFunction<>(0.5);
        v.setValue("s0", 1.0);
        // V(s0)=1, target=3  →  new V(s0) = 1 + 0.5*(3-1) = 2.0
        v.update("s0", 3.0);
        assertEquals(2.0, v.getValue("s0"), EPS);
    }

    @Test
    public void updateWithAlphaOneConvergesInOneStep() {
        TabularVFunction<String> v = new TabularVFunction<>(1.0);
        v.setValue("s0", 0.5);
        // alpha=1: V(s0) = V(s0) + 1*(target-V(s0)) = target
        v.update("s0", 7.0);
        assertEquals(7.0, v.getValue("s0"), EPS);
    }

    @Test
    public void updateDoesNotAffectOtherStates() {
        TabularVFunction<String> v = new TabularVFunction<>(0.5);
        v.setValue("s1", 9.0);
        v.update("s0", 2.0);
        assertEquals(9.0, v.getValue("s1"), EPS);
    }

    // -------------------------------------------------------------------------
    // No-arg constructor — alpha=1.0, intended for DP planning
    // -------------------------------------------------------------------------

    @Test
    public void noArgConstructorActsAsDirectAssignment() {
        TabularVFunction<String> v = new TabularVFunction<>();
        v.update("s0", 5.0); // alpha=1: V(s0) = 5.0
        assertEquals(5.0, v.getValue("s0"), EPS);
    }
}
