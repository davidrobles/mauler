package net.davidrobles.rl.agents;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.GreedyPolicy;
import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.TabularQFunction;
import org.junit.Before;
import org.junit.Test;

public class QLearningTest {

    private static final double EPS = 1e-9;

    private TabularQFunction<String, String> table;
    private QLearning<String, String> agent;

    @Before
    public void setUp() {
        table = new TabularQFunction<>(0.5);
        agent = new QLearning<>(table, new GreedyPolicy<>(table), 0.9);
    }

    // -------------------------------------------------------------------------
    // Update rule: Q(s,a) ← Q(s,a) + α*(r + γ*maxQ(s',·) − Q(s,a))
    // -------------------------------------------------------------------------

    @Test
    public void updateNonTerminal() {
        table.setValue("s1", "a1", 2.0);
        // target = 0.5 + 0.9*2.0 = 2.3;  new Q = 0 + 0.5*(2.3-0) = 1.15
        agent.update("s0", "a0", new StepResult<>("s1", 0.5, false), List.of("a1"));
        assertEquals(1.15, table.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updateTerminalIgnoresFutureReward() {
        table.setValue("s1", "a1", 100.0); // should be ignored
        // target = 1.0 + 0 = 1.0;  new Q = 0 + 0.5*1.0 = 0.5
        agent.update("s0", "a0", new StepResult<>("s1", 1.0, true), List.of());
        assertEquals(0.5, table.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updatePicksMaxNextQAmongMultipleActions() {
        table.setValue("s1", "a1", 1.0);
        table.setValue("s1", "a2", 5.0); // max
        table.setValue("s1", "a3", 3.0);
        // target = 0 + 0.9*5.0 = 4.5;  new Q = 0 + 0.5*4.5 = 2.25
        agent.update("s0", "a0", new StepResult<>("s1", 0.0, false), List.of("a1", "a2", "a3"));
        assertEquals(2.25, table.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updateWithGammaZeroIgnoresFuture() {
        QLearning<String, String> noDiscount =
                new QLearning<>(table, new GreedyPolicy<>(table), 0.0);
        table.setValue("s1", "a1", 999.0);
        // target = 2.0 + 0*999 = 2.0;  new Q = 0 + 0.5*2.0 = 1.0
        noDiscount.update("s0", "a0", new StepResult<>("s1", 2.0, false), List.of("a1"));
        assertEquals(1.0, table.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updateWithGammaOneAndDoneEqualsReward() {
        QLearning<String, String> fullDiscount =
                new QLearning<>(table, new GreedyPolicy<>(table), 1.0);
        // target = 3.0;  new Q = 0 + 0.5*3.0 = 1.5
        fullDiscount.update("s0", "a0", new StepResult<>("s1", 3.0, true), List.of());
        assertEquals(1.5, table.getValue("s0", "a0"), EPS);
    }

    @Test
    public void updateDoesNotChangeOtherQValues() {
        table.setValue("s0", "a1", 7.0);
        table.setValue("s1", "a1", 2.0);
        agent.update("s0", "a0", new StepResult<>("s1", 1.0, false), List.of("a1"));
        assertEquals(7.0, table.getValue("s0", "a1"), EPS);
    }

    @Test
    public void consecutiveUpdatesAccumulate() {
        // Two steps from s0,a0: first with target 2, then with target 4
        // After step 1: Q(s0,a0) = 0 + 0.5*(2-0) = 1.0
        agent.update("s0", "a0", new StepResult<>("s1", 2.0, true), List.of());
        assertEquals(1.0, table.getValue("s0", "a0"), EPS);

        // After step 2: Q(s0,a0) = 1 + 0.5*(4-1) = 2.5
        agent.update("s0", "a0", new StepResult<>("s1", 4.0, true), List.of());
        assertEquals(2.5, table.getValue("s0", "a0"), EPS);
    }

    // -------------------------------------------------------------------------
    // Off-policy: selectAction delegates to the behavior policy
    // -------------------------------------------------------------------------

    @Test
    public void selectActionDelegatesToPolicy() {
        table.setValue("s0", "a1", 10.0); // greedy picks a1
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
    public void observerReceivesCurrentQFunction() {
        QFunction<String, String>[] captured = new QFunction[1];
        agent.addQFunctionObserver(qf -> captured[0] = qf);

        agent.update("s0", "a0", new StepResult<>("s1", 2.0, true), List.of());

        assertNotNull(captured[0]);
        assertEquals(1.0, captured[0].getValue("s0", "a0"), EPS); // 0 + 0.5*2 = 1.0
    }

    @Test
    public void duplicateObserverIsRegisteredOnce() {
        AtomicInteger count = new AtomicInteger();
        Runnable obs = count::incrementAndGet;
        // Same lambda reference
        net.davidrobles.rl.valuefunctions.QFunctionObserver<String, String> o = qf -> obs.run();
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
        new QLearning<>(table, new GreedyPolicy<>(table), -0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gammaAboveOneIsRejected() {
        new QLearning<>(table, new GreedyPolicy<>(table), 1.1);
    }

    @Test(expected = NullPointerException.class)
    public void nullTableIsRejected() {
        new QLearning<>(null, new GreedyPolicy<>(table), 0.9);
    }

    @Test(expected = NullPointerException.class)
    public void nullPolicyIsRejected() {
        new QLearning<>(table, null, 0.9);
    }
}
