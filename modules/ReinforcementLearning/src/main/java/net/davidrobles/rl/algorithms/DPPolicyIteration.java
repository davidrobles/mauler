package net.davidrobles.rl.algorithms;//package dr.rl.algorithms;
//
//import MDP;
//import RLPolicy;
//import VFunctionObserver;
//import dr.rl.VValuesAlgorithm;
//import dr.rl.policies.LookupTablePolicy;
//import TabularVFunction;
//
//import java.util.*;
//
//// Model-based policy iteration
//public class DPPolicyIteration<S, A> implements VValuesAlgorithm<S, A>
//{
//    private MDP<S, A> mdp;
//    private TabularVFunction<S> valueFunction;
//    private LookupTablePolicy<S, A> policy;
//    private boolean policyStable = false;
//    private double theta;    // A small positive number used as a termination condition
//    private double gamma;    // Discount factor
//    private List<VFunctionObserver<S>> observers = new ArrayList<VFunctionObserver<S>>();
//    private final Random rng;
//    private int steps = 0;
//
//    public DPPolicyIteration(MDP<S, A> mdp, double theta, double gamma, Random rng) {
//        this.mdp = mdp;
//        this.theta = theta;
//        this.gamma = gamma;
//        this.rng = rng;
//    }
//
//    public void notifyValueFunctionUpdate() {
//        for (VFunctionObserver<S> observer : observers) {
//            observer.valueFunctionChanged(valueFunction);
//        }
//    }
//
//    private void policyEvaluation()
//    {
//        double delta;
//
//        do
//        {
//            delta = 0;
//
//            for (S state : mdp.getStates())
//            {
//                double oldStateValue = valueFunction.getValue(state);
//                double newStateValue = 0;
//
//                for (A action : mdp.getActions(state))
//                {
//                    double stateActionProbability = policy.getStateActionProbability(state, action);
//                    double tot = 0;
//                    Map<S, Double> nextStatesProbs =
//                            mdp.getTransitions(state, action);
//
//                    for (S nextState : nextStatesProbs.keySet()) {
//                        double transitionProbability = nextStatesProbs.get(nextState);
//                        double reward = mdp.getReward(state, action, nextState);
//                        double nextStateValue = valueFunction.getValue(nextState);
//                        tot += transitionProbability * (reward + (gamma * nextStateValue));
//                    }
//
//                    newStateValue += stateActionProbability * tot;
//                }
//
//                valueFunction.setValue(state, newStateValue);
//                delta = Math.max(delta, Math.abs(oldStateValue - valueFunction.getValue(state)));
//                notifyValueFunctionUpdate();
//            }
//        } while (delta > theta);
//    }
//
//    private void policyImprovement()
//    {
//        policyStable = true;
//
//        for (S state : mdp.getStates())
//        {
//            A oldAction = policy.getAction(state);
//            A bestAction = null;
//            double bestScore = Double.NEGATIVE_INFINITY;
//
//            for (A action : mdp.getActions(state))
//            {
//                double tot = 0;
//                Map<S, Double> nextStatesProbs =
//                        mdp.getTransitions(state, action);
//
//                for (S nextState : nextStatesProbs.keySet()) {
//                    double transitionProbability = nextStatesProbs.get(nextState);
//                    double reward = mdp.getReward(state, action, nextState);
//                    double nextValue = valueFunction.getValue(nextState);
//                    tot += transitionProbability * (reward + (gamma * nextValue));
//                }
//
//                if (tot > bestScore) {
//                    bestAction = action;
//                    bestScore = tot;
//                }
//            }
//
//            policy.setStateAction(state, bestAction);
//
//            if (oldAction != policy.getAction(state)) {
//                policyStable = false;
//            }
//        }
//    }
//
//    @Override
//    public RLPolicy<S, A> run()
//    {
//        valueFunction = new TabularVFunction<S>();
////        MDPUtil.initialiseVFunction(mdp, valueFunction);
//        policy = new LookupTablePolicy<S, A>();
//
//        // initialise the policy arbitrarily
//        for (S state : mdp.getStates()) {
//            List<A> actions = mdp.getActions(state);
//            Collections.shuffle(actions);
//            policy.setStateAction(state, actions.get(rng.nextInt(actions.size())));
////            policy.setStateAction(state, GWAction.values()[RNG.nextInt(GWAction.values().length)]);
//        }
//
//        while (!policyStable) {
//            System.out.println("Step " + steps++);
//            policyEvaluation();
//            policyImprovement();
//        }
//
//        System.out.println("Policy iteration finished.");
//
//        return policy;
//    }
//
//    @Override
//    public void addVFunctionObserver(VFunctionObserver<S> observer) {
//        observers.add(observer);
//    }
//}
