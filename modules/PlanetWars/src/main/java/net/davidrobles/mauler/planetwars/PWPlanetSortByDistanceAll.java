package net.davidrobles.mauler.planetwars;

import java.util.Comparator;

public class PWPlanetSortByDistanceAll implements Comparator<PWPlanet> {

    private PWGameState gameState;

    public void setModel(PWGameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public int compare(PWPlanet p1, PWPlanet p2)
    {
        int p1Dist = 0;

        for (PWPlanet myPlanet : gameState.myPlanets(1)) {
            // check that we are testing a not my planet
            int dist = gameState.distance(myPlanet.getPlanetID(), p1.getPlanetID());
            p1Dist += dist;
        }

        int p2Dist = 0;

        for (PWPlanet myPlanet : gameState.myPlanets(1)) {
            // check that we are testing a not my planet
            int dist = gameState.distance(myPlanet.getPlanetID(), p2.getPlanetID());
            p2Dist += dist;
        }

        if (p1Dist < p2Dist)
            return -1;
        else if (p1Dist > p2Dist)
            return 1;
        return 0;
    }

}
