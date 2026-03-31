package net.davidrobles.rl;

import java.util.Collections;
import java.util.List;

/**
 * Drives the standard RL episode loop: reset, select action, step, update — repeat.
 *
 * <p>Keeps the loop logic out of individual {@link Agent} implementations so each algorithm only
 * needs to define its update rule.
 */
public class RLLoop {
    /**
     * Runs {@code numEpisodes} full episodes of interaction between {@code env} and {@code agent}.
     *
     * @param env the environment
     * @param agent the agent to train
     * @param numEpisodes number of episodes to run
     * @param <S> state type
     * @param <A> action type
     */
    public static <S, A> void run(Environment<S, A> env, Agent<S, A> agent, int numEpisodes) {
        for (int ep = 0; ep < numEpisodes; ep++) {
            S state = env.reset();
            List<A> actions = env.getActions(state);

            while (!actions.isEmpty()) {
                A action = agent.selectAction(state, actions);
                StepResult<S> result = env.step(action);
                List<A> nextActions =
                        result.done ? Collections.emptyList() : env.getActions(result.nextState);
                agent.update(state, action, result, nextActions);

                if (result.done) break;
                state = result.nextState;
                actions = nextActions;
            }
        }
    }
}
