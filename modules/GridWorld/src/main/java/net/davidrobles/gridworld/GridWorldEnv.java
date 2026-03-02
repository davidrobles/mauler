package net.davidrobles.gridworld;

import net.davidrobles.rl.MDPObserver;
import net.davidrobles.rl.RLEnv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GridWorldEnv implements RLEnv<GWState, GWAction>
{
    private GridWorldMDP mdp;
    private GWState currentState;
    private Random rng;
    private List<MDPObserver> observers = new ArrayList<MDPObserver>();

    public GridWorldEnv(GridWorldMDP mdp, Random rng)
    {
        this.mdp = mdp;
        this.rng = rng;
        reset();
    }

    public void notifyCurrentStateChange()
    {
        for (MDPObserver observer : observers)
            observer.currentStateChanged();
    }

    //////////////////////////////
    // RL Environment Interface //
    //////////////////////////////

    @Override
    public GWState getCurrentState()
    {
        return currentState;
    }

    @Override
    public List<GWAction> getPossibleActions(GWState state)
    {
        return mdp.getActions(state);
    }

    @Override
    public double performAction(GWAction action)
    {
        if (!mdp.getActions(currentState).contains(action))
            throw new IllegalArgumentException("Invalid action!");

        Map<GWState, Double> stateDoubleMap = currentState.getActionNextStatesMap().get(action);
        currentState = stateDoubleMap.keySet().iterator().next();
        notifyCurrentStateChange();
//        return reward(currentState);
        return -1;
    }

    @Override
    public void reset()
    {
        currentState = mdp.getStartState();
    }

    @Override
    public boolean isTerminal()
    {
        return mdp.isTerminal(currentState);
    }
}
