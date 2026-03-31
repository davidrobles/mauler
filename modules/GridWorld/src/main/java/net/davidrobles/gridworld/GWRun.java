package net.davidrobles.gridworld;

import java.util.Random;
import net.davidrobles.gridworld.view.GWVView;
import net.davidrobles.gridworld.view.GWViewQValues;
import net.davidrobles.rl.RLLoop;
import net.davidrobles.rl.algorithms.QLearning;
import net.davidrobles.rl.algorithms.TabularSARSA;
import net.davidrobles.rl.algorithms.TabularSARSALambda;
import net.davidrobles.rl.algorithms.TabularTD0;
import net.davidrobles.rl.algorithms.TabularTDLambda;
import net.davidrobles.rl.policies.EpsilonGreedy;
import net.davidrobles.rl.policies.RLPolicy;
import net.davidrobles.rl.policies.RandomPolicy;
import net.davidrobles.util.DRFrame;

public class GWRun {
    private static final Random RNG = new Random();

    private static void TabularTD0() {
        double alpha = 0.01;
        double gamma = 0.99;
        int numEpisodes = 500;
        GridWorldMDP mdp = new GridWorldMDP(50, 50, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RLPolicy<GWState, GWAction> policy = new EpsilonGreedy<>(1.0, RNG);
        GWVView view = new GWVView(mdp, 10, 10, env);
        new DRFrame(view, "TD(0)");
        TabularTD0<GWState, GWAction> agent = new TabularTD0<>(policy, alpha, gamma);
        agent.addVFunctionObserver(view);
        RLLoop.run(env, agent, numEpisodes);
    }

    private static void TabularTDLambda() {
        double alpha = 0.001;
        double gamma = 0.99;
        double lambda = 0.1;
        int numEpisodes = 1000;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<>(RNG);
        GWVView view = new GWVView(mdp, 20, 20, env);
        new DRFrame(view, "TD Lambda");
        TabularTDLambda<GWState, GWAction> agent =
                new TabularTDLambda<>(policy, alpha, gamma, lambda);
        agent.addVFunctionObserver(view);
        RLLoop.run(env, agent, numEpisodes);
    }

    private static void TabularSARSA0() {
        double alpha = 0.1;
        double gamma = 0.99;
        int numEpisodes = 100;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<>(RNG);
        GWViewQValues view = new GWViewQValues(mdp, 20, 20, env);
        view.setGridEnabled(true);
        new DRFrame(view, "SARSA");
        TabularSARSA<GWState, GWAction> agent = new TabularSARSA<>(policy, alpha, gamma);
        agent.addQFunctionObserver(view);
        RLLoop.run(env, agent, numEpisodes);
    }

    private static void TabularQLearning() {
        double alpha = 0.1;
        double gamma = 0.99;
        int numEpisodes = 300;
        GridWorldMDP mdp = new GridWorldMDP(25, 25, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<>(RNG);
        GWViewQValues view = new GWViewQValues(mdp, 20, 20, env);
        view.setGridEnabled(true);
        new DRFrame(view, "QLearning");
        QLearning<GWState, GWAction> agent = new QLearning<>(policy, alpha, gamma);
        agent.addQFunctionObserver(view);
        RLLoop.run(env, agent, numEpisodes);
    }

    private static void TabularSARSALambda() {
        double alpha = 0.1;
        double gamma = 0.99;
        double lambda = 0.9;
        int numEpisodes = 100;
        GridWorldMDP mdp = new GridWorldMDP(20, 20, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<>(RNG);
        GWViewQValues view = new GWViewQValues(mdp, 20, 20, env);
        view.setGridEnabled(true);
        new DRFrame(view, "SARSA Lambda");
        TabularSARSALambda<GWState, GWAction> agent =
                new TabularSARSALambda<>(policy, alpha, gamma, lambda);
        agent.addQFunctionObserver(view);
        RLLoop.run(env, agent, numEpisodes);
    }

    public static void main(String[] args) {
        //        TabularTD0();
        TabularSARSA0();
        //        TabularQLearning();
        //        TabularTDLambda();
        //        TabularSARSALambda();
    }
}
