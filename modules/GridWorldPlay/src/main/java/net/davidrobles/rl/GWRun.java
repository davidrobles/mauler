package net.davidrobles.rl;

import java.util.Random;
import net.davidrobles.gridworld.GWAction;
import net.davidrobles.gridworld.GWState;
import net.davidrobles.gridworld.GridWorldEnv;
import net.davidrobles.gridworld.GridWorldMDP;
import net.davidrobles.gridworld.view.GWVView;
import net.davidrobles.gridworld.view.GWViewQValues;
import net.davidrobles.rl.agents.*;
import net.davidrobles.rl.planning.*;
import net.davidrobles.rl.policies.EpsilonGreedy;
import net.davidrobles.rl.policies.RandomPolicy;
import net.davidrobles.rl.prediction.*;
import net.davidrobles.rl.valuefunctions.TabularQFunction;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.util.DRFrame;

public class GWRun {
    private static final Random RNG = new Random();

    private static void policyIteration() {
        double theta = 0.01;
        double gamma = 0.99;
        GridWorldMDP mdp = new GridWorldMDP(25, 25, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        GWVView view = new GWVView(mdp, 20, 20, env);
        new DRFrame(view);
        PolicyIteration<GWState, GWAction> learner = new PolicyIteration<>(mdp, theta, gamma);
        learner.addVFunctionObserver(view);
        learner.solve();
    }

    private static void valueIteration() {
        double theta = 0.1;
        double gamma = 0.99;
        GridWorldMDP mdp = new GridWorldMDP(25, 25, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        GWVView view = new GWVView(mdp, 20, 20, env);
        new DRFrame(view);
        ValueIteration<GWState, GWAction> learner = new ValueIteration<>(mdp, theta, gamma);
        learner.addVFunctionObserver(view);
        learner.solve();
    }

    private static void tabularTD0() {
        double alpha = 0.01;
        double gamma = 0.99;
        int numEpisodes = 5000;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        TabularVFunction<GWState> vTable = new TabularVFunction<>(alpha);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<>(RNG);
        GWVView view = new GWVView(mdp, 20, 20, env);
        new DRFrame(view, "TD(0)");
        TD0<GWState, GWAction> agent = new TD0<>(vTable, policy, gamma);
        agent.addVFunctionObserver(view);
        RLLoop.run(env, agent, policy, numEpisodes);
    }

    private static void tabularTDLambda() {
        double alpha = 0.001;
        double gamma = 0.99;
        double lambda = 0.1;
        int numEpisodes = 1000;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        TabularVFunction<GWState> vTable = new TabularVFunction<>(alpha);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<>(RNG);
        GWVView view = new GWVView(mdp, 20, 20, env);
        new DRFrame(view, "TD(λ)");
        TDLambda<GWState, GWAction> agent = new TDLambda<>(vTable, policy, gamma, lambda);
        agent.addVFunctionObserver(view);
        RLLoop.run(env, agent, policy, numEpisodes);
    }

    private static void tabularSARSA() {
        double alpha = 0.1;
        double gamma = 0.99;
        int numEpisodes = 100;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        TabularQFunction<GWState, GWAction> qTable = new TabularQFunction<>(alpha);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<>(RNG);
        GWViewQValues view = new GWViewQValues(mdp, 20, 20, env);
        view.setGridEnabled(true);
        new DRFrame(view, "SARSA");
        SARSA<GWState, GWAction> agent = new SARSA<>(qTable, policy, gamma);
        agent.addQFunctionObserver(view);
        RLLoop.run(env, agent, policy, numEpisodes);
    }

    private static void tabularQLearning() {
        double alpha = 0.1;
        double gamma = 0.99;
        int numEpisodes = 300;
        GridWorldMDP mdp = new GridWorldMDP(25, 25, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        TabularQFunction<GWState, GWAction> qTable = new TabularQFunction<>(alpha);
        EpsilonGreedy<GWState, GWAction> policy = new EpsilonGreedy<>(qTable, 0.1, RNG);
        GWViewQValues view = new GWViewQValues(mdp, 20, 20, env);
        view.setGridEnabled(true);
        new DRFrame(view, "Q-Learning");
        QLearning<GWState, GWAction> agent = new QLearning<>(qTable, policy, gamma);
        agent.addQFunctionObserver(view);
        RLLoop.run(env, agent, policy, numEpisodes);
    }

    private static void tabularSARSALambda() {
        double alpha = 0.1;
        double gamma = 0.99;
        double lambda = 0.9;
        int numEpisodes = 100;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        TabularQFunction<GWState, GWAction> qTable = new TabularQFunction<>(alpha);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<>(RNG);
        GWViewQValues view = new GWViewQValues(mdp, 20, 20, env);
        view.setGridEnabled(true);
        new DRFrame(view, "SARSA(λ)");
        SARSALambda<GWState, GWAction> agent = new SARSALambda<>(qTable, policy, gamma, lambda);
        agent.addQFunctionObserver(view);
        RLLoop.run(env, agent, policy, numEpisodes);
    }

    public static void main(String[] args) {
        //        tabularTD0();
        tabularSARSA();
        //        valueIteration();
        //        tabularQLearning();
        //        tabularTDLambda();
        //        tabularSARSALambda();
        //        policyIteration();
    }
}
