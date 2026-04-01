package net.davidrobles.rl;

import static org.junit.Assert.*;

import java.util.Set;
import org.junit.Test;

public class MDPUtilTest {

    private final ChainMDP mdp = new ChainMDP();

    @Test
    public void nextStatesForNonTerminalState() {
        Set<Integer> next = MDPUtil.getNextStates(mdp, 0);
        assertEquals(Set.of(1), next);
    }

    @Test
    public void nextStatesForStateBeforeTerminal() {
        Set<Integer> next = MDPUtil.getNextStates(mdp, 1);
        assertEquals(Set.of(2), next);
    }

    @Test
    public void nextStatesForTerminalStateIsEmpty() {
        // State 2 is terminal and has no actions → no next states
        Set<Integer> next = MDPUtil.getNextStates(mdp, 2);
        assertTrue(next.isEmpty());
    }

    @Test
    public void nextStatesUnionAcrossAllActions() {
        // MDP with two actions from state 0 leading to different states
        MDP<Integer, String> branching =
                new MDP<>() {
                    @Override
                    public Integer getStartState() {
                        return 0;
                    }

                    @Override
                    public java.util.Collection<String> getActions(Integer s) {
                        return s == 0 ? java.util.List.of("L", "R") : java.util.List.of();
                    }

                    @Override
                    public java.util.Collection<Integer> getStates() {
                        return java.util.List.of(0);
                    }

                    @Override
                    public java.util.Map<Integer, Double> getTransitions(Integer s, String a) {
                        if (s == 0 && a.equals("L")) return java.util.Map.of(1, 1.0);
                        if (s == 0 && a.equals("R")) return java.util.Map.of(2, 1.0);
                        return java.util.Map.of();
                    }

                    @Override
                    public double getReward(Integer s, String a, Integer ns) {
                        return 0;
                    }

                    @Override
                    public boolean isTerminal(Integer s) {
                        return s != 0;
                    }
                };

        Set<Integer> next = MDPUtil.getNextStates(branching, 0);
        assertEquals(Set.of(1, 2), next);
    }
}
