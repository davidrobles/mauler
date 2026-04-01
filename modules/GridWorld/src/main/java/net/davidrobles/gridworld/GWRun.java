package net.davidrobles.gridworld;

import java.util.Random;
import net.davidrobles.gridworld.view.GWVView;
import net.davidrobles.gridworld.view.GWViewQValues;
import net.davidrobles.rl.RLLoop;
import net.davidrobles.rl.agents.TabularQLearning;
import net.davidrobles.rl.agents.TabularSARSA;
import net.davidrobles.rl.agents.TabularSARSALambda;
import net.davidrobles.rl.agents.TabularTD0;
import net.davidrobles.rl.agents.TabularTDLambda;
import net.davidrobles.rl.policies.EpsilonGreedy;
import net.davidrobles.rl.policies.RandomPolicy;
import net.davidrobles.rl.valuefunctions.TabularQFunction;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.util.DRFrame;

public class GWRun {
    private static final Random RNG = new Random();

    private static void tabularTD0() {
        double alpha = 0.01;
        double gamma = 0.99;
        int numEpisodes = 500;
        GridWorldMDP mdp = new GridWorldMDP(50, 50, RNG);
        GridWorldEnv env = new GridWorldEnv(mdp, RNG);
        TabularVFunction<GWState> vTable = new TabularVFunction<>(alpha);
        RandomPolicy<GWState, GWAction> policy = new RandomPolicy<>(RNG);
        GWVView view = new GWVView(mdp, 10, 10, env);
        new DRFrame(view, "TD(0)");
        TabularTD0<GWState, GWAction> agent = new TabularTD0<>(vTable, policy, gamma);
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
        TabularTDLambda<GWState, GWAction> agent =
                new TabularTDLambda<>(vTable, policy, gamma, lambda);
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
        TabularSARSA<GWState, GWAction> agent = new TabularSARSA<>(qTable, policy, gamma);
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
        TabularQLearning<GWState, GWAction> agent = new TabularQLearning<>(qTable, policy, gamma);
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
        TabularSARSALambda<GWState, GWAction> agent =
                new TabularSARSALambda<>(qTable, policy, gamma, lambda);
        agent.addQFunctionObserver(view);
        RLLoop.run(env, agent, policy, numEpisodes);
    }

    public static void main(String[] args) {
        //        tabularTD0();
        tabularSARSA();
        //        tabularQLearning();
        //        tabularTDLambda();
        //        tabularSARSALambda();
    }
}
