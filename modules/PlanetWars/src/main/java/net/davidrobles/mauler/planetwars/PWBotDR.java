package net.davidrobles.mauler.planetwars;

import java.util.*;

public class PWBotDR implements PWBot {

    private int playerID;
    private int turns = 0;
    private PWGameState gameState;

    // Planet comparators
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_ASC = new PWPlanetSortByNumShips();
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_DESC =
                                                        Collections.reverseOrder(PLANETS_SORT_BY_NUM_SHIPS_ASC);

    private static final PWPlanetSortByDistance PLANETS_SORT_BY_DIST_ASC = new PWPlanetSortByDistance();
//    private static final PWPlanetSortByDistance PLANETS_SORT_BY_DIST_DESC =
//            (PWPlanetSortByDistance) Collections.reverseOrder(new PWPlanetSortByDistance());

    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_ASC = new PWPlanetSortByGrowthRate();

    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_DESC =
                                                        Collections.reverseOrder(PLANETS_SORT_BY_GROWTH_RATE_ASC);

    // Fleet comparators
//    private static final Comparator<PWFleet> FLEETS_SORT_BY_NUM_SHIPS = new PWFleetSortByNumShips();
//    private static final Comparator<PWFleet> FLEETS_SORT_BY_TURNS_REMAINING = new PWFleetSortByTurnsRemaining();

    public PWBotDR(int playerID) {
        this.playerID = playerID;
    }

    @Override
    public List<PWOrder> getOrders(PWGameState gameState)
    {
        this.gameState = gameState;
        return expand2();
    }

    private List<PWOrder> expand2()
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        for (PWPlanet myPlanet : gameState.myPlanets(playerID))
        {
            List<PWPlanet> myNeighbors = myPlanet.getNeighbors();
            Collections.sort(myNeighbors, PLANETS_SORT_BY_NUM_SHIPS_ASC);
//            Collections.sort(myNeighbors, PLANETS_SORT_BY_GROWTH_RATE_DESC);

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

//            Collections.sort(myNeighbors, PLANETS_SORT_BY_GROWTH_RATE_DESC);
//
//            end:
//            if ((myPlanet.getNumShips() - shipsAllocated) > 5) {
//                for (PWPlanet neighbor : myNeighbors) {
//                    if (neighbor.getOwner() == playerID) {
//                        if (myPlanet.getGrowthRate() < neighbor.getGrowthRate()) {
//                            if (myPlanet.getNumShips() > 1) {
//                                orders.add(new PWOrder(playerID, myPlanet.getPlanetID(), neighbor.getPlanetID(),
//                                        myPlanet.getNumShips() - shipsAllocated - 1));
//                                break end;
//                            }
//                        }
//                    }
//                }
//            }
        }
        
        return orders;
    }

    public boolean myFleeGoingToPlanet(PWPlanet planet) {
        List<PWFleet> myFleets = gameState.myFleets(playerID);
        for (PWFleet fleet : myFleets) {
            if (fleet.getOwner() != playerID
                || (fleet.getOwner() == playerID && fleet.getDestinationPlanetID() == planet.getPlanetID())) {
                return true;
            }
        }
        return false;
    }

    public boolean myFleeGoingToOppPlanet(PWPlanet planet) {
        List<PWFleet> myFleets = gameState.myFleets(playerID);
        for (PWFleet fleet : myFleets) {
            if (fleet.getOwner() == playerID && fleet.getDestinationPlanetID() == planet.getPlanetID()) {
                return true;
            }
        }
        return false;
    }

    private List<PWOrder> expand()
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        // Find my strongest planet
        PWPlanet strongestPlanet = null;
        LinkedList<PWPlanet> myPlanetsByNumShipsDesc = myPlanetsSorted(PLANETS_SORT_BY_NUM_SHIPS_DESC);
        if (!myPlanetsByNumShipsDesc.isEmpty())
            strongestPlanet = myPlanetsByNumShipsDesc.getFirst();

