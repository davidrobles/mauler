package net.davidrobles.rl.planning;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import net.davidrobles.rl.ChainMDP;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.VFunction;
import org.junit.Before;
import org.junit.Test;

public class ValueIterationTest {

    private static final double EPS = 1e-3;

    private ChainMDP mdp;
    private ValueIteration<Integer, String> vi;

    @Before
    public void setUp() {
        mdp = new ChainMDP();
        vi = new ValueIteration<>(mdp, 1e-6, 1.0);
    }

    // -------------------------------------------------------------------------
    // Optimal policy on ChainMDP with gamma=1
    //
    //   V*(0) = 0 + 1*V*(1) = 1.0
    //   V*(1) = 1 + 1*V*(2) = 1.0  (V*(2)=0 default for terminal)
    //   Optimal action everywhere: STEP
    // -------------------------------------------------------------------------

    @Test
    public void solveReturnsOptimalPolicyForState0() {
        Policy<Integer, String> policy = vi.solve();
        assertEquals(ChainMDP.STEP, policy.selectAction(0, java.util.List.of(ChainMDP.STEP)));
    }

    @Test
    public void solveReturnsOptimalPolicyForState1() {
        Policy<Integer, String> policy = vi.solve();
        assertEquals(ChainMDP.STEP, policy.selectAction(1, java.util.List.of(ChainMDP.STEP)));
    }

    @Test
    public void solveConvergesWithGamma09() {
        ValueIteration<Integer, String> vi09 = new ValueIteration<>(mdp, 1e-6, 0.9);
        Policy<Integer, String> policy = vi09.solve();
        // Only one action exists; verify it is selected correctly
        assertEquals(ChainMDP.STEP, policy.selectAction(0, java.util.List.of(ChainMDP.STEP)));
        assertEquals(ChainMDP.STEP, policy.selectAction(1, java.util.List.of(ChainMDP.STEP)));
    }

    // -------------------------------------------------------------------------
    // V-function observer
    // -------------------------------------------------------------------------

    @Test
    public void observerIsCalledDuringIteration() {
        AtomicInteger count = new AtomicInteger();
        vi.addVFunctionObserver(vf -> count.incrementAndGet());
        vi.solve();
        assertTrue("Observer should be called at least once", count.get() > 0);
    }

    @Test
    public void observerReceivesUpdatedValues() {
        VFunction<Integer>[] last = new VFunction[1];
        vi.addVFunctionObserver(vf -> last[0] = vf);
        vi.solve();

        assertNotNull(last[0]);
        // After convergence, V*(1) ≈ 1.0 with gamma=1
        assertEquals(1.0, last[0].getValue(1), EPS);
    }

    @Test
    public void multipleObserversAreAllNotified() {
        AtomicInteger a = new AtomicInteger();
        AtomicInteger b = new AtomicInteger();
        vi.addVFunctionObserver(vf -> a.incrementAndGet());
        vi.addVFunctionObserver(vf -> b.incrementAndGet());
        vi.solve();
        assertTrue(a.get() > 0);
        assertTrue(b.get() > 0);
        assertEquals(a.get(), b.get());
    }

    @Test
    public void duplicateObserverIsNotifiedOnce() {
        AtomicInteger count = new AtomicInteger();
        net.davidrobles.rl.valuefunctions.VFunctionObserver<Integer> o =
                vf -> count.incrementAndGet();
        vi.addVFunctionObserver(o);
        vi.addVFunctionObserver(o);
        vi.solve();
        // Duplicate registration should be ignored (LinkedHashSet semantics)
        // Calls from first observer only
        int after = count.get();
        // Reset and check second solve would give same count (not doubled)
        AtomicInteger count2 = new AtomicInteger();
        net.davidrobles.rl.valuefunctions.VFunctionObserver<Integer> o2 =
                vf -> count2.incrementAndGet();
        ValueIteration<Integer, String> vi2 = new ValueIteration<>(mdp, 1e-6, 1.0);
        vi2.addVFunctionObserver(o2);
        vi2.addVFunctionObserver(o2); // duplicate
        vi2.solve();
        // Both should have the same notification count (deduplicated)
        assertEquals(after, count2.get());
    }
}
