package net.davidrobles.rl.planning;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.davidrobles.rl.MDP;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.policies.TabularPolicy;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;

public class ValueIteration<S, A> implements Planner<S, A> {
    private MDP<S, A> mdp;
    private TabularVFunction<S> table = new TabularVFunction<S>();
    private double theta; // A small positive number used as a termination condition
    private double gamma; // Discount factor
    private final Set<VFunctionObserver<S>> observers = new LinkedHashSet<>();

    public ValueIteration(MDP<S, A> mdp, double theta, double gamma) {
        this.mdp = mdp;
        this.theta = theta;
        this.gamma = gamma;
    }

    public void notifyValueFunctionUpdate() {
        for (VFunctionObserver<S> observer : observers) observer.valueFunctionUpdated(table);
    }

    public void addVFunctionObserver(VFunctionObserver<S> observer) {
        observers.add(observer);
    }

    @Override
    public Policy<S, A> solve() {
        double delta;

        System.out.println("Value Iteration started...");

        do {
            delta = 0;

            for (S state : mdp.getStates()) {
                double oldStateValue = table.getValue(state);
                double newStateValue = Double.NEGATIVE_INFINITY;

                for (A action : mdp.getActions(state)) {
                    double tot = 0;
                    Map<S, Double> nextTransitions = mdp.getTransitions(state, action);

                    for (S nextState : nextTransitions.keySet()) {
                        double probability = nextTransitions.get(nextState);
                        double reward = mdp.getReward(state, action, nextState);
                        double nextStateValue = table.getValue(nextState);
                        tot += probability * (reward + (gamma * nextStateValue));
                    }

                    if (tot > newStateValue) newStateValue = tot;
                }

                table.setValue(state, newStateValue);
                delta = Math.max(delta, Math.abs(oldStateValue - table.getValue(state)));
                notifyValueFunctionUpdate();
            }

            System.out.printf("%.4f (delta) < %.4f (theta) ?????\n", delta, theta);

        } while (delta > theta);

        System.out.println("DP Value Iteration finished.");

        // Extract greedy policy via one-step lookahead
        TabularPolicy<S, A> policy = new TabularPolicy<>();
        for (S state : mdp.getStates()) {
            A bestAction = null;
            double bestValue = Double.NEGATIVE_INFINITY;
            for (A action : mdp.getActions(state)) {
                double tot = 0;
                for (Map.Entry<S, Double> e : mdp.getTransitions(state, action).entrySet()) {
                    tot +=
                            e.getValue()
                                    * (mdp.getReward(state, action, e.getKey())
                                            + gamma * table.getValue(e.getKey()));
                }
                if (tot > bestValue) {
                    bestValue = tot;
                    bestAction = action;
                }
            }
            if (bestAction != null) policy.setAction(state, bestAction);
        }
        return policy;
    }
}
