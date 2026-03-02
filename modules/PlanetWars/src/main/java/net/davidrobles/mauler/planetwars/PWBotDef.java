package net.davidrobles.mauler.planetwars;

import java.util.*;

public class PWBotDef implements PWBot {

    private PWGameState gameState;
    private int playerID;
    private int turns = 0;
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_ASC = new PWPlanetSortByNumShips();
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_DESC =
            Collections.reverseOrder(PLANETS_SORT_BY_NUM_SHIPS_ASC);
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_ASC = new PWPlanetSortByGrowthRate();
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_DESC =
            Collections.reverseOrder(PLANETS_SORT_BY_GROWTH_RATE_ASC);

    private static final PWPlanetSortByDistance PLANETS_SORT_BY_DIST_ASC = new PWPlanetSortByDistance();

    private static final PWPlanetSortByDistanceAll PLANETS_SORT_BY_DIST_ALL_ASC = new PWPlanetSortByDistanceAll();

    public PWBotDef(int playerID) {
        this.playerID = playerID;
    }

    // Cached data
    private PWPlanet firstMyPlanet = null;
    private PWPlanet firstOppPlanet = null;
    private PWPlanet bestPlanet = null;
    List<PWPlanet> allPlanets;
    List<PWPlanet> myPlanets;
    List<PWPlanet> oppPlanets;
    List<PWPlanet> notMyPlanets;

    private List<PWPlanet> nearestPlanets(PWPlanet fromPlanet) {
        List<PWPlanet> allPlanets = gameState.getAllPlanets();
        PLANETS_SORT_BY_DIST_ASC.setModel(gameState);
        PLANETS_SORT_BY_DIST_ASC.setDstPlanet(fromPlanet);
        Collections.sort(allPlanets, PLANETS_SORT_BY_DIST_ASC);
        return allPlanets;
    }

    private List<PWPlanet> nearestMyPlanets(PWPlanet targetPlanet) {
        PLANETS_SORT_BY_DIST_ASC.setModel(gameState);
        PLANETS_SORT_BY_DIST_ASC.setDstPlanet(targetPlanet);
        Collections.sort(myPlanets, PLANETS_SORT_BY_DIST_ASC);
        return myPlanets;
    }

    private void initData() {
        allPlanets = gameState.getAllPlanets();
        myPlanets = gameState.myPlanets(playerID);
        oppPlanets = gameState.oppPlanets(playerID);
        notMyPlanets = gameState.notMyPlanets(playerID);
    }

    private Map<PWPlanet, Integer> fightPlanetFromMyAllPlanets(PWPlanet targetPlanet) {
        // Total of ships in the planes from which to attack
        int shipsFrom = 0;

        for (PWPlanet myPlanet : myPlanets)
            shipsFrom += myPlanet.getNumShips();

        // Percentage of ships to send from each planet
        double pShips = (targetPlanet.getNumShips() + 50) / (double) shipsFrom;
        pShips = pShips > 1.0 ? 0.75 : pShips;

        Map<PWPlanet, Integer> map = new HashMap<PWPlanet, Integer>();
        int shipsToSendAll = 0;

        PLANETS_SORT_BY_DIST_ASC.setModel(gameState);
        PLANETS_SORT_BY_DIST_ASC.setDstPlanet(targetPlanet);
        Collections.sort(myPlanets, PLANETS_SORT_BY_DIST_ASC);

        for (PWPlanet myPlanet : myPlanets) {
//            int shipsToSend = myPlanet.getNumShips() - 1
//                    + (gameState.distance(myPlanet.getPlanetID(), targetPlanet.getPlanetID()) * targetPlanet.getGrowthRate());
            int shipsToSend = (int) (myPlanet.getNumShips() * pShips);
            map.put(myPlanet, shipsToSend);
            shipsToSendAll += shipsToSend;
//            if (shipsToSendAll > targetPlanet.getNumShips()) {
//                break;
//            }
        }

        return map;
    }

//                            shipsToNeighbor = dstPlanet.getNumShips()
//                                    + (gameState.distance(strongestPlanet.getPlanetID(),
//                                    dstPlanet.getPlanetID()) * dstPlanet.getGrowthRate()) + 1;

    private PWPlanet nearestFromAll() {
        PWPlanet nearest = null;
        int overallBestDist = Integer.MAX_VALUE;

        // tested planet
        for (PWPlanet somePlanet : notMyPlanets) {
            int overallDist = 0;
            for (PWPlanet myPlanet : myPlanets) {
                // check that we are testing a not my planet
                int dist = gameState.distance(myPlanet.getPlanetID(), somePlanet.getPlanetID());
                overallDist += dist;
            }
            if (overallDist < overallBestDist) {
                nearest = somePlanet;
                overallBestDist = overallDist;
            }
        }

        return nearest;
    }

