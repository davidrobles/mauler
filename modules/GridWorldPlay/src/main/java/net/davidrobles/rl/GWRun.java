package net.davidrobles.rl;

import net.davidrobles.gridworld.GWAction;
import net.davidrobles.gridworld.GWState;
import net.davidrobles.gridworld.GridWorldEnv;
import net.davidrobles.gridworld.GridWorldMDP;
import net.davidrobles.gridworld.view.GWVView;
import net.davidrobles.gridworld.view.GWViewQValues;
import net.davidrobles.rl.algorithms.*;
import net.davidrobles.rl.policies.EpsilonGreedy;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.policies.RandomPolicy;
import net.davidrobles.util.DRFrame;

import java.util.Random;

public class GWRun
{
    private static final Random RNG = new Random();

//    private static void dpValueIteration()
//    {
//        double alpha = 0.1;
//        double gamma = 0.99;
//        GridWorldMDP mdp = new GridWorldMDP(100, 100, RNG);
//        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
//        GWVView view = new GWVView(mdp, 4, 4, null, env);
//        TabularVFunction<GWState> vFunction = new TabularVFunction<GWState>();
////        MDPUtil.initialiseVFunction(mdp, vFunction);
//        new DRFrame(view);
//        VValuesAlgorithm<GWState, GWAction> algorithm =
//                new DPValueIteration<GWState, GWAction>(mdp, vFunction, alpha, gamma);
//        algorithm.addVFunctionObserver(view);
//        algorithm.run();
////        System.out.println("Optimal policy found.");
////        play(mdp, policy, view);
//    }

//    private static void dpPolicyIteration()
//    {
//        double alpha = 0.01;
//        double gamma = 0.99;
//        GridWorldMDP mdp = new GridWorldMDP(100, 100, RNG);
//        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
//        GWVView view = new GWVView(mdp, 4, 4, null, env);
//        new DRFrame(view);
//        VValuesAlgorithm<GWState, GWAction> algorithm = new DPPolicyIteration<GWState, GWAction>(mdp, alpha, gamma, RNG);
//        algorithm.addVFunctionObserver(view);
//        RLPolicy newPolicy = algorithm.run();
////        System.out.println("Optimal policy found.");
////        play(mdp, newPolicy, view);
//    }

//    private static void firstVisitMC()
//    {
//        GridWorldMDP mdp = new GridWorldMDP(12, 12, RNG);
//        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
//        RandomPolicy<GWState, GWAction> policy =
//                new RandomPolicy<GWState, GWAction>(new ActionsFunction<GWState, GWAction>(mdp), RNG);
//        GWVView view = new GWVView(mdp, 30, 30, null, env);
//        view.setShowCurrentState(true);
//        new DRFrame(view);
//        VValuesAlgorithm<GWState, GWAction> algorithm = new FirstVisitMC<GWState, GWAction>(env, policy, 500);
//        algorithm.addVFunctionObserver(view);
//        algorithm.run();
////        System.out.println("Optimal policy found.");
////        play(mdp, policy, view);
//    }

