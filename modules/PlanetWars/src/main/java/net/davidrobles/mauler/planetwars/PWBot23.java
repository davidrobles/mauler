package net.davidrobles.mauler.planetwars;

import java.util.*;

public class PWBot23 implements PWBot {

    enum PWState {
        EXPAND, GROW_MASTER
    }

    private int playerID;
    private int turns;
    private PWState state = PWState.EXPAND;
    private PWGameState gameState;

    //////////////////
    // Planet lists //
    //////////////////

    private List<PWPlanet> allPlanets;
    private List<PWPlanet> myPlanets;
    private List<PWPlanet> oppPlanets;
    private List<PWPlanet> notMyPlanets;
    private List<PWPlanet> growth5Planets;

    ////////////////////////
    // Individual planets //
    ////////////////////////
    private PWPlanet startOppPlanet;
    private PWPlanet startMyPlanet;
    private PWPlanet masterPlanet;

    // Comparators
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_ASC = new PWPlanetSortByNumShips();
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_NUM_SHIPS_DESC =
            Collections.reverseOrder(PLANETS_SORT_BY_NUM_SHIPS_ASC);
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_ASC = new PWPlanetSortByGrowthRate();
    private static final Comparator<PWPlanet> PLANETS_SORT_BY_GROWTH_RATE_DESC =
            Collections.reverseOrder(PLANETS_SORT_BY_GROWTH_RATE_ASC);

    // Utilities
    private Random rand = new Random();

    public PWBot23(int playerID) {
        this.playerID = playerID;
    }

    // Returns a mapping from a Planet ID to a list of possible moves
    public Map<Integer, List<PWMiniMove>> planetAllMoves(PWGameState gs)
    {
        Map<Integer, List<PWMiniMove>> planetAllMoves = new HashMap<Integer, List<PWMiniMove>>();

        // for each of my planets get the neighbor planets
        for (PWPlanet myPlanet : gs.myPlanets(playerID)) {
            List<PWMiniMove> pMoves = new ArrayList<PWMiniMove>();
            for (PWPlanet adjPlanet : myPlanet.getNeighbors()) {
                // Send all
                pMoves.add(new PWMiniMove(myPlanet, adjPlanet, myPlanet.getNumShips() - 1));
                // Send half
                pMoves.add(new PWMiniMove(myPlanet, adjPlanet, myPlanet.getNumShips() / 2));
            }
            if (!pMoves.isEmpty()) {
                planetAllMoves.put(myPlanet.getPlanetID(), pMoves);
            }
        }
        
        return planetAllMoves;
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
        this.notMyPlanets = gameState.notMyPlanets(playerID);
        this.growth5Planets = gameState.growth5Planets();

        ////////////////
        // BOT START //
        ///////////////

//        // Copy the game state
//        PWGameState newPWGameState = new PWGameState(gameState);
//
//        // List used to add orders during the simulations. Must be cleared.
//        List<PWOrder> playOrders = new ArrayList<PWOrder>();
//
//        // A mapping from a Planet ID to a list of possible moves
//        Map<Integer, List<PWMiniMove>> planetAllMoves = planetAllMoves(newPWGameState);
//
//        // A mapping of the moves to the score
//        Map<PWMiniMove, Double> moveResults = new HashMap<PWMiniMove, Double>();
//
//        // Run 100 simulations
//        for (int i = 0; i < 100; i++)
//        {
//            // Mapping of the move performed for a given move
//            Map<PWMiniMove, Integer> mv = new HashMap<PWMiniMove, Integer>();
//
//            // Make the first moves
//            for (Map.Entry<Integer, List<PWMiniMove>> entry : planetAllMoves.entrySet()) {
//                // Pick a random move from the available moves
//                int ix = entry.getValue().size();
//                int randomMoveIndex = rand.nextInt(ix);
//                PWMiniMove randomMove = entry.getValue().get(randomMoveIndex);
//                // Add the selected move to update the value later during backpropagation
//                mv.put(randomMove, randomMoveIndex);
//                // Add the order
//                int srcPlanetID = randomMove.getSrcPlanet().getPlanetID();
//                int dstPlanetID = randomMove.getDstPlanet().getPlanetID();
//                int numShips = randomMove.getnShips();
//                PWOrder order = new PWOrder(playerID, srcPlanetID, dstPlanetID, numShips);
//                playOrders.add(order);
//            }
//
//            // Make the first moves for each of my planets
//            newPWGameState.makeMoves(playOrders);
//            newPWGameState.doTimeStep();
//
//            // Clear orders for the next game state
//            playOrders.clear();
//
//            int startTurns = newPWGameState.getTurns();
//
//            while (newPWGameState.Winner() < 0 && newPWGameState.getTurns() < (startTurns + 100))
//            {
//                Map<Integer, List<PWMiniMove>> pmoves = planetAllMoves(newPWGameState);
//
//                for (Map.Entry<Integer, List<PWMiniMove>> entry : pmoves.entrySet()) {
//                    // Pick a random move from the available moves
//                    int randomMoveIndex = rand.nextInt(entry.getValue().size());
//                    PWMiniMove randomMove = entry.getValue().get(randomMoveIndex);
//                    int srcPlanetID = randomMove.getSrcPlanet().getPlanetID();
//                    int dstPlanetID = randomMove.getDstPlanet().getPlanetID();
//                    int numShips = randomMove.getnShips();
//                    PWOrder order = new PWOrder(playerID, srcPlanetID, dstPlanetID, numShips);
//                    playOrders.add(order);
//                }
//
//                // Make moves
//                newPWGameState.makeMoves(playOrders);
//                newPWGameState.doTimeStep();
//
//                // Clear orders for the next game state
//                playOrders.clear();
//            }
//
//            double total = evaluate(newPWGameState, playerID);
//
//            // Update the values
//            for (PWMiniMove miniMove : mv.keySet()) {
//                double prevValue = 0;
//                if (moveResults.containsKey(miniMove)) {
//                    prevValue = moveResults.get(miniMove);
//                }
//                moveResults.put(miniMove, prevValue + total);
//            }
//        }
//
//        // Add best orders for each planet
//        for (Map.Entry<Integer, List<PWMiniMove>> entry : planetAllMoves.entrySet()) {
//            double bestScore = Integer.MIN_VALUE;
//            PWMiniMove bestMove = null;
//            for (PWMiniMove move : entry.getValue()) {
//                // TODO: temp fix
//                if (moveResults.containsKey(move)) {
//                    double score = moveResults.get(move);
//                    if (score > bestScore) {
//                        bestMove = move;
//                        bestScore = score;
//                    }
//                }
//            }
//            int srcPlanetID = bestMove.getSrcPlanet().getPlanetID();
//            int dstPlanetID = bestMove.getDstPlanet().getPlanetID();
//            int numShips = bestMove.getnShips();
//            PWOrder order = new PWOrder(playerID, srcPlanetID, dstPlanetID, numShips);
//            orders.add(order);
//        }

        ////////////////
        // BOT END //
        ///////////////

        turns++;
        return expand();
//        return orders;
    }

