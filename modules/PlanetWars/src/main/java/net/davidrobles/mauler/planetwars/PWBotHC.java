package net.davidrobles.mauler.planetwars;

import java.util.*;

public class PWBotHC implements PWBot
{
    private int playerID;
    private int turns;
    private PWGameState gameState;

    //////////////////
    // Planet lists //
    //////////////////

    private List<PWPlanet> allPlanets;
    private List<PWPlanet> myPlanets;
    private List<PWPlanet> oppPlanets;
    private List<PWPlanet> neutralPlanets;
    private List<PWPlanet> notMyPlanets;

    ////////////
    // Fleets //
    ////////////
    private List<PWFleet> allFleets;

    ////////////////////////////////////////////////////////////
    // Temporary maps. Must be cleared at the end of the turn //
    ////////////////////////////////////////////////////////////

    private Map<PWPlanet, Integer> shipsAvailable = new LinkedHashMap<PWPlanet, Integer>();
    private Map<PWPlanet, Integer> planetsInDanger = new LinkedHashMap<PWPlanet, Integer>();
    private Map<PWPlanet, Integer> planetsToExpand = new LinkedHashMap<PWPlanet, Integer>();
    private Map<PWPlanet, Integer> planetsToAttack = new LinkedHashMap<PWPlanet, Integer>();

    //////////////////////
    // Ship comparators //
    //////////////////////

    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_ASC = new PWPlanetSortByNumShips();
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_DESC =
                                                            Collections.reverseOrder(PLANETS_SORT_BY_NUM_SHIPS_ASC);
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_ASC = new PWPlanetSortByGrowthRate();
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_DESC =
                                                            Collections.reverseOrder(PLANETS_SORT_BY_GROWTH_RATE_ASC);
    private static final PWPlanetSortByDistance PLANETS_SORT_DIST_TO_PLANET_ASC = new PWPlanetSortByDistance();
    private static final PWPlanetSortByDistanceToPoint PLANETS_SORT_BY_DIST_POINT_ASC =
                                                                                new PWPlanetSortByDistanceToPoint();

    ///////////////////////
    // Fleet comparators //
    ///////////////////////

    private static final Comparator<PWFleet> SORT_FLEETS_TURNS_REM_ASC = new PWFleetSortByTurnsRemaining();
    private static final Comparator<PWFleet> SORT_FLEETS_TURNS_REM_DESC =
                                                                Collections.reverseOrder(SORT_FLEETS_TURNS_REM_ASC);

    // Utilities
    private Random rand = new Random();

    public PWBotHC(int playerID) {
        this.playerID = playerID;
    }

    private List<PWPlanet> cutList(List<PWPlanet> list) {
        if (list.size() > 4) {
            List<PWPlanet> newList = new ArrayList<PWPlanet>();
            for (int i = 0; i < (list.size() / 3); i++) {
                PWPlanet planet = list.get(i);
                newList.add(planet);
            }
            return newList;
        }
        return list;
    }

    private PWPoint centerOfDensity(List<PWPlanet> planets) {
        int xTotal = 0;
        int yTotal = 0;
        for (PWPlanet planet : planets) {
            xTotal += planet.getX();
            yTotal += planet.getY();
        }
        return new PWPoint(xTotal / planets.size(), yTotal / planets.size());
    }

    @Override
    public List<PWOrder> getOrders(PWGameState gameState)
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        // Load variables for caching
        this.gameState = gameState;
        this.allPlanets = gameState.getAllPlanets();
        this.myPlanets = gameState.myPlanets(playerID);
        this.oppPlanets = gameState.oppPlanets(playerID);
        this.neutralPlanets = gameState.neutralPlanets();
        this.notMyPlanets = gameState.notMyPlanets(playerID);
        this.allFleets = gameState.getAllFleets();

        // Calculates the ships available for defense, expansion and attack
        for (PWPlanet myPlanet : myPlanets) {
            PWPlanet futurePlanet = predictPlanetFuture(myPlanet);
            int ships;
            if (futurePlanet.getOwner() == playerID) {
                if (futurePlanet.getNumShips() >= myPlanet.getNumShips()) {
                    ships = myPlanet.getNumShips();
                } else {
                    ships = myPlanet.getNumShips() - futurePlanet.getNumShips();
                }
            } else {
                ships = 0;
            }
            this.shipsAvailable.put(myPlanet, ships);
        }

          //////////////////////////////////
         // Protect the planets in danger //
        //////////////////////////////////

