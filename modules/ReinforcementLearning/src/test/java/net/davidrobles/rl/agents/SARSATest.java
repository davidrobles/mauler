package net.davidrobles.rl.agents;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.GreedyPolicy;
import net.davidrobles.rl.valuefunctions.TabularQFunction;
import org.junit.Before;
import org.junit.Test;

public class SARSATest {

    private static final double EPS = 1e-9;

    private TabularQFunction<String, String> table;
    private SARSA<String, String> agent;

    @Before
    public void setUp() {
        table = new TabularQFunction<>(0.5);
        agent = new SARSA<>(table, new GreedyPolicy<>(table), 0.9);
    }

    // -------------------------------------------------------------------------
    // Update rule: Q(s,a) ← Q(s,a) + α*(r + γ*Q(s',a') − Q(s,a))
    // where a' is the on-policy next action
    // -------------------------------------------------------------------------

    @Test
    public void updateNonTerminal() {
        // Greedy picks a1 (only option in nextActions)
        table.setValue("s1", "a1", 2.0);
        // target = 0.5 + 0.9*2.0 = 2.3;  new Q = 0 + 0.5*2.3 = 1.15
        agent.update("s0", "a0", new StepResult<>("s1", 0.5, false), List.of("a1"));
        assertEquals(1.15, table.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updateTerminalSetsNextQToZero() {
        table.setValue("s1", "a1", 100.0); // ignored when done
        // target = 1.0;  new Q = 0 + 0.5*1.0 = 0.5
        agent.update("s0", "a0", new StepResult<>("s1", 1.0, true), List.of());
        assertEquals(0.5, table.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updateUsesOnPolicyNextActionNotMax() {
        // a1=2.0, a2=5.0 — Q-learning would use a2 (max), SARSA uses whichever greedy picks.
        // With GreedyPolicy, greedy also picks a2. So target = 0 + 0.9*5.0 = 4.5
        table.setValue("s1", "a1", 2.0);
        table.setValue("s1", "a2", 5.0);
        agent.update("s0", "a0", new StepResult<>("s1", 0.0, false), List.of("a1", "a2"));
        // new Q = 0 + 0.5*4.5 = 2.25
        assertEquals(2.25, table.getValue("s0", "a0"), EPS);
    }

    // -------------------------------------------------------------------------
    // On-policy coupling: the action used in the TD target is also returned
    // by the next selectAction call (SARSA coupling invariant)
    // -------------------------------------------------------------------------

    @Test
    public void nextSelectActionReturnsPreSelectedAction() {
        // a2 has higher Q → greedy picks a2
        table.setValue("s1", "a1", 1.0);
        table.setValue("s1", "a2", 9.0);

        agent.update("s0", "a0", new StepResult<>("s1", 0.0, false), List.of("a1", "a2"));

        // The pre-selected action must be returned (a2, greedy choice)
        assertEquals("a2", agent.selectAction("s1", List.of("a1", "a2")));
    }

    @Test
    public void afterPreSelectedActionIsConsumedPolicyIsConsulted() {
        table.setValue("s1", "a2", 9.0); // greedy at s1 = a2
        table.setValue("s2", "a0", 5.0); // greedy at s2 = a0

        agent.update("s0", "a0", new StepResult<>("s1", 0.0, false), List.of("a1", "a2"));

        // First call consumes pre-selected action
        assertEquals("a2", agent.selectAction("s1", List.of("a1", "a2")));
        // Second call consults the policy normally (greedy at s2 → a0)
        assertEquals("a0", agent.selectAction("s2", List.of("a0", "a1")));
    }

    @Test
    public void terminalStepClearsPreSelectedAction() {
        table.setValue("s1", "a2", 9.0);
        // Terminal step: nextAction should be cleared
        agent.update("s0", "a0", new StepResult<>("s1", 1.0, true), List.of());

        // Next selectAction consults policy (greedy at s0 → a0 since Q(s0,a0) is highest)
        table.setValue("s0", "a1", 3.0);
        assertEquals("a1", agent.selectAction("s0", List.of("a0", "a1")));
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
        new SARSA<>(table, new GreedyPolicy<>(table), -0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gammaAboveOneIsRejected() {
        new SARSA<>(table, new GreedyPolicy<>(table), 1.1);
    }

    @Test(expected = NullPointerException.class)
    public void nullTableIsRejected() {
        new SARSA<>(null, new GreedyPolicy<>(table), 0.9);
    }

    @Test(expected = NullPointerException.class)
    public void nullPolicyIsRejected() {
        new SARSA<>(table, null, 0.9);
    }
}