//        if (strongestPlanet == null)
//            return orders;

        // Set destination planet to use distance comparator
        PLANETS_SORT_BY_DIST_ASC.setDstPlanet(strongestPlanet);

        // Expand
        LinkedList<PWPlanet> neutralPlanetsByNumShipsAsc = neutralPlanetsSorted(PLANETS_SORT_BY_NUM_SHIPS_ASC);
        LinkedList<PWPlanet> neutralPlanetsByDistAsc = neutralPlanetsSorted(PLANETS_SORT_BY_DIST_ASC);
        List<PWPlanet> planetsToExpand = new ArrayList<PWPlanet>();
        int shipsToSend = 0;

        for (PWPlanet planet : neutralPlanetsByDistAsc) {
//            System.out.println("Planet dist: "+ gameState.distance(strongestPlanet.getPlanetID(),
//                    planet.getPlanetID()));
            if ((strongestPlanet.getNumShips() - shipsToSend) > (planet.getNumShips() + 1)) {
                if (!myFleeGoingToPlanet(planet)) {
                    planetsToExpand.add(planet);
                    shipsToSend += planet.getNumShips() + 1;
                }
            }
        }

        for (PWPlanet planet : planetsToExpand) {
            orders.add(new PWOrder(playerID, strongestPlanet.getPlanetID(), planet.getPlanetID(),
                    planet.getNumShips() + 1));
        }
//        System.out.println("()()()()()()()()()()(()()()(");
        return orders;
    }

    private List<PWOrder> attack()
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        // Get my planets sorted by number of ships (top to bottom)
        LinkedList<PWPlanet> myPlanetsByNumShipsDesc = myPlanetsSorted(PLANETS_SORT_BY_NUM_SHIPS_DESC);

        // Get opponent planets with the least number of ships
        LinkedList<PWPlanet> oppPlanetsByNumShipsAsc = oppPlanetsSorted(PLANETS_SORT_BY_NUM_SHIPS_ASC);
        PWPlanet oppWeakestPlanet = null;
        if (!oppPlanetsByNumShipsAsc.isEmpty())
            oppWeakestPlanet = oppPlanetsByNumShipsAsc.getFirst();

        // Pick the my planets and how many number of ships to send to the attacked planet
        Map<PWPlanet, Integer> map = new HashMap<PWPlanet, Integer>();
        int shipsToSend = 0;

        for (PWPlanet planet : myPlanetsByNumShipsDesc) {
            if (shipsToSend < (oppWeakestPlanet.getNumShips() + 80)) {
                shipsToSend += planet.getNumShips() - 1; 
                map.put(planet, planet.getNumShips() - 1);
            } else {
                break;
            }
        }

        for (Map.Entry<PWPlanet, Integer> entry : map.entrySet()) {
            orders.add(new PWOrder(playerID, entry.getKey().getPlanetID(),
                    oppWeakestPlanet.getPlanetID(), entry.getValue()));
        }

        return orders;
    }


    // Helper methods

//    private PWPlanet myPlanetWithMostShips()
//    {
//        PWPlanet planet = null;
//        double score = Double.MIN_VALUE;
//
//        for (PWPlanet p : gameState.myPlanets(playerID))
//        {
//            double tmpScore = (double) p.getNumShips();
//
//            if (tmpScore > score) {
//                score = tmpScore;
//                planet = p;
//            }
//        }
//
//        return planet;
//    }

    private LinkedList<PWPlanet> myPlanetsSorted(Comparator<PWPlanet> comparator) {
        LinkedList<PWPlanet> myPlanets = gameState.myPlanets(playerID);
        Collections.sort(myPlanets, comparator);
        return myPlanets;
    }

    private List<PWPlanet> notMyPlanetsSorted(Comparator<PWPlanet> comparator) {
        List<PWPlanet> notMyPlanets = gameState.notMyPlanets(playerID);
        Collections.sort(notMyPlanets, comparator);
        return notMyPlanets;
    }

    private LinkedList<PWPlanet> oppPlanetsSorted(Comparator<PWPlanet> comparator) {
        LinkedList<PWPlanet> oppPlanets = gameState.oppPlanets(playerID);
        Collections.sort(oppPlanets, comparator);
        return oppPlanets;
    }

    private LinkedList<PWPlanet> neutralPlanetsSorted(Comparator<PWPlanet> comparator) {
        LinkedList<PWPlanet> neutralPlanets = gameState.neutralPlanets();
        Collections.sort(neutralPlanets, comparator);
        return neutralPlanets;
    }

    private PWPlanet oppPlanetWithMostShips()
    {
        PWPlanet planet = null;
        double score = Double.MIN_VALUE;

        for (PWPlanet p : gameState.oppPlanets(playerID))
        {
            double tmpScore = (double) p.getNumShips();

            if (tmpScore > score) {
                score = tmpScore;
                planet = p;
            }
        }

        return planet;
    }

    private boolean fleetInFlight() {
        return gameState.myFleets(playerID).size() >= 1;
    }

}

