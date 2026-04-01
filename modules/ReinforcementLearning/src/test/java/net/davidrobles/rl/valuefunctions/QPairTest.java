package net.davidrobles.rl.valuefunctions;

import static org.junit.Assert.*;

import org.junit.Test;

public class QPairTest {

    @Test
    public void accessors() {
        QPair<String, Integer> pair = new QPair<>("s0", 1);
        assertEquals("s0", pair.state());
        assertEquals(Integer.valueOf(1), pair.action());
    }

    @Test
    public void equalPairsAreEqual() {
        QPair<String, String> a = new QPair<>("s0", "up");
        QPair<String, String> b = new QPair<>("s0", "up");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void differentStateMeansNotEqual() {
        assertNotEquals(new QPair<>("s0", "up"), new QPair<>("s1", "up"));
    }

    @Test
    public void differentActionMeansNotEqual() {
        assertNotEquals(new QPair<>("s0", "up"), new QPair<>("s0", "down"));
    }

    @Test
    public void nullComponentsAreSupported() {
        QPair<String, String> pair = new QPair<>(null, null);
        assertNull(pair.state());
        assertNull(pair.action());
        assertEquals(pair, new QPair<>(null, null));
    }

    @Test
    public void toStringContainsBothComponents() {
        String s = new QPair<>("s0", "up").toString();
        assertTrue(s.contains("s0"));
        assertTrue(s.contains("up"));
    }
}
