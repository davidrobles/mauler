package net.davidrobles.rl;

import java.util.Collections;
import java.util.List;
import net.davidrobles.rl.policies.RLPolicy;

/**
 * Drives the standard RL episode loop: reset, select action, step, update — repeat.
 *
 * <p>Keeps the loop logic out of individual {@link Agent} implementations so each algorithm only
 * needs to define its update rule.
 */
public class RLLoop {
    /**
     * Runs {@code numEpisodes} full episodes of interaction between {@code env} and {@code agent}.
     * After each step the policy's {@link RLPolicy#update(int)} is called with the running total of
     * steps, allowing decay schedules (e.g. ε-annealing) to react to training progress.
     *
     * @param env the environment
     * @param agent the agent to train
     * @param policy the policy driving action selection (called for schedule updates)
     * @param numEpisodes number of episodes to run
     * @param <S> state type
     * @param <A> action type
     */
    public static <S, A> void run(
            Environment<S, A> env, Agent<S, A> agent, RLPolicy<S, A> policy, int numEpisodes) {
        int totalSteps = 0;

        for (int ep = 0; ep < numEpisodes; ep++) {
            S state = env.reset();
            List<A> actions = env.getActions(state);

            while (!actions.isEmpty()) {
                A action = agent.selectAction(state, actions);
                StepResult<S> result = env.step(action);
                List<A> nextActions =
                        result.done ? Collections.emptyList() : env.getActions(result.nextState);
                agent.update(state, action, result, nextActions);
                policy.update(++totalSteps);

                if (result.done) break;
                state = result.nextState;
                actions = nextActions;
            }
        }
    }
}
