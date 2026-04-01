package net.davidrobles.rl.prediction;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.RandomPolicy;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import org.junit.Before;
import org.junit.Test;

public class TDLambdaTest {

    private static final double EPS = 1e-9;

    private TabularVFunction<String> table;
    private TDLambda<String, String> tdl;

    @Before
    public void setUp() {
        // alpha=1.0 for exact arithmetic; gamma=1.0; lambda=0.5
        table = new TabularVFunction<>(1.0);
        tdl = new TDLambda<>(table, new RandomPolicy<>(new java.util.Random(0)), 1.0, 0.5);
    }

    // -------------------------------------------------------------------------
    // Single-step (behaves like TD(0) when trace covers one state)
    // -------------------------------------------------------------------------

    @Test
    public void singleTerminalStep() {
        // V(s0)=0, r=1, done → target=1, e(s0)=1
        // table.update(s0, 0+1*1=1) → V(s0) = 0+1*(1-0) = 1.0
        tdl.observe("s0", new StepResult<>("s1", 1.0, true));
        assertEquals(1.0, table.getValue("s0"), EPS);
    }

    // -------------------------------------------------------------------------
    // Two-step trace propagation
    // alpha=1, gamma=1, lambda=0.5
    //
    //  Step 1: observe(s0, {s1, 0.0, false})
    //    V(s1)=0 → tdError = 0+1*0 - V(s0) = 0
    //    e(s0)=1 → V(s0) unchanged → e(s0) decays to 0.5
    //
    //  Step 2: observe(s1, {s2, 1.0, true})
    //    V(s2)=0 → tdError = 1+0 - V(s1) = 1
    //    e(s1)=1
    //    V(s0) += alpha*tdError*e(s0) = 1*1*0.5 = 0.5
    //    V(s1) += alpha*tdError*e(s1) = 1*1*1.0 = 1.0
    //    traces cleared
    // -------------------------------------------------------------------------

    @Test
    public void tracesPropagateRewardBackwards() {
        tdl.observe("s0", new StepResult<>("s1", 0.0, false));
        tdl.observe("s1", new StepResult<>("s2", 1.0, true));

        assertEquals(0.5, table.getValue("s0"), EPS);
        assertEquals(1.0, table.getValue("s1"), EPS);
    }

    @Test
    public void lambda0BehavesLikeTD0() {
        TabularVFunction<String> localTable = new TabularVFunction<>(1.0);
        TDLambda<String, String> td0Like =
                new TDLambda<>(localTable, new RandomPolicy<>(new java.util.Random(0)), 1.0, 0.0);

        // lambda=0: traces decay to 0 after each step, so only the current state gets credit.
        // Step 1: V(s0) stays 0 (tdError=0 since V(s1)=0); e(s0) decays to 0
        // Step 2: V(s1) = 0 + 1*(1-0) = 1.0; e(s0) is already 0 → V(s0) unchanged
        td0Like.observe("s0", new StepResult<>("s1", 0.0, false));
        td0Like.observe("s1", new StepResult<>("s2", 1.0, true));

        assertEquals(0.0, localTable.getValue("s0"), EPS);
        assertEquals(1.0, localTable.getValue("s1"), EPS);
    }

    @Test
    public void tracesAreClearedAfterTerminalStep() {
        tdl.observe("s0", new StepResult<>("s1", 0.0, false));
        tdl.observe("s1", new StepResult<>("s2", 1.0, true));
        // Traces cleared. New episode — only s0 should be updated by this single step
        tdl.observe("s0", new StepResult<>("s3", 1.0, true));
        // V(s0) was 0.5; update: V(s0) = 0.5 + 1*(1-0.5) = 1.0
        assertEquals(1.0, table.getValue("s0"), EPS);
    }

    // -------------------------------------------------------------------------
    // update() delegates to observe()
    // -------------------------------------------------------------------------

    @Test
    public void updateDelegatesToObserve() {
        tdl.update("s0", "any-action", new StepResult<>("s1", 1.0, true), List.of());
        assertEquals(1.0, table.getValue("s0"), EPS);
    }

    // -------------------------------------------------------------------------
    // Observer
    // -------------------------------------------------------------------------

    @Test
    public void observerNotifiedOnEachObserve() {
        AtomicInteger count = new AtomicInteger();
        tdl.addVFunctionObserver(vf -> count.incrementAndGet());

        tdl.observe("s0", new StepResult<>("s1", 1.0, true));
        tdl.observe("s0", new StepResult<>("s1", 1.0, true));
        assertEquals(2, count.get());
    }

    @Test
    public void duplicateObserverIsRegisteredOnce() {
        AtomicInteger count = new AtomicInteger();
        net.davidrobles.rl.valuefunctions.VFunctionObserver<String> o =
                vf -> count.incrementAndGet();
        tdl.addVFunctionObserver(o);
        tdl.addVFunctionObserver(o);

        tdl.observe("s0", new StepResult<>("s1", 1.0, true));
        assertEquals(1, count.get());
    }

    // -------------------------------------------------------------------------
    // Construction validation
    // -------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void gammaBelowZeroIsRejected() {
        new TDLambda<>(table, new RandomPolicy<>(new java.util.Random(0)), -0.1, 0.5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lambdaBelowZeroIsRejected() {
        new TDLambda<>(table, new RandomPolicy<>(new java.util.Random(0)), 0.9, -0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lambdaAboveOneIsRejected() {
        new TDLambda<>(table, new RandomPolicy<>(new java.util.Random(0)), 0.9, 1.1);
    }

    @Test(expected = NullPointerException.class)
    public void nullTableIsRejected() {
        new TDLambda<>(null, new RandomPolicy<>(new java.util.Random(0)), 0.9, 0.5);
    }

    @Test(expected = NullPointerException.class)
    public void nullPolicyIsRejected() {
        new TDLambda<>(table, null, 0.9, 0.5);
    }
}
