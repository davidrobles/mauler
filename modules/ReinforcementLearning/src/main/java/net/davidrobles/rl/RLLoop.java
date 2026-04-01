package net.davidrobles.rl;

import java.util.Collections;
import java.util.List;
import net.davidrobles.rl.policies.Policy;

/**
 * Drives the standard RL episode loop: reset, select action, step, update — repeat.
 *
 * <p>Keeps the loop logic out of individual {@link Agent} implementations so each algorithm only
 * needs to define its update rule.
 *
 * <p>Policy lifecycle calls per episode:
 *
 * <ol>
 *   <li>{@link Policy#reset()} — start of episode
 *   <li>{@link Policy#onStep(int)} — after every step
 *   <li>{@link Policy#onEpisodeEnd(int)} — end of episode
 * </ol>
 */
public class RLLoop {
    private RLLoop() {}

    /**
     * Runs {@code numEpisodes} full episodes of interaction between {@code env} and {@code agent}.
     *
     * @param env the environment
     * @param agent the agent to train
     * @param policy the policy driving action selection (receives lifecycle callbacks)
     * @param numEpisodes number of episodes to run; must be non-negative
     * @param <S> state type
     * @param <A> action type
     * @throws IllegalArgumentException if {@code numEpisodes} is negative
     */
    public static <S, A> void run(
            Environment<S, A> env, Agent<S, A> agent, Policy<S, A> policy, int numEpisodes) {
        if (numEpisodes < 0)
            throw new IllegalArgumentException(
                    "numEpisodes must be non-negative, got: " + numEpisodes);
        int totalSteps = 0;

        for (int ep = 0; ep < numEpisodes; ep++) {
            policy.reset();
            S state = env.reset();
            List<A> actions = env.getActions(state);

            while (!actions.isEmpty()) {
                A action = agent.selectAction(state, actions);
                StepResult<S> result = env.step(action);
                List<A> nextActions =
                        result.done()
                                ? Collections.emptyList()
                                : env.getActions(result.nextState());
                agent.update(state, action, result, nextActions);
                policy.onStep(++totalSteps);

                if (result.done()) break;
                state = result.nextState();
                actions = nextActions;
            }

            policy.onEpisodeEnd(ep);
        }
    }
}
