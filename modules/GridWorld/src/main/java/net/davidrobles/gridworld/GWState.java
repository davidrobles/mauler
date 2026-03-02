package net.davidrobles.gridworld;

import java.util.Map;

public class GWState
{
    private int x;
    private int y;
    private Map<GWAction, Map<GWState, Double>> actionNextStatesMap;

    public GWState(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Map<GWAction, Map<GWState, Double>> getActionNextStatesMap() {
        return actionNextStatesMap;
    }

    public void setActionNextStatesMap(Map<GWAction, Map<GWState, Double>> actionNextStatesMap) {
        this.actionNextStatesMap = actionNextStatesMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GWState gwState = (GWState) o;

        if (x != gwState.x) return false;
        if (y != gwState.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "GWState{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