    private static void policyIteration()
    {
        double theta = 0.01;
        double gamma = 0.99;
        GridWorldMDP mdp = new GridWorldMDP(25, 25, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        GWVView view = new GWVView(mdp, 20, 20, env);
        new DRFrame(view);
        PolicyIteration<GWState, GWAction> learner = new PolicyIteration<GWState, GWAction>(mdp, theta, gamma);
        learner.addVFunctionObserver(view);
        learner.learn();
    }

    private static void valueIteration()
    {
        double alpha = 0.1;
        double gamma = 0.99;
        GridWorldMDP mdp = new GridWorldMDP(25, 25, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        GWVView view = new GWVView(mdp, 20, 20, env);
        new DRFrame(view);
        ValueIteration<GWState, GWAction> learner = new ValueIteration<GWState, GWAction>(mdp, alpha, gamma);
        learner.addVFunctionObserver(view);
        learner.learn();
    }

    private static void TabularTD0()
    {
        double alpha = 0.01;
        double gamma = 0.99;
        int numEpisodes = 5000;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RLPolicy<GWState, GWAction> policy = new EpsilonGreedy<GWState, GWAction>(1.0, RNG);
        GWVView view = new GWVView(mdp, 20, 20, env);
        new DRFrame(view, "TD(0)");
        TabularTD0<GWState, GWAction> learner = new TabularTD0<GWState, GWAction>(env, policy, alpha, gamma, numEpisodes);
        learner.addVFunctionObserver(view);
        learner.learn();
    }

    private static void TabularTDLambda()
    {
        double alpha = 0.001;
        double gamma = 0.99;
        double lambda = 0.1;
        int numEpisodes = 1000;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<GWState, GWAction>(RNG);
        GWVView view = new GWVView(mdp, 20, 20, env);
        new DRFrame(view, "TD Lambda");
        TabularTDLambda<GWState, GWAction> learner = new TabularTDLambda<GWState, GWAction>(env, policy, alpha,
                gamma, lambda, numEpisodes);
        learner.addVFunctionObserver(view);
        learner.learn();
    }

    private static void TabularSARSA0()
    {
        double alpha = 0.1;
        double gamma = 0.99;
        int numEpisodes = 100;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<GWState, GWAction>(RNG);
        GWViewQValues view = new GWViewQValues(mdp, 20, 20, env);
        view.setGridEnabled(true);
        new DRFrame(view, "SARSA");
        TabularSARSA<GWState, GWAction> learner = new TabularSARSA<GWState, GWAction>(env, policy, alpha, gamma, numEpisodes);
        learner.addQFunctionObserver(view);
        learner.learn();
    }

    private static void TabularQLearning()
    {
        double alpha = 0.1;
        double gamma = 0.99;
        int numEpisodes = 300;
        GridWorldMDP mdp = new GridWorldMDP(25, 25, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<GWState, GWAction>(RNG);
        GWViewQValues view = new GWViewQValues(mdp, 20, 20, env);
        view.setGridEnabled(true);
        new DRFrame(view, "QLearning");
        QLearning<GWState, GWAction> learner = new QLearning<GWState, GWAction>(env, policy, alpha, gamma, numEpisodes);
        learner.addQFunctionObserver(view);
        learner.learn();
    }

    private static void TabularSARSALambda()
    {
        double alpha = 0.1;
        double gamma = 0.99;
        double lambda = 0.9;
        int numEpisodes = 100;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<GWState, GWAction>(RNG);
        GWViewQValues view = new GWViewQValues(mdp, 20, 20, env);
        view.setGridEnabled(true);
        new DRFrame(view, "SARSA");
        TabularSARSALambda<GWState, GWAction> learner =
                new TabularSARSALambda<GWState, GWAction>(env, policy, alpha, gamma, lambda, numEpisodes);
        learner.addQFunctionObserver(view);
        learner.learn();
    }

    public static void main(String[] args)
    {
//        TabularTD0();
        TabularSARSA0();
//        valueIteration();
//        TabularQLearning();
//        TabularTDLambda();
//       TabularSARSALambda();
//        policyIteration();
    }

//        dpValueIteration();
//        dpPolicyIteration();
//        firstVisitMC();
//        mdp();
//        SARSA();

//        GridWorldMDP mdp = new GridWorldMDP(100, 100);
//        GridWorldView view = new GridWorldView(mdp, null, 4, 4);
//        new DRFrame(view);
//        VValuesAlgorithm algorithm = new TD0(mdp, 0.8, 0.99999, 1000);
//        VValuesAlgorithm algorithm = new DPValueIteration(mdp, 0.001, 0.945);
//        VValuesAlgorithm algorithm = new DPPolicyIteration(mdp, 0.001, 0.945);
//        VValuesAlgorithm algorithm = new FirstVisitMC(mdp, 1000);
//        algorithm.addObserver(view);
//        MDPPolicy policy = algorithm.runAlgorithm();


//        try {
//            System.out.println("waiting...");
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        while (mdp.getCurrentState() != mdp.getGoalState())
//        {
//            try {
//                mdp.takeAction(policy.selectAction(mdp.getCurrentState()));
////                mdp.notifyCurrentStateChange();
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        System.out.println("Goal reached.");
}
