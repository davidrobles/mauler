package net.davidrobles.rl.policies;

import java.util.List;
import java.util.Random;
import net.davidrobles.rl.valuefunctions.QFunction;

/**
 * ε-greedy policy: takes a random action with probability ε, otherwise the greedy action (highest
 * Q-value).
 *
 * <p>The Q-function is bound at construction and shared with the learning algorithm, so the policy
 * always exploits the latest estimates. Epsilon can be updated over time by overriding {@link
 * #update(int)} or by calling {@link #setEpsilon(double)} directly.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class EpsilonGreedy<S, A> implements RLPolicy<S, A> {
    private double epsilon;
    private final QFunction<S, A> qFunc;
    private final Random rng;

    /**
     * @param qFunc the Q-function used for greedy action selection
     * @param epsilon probability of taking a random action (0 = fully greedy, 1 = fully random)
     * @param rng random number generator
     */
    public EpsilonGreedy(QFunction<S, A> qFunc, double epsilon, Random rng) {
        this.qFunc = qFunc;
        this.epsilon = epsilon;
        this.rng = rng;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        if (rng.nextDouble() < epsilon) {
            return actions.get(rng.nextInt(actions.size()));
        }

        A bestAction = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (A action : actions) {
            double value = qFunc.getValue(state, action);
            if (value > bestValue) {
                bestAction = action;
                bestValue = value;
            }
        }
        return bestAction;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
}
