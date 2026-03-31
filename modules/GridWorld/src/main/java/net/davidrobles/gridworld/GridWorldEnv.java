package net.davidrobles.gridworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.davidrobles.rl.Environment;
import net.davidrobles.rl.MDPObserver;
import net.davidrobles.rl.StepResult;

public class GridWorldEnv implements Environment<GWState, GWAction> {
    private GridWorldMDP mdp;
    private GWState currentState;
    private Random rng;
    private List<MDPObserver> observers = new ArrayList<MDPObserver>();

    public GridWorldEnv(GridWorldMDP mdp, Random rng) {
        this.mdp = mdp;
        this.rng = rng;
        reset();
    }

    public void notifyCurrentStateChange() {
        for (MDPObserver observer : observers) observer.currentStateChanged();
    }

    //////////////////////////////
    // RL Environment Interface //
    //////////////////////////////

    @Override
    public GWState getCurrentState() {
        return currentState;
    }

    @Override
    public List<GWAction> getActions(GWState state) {
        return mdp.getActions(state);
    }

    @Override
    public StepResult<GWState> step(GWAction action) {
        if (!mdp.getActions(currentState).contains(action))
            throw new IllegalArgumentException("Invalid action!");

        Map<GWState, Double> stateDoubleMap = currentState.getActionNextStatesMap().get(action);
        currentState = stateDoubleMap.keySet().iterator().next();
        notifyCurrentStateChange();
        double reward = mdp.getReward(currentState, action, currentState);
        return new StepResult<>(currentState, reward, isTerminal());
    }

    @Override
    public GWState reset() {
        currentState = mdp.getStartState();
        return currentState;
    }

    private boolean isTerminal() {
        return mdp.isTerminal(currentState);
    }
}
