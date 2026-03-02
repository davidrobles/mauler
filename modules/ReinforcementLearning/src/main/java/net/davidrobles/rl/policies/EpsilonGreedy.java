package net.davidrobles.rl.policies;

import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.VFunction;
import net.davidrobles.rl.RLEnv;

import java.util.Random;

/**
 * An Epsilon Greedy policy. Takes a greedy action with a '1 - epsilon' probability,
 * or random action otherwise.
 *
 * @param <S> the type of the states
 * @param <A> the type of the actions
 */
public class EpsilonGreedy<S, A> implements RLPolicy<S, A>
{
    /**
     * Probability of taking a random action.
     */
    private double epsilon;

    /**
     * The random policy used to take the random actions (random value < epsilon).
     */
    private RandomPolicy<S, A> randomPolicy;

    /**
     * The policy used to take greedy actions (random value >= epsilon).
     */
    private GreedyPolicy<S, A> greedyPolicy;

    /**
     * Random Number Generator.
     */
    private final Random rng;

    /**
     * Constructs an epsilon greedy policy with the specified epsilon value.
     *
     * @param epsilon the probability of taking a random action
     * @param rng random number generator
     */
    public EpsilonGreedy(double epsilon, Random rng)
    {
        this.epsilon = epsilon;
        this.greedyPolicy = new GreedyPolicy<S, A>();
        this.randomPolicy = new RandomPolicy<S, A>(rng);
        this.rng = rng;
    }

    @Override
    public A getAction(RLEnv<S, A> env, QFunction<S, A> qFunction)
    {
        // explore
        if (rng.nextDouble() < epsilon)
            return randomPolicy.getAction(env, qFunction);

        // exploit
        return greedyPolicy.getAction(env, qFunction);
    }

    @Override
    public A getAction(RLEnv<S, A> env, VFunction<S> vFunction)
    {
        // explore
        if (rng.nextDouble() < epsilon)
            return randomPolicy.getAction(env, vFunction);

        // exploit
        return greedyPolicy.getAction(env, vFunction);
    }
}
