package net.davidrobles.gridworld;

import net.davidrobles.rl.MDP;

import java.util.*;

/**
 * A Grid World Markov Decision Process.
 */
public class GridWorldMDP implements MDP<GWState, GWAction>
{
    private int cols;
    private int rows;
    private GWState startState;
    private GWState[][] states;
    private List<GWState> allStates;
    private List<GWState> terminalStates;
    private Map<GWState, Double> rewards = new HashMap<GWState, Double>();
    private final Random rng;

    public GridWorldMDP(int cols, int rows, Random rng)
    {
        this.cols = cols;
        this.rows = rows;
        this.rng = rng;
        this.states = new GWState[cols][rows];
        this.allStates = new ArrayList<GWState>();
        this.terminalStates = new ArrayList<GWState>();

        createStates();
        createTransitions();
        initTerminalStates();
        setStartState();
    }

    private void setStartState()
    {
        do
        {
            startState = getRandomState();
        }
        while (isTerminal(startState));
    }

    private void initTerminalStates()
    {
        for (GWState state : allStates)
            rewards.put(state, -1.0);

        GWState t1;

        for (int i = 0; i < 1; i++)
        {
            t1 = getRandomState();
            terminalStates.add(t1);
        }

        for (GWState terminalState : terminalStates)
            terminalState.getActionNextStatesMap().clear();
    }

    public void createStates()
    {
        for (int x = 0; x < cols; x++)
        {
            for (int y = 0; y < rows; y++)
            {
                GWState newState = new GWState(x, y);
                states[x][y] = newState;
                allStates.add(newState);
            }
        }
    }

    private void createTransitions()
    {
        for (GWState state : allStates)
        {
            Map<GWAction, Map<GWState, Double>> map = new HashMap<GWAction, Map<GWState, Double>>();

            // UP
            Map<GWState, Double> upTransProb = new HashMap<GWState, Double>();

            if (state.getY() > 0 && state.getY() < rows)
                upTransProb.put(states[state.getX()][state.getY() - 1], 1.0);
            else
                upTransProb.put(states[state.getX()][state.getY()], 1.0);

            map.put(GWAction.UP, upTransProb);

            // LEFT
            Map<GWState, Double> leftTransProb = new HashMap<GWState, Double>();

            if (state.getX() > 0 && state.getX() < rows)
                leftTransProb.put(states[state.getX() - 1][state.getY()], 1.0);
            else
                leftTransProb.put(states[state.getX()][state.getY()], 1.0);

            map.put(GWAction.LEFT, leftTransProb);

            // RIGHT
            Map<GWState, Double> rightTransProb = new HashMap<GWState, Double>();

            if (state.getX() >= 0 && state.getX() < (rows - 1))
                rightTransProb.put(states[state.getX() + 1][state.getY()], 1.0);
            else
                rightTransProb.put(states[state.getX()][state.getY()], 1.0);

            map.put(GWAction.RIGHT, rightTransProb);

            // DOWN
            Map<GWState, Double> downTransProb = new HashMap<GWState, Double>();

            if (state.getY() >= 0 && state.getY() < (rows - 1))
                downTransProb.put(states[state.getX()][state.getY() + 1], 1.0);
            else
                downTransProb.put(states[state.getX()][state.getY()], 1.0);

            map.put(GWAction.DOWN, downTransProb);

            // Don't forget to set the map in the states!
            state.setActionNextStatesMap(map);
        }
    }

    protected GWState getRandomState()
    {
        return allStates.get(rng.nextInt(allStates.size()));
    }

    public int getCols()
    {
        return cols;
    }

    public int getRows()
    {
        return rows;
    }

    public GWState getState(int x, int y)
    {
        return states[x][y];
    }

    public List<GWState> getTerminalStates()
    {
        return terminalStates;
    }

    // MDP Interface

    @Override
    public GWState getStartState()
    {
        return startState;
    }

    @Override
    public List<GWAction> getActions(GWState state)
    {
        return new ArrayList<GWAction>(state.getActionNextStatesMap().keySet());
    }

    @Override
    public List<GWState> getStates()
    {
        return allStates;
    }

    @Override
    public Map<GWState, Double> getTransitions(GWState state, GWAction action)
    {
        if (isTerminal(state))
            return new HashMap<GWState, Double>();

        return state.getActionNextStatesMap().get(action);
    }

    @Override
    public double getReward(GWState state, GWAction action, GWState nextState)
    {
        return rewards.get(state);
    }

    @Override
    public boolean isTerminal(GWState state)
    {
        return terminalStates.contains(state);
    }
}
