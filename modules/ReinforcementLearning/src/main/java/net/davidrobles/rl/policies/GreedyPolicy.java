package net.davidrobles.rl.policies;

import java.util.List;
import net.davidrobles.rl.Environment;
import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.VFunction;

public class GreedyPolicy<S, A> implements RLPolicy<S, A> {
    @Override
    public A getAction(Environment<S, A> env, QFunction<S, A> qFunc) {
        A bestAction = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        List<A> actions = env.getPossibleActions(env.getCurrentState());

        for (A action : actions) {
            double value = qFunc.getValue(env.getCurrentState(), action);

            if (value > bestValue) {
                bestAction = action;
                bestValue = value;
            }
        }

        return bestAction;
    }

    @Override
    public A getAction(Environment<S, A> env, VFunction<S> vFunc) {
        A bestAction = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        List<A> actions = env.getPossibleActions(env.getCurrentState());

        for (A action : actions) {
            double value = vFunc.getValue(env.getCurrentState());

            if (value > bestValue) {
                bestAction = action;
                bestValue = value;
            }
        }

        return bestAction;
    }
}