    private Map<Integer, Integer> myPlanetsMap = new HashMap<Integer, Integer>();

    // Returns the balance of the ships on their way the given planet
    // A positive balance means more ships from its owner
    // A negative balance means more ships coming from the opponent
    private int shipsComing(PWPlanet planet) {
        int balance = 0;
        for (PWFleet fleet : gameState.getAllFleets()) {
            if (fleet.getDestinationPlanetID() == planet.getPlanetID()) {
                if (fleet.getOwner() == 1)
                    balance += fleet.getNumShips();
                else
                    balance -= fleet.getNumShips();
            }
        }
        return balance;
    }

    @Override
    public List<PWOrder> getOrders(PWGameState gameState)
    {
        this.gameState = gameState;
        List<PWOrder> orders = new ArrayList<PWOrder>();
        initData();

        if (turns % 5 != 0) {
            turns++;
            return orders;
        }

        // Update map
        myPlanetsMap.clear();
        for (PWPlanet myPlanet : myPlanets)
            myPlanetsMap.put(myPlanet.getPlanetID(), myPlanet.getNumShips());

        // Ships available to send
        int numMyShipsInPlanets = 0;
        for (PWPlanet myPlanet : myPlanets)
            numMyShipsInPlanets += myPlanet.getNumShips();
        numMyShipsInPlanets = (int) (numMyShipsInPlanets * 0.75); // TODO: why 0.75 ??

        int shipCount = numMyShipsInPlanets;

        // The planets that will be attacked (either neutral or opponent planets)
        List<PWPlanet> targetPlanets = new ArrayList<PWPlanet>();

        // Sort not my planets by distance to all my planets
        PLANETS_SORT_BY_DIST_ALL_ASC.setModel(gameState);
        Collections.sort(notMyPlanets, PLANETS_SORT_BY_DIST_ALL_ASC);

        // Select the target planets
        for (PWPlanet targetCandidate : notMyPlanets) {
            if (turns < 30 && targetCandidate.getNumShips() > 40)
                continue;

            int balance = targetCandidate.getNumShips() - shipsComing(targetCandidate);
            // if we need more ships
            if (balance >= 0) {
                if (balance < shipCount) {
                    targetPlanets.add(targetCandidate);
                    shipCount -= balance + 1;
                }
            }
        }

        // Make orders
        for (PWPlanet targetPlanet : targetPlanets) {
            int balance = targetPlanet.getNumShips() - shipsComing(targetPlanet);
            // Ships needed to attack the target planet, excluding its growth
            int shipsNeededExc = balance + 1;
            // List of my planets sorted by their proximity to the target planet
            List<PWPlanet> nearestMyPlanets = nearestMyPlanets(targetPlanet);
            for (PWPlanet nearPlanet : nearestMyPlanets) {
                if (myPlanetsMap.containsKey(nearPlanet.getPlanetID()) && myPlanetsMap.get(nearPlanet.getPlanetID()) > 1) {
                    // ships in this planet taking into account previous orders
                    int shipsRemaining = myPlanetsMap.get(nearPlanet.getPlanetID());
                    // ships needed taking into account the growth of the destination planet
                    int shipsToSendInc = shipsNeededExc;
                    // extra ships needed to compensate the growth
                    int extraGrowthShips = (gameState.distance(nearPlanet.getPlanetID(),
                            targetPlanet.getPlanetID()) * targetPlanet.getGrowthRate()) + 1;
                    if (targetPlanet.getOwner() == 2)
                        shipsToSendInc += extraGrowthShips;
                    // if this planet has all the ships needed to get the destination planet
                    if (shipsRemaining > shipsToSendInc) {
                        assert shipsToSendInc > 0;
                        orders.add(new PWOrder(playerID, nearPlanet.getPlanetID(), targetPlanet.getPlanetID(),
                                shipsToSendInc));
                        assert shipsRemaining - shipsToSendInc > 0;
                        myPlanetsMap.put(nearPlanet.getPlanetID(), shipsRemaining - shipsToSendInc);
                        break;
                    }
                    else {
                        if (shipsRemaining < extraGrowthShips) {
                            int sendTemp = shipsRemaining - 1;
                            shipsNeededExc -= sendTemp - extraGrowthShips;
                            assert sendTemp > 0 : sendTemp;
                            orders.add(new PWOrder(playerID, nearPlanet.getPlanetID(), targetPlanet.getPlanetID(),
                                    sendTemp));
                            myPlanetsMap.put(nearPlanet.getPlanetID(), shipsRemaining - sendTemp);
                        }
                    }
                }
            }
        }

//        // Percentage of ships to send from each planet
//        double pShips = (targetPlanet.getNumShips()) / (double) shipsFrom;
//        pShips = pShips > 1.0 ? 0.75 : pShips;



//
//        initData();
//
//        PWPlanet nearestPlanet = nearestFromAll();
//
//        if (nearestPlanet == null) {
//            turns++;
//            return orders;
//        }
//
//        Map<PWPlanet, Integer> myAttackingPlanets = fightPlanetFromMyAllPlanets(nearestPlanet);
//
//        for (Map.Entry<PWPlanet, Integer> entry : myAttackingPlanets.entrySet()) {
//            orders.add(new PWOrder(playerID, entry.getKey().getPlanetID(), nearestPlanet.getPlanetID(), entry.getValue()));
//        }


        // save data on the first run
//        if (turns == 0) {
//            // Save first planets
//            firstMyPlanet = myPlanets.get(0);
//            firstOppPlanet = oppPlanets.get(0);
//            // Sort them by higher growth rate
//            Collections.sort(allPlanets, PLANETS_SORT_BY_GROWTH_RATE_DESC);
//            // dist to best planet
//            int bestGrowthRate = Integer.MIN_VALUE;
//            // Get the best planet
//            for (PWPlanet planet : allPlanets) {
//                if (gameState.distance(firstMyPlanet.getPlanetID(), planet.getPlanetID()) < 10
//                        && planet.getGrowthRate() > bestGrowthRate) {
//                    bestPlanet = planet;
//                    bestGrowthRate = planet.getGrowthRate();
//                }
//            }
//        }

        // Expand mode
//        if (turns < 30)
//        {
//        if (!myPlanets.isEmpty()) {
//            Collections.sort(myPlanets, PLANETS_SORT_BY_NUM_SHIPS_DESC);
//            PWPlanet strongestPlanet = myPlanets.get(0);
//            Collections.sort(allPlanets, PLANETS_SORT_BY_NUM_SHIPS_ASC);
//            int shipsAllocated = 0;
//
//            for (PWPlanet dstPlanet : allPlanets) {
//                if (dstPlanet != strongestPlanet && dstPlanet.getOwner() != playerID
//                        && gameState.distance(strongestPlanet.getPlanetID(), dstPlanet.getPlanetID()) < 15) {
//                    int shipsToNeighbor = 0;
//
//                    if (dstPlanet.getNumShips() < (strongestPlanet.getNumShips() - shipsAllocated - 2)) {
//                        if (dstPlanet.getOwner() == 2) {
//                            // TODO: bug when sending more ships than allowed
//                            shipsToNeighbor = dstPlanet.getNumShips()
//                                    + (gameState.distance(strongestPlanet.getPlanetID(),
//                                    dstPlanet.getPlanetID()) * dstPlanet.getGrowthRate()) + 1;
//                        } else {
//                            shipsToNeighbor = dstPlanet.getNumShips() + 1;
//                        }
//                        orders.add(new PWOrder(playerID, strongestPlanet.getPlanetID(), dstPlanet.getPlanetID(),
//                                shipsToNeighbor));
//                        shipsAllocated += shipsToNeighbor;
//                    } else if ((strongestPlanet.getNumShips() - shipsAllocated - 2) >= 10) {
//                        shipsToNeighbor = (strongestPlanet.getNumShips() - shipsAllocated) - 1;
//                        orders.add(new PWOrder(playerID, strongestPlanet.getPlanetID(), dstPlanet.getPlanetID(),
//                                shipsToNeighbor));
//                        shipsAllocated += shipsToNeighbor;
//                    }
//                }
//            }
//            }


//            if (!myPlanets.isEmpty())
//            {
//                Collections.sort(myPlanets, PLANETS_SORT_BY_NUM_SHIPS_DESC);
//                PWPlanet strongestPlanet = myPlanets.get(0);
//                PLANETS_SORT_BY_DIST_ASC.setModel(gameState);
//                PLANETS_SORT_BY_DIST_ASC.setDestPlanet(strongestPlanet);
//                Collections.sort(allPlanets, PLANETS_SORT_BY_DIST_ASC);
//                int shipsAllocated = 0;
//
//                for (PWPlanet dstPlanet : allPlanets)
//                {
//                    if (dstPlanet != strongestPlanet && dstPlanet.getOwner() != playerID /*&& neighbor.getNumShips() < 50*/)
//                    {
//                        int shipsToNeighbor = 0;
//
//                        if (dstPlanet.getNumShips() < (strongestPlanet.getNumShips() - shipsAllocated - 2)) {
//                            shipsToNeighbor = dstPlanet.getNumShips() + 1;
//                            orders.add(new PWOrder(playerID, strongestPlanet.getPlanetID(), dstPlanet.getPlanetID(),
//                                    shipsToNeighbor));
//                            shipsAllocated += shipsToNeighbor;
//                        } else if ((strongestPlanet.getNumShips() - shipsAllocated - 2) >= 10) {
//                            shipsToNeighbor = (strongestPlanet.getNumShips() - shipsAllocated) - 1;
//                            orders.add(new PWOrder(playerID, strongestPlanet.getPlanetID(), dstPlanet.getPlanetID(),
//                                    shipsToNeighbor));
//                            shipsAllocated += shipsToNeighbor;
//                        }
//                    }
//                }
//            }
//        }

//        else {
//            System.out.println("Attacking now!");
//            for (PWPlanet myPlanet : myPlanets) {
//                if (myPlanet != bestPlanet) {
//                    orders.add(new PWOrder(playerID, myPlanet.getPlanetID(), bestPlanet.getPlanetID(),
//                            myPlanet.getNumShips() - 1));
//                }
//            }
//        }


//        orders.add(new PWOrder(playerID, strongestPlanet.getPlanetID(), worst.getPlanetID(),
//                strongestPlanet.getNumShips() / 2));

        // 2. get nearest planet
//            int bestScore = Integer.MAX_VALUE;
//            PWPlanet nearestPlanet = null;
//            myPlanets = gameState.myPlanets(playerID);
//            for (PWPlanet myPlanet : myPlanets) {
//                for (PWPlanet otherPlanet : gameState.getAllPlanets()) {
//                    if (myPlanet != otherPlanet) {
//                        if (gameState.distance(myPlanet.getPlanetID(), otherPlanet.getPlanetID()) < bestScore
//                                && otherPlanet.getOwner() != playerID) {
//                            nearestPlanet = otherPlanet;
//                            bestScore = gameState.distance(myPlanet.getPlanetID(), otherPlanet.getPlanetID());
//                        }
//                    }
//                }
//            }
//            if (nearestPlanet != null) {
//                orders.add(new PWOrder(playerID, strongestPlanet.getPlanetID(), nearestPlanet.getPlanetID(),
//                        strongestPlanet.getNumShips() / 2));
//            }


//        }

//        {
//            for (PWPlanet srcPlanet : gameState.myPlanets(playerID))
//            {
//                List<PWPlanet> allPlanets = gameState.getAllPlanets();
//                Collections.sort(allPlanets, PLANETS_SORT_BY_NUM_SHIPS_ASC);
//                int shipsAllocated = 0;
//
//                for (PWPlanet dstPlanet : allPlanets)
//                {
//                    System.out.println(gameState.distance(srcPlanet.getPlanetID(), dstPlanet.getPlanetID()));
//                    if (dstPlanet.getOwner() == 0 && dstPlanet != srcPlanet
//                            && gameState.distance(srcPlanet.getPlanetID(), dstPlanet.getPlanetID()) < 4)
//                    {
//                        int shipsToNeighbor = 0;
//
//                        if (dstPlanet.getNumShips() < (srcPlanet.getNumShips() - shipsAllocated - 2)) {
//                            shipsToNeighbor = dstPlanet.getNumShips() + 1;
//                            orders.add(new PWOrder(playerID, srcPlanet.getPlanetID(), dstPlanet.getPlanetID(),
//                                    shipsToNeighbor));
//                            shipsAllocated += shipsToNeighbor;
//                        } else if ((srcPlanet.getNumShips() - shipsAllocated - 2) >= 10) {
//                            shipsToNeighbor = (srcPlanet.getNumShips() - shipsAllocated) - 1;
//                            orders.add(new PWOrder(playerID, srcPlanet.getPlanetID(), dstPlanet.getPlanetID(),
//                                    shipsToNeighbor));
//                            shipsAllocated += shipsToNeighbor;
//                        }
//                    }
//                }
//            }
//        }

        turns++;

        return orders;
    }

}
