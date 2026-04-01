package net.davidrobles.rl.algorithms;

import java.util.*;
import net.davidrobles.rl.Environment;
import net.davidrobles.rl.Learner;
import net.davidrobles.rl.policies.Policy;
import net.davidrobles.rl.valuefunctions.TabularVFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;

public class FirstVisit<S, A> implements Learner {
    // Lists all the states visited in the current episode
    private LinkedHashSet<S> visitedStates = new LinkedHashSet<S>();

    // Rewards
    private List<Double> rewards = new ArrayList<Double>();

    // Maps a state with a list of its returns
    private Map<S, List<Double>> stateReturns = new HashMap<S, List<Double>>();

    // Maps a state to the step of its first visit
    private Map<S, Integer> stateFirstOcc = new HashMap<S, Integer>();

    private Environment<S, A> env;
    private int numEpisodes;
    private int currentStep = 0;
    private List<VFunctionObserver<S>> observers = new ArrayList<VFunctionObserver<S>>();
    private TabularVFunction<S> vFunction;
    private Policy<S, A> policy;

    public FirstVisit(Environment<S, A> env, Policy<S, A> policy, int numEpisodes) {
        this.env = env;
        this.policy = policy;
        this.numEpisodes = numEpisodes;
        vFunction = new TabularVFunction<S>();
    }

    @Override
    public void learn() {}
}
