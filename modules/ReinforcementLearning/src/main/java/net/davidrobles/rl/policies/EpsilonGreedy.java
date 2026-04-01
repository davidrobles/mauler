package net.davidrobles.rl.policies;

import java.util.List;
import java.util.Random;
import net.davidrobles.rl.valuefunctions.QFunction;

/**
 * ε-greedy policy: takes a random action with probability ε, otherwise the greedy action (highest
 * Q-value).
 *
 * <p>The Q-function is bound at construction and shared with the learning algorithm, so the policy
 * always exploits the latest estimates.
 *
 * <h3>Epsilon decay</h3>
 *
 * Linear decay from {@code epsilonStart} to {@code epsilonEnd} over {@code decayEpisodes} episodes
 * is built in. Override {@link #onEpisodeEnd(int)} for a custom schedule, or call {@link
 * #setEpsilon(double)} directly.
 *
 * <h3>Evaluation mode</h3>
 *
 * Call {@link #setTrainingMode(boolean) setTrainingMode(false)} to disable exploration entirely
 * (acts greedily). Useful for evaluating a trained agent without rebuilding the policy.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class EpsilonGreedy<S, A> implements Policy<S, A> {
    private double epsilon;
    private final double epsilonStart;
    private final double epsilonEnd;
    private final int decayEpisodes;
    private boolean training = true;
    private final QFunction<S, A> qFunc;
    private final Random rng;

    /**
     * Constructs an ε-greedy policy with a fixed epsilon (no decay).
     *
     * @param qFunc the Q-function used for greedy action selection
     * @param epsilon fixed exploration probability
     * @param rng random number generator
     */
    public EpsilonGreedy(QFunction<S, A> qFunc, double epsilon, Random rng) {
        this(qFunc, epsilon, epsilon, 1, rng);
    }

    /**
     * Constructs an ε-greedy policy with linear epsilon decay.
     *
     * @param qFunc the Q-function used for greedy action selection
     * @param epsilonStart initial exploration probability
     * @param epsilonEnd final exploration probability (reached after {@code decayEpisodes})
     * @param decayEpisodes number of episodes over which epsilon is linearly annealed
     * @param rng random number generator
     */
    public EpsilonGreedy(
            QFunction<S, A> qFunc,
            double epsilonStart,
            double epsilonEnd,
            int decayEpisodes,
            Random rng) {
        this.qFunc = qFunc;
        this.epsilonStart = epsilonStart;
        this.epsilonEnd = epsilonEnd;
        this.decayEpisodes = decayEpisodes;
        this.epsilon = epsilonStart;
        this.rng = rng;
    }

    @Override
    public A selectAction(S state, List<A> actions) {
        if (training && rng.nextDouble() < epsilon) {
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

    /** Linearly decays epsilon from {@code epsilonStart} to {@code epsilonEnd}. */
    @Override
    public void onEpisodeEnd(int episode) {
        if (decayEpisodes <= 1) return;
        double t = Math.min(1.0, (double) (episode + 1) / decayEpisodes);
        epsilon = epsilonStart + t * (epsilonEnd - epsilonStart);
    }

    /** Enables exploration when {@code training=true}, acts fully greedy when {@code false}. */
    @Override
    public void setTrainingMode(boolean training) {
        this.training = training;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
}
