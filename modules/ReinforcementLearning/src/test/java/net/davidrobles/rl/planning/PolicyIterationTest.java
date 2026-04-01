package net.davidrobles.rl.planning;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import net.davidrobles.rl.ChainMDP;
import net.davidrobles.rl.policies.Policy;
import org.junit.Before;
import org.junit.Test;

public class PolicyIterationTest {

    private ChainMDP mdp;
    private PolicyIteration<Integer, String> pi;

    @Before
    public void setUp() {
        mdp = new ChainMDP();
        pi = new PolicyIteration<>(mdp, 1e-6, 1.0);
    }

    // -------------------------------------------------------------------------
    // Optimal policy on ChainMDP with gamma=1
    //
    //   Only one action per state → policy always converges to STEP
    // -------------------------------------------------------------------------

    @Test
    public void solveReturnsOptimalPolicyForState0() {
        Policy<Integer, String> policy = pi.solve();
        assertEquals(ChainMDP.STEP, policy.selectAction(0, java.util.List.of(ChainMDP.STEP)));
    }

    @Test
    public void solveReturnsOptimalPolicyForState1() {
        Policy<Integer, String> policy = pi.solve();
        assertEquals(ChainMDP.STEP, policy.selectAction(1, java.util.List.of(ChainMDP.STEP)));
    }

    @Test
    public void solveWithGamma09ReturnsCorrectPolicy() {
        PolicyIteration<Integer, String> pi09 = new PolicyIteration<>(mdp, 1e-6, 0.9);
        Policy<Integer, String> policy = pi09.solve();
        assertEquals(ChainMDP.STEP, policy.selectAction(0, java.util.List.of(ChainMDP.STEP)));
        assertEquals(ChainMDP.STEP, policy.selectAction(1, java.util.List.of(ChainMDP.STEP)));
    }

    // -------------------------------------------------------------------------
    // V-function observer
    // -------------------------------------------------------------------------

    @Test
    public void observerIsCalledDuringIteration() {
        AtomicInteger count = new AtomicInteger();
        pi.addVFunctionObserver(vf -> count.incrementAndGet());
        pi.solve();
        assertTrue("Observer should be called at least once", count.get() > 0);
    }

    @Test
    public void multipleObserversAreAllNotified() {
        AtomicInteger a = new AtomicInteger();
        AtomicInteger b = new AtomicInteger();
        pi.addVFunctionObserver(vf -> a.incrementAndGet());
        pi.addVFunctionObserver(vf -> b.incrementAndGet());
        pi.solve();
        assertTrue(a.get() > 0);
        assertEquals(a.get(), b.get());
    }

    @Test
    public void duplicateObserverIsNotifiedOnce() {
        AtomicInteger count = new AtomicInteger();
        net.davidrobles.rl.valuefunctions.VFunctionObserver<Integer> o =
                vf -> count.incrementAndGet();
        pi.addVFunctionObserver(o);
        pi.addVFunctionObserver(o); // duplicate
        pi.solve();
        int first = count.get();

        // A fresh instance with a single observer should have the same count
        PolicyIteration<Integer, String> pi2 = new PolicyIteration<>(mdp, 1e-6, 1.0);
        AtomicInteger count2 = new AtomicInteger();
        pi2.addVFunctionObserver(vf -> count2.incrementAndGet());
        pi2.solve();
        assertEquals(first, count2.get());
    }
}