    ////////////////////
    // HELPER METHODS //
    ////////////////////

    double evaluate(PWGameState gameState, int playerIndex) {
//        if (gameState.NumShips(1) > gameState.NumShips(2))
//            return 1.0;
//        else
//            return -1.0;
        return gameState.NumShips(1) / (double) gameState.NumShips(2);
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

    private List<PWOrder> growMaster() {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        for (PWPlanet myPlanet : myPlanets) {
            if (myPlanet.getNumShips() > 5) {
                PWOrder order = new PWOrder(playerID, myPlanet.getPlanetID(), masterPlanet.getPlanetID(), myPlanet.getNumShips());
                orders.add(order);
            }
        }

        return orders;
    }

    private void identifyMasterPlanet() {
        // Sort not my planets by growth rate (top to bottom)
        Collections.sort(notMyPlanets, PLANETS_SORT_BY_GROWTH_RATE_DESC);

        // Identify the master planet
        int bestDistance = Integer.MAX_VALUE;
        PWPlanet masterPlanet = null;

        for (PWPlanet p : growth5Planets) {
            int myDist = gameState.distance(startMyPlanet.getPlanetID(), p.getPlanetID());
            int oppDist = gameState.distance(startOppPlanet.getPlanetID(), p.getPlanetID());
            int diff = myDist - oppDist;
            if (diff < bestDistance) {
                masterPlanet = p;
                bestDistance = diff;
            }
        }

        this.masterPlanet = masterPlanet;
    }

}

class PWMiniMove {

    private PWPlanet srcPlanet;
    private PWPlanet dstPlanet;
    private int nShips;

    PWMiniMove(PWPlanet srcPlanet, PWPlanet dstPlanet, int nShips) {
        this.srcPlanet = srcPlanet;
        this.dstPlanet = dstPlanet;
        this.nShips = nShips;
    }

    public PWPlanet getSrcPlanet() {
        return srcPlanet;
    }

    public PWPlanet getDstPlanet() {
        return dstPlanet;
    }

    public int getnShips() {
        return nShips;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PWMiniMove that = (PWMiniMove) o;

        if (nShips != that.nShips) return false;
        if (dstPlanet != null ? !dstPlanet.equals(that.dstPlanet) : that.dstPlanet != null) return false;
        if (srcPlanet != null ? !srcPlanet.equals(that.srcPlanet) : that.srcPlanet != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = srcPlanet != null ? srcPlanet.hashCode() : 0;
        result = 31 * result + (dstPlanet != null ? dstPlanet.hashCode() : 0);
        result = 31 * result + nShips;
        return result;
    }
}