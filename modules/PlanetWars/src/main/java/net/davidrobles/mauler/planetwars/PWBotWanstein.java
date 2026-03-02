package net.davidrobles.mauler.planetwars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PWBotWanstein implements PWBot {

    private int playerID;
    private int turns = 0;
    private PWGameState gameState;

    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_ASC = new PWPlanetSortByNumShips();
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_DESC =
                                                        Collections.reverseOrder(PLANETS_SORT_BY_NUM_SHIPS_ASC);
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_ASC = new PWPlanetSortByGrowthRate();
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_DESC =
                                                        Collections.reverseOrder(PLANETS_SORT_BY_GROWTH_RATE_ASC);

    public PWBotWanstein(int playerID) {
        this.playerID = playerID;
    }

    @Override
    public List<PWOrder> getOrders(PWGameState gameState)
    {
        this.gameState = gameState;

        // Start here
        List<PWPlanet> myPlanets = gameState.myPlanets(playerID);

        if (myPlanets.size() < 3) {
            return expand();
        }

        if (gameState.NumShips(1) > gameState.NumShips(2))
            return expand();
        else
            return sendtoBest();

        // End here
    }

    private PWPlanet bestPlanet = null;
    private int bestGrowth = Integer.MIN_VALUE;

    private List<PWOrder> sendtoBest()
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        for (PWPlanet myPlanet : gameState.myPlanets(playerID))
        {
            List<PWPlanet> myNeighbors = myPlanet.getNeighbors();

            for (PWPlanet neighbor : myNeighbors) {
                if (neighbor.getGrowthRate() > bestGrowth) {
                    bestPlanet = neighbor;
                    bestGrowth = neighbor.getGrowthRate();
                }
            }
        }

        for (PWPlanet myPlanet : gameState.myPlanets(playerID)) {
            if (myPlanet != bestPlanet) {
                orders.add(new PWOrder(playerID, myPlanet.getPlanetID(), bestPlanet.getPlanetID(),
                        myPlanet.getNumShips() - 1));
            }
        }

        return orders;
    }

    private List<PWOrder> expand()
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        for (PWPlanet myPlanet : gameState.myPlanets(playerID))
        {
            List<PWPlanet> myNeighbors = myPlanet.getNeighbors();
            Collections.sort(myNeighbors, PLANETS_SORT_BY_NUM_SHIPS_ASC);

            int shipsAllocated = 0;

            for (PWPlanet neighbor : myNeighbors)
            {
                if (neighbor.getOwner() != playerID)
                {
                    int shipsToNeighbor = 0;

                    if (neighbor.getNumShips() < (myPlanet.getNumShips() - shipsAllocated - 2)) {
                        shipsToNeighbor = neighbor.getNumShips() + 1;
                        orders.add(new PWOrder(playerID, myPlanet.getPlanetID(), neighbor.getPlanetID(),
                                shipsToNeighbor));
                        shipsAllocated += shipsToNeighbor;
                    } else if ((myPlanet.getNumShips() - shipsAllocated - 2) >= 10) {
                        shipsToNeighbor = (myPlanet.getNumShips() - shipsAllocated) - 1;
                        orders.add(new PWOrder(playerID, myPlanet.getPlanetID(), neighbor.getPlanetID(),
                                shipsToNeighbor));
                        shipsAllocated += shipsToNeighbor;
                    }
                }
            }
        }
        
        return orders;
    }

}
