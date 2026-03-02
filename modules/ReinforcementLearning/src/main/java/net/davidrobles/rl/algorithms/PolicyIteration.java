package net.davidrobles.rl.algorithms;

import net.davidrobles.rl.Learner;
import net.davidrobles.rl.MDP;
import net.davidrobles.rl.MDPUtil;
import net.davidrobles.rl.policies.TabularPolicy;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;

import java.util.*;

public class PolicyIteration<S, A> implements Learner
{
    private MDP<S, A> mdp;
    private TabularVFunction<S> table = new TabularVFunction<S>();
    private boolean policyStable = false;
    private double theta;    // A small positive number used as a termination condition
    private double gamma;    // Discount factor
    private List<VFunctionObserver<S>> observers = new ArrayList<VFunctionObserver<S>>();
    private TabularPolicy<S, A> policy;

    public PolicyIteration(MDP<S, A> mdp, double theta, double gamma)
    {
        this.mdp = mdp;
        this.theta = theta;
        this.gamma = gamma;
    }

    public void notifyValueFunctionUpdate()
    {
        for (VFunctionObserver<S> observer : observers)
            observer.valueFunctionChanged(table);
    }

    public void addVFunctionObserver(VFunctionObserver<S> observer)
    {
        observers.add(observer);
    }

    private void policyEvaluation()
    {
        double delta;

        do
        {
            delta = 0;

            for (S state : mdp.getStates())
            {
                double oldValue = table.getValue(state);
                Set<S> nextStates = MDPUtil.getNextStates(mdp, state);
                A action = policy.getAction(state);
                double newValue = 0;

                for (S nextState : nextStates)
                {
                    Map<S, Double> tt = mdp.getTransitions(state, action);

                    if (tt.get(nextState) != null)
                    {
                        double transition = mdp.getTransitions(state, action).get(nextState);
                        double reward = mdp.getReward(state, action, nextState);
                        double nextStateValue = table.getValue(nextState);
                        newValue += transition * (reward + (gamma * nextStateValue));
                    }
                }

                table.setValue(state, newValue);
                delta = Math.max(delta, Math.abs(oldValue - table.getValue(state)));
                notifyValueFunctionUpdate();
            }
        }
        while (delta > theta);
    }

    private void policyImprovement()
    {
        policyStable = true;

        for (S state : mdp.getStates())
        {
            A oldAction = policy.getAction(state);
            A bestAction = null;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (A action : mdp.getActions(state))
            {
                double tot = 0;
                Map<S, Double> transitions = mdp.getTransitions(state, action);

                for (S nextState : transitions.keySet())
                {
                    double transition = transitions.get(nextState);
                    double reward = mdp.getReward(state, action, nextState);
                    double nextValue = table.getValue(nextState);
                    tot += transition * (reward + (gamma * nextValue));
                }

                if (tot > bestScore)
                {
                    bestAction = action;
                    bestScore = tot;
                }
            }

            policy.setAction(state, bestAction);

            if (!mdp.isTerminal(state) && !oldAction.equals(policy.getAction(state)))
                policyStable = false;
        }
    }

    private Random rng = new Random();

    @Override
    public void learn()
    {
//        policy = new TabularPolicy<S, A>();
//
//        // initialise the policy arbitrarily
//        for (S state : mdp.getStates())
//        {
//            List<A> actions = mdp.getActions(state);
//
//            if (!actions.isEmpty())
//            {
//                Collections.shuffle(actions);
//                policy.setAction(state, actions.get(rng.nextInt(actions.size())));
//            }
//        }
//
//        while (!policyStable)
//        {
//            policyEvaluation();
//            policyImprovement();
//        }
//
//        System.out.println("Policy iteration finished.");
    }
}
