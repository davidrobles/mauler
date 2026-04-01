package net.davidrobles.rl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import net.davidrobles.rl.policies.Policy;
import org.junit.Before;
import org.junit.Test;

public class RLLoopTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Counts every Agent lifecycle call. */
    private static class CountingAgent implements Agent<Integer, String> {
        final List<Integer> statesSeen = new ArrayList<>();
        final List<String> actionsSeen = new ArrayList<>();
        int updateCount = 0;

        @Override
        public String selectAction(Integer state, List<String> actions) {
            statesSeen.add(state);
            return actions.get(0);
        }

        @Override
        public void update(
                Integer state,
                String action,
                StepResult<Integer> result,
                List<String> nextActions) {
            actionsSeen.add(action);
            updateCount++;
        }
    }

    /** Counts every Policy lifecycle call. */
    private static class CountingPolicy implements Policy<Integer, String> {
        int resetCount = 0;
        final List<Integer> onStepArgs = new ArrayList<>();
        final List<Integer> onEpisodeEndArgs = new ArrayList<>();

        @Override
        public String selectAction(Integer state, List<String> actions) {
            return actions.get(0);
        }

        @Override
        public void reset() {
            resetCount++;
        }

        @Override
        public void onStep(int totalSteps) {
            onStepArgs.add(totalSteps);
        }

        @Override
        public void onEpisodeEnd(int episode) {
            onEpisodeEndArgs.add(episode);
        }
    }

    private TestEnvironment env;
    private CountingAgent agent;
    private CountingPolicy policy;

    @Before
    public void setUp() {
        env = new TestEnvironment();
        agent = new CountingAgent();
        policy = new CountingPolicy();
    }

    // -------------------------------------------------------------------------
    // Episode count
    // -------------------------------------------------------------------------

    @Test
    public void zeroEpisodesRunsNoSteps() {
        RLLoop.run(env, agent, policy, 0);
        assertEquals(0, agent.updateCount);
        assertEquals(0, policy.resetCount);
    }

    @Test
    public void singleEpisodeTwoSteps() {
        // TestEnvironment: step 0→1 (not done), step 1→2 (done)
        RLLoop.run(env, agent, policy, 1);
        assertEquals(2, agent.updateCount);
    }

    @Test
    public void threeEpisodesRunsSixUpdates() {
        RLLoop.run(env, agent, policy, 3);
        assertEquals(6, agent.updateCount);
    }

    // -------------------------------------------------------------------------
    // Policy lifecycle hook correctness
    // -------------------------------------------------------------------------

    @Test
    public void resetCalledOncePerEpisode() {
        RLLoop.run(env, agent, policy, 3);
        assertEquals(3, policy.resetCount);
    }

    @Test
    public void onEpisodeEndCalledOncePerEpisodeWithCorrectIndex() {
        RLLoop.run(env, agent, policy, 3);
        assertEquals(List.of(0, 1, 2), policy.onEpisodeEndArgs);
    }

    @Test
    public void onStepCalledWithMonotonicallyIncreasingTotalSteps() {
        RLLoop.run(env, agent, policy, 2);
        // 2 episodes × 2 steps each = 4 total steps, numbered 1..4
        assertEquals(List.of(1, 2, 3, 4), policy.onStepArgs);
    }

    @Test
    public void totalStepCountContinuesAcrossEpisodes() {
        RLLoop.run(env, agent, policy, 3);
        // 6 steps total, numbered 1..6
        assertEquals(List.of(1, 2, 3, 4, 5, 6), policy.onStepArgs);
    }

    // -------------------------------------------------------------------------
    // Agent receives correct (state, action, result, nextActions)
    // -------------------------------------------------------------------------

    @Test
    public void agentSeesCorrectStateSequence() {
        RLLoop.run(env, agent, policy, 1);
        // selectAction called for state 0 and state 1
        assertEquals(List.of(0, 1), agent.statesSeen);
    }

    @Test
    public void agentUpdateReceivesTerminalNextActionsAsEmpty() {
        // Capture nextActions from last update call
        List<List<String>> nextActionsList = new ArrayList<>();
        Agent<Integer, String> capturingAgent =
                new Agent<>() {
                    @Override
                    public String selectAction(Integer s, List<String> a) {
                        return a.get(0);
                    }

                    @Override
                    public void update(
                            Integer s, String a, StepResult<Integer> r, List<String> next) {
                        nextActionsList.add(next);
                    }
                };

        RLLoop.run(env, capturingAgent, policy, 1);
        // Second update (terminal step) should have empty nextActions
        assertTrue(nextActionsList.get(1).isEmpty());
    }

    @Test
    public void agentUpdateReceivesNonTerminalNextActionsNonEmpty() {
        List<List<String>> nextActionsList = new ArrayList<>();
        Agent<Integer, String> capturingAgent =
                new Agent<>() {
                    @Override
                    public String selectAction(Integer s, List<String> a) {
                        return a.get(0);
                    }

                    @Override
                    public void update(
                            Integer s, String a, StepResult<Integer> r, List<String> next) {
                        nextActionsList.add(next);
                    }
                };

        RLLoop.run(env, capturingAgent, policy, 1);
        // First update (non-terminal step) should have non-empty nextActions
        assertFalse(nextActionsList.get(0).isEmpty());
    }

    // -------------------------------------------------------------------------
    // numEpisodes validation
    // -------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void negativeNumEpisodesThrows() {
        RLLoop.run(env, agent, policy, -1);
    }

    // -------------------------------------------------------------------------
    // Integration: QLearning converges on TestEnvironment
    // -------------------------------------------------------------------------

    @Test
    public void qLearningLearnsPositiveQValueAfterManyEpisodes() {
        net.davidrobles.rl.valuefunctions.TabularQFunction<Integer, String> q =
                new net.davidrobles.rl.valuefunctions.TabularQFunction<>(0.1);
        net.davidrobles.rl.policies.EpsilonGreedy<Integer, String> epsilonGreedy =
                new net.davidrobles.rl.policies.EpsilonGreedy<>(q, 0.1, new java.util.Random(0));
        net.davidrobles.rl.agents.QLearning<Integer, String> ql =
                new net.davidrobles.rl.agents.QLearning<>(q, epsilonGreedy, 0.9);

        RLLoop.run(env, ql, epsilonGreedy, 500);

        // After 500 episodes the Q-values at state 0 and 1 should be positive
        assertTrue("Q(0,go) should be positive", q.getValue(0, TestEnvironment.GO) > 0.5);
        assertTrue("Q(1,go) should be positive", q.getValue(1, TestEnvironment.GO) > 0.8);
    }
}
