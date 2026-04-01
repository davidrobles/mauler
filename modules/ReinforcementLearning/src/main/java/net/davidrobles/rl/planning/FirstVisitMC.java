package net.davidrobles.rl.planning; // package dr.rl.algorithms;
//
// import dr.rl.*;
// import TabularVFunction;
//
// import java.util.*;
//
// public class FirstVisitMC<S, A> implements VValuesAlgorithm<S, A>
// {
//    // Lists all the states visited in the current episode
//    private LinkedHashSet<S> visitedStates = new LinkedHashSet<S>();
//
//    // Rewards
//    private List<Double> rewards = new ArrayList<Double>();
//
//    // Maps a state with a list of its returns
//    private Map<S, List<Double>> stateReturns = new HashMap<S, List<Double>>();
//
//    // Maps a state to the step of its first visit
//    private Map<S, Integer> stateFirstOcc = new HashMap<S, Integer>();
//
//    private Environment<S, A> environment;
//    private int numEpisodes;
//    private int currentStep = 0;
//    private List<VFunctionObserver<S>> observers = new ArrayList<VFunctionObserver<S>>();
//
//    private TabularVFunction<S> vFunction;
//    private Policy<S, A> policy;
//
//    public FirstVisitMC(Environment<S, A> environment, Policy<S, A> policy, int numEpisodes) {
//        this.environment = environment;
//        this.policy = policy;
//        this.numEpisodes = numEpisodes;
//        vFunction = new TabularVFunction<S>();
//    }
//
//    private List<Double> returns(S state)
//    {
//        if (!stateReturns.containsKey(state)) {
//            stateReturns.put(state, new ArrayList<Double>());
//        }
//
//        return stateReturns.get(state);
//    }
//
//    private double average(List<Double> returns)
//    {
//        double sum = 0;
//
//        for (Double r : returns) {
//            sum += r;
//        }
//
//        return sum / (double) returns.size();
//    }
//
//    public void notifyValueFunctionUpdate(VFunction<S> valueFunction) {
//        for (VFunctionObserver<S> observer : observers) {
//            observer.valueFunctionChanged(valueFunction);
//        }
//    }
//
//    @Override
//    public Policy<S, A> run()
//    {
////        TabularVFunction newValueFunction = new TabularVFunction();
////        MDPUtil.initializeVFunction(mdp, newValueFunction);
////        Policy policy = new RandomPolicy(mdp);
////        Policy policy = new VEpsilonGreedy(mdp, 0.01, newValueFunction);
////        addVFunctionObserver((VEpsilonGreedy)policy);
//
//        // Run n episodes
//        for (int i = 0; i < numEpisodes; i++)
//        {
//            environment.reset();
//            System.out.println("Episode " + i);
//
//            // Generate an episode using the policy
//            while (!environment.getActions(environment.getCurrentState()).isEmpty())
////            while (!environment.isTerminal(environment.getCurrentState()))
//            {
//                A action = policy.getAction(environment.getCurrentState());
////                MDPState prevState = dr.mdp.getCurrentState();
//                double reward = environment.step(action);
//                visitedStates.add(environment.getCurrentState());
//                rewards.add(reward);
//                if (!stateFirstOcc.containsKey(environment.getCurrentState())) {
//                    stateFirstOcc.put(environment.getCurrentState(), currentStep);
//                }
//                currentStep++;
//                notifyValueFunctionUpdate(vFunction);
//            }
//
//            for (S state : visitedStates)
//            {
//                // New entry
//                if (!stateReturns.containsKey(state)) {
//                    stateReturns.put(state, new ArrayList<Double>());
//                }
//
//                // Return following the first occurrence of s
//                double totalReward = 0;
//                int index = stateFirstOcc.get(state);
//
//                for (int j = index; j < rewards.size(); j++) {
//                    totalReward += rewards.get(j);
//                }
//
//                // Append R to Returns(s)
//                stateReturns.get(state).add(totalReward);
//                // V(s) <- average(Returns(s))
//
//                vFunction.setValue(state, average(returns(state)));
//            }
//
//            visitedStates.clear();
//            rewards.clear();
//            stateFirstOcc.clear();
//            currentStep = 0;
////            notifyValueFunctionUpdate(newValueFunction);
////            try {
////                Thread.sleep(10);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
//
////        LookupTablePolicy newPolicy = new LookupTablePolicy();
////
////        for (MDPState state : mdp.getStates())
////        {
////            MDPAction bestAction = null;
////            double bestValue = Double.NEGATIVE_INFINITY;
////
////            for (MDPAction action : mdp.getActions(state))
////            {
////                for (MDPState nextState : mdp.successors(state, action)) {
////                    if (newValueFunction.getValue(nextState) > bestValue) {
////                        bestValue = newValueFunction.getValue(nextState);
////                        bestAction = action;
////                    }
////                }
////            }
////
////            newPolicy.setStateAction(state, bestAction);
////        }
//
////        return newPolicy;
//        return null;
//    }
//
//    @Override
//    public void addVFunctionObserver(VFunctionObserver observer) {
//        observers.add(observer);
//    }
// }
