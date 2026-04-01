package net.davidrobles.rl.agents;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.GreedyPolicy;
import net.davidrobles.rl.valuefunctions.TabularQFunction;
import org.junit.Before;
import org.junit.Test;

public class SARSALambdaTest {

    private static final double EPS = 1e-9;

    private TabularQFunction<String, String> table;
    private SARSALambda<String, String> agent;

    @Before
    public void setUp() {
        // alpha=1.0 for exact arithmetic; gamma=1.0; lambda=0.5
        table = new TabularQFunction<>(1.0);
        agent = new SARSALambda<>(table, new GreedyPolicy<>(table), 1.0, 0.5);
    }

    // -------------------------------------------------------------------------
    // Single-step update (lambda=0.5 with only one traced pair)
    // -------------------------------------------------------------------------

    @Test
    public void terminalStepProducesCorrectUpdate() {
        // Q(s0,a0)=0, r=1, done → target=1; e(s0,a0)=1
        // new Q(s0,a0) = 0 + 1*(0+1*1-0) = 0+1*(1)=1...
        // Wait: table.update(s0,a0, currentQ+tdError*trace) = table.update(s0,a0, 0+1*1=1)
        // update internals: Q = 0 + 1*(1-0) = 1.0
        agent.update("s0", "a0", new StepResult<>("s1", 1.0, true), List.of());
        assertEquals(1.0, table.getValue("s0", "a0"), EPS);
    }

    @Test
    public void nonTerminalStepWithZeroRewardAndZeroNextQ() {
        // Q(s1,a1)=0, r=0 → tdError=0 → no change in Q
        agent.update("s0", "a0", new StepResult<>("s1", 0.0, false), List.of("a1"));
        assertEquals(0.0, table.getValue("s0", "a0"), EPS);
    }

    // -------------------------------------------------------------------------
    // Two-step trace propagation
    // Setup: alpha=1, gamma=1, lambda=0.5
    //
    //  Step 1: s0,a0 → s1, r=0, not done, nextAction=a1 (only option)
    //    tdError = 0 + 1*Q(s1,a1) - Q(s0,a0) = 0
    //    e(s0,a0) = 1  →  no Q change  →  e(s0,a0) decays to 0.5
    //
    //  Step 2: s1,a1 → s2, r=1, done
    //    tdError = 1 + 0 - Q(s1,a1) = 1
    //    e(s1,a1) = 1
    //    Q(s0,a0) += alpha * tdError * e(s0,a0) = 1*1*0.5 = 0.5
    //    Q(s1,a1) += alpha * tdError * e(s1,a1) = 1*1*1.0 = 1.0
    //    traces cleared
    // -------------------------------------------------------------------------

    @Test
    public void tracesPropagateRewardBackwards() {
        agent.update("s0", "a0", new StepResult<>("s1", 0.0, false), List.of("a1"));
        agent.update("s1", "a1", new StepResult<>("s2", 1.0, true), List.of());

        assertEquals(0.5, table.getValue("s0", "a0"), EPS);
        assertEquals(1.0, table.getValue("s1", "a1"), EPS);
    }

    @Test
    public void tracesAreAccumulating() {
        // Visiting s0,a0 twice before terminal should accumulate the trace
        // Step 1: s0,a0 → s0 (self-loop), r=0
        //   tdError=0, e(s0,a0)=1, no Q change, e decays to 0.5
        // Step 2: s0,a0 → s0 (self-loop), r=0
        //   e(s0,a0) += 1 → e=1.5, tdError=0, no Q change, e decays to 0.75
        // Step 3: s0,a0 → done, r=1
        //   tdError=1, e(s0,a0) +=1 → 1.75 but the merge happens before the loop
        //   Actually: step 3 updates e(s0,a0) from 0.75 to 1.75, then Q update
        //   Q(s0,a0) = 0 + 1*1*1.75 = 1.75
        // This uses a self-loop: nextActions=[a0], so greedy picks a0
        agent.update("s0", "a0", new StepResult<>("s0", 0.0, false), List.of("a0"));
        agent.update("s0", "a0", new StepResult<>("s0", 0.0, false), List.of("a0"));
        agent.update("s0", "a0", new StepResult<>("s1", 1.0, true), List.of());

        // e after step1: 1→0.5; after step2: (0.5+1)→1.5→0.75; after step3: (0.75+1)→1.75
        // Q(s0,a0) = 0 + 1*(1.75) = 1.75
        assertEquals(1.75, table.getValue("s0", "a0"), EPS);
    }

    @Test
    public void tracesAreClearedAfterTerminalStep() {
        agent.update("s0", "a0", new StepResult<>("s1", 0.0, false), List.of("a1"));
        agent.update("s1", "a1", new StepResult<>("s2", 1.0, true), List.of());
        // Traces are now cleared. Starting a new episode:
        // A fresh step from s0,a0 with r=1, done should give Q(s0,a0) += 1*(1) = 1.0 more
        // Currently Q(s0,a0)=0.5; after: 0.5 + 1*(1-0.5) = 1.0
        agent.update("s0", "a0", new StepResult<>("s3", 1.0, true), List.of());
        assertEquals(1.0, table.getValue("s0", "a0"), EPS);
    }

    // -------------------------------------------------------------------------
    // Observer
    // -------------------------------------------------------------------------

    @Test
    public void observerNotifiedOnEachUpdate() {
        AtomicInteger count = new AtomicInteger();
        agent.addQFunctionObserver(qf -> count.incrementAndGet());

        agent.update("s0", "a0", new StepResult<>("s1", 1.0, true), List.of());
        agent.update("s0", "a0", new StepResult<>("s1", 1.0, true), List.of());
        assertEquals(2, count.get());
    }

    @Test
    public void duplicateObserverIsRegisteredOnce() {
        AtomicInteger count = new AtomicInteger();
        net.davidrobles.rl.valuefunctions.QFunctionObserver<String, String> o =
                qf -> count.incrementAndGet();
        agent.addQFunctionObserver(o);
        agent.addQFunctionObserver(o);

        agent.update("s0", "a0", new StepResult<>("s1", 1.0, true), List.of());
        assertEquals(1, count.get());
    }

    // -------------------------------------------------------------------------
    // Construction validation
    // -------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void gammaBelowZeroIsRejected() {
        new SARSALambda<>(table, new GreedyPolicy<>(table), -0.1, 0.5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lambdaBelowZeroIsRejected() {
        new SARSALambda<>(table, new GreedyPolicy<>(table), 0.9, -0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lambdaAboveOneIsRejected() {
        new SARSALambda<>(table, new GreedyPolicy<>(table), 0.9, 1.1);
    }

    @Test(expected = NullPointerException.class)
    public void nullTableIsRejected() {
        new SARSALambda<>(null, new GreedyPolicy<>(table), 0.9, 0.5);
    }

    @Test(expected = NullPointerException.class)
    public void nullPolicyIsRejected() {
        new SARSALambda<>(table, null, 0.9, 0.5);
    }
}