        // Identify them
        for (PWPlanet myPlanet : myPlanets) {
            PWPlanet futurePlanet = predictPlanetFuture(myPlanet);
            if (futurePlanet.getOwner() != playerID) {
                int numShips = futurePlanet.getNumShips();
                planetsInDanger.put(myPlanet, numShips);
            }
        }

        // Sort them by the ships needed for protection (Ascending)
        sortMapByValueAsc(planetsInDanger);

        // Add the required orders
        for (Map.Entry<PWPlanet, Integer> entry : planetsInDanger.entrySet()) {
            List<PWOrder> protectionOrders = ordersToProtectPlanet(entry.getKey());
            validateOrders(protectionOrders);
            if (!protectionOrders.isEmpty())
                orders.addAll(protectionOrders);
        }


          ////////////
         // Expand //
        ////////////
        orders.addAll(expandOrders());

          ////////////
         // Attack //
        ////////////
        orders.addAll(attackOrders());

          /////////////////
         // Clear Cache //
        /////////////////
        shipsAvailable.clear();
        planetsInDanger.clear();
        planetsToExpand.clear();
        planetsToAttack.clear();

        turns++;

        return orders;
    }

    public List<PWOrder> expandOrders()
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        // Get not my planets sorted by distance to my start planet

        PLANETS_SORT_BY_DIST_POINT_ASC.setModel(gameState);
        PLANETS_SORT_BY_DIST_POINT_ASC.setPoint(centerOfDensity(myPlanets));
        Collections.sort(neutralPlanets, PLANETS_SORT_BY_DIST_POINT_ASC);
        neutralPlanets = cutList(neutralPlanets);

        for (PWPlanet neutralPlanet : neutralPlanets) {
            PWPlanet futurePlanet = predictPlanetFuture(neutralPlanet);
            // If the planet ends up being neutral
            if (futurePlanet.getOwner() == 0) {
                int numShips = futurePlanet.getNumShips() + 1;
                planetsToExpand.put(neutralPlanet, numShips);
            }
            // If the planet ends up being neutral
            else if (futurePlanet.getOwner() == 2) {
                int numShips = futurePlanet.getNumShips() + 30; // TODO: heuristic
                planetsToExpand.put(neutralPlanet, numShips);
            }
        }

        for (Map.Entry<PWPlanet, Integer> entry : planetsToExpand.entrySet()) {
            List<PWOrder> expansionOrders = ordersToNeutralPlanet(entry.getKey());
            validateOrders(expansionOrders);
            if (!expansionOrders.isEmpty())
                orders.addAll(expansionOrders);
        }

        return orders;
    }


    public List<PWOrder> attackOrders()
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();
        Collections.sort(oppPlanets, PLANETS_SORT_BY_NUM_SHIPS_ASC);

        for (PWPlanet oppPlanet : oppPlanets) {
            PWPlanet futurePlanet = predictPlanetFuture(oppPlanet);
            if (futurePlanet.getOwner() != playerID) {
                int numShips = futurePlanet.getNumShips() + 40;
                planetsToAttack.put(oppPlanet, numShips);
            }
        }

        for (Map.Entry<PWPlanet, Integer> entry : planetsToAttack.entrySet()) {
            List<PWOrder> attackOrders = ordersToAttackPlanet(entry.getKey());
            orders.addAll(attackOrders);
        }

        return orders;
    }

    public void checkConsistency() {
        for (PWPlanet myPlanet : myPlanets) {
            if (shipsAvailable.get(myPlanet) > myPlanet.getNumShips()) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    System.err.println(shipsAvailable.get(myPlanet) + " > " + myPlanet.getNumShips());
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    /////////////////////
    // ULTIMATE METHOD //
    /////////////////////
    public PWPlanet predictPlanetFuture(PWPlanet planet)
    {
        List<PWFleet> upcomingFleets = new ArrayList<PWFleet>();

        // Identify the upcoming fleets
        for (PWFleet fleet : allFleets) {
            if (fleet.getDestinationPlanetID() == planet.getPlanetID()) {
                upcomingFleets.add(new PWFleet(fleet));
            }
        }

        // If there are no fleets going into the planet
        if (upcomingFleets.isEmpty())
            return planet;

        // Sort the fleets by their turns remaining (top to bottom)
        Collections.sort(upcomingFleets, SORT_FLEETS_TURNS_REM_DESC);

        // Get the turns remaining of the further fleet
        int turns = upcomingFleets.get(0).getTurnsRemaining();

        // Make a copy of the game state to move into the future
        PWGameState newGameState = new PWGameState(gameState);
        List<PWOrder> emptyOrders = new ArrayList<PWOrder>();

        int turnsBefore = newGameState.getTurns();

        // Move into the future
        while (newGameState.Winner() < 0 && newGameState.getTurns() < (turnsBefore + turns)) {
            newGameState.makeMoves(emptyOrders);
            newGameState.doTimeStep();
        }

        // Return future predicted planet 
        return newGameState.getPlanet(planet.getPlanetID());
    }

    public void validateOrders(List<PWOrder> orders) {
        for (PWOrder order : orders) {
            int shipsOnPlanet = gameState.getPlanet(order.getSourcePlanet()).getNumShips();
            if (order.getNumShips() == 0) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (order.getNumShips() > shipsOnPlanet) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    System.err.printf("Sending %d ships, but you can %d", order.getNumShips(), shipsOnPlanet);
                    e.printStackTrace();
                }
            }
        }
    }

    private void reduceShipsMap(PWPlanet planet, int shipsToReduce) {
        int prev = shipsAvailable.get(planet);
        assert shipsToReduce <= prev;
        shipsAvailable.put(planet, prev - shipsToReduce);
    }

    private List<PWOrder> ordersToAttackPlanet(PWPlanet attackedPlanet)
    {
        // Sort my planets by their distance to the planet in danger
        PLANETS_SORT_DIST_TO_PLANET_ASC.setModel(gameState);
        PLANETS_SORT_DIST_TO_PLANET_ASC.setDstPlanet(attackedPlanet);
        Collections.sort(myPlanets, PLANETS_SORT_DIST_TO_PLANET_ASC);
        List<PWOrder> protectionOrders = new ArrayList<PWOrder>();
        int shipsNeeded = planetsToAttack.get(attackedPlanet);

        for (PWPlanet myPlanet : myPlanets) {
            // As long as myPlanet is not in danger, and it has at least 1
            if (!planetsInDanger.containsKey(myPlanet) && shipsAvailable.get(myPlanet) >= 1) {
                int srcID = myPlanet.getPlanetID();
                int dstID = attackedPlanet.getPlanetID();
                int shipsToSend = 0;
                if (shipsAvailable.get(myPlanet) >= shipsNeeded) {
                    shipsToSend = shipsNeeded;
                } /*else {
                    shipsToSend = shipsAvailable.get(myPlanet);
                }*/
                if (shipsToSend >= 1) {
                    shipsNeeded -= shipsToSend;
                    assert shipsToSend <= shipsAvailable.get(myPlanet) : shipsToSend + " <= " + shipsAvailable.get(myPlanet);
                    assert shipsToSend != 0 : "not zero! " + shipsToSend;
                    protectionOrders.add(new PWOrder(playerID, srcID, dstID, shipsToSend));
                    reduceShipsMap(myPlanet, shipsToSend);
                    assert shipsNeeded >= 0;
                    if (shipsNeeded == 0) {
                        break;
                    }
                }
            }
        }

        return protectionOrders;
    }


    private List<PWOrder> ordersToNeutralPlanet(PWPlanet neutralPlanet)
    {
        // Minimium required orders to get a neutral planet
        List<PWOrder> expandOrders = new ArrayList<PWOrder>();

        // Sort my planets by their distance to the planet in danger
        PLANETS_SORT_DIST_TO_PLANET_ASC.setModel(gameState);
        PLANETS_SORT_DIST_TO_PLANET_ASC.setDstPlanet(neutralPlanet);
        Collections.sort(myPlanets, PLANETS_SORT_DIST_TO_PLANET_ASC);

        int shipsNeeded = planetsToExpand.get(neutralPlanet);
        assert shipsNeeded > 0;
        
        for (PWPlanet myPlanet : myPlanets) {
            // As long as myPlanet is not in danger, and it has at least 1
            if (!planetsInDanger.containsKey(myPlanet) && shipsAvailable.get(myPlanet) >= 1) {
                int srcID = myPlanet.getPlanetID();
                int dstID = neutralPlanet.getPlanetID();
                int shipsToSend = 0;
                if (shipsAvailable.get(myPlanet) >= shipsNeeded) {
                    shipsToSend = shipsNeeded;
                } /*else {
                    shipsToSend = shipsAvailable.get(myPlanet);
                }*/
                if (shipsToSend >= 1) {
                    shipsNeeded -= shipsToSend;
                    assert shipsToSend <= shipsAvailable.get(myPlanet) : shipsToSend + " <= " + shipsAvailable.get(myPlanet);
                    expandOrders.add(new PWOrder(playerID, srcID, dstID, shipsToSend));
                    reduceShipsMap(myPlanet, shipsToSend);
                }
            }
//            assert shipsNeeded >= 0;
            if (shipsNeeded == 0) {
                break;
            }
        }

        return expandOrders;
    }

    // Send the needed ships to a planet in danger
    private List<PWOrder> ordersToProtectPlanet(PWPlanet planetInDanger)
    {
        // Minimum orders required to protect the planet in danger
        List<PWOrder> protectionOrders = new ArrayList<PWOrder>();
        
        // Sort my planets by their distance to the planet in danger (ascending)
        PLANETS_SORT_DIST_TO_PLANET_ASC.setModel(gameState);
        PLANETS_SORT_DIST_TO_PLANET_ASC.setDstPlanet(planetInDanger);
        Collections.sort(myPlanets, PLANETS_SORT_DIST_TO_PLANET_ASC);

        int shipsNeeded = planetsInDanger.get(planetInDanger);

        for (PWPlanet myPlanet : myPlanets) {
            // As long as myPlanet is not in danger, and it has at least 1
            if (!planetsInDanger.containsKey(myPlanet) && shipsAvailable.get(myPlanet) >= 1) {
                int srcID = myPlanet.getPlanetID();
                int dstID = planetInDanger.getPlanetID();
                int shipsToSend = 0;
                if (shipsAvailable.get(myPlanet) >= shipsNeeded) {
                    shipsToSend = shipsNeeded;
                } /*else {
                    shipsToSend = shipsAvailable.get(myPlanet);
                } */
                if (shipsToSend >= 1) {
                    shipsNeeded -= shipsToSend;
                    assert shipsToSend <= shipsAvailable.get(myPlanet) : shipsToSend + " <= " + shipsAvailable.get(myPlanet);
                    assert shipsToSend != 0 : "not zero! " + shipsToSend;
                    protectionOrders.add(new PWOrder(playerID, srcID, dstID, shipsToSend));
                    reduceShipsMap(myPlanet, shipsToSend);
                }
            }
            assert shipsNeeded >= 0;
            if (shipsNeeded == 0) {
                break;
            }
        }

        return protectionOrders;
    }

    private Map sortMapByValueAsc(Map<PWPlanet, Integer> map) {
        List<Map.Entry<PWPlanet, Integer>> list = new LinkedList<Map.Entry<PWPlanet, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<PWPlanet, Integer>>() {
            @Override
            public int compare(Map.Entry<PWPlanet, Integer> entry1, Map.Entry<PWPlanet, Integer> entry2) {
                if (entry1.getValue() < entry2.getValue())
                    return -1;
                else if (entry1.getValue() > entry2.getValue()) {
                    return 1;
                }
                return 0;
            }
        });
        Map<PWPlanet, Integer> result = new LinkedHashMap<PWPlanet, Integer>();
        for (Map.Entry<PWPlanet, Integer> entry : result.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private List<PWOrder> expand()
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        for (PWPlanet myPlanet : gameState.myPlanets(playerID)) {
            // Get the nearest 'my planets'
            List<PWPlanet> myNeighbors = myPlanet.getNeighbors();
            // Sort them by the number of ships (bottom to top)
            Collections.sort(myNeighbors, PLANETS_SORT_BY_NUM_SHIPS_ASC);

            int shipsAllocated = 0;

            for (PWPlanet neighbor : myNeighbors) {
                if (neighbor.getOwner() != playerID) {
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

    // Returns the balance of the ships on their way the given planet
    // A positive balance means more ships from its owner
    // A negative balance means more ships coming from the opponent
    private int upcomingShipsBalance(PWPlanet planet) {
        int upcomingBalance = planet.getNumShips();
        for (PWFleet fleet : gameState.getAllFleets()) {
            if (fleet.getDestinationPlanetID() == planet.getPlanetID()) {
                if (fleet.getOwner() == playerID) {
                    upcomingBalance += fleet.getNumShips();
                } else {
                    upcomingBalance -= fleet.getNumShips();
                }
            }
        }
        return upcomingBalance;
    }

}
