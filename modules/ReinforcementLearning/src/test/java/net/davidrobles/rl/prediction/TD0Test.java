package net.davidrobles.rl.prediction;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.davidrobles.rl.StepResult;
import net.davidrobles.rl.policies.RandomPolicy;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.rl.valuefunctions.VFunction;
import org.junit.Before;
import org.junit.Test;

public class TD0Test {

    private static final double EPS = 1e-9;

    private TabularVFunction<String> table;
    private TD0<String, String> td0;

    @Before
    public void setUp() {
        table = new TabularVFunction<>(0.5);
        td0 = new TD0<>(table, new RandomPolicy<>(new java.util.Random(0)), 0.9);
    }

    // -------------------------------------------------------------------------
    // observe(): V(s) ← V(s) + α*(r + γ*V(s') − V(s))
    // -------------------------------------------------------------------------

    @Test
    public void observeNonTerminal() {
        table.setValue("s1", 2.0);
        // target = 1.0 + 0.9*2.0 = 2.8;  new V(s0) = 0 + 0.5*2.8 = 1.4
        td0.observe("s0", new StepResult<>("s1", 1.0, false));
        assertEquals(1.4, table.getValue("s0"), EPS);
    }

    @Test
    public void observeTerminalIgnoresFutureValue() {
        table.setValue("s1", 100.0); // should be ignored
        // target = 2.0 + 0 = 2.0;  new V(s0) = 0 + 0.5*2.0 = 1.0
        td0.observe("s0", new StepResult<>("s1", 2.0, true));
        assertEquals(1.0, table.getValue("s0"), EPS);
    }

    @Test
    public void observeWithGammaZeroIgnoresFuture() {
        TD0<String, String> noDiscount =
                new TD0<>(table, new RandomPolicy<>(new java.util.Random(0)), 0.0);
        table.setValue("s1", 999.0);
        // target = 3.0 + 0 = 3.0;  new V = 0 + 0.5*3.0 = 1.5
        noDiscount.observe("s0", new StepResult<>("s1", 3.0, false));
        assertEquals(1.5, table.getValue("s0"), EPS);
    }

    @Test
    public void observeDoesNotAffectOtherStates() {
        table.setValue("s1", 7.0);
        td0.observe("s0", new StepResult<>("s1", 0.0, false));
        assertEquals(7.0, table.getValue("s1"), EPS);
    }

    // -------------------------------------------------------------------------
    // update() delegates to observe() — Evaluator interface default
    // -------------------------------------------------------------------------

    @Test
    public void updateDelegatesToObserve() {
        table.setValue("s1", 2.0);
        // Same call as observeNonTerminal above but via update()
        td0.update("s0", "ignored-action", new StepResult<>("s1", 1.0, false), List.of("a0"));
        assertEquals(1.4, table.getValue("s0"), EPS);
    }

    // -------------------------------------------------------------------------
    // selectAction delegates to the policy
    // -------------------------------------------------------------------------

    @Test
    public void selectActionDelegatesToPolicy() {
        // RandomPolicy picks from the given list; just verify it returns one of them
        String selected = td0.selectAction("s0", List.of("a0", "a1"));
        assertTrue(List.of("a0", "a1").contains(selected));
    }

    // -------------------------------------------------------------------------
    // Observer
    // -------------------------------------------------------------------------

    @Test
    public void observerNotifiedOnEachObserve() {
        AtomicInteger count = new AtomicInteger();
        td0.addVFunctionObserver(vf -> count.incrementAndGet());

        td0.observe("s0", new StepResult<>("s1", 1.0, true));
        td0.observe("s0", new StepResult<>("s1", 1.0, true));
        assertEquals(2, count.get());
    }

    @Test
    public void observerReceivesUpdatedVFunction() {
        VFunction<String>[] captured = new VFunction[1];
        td0.addVFunctionObserver(vf -> captured[0] = vf);

        td0.observe("s0", new StepResult<>("s1", 2.0, true));

        assertNotNull(captured[0]);
        assertEquals(1.0, captured[0].getValue("s0"), EPS); // 0 + 0.5*2 = 1.0
    }

    @Test
    public void duplicateObserverIsRegisteredOnce() {
        AtomicInteger count = new AtomicInteger();
        net.davidrobles.rl.valuefunctions.VFunctionObserver<String> o =
                vf -> count.incrementAndGet();
        td0.addVFunctionObserver(o);
        td0.addVFunctionObserver(o);

        td0.observe("s0", new StepResult<>("s1", 1.0, true));
        assertEquals(1, count.get());
    }

    // -------------------------------------------------------------------------
    // Construction validation
    // -------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void gammaBelowZeroIsRejected() {
        new TD0<>(table, new RandomPolicy<>(new java.util.Random(0)), -0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gammaAboveOneIsRejected() {
        new TD0<>(table, new RandomPolicy<>(new java.util.Random(0)), 1.1);
    }

    @Test(expected = NullPointerException.class)
    public void nullTableIsRejected() {
        new TD0<>(null, new RandomPolicy<>(new java.util.Random(0)), 0.9);
    }

    @Test(expected = NullPointerException.class)
    public void nullPolicyIsRejected() {
        new TD0<>(table, null, 0.9);
    }
}
