package net.davidrobles.mauler.planetwars;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PWBotMC implements PWBot {

    private int playerID;
    private Random random = new Random();
    private static final int PLAYS_PER_MOVE = 5;
    
    public PWBotMC(int playerID) {
        this.playerID = playerID;
    }

    @Override
    public List<PWOrder> getOrders(PWGameState gameState)
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();

        // 1. Define the set of possible moves 
        List<PWMove> myMoves = getMoves(gameState, playerID);

        // 2. Simulations
        PWMove bestMove = null;
        double bestScore = Double.MIN_VALUE;

        for (PWMove move : myMoves)
        {
            double total = 0;

            // Play N random mauler for every next move
            for (int i = 0; i < PLAYS_PER_MOVE; i++)
            {
                // Copy the game state to be used for the simulations
                PWGameState playGameState = new PWGameState(gameState);
                // List of orders to make the first move in the simulation
                List<PWOrder> playOrders = new ArrayList<PWOrder>();
                // Add this player move
                if (move.getSrcPlanet() != null) {
                    playOrders.add(new PWOrder(move.getSrcPlanet().getOwner(), move.getSrcPlanet().getPlanetID(),
                            move.getDstPlanet().getPlanetID(), move.getnShips()));
                }
                // Add other player move
                PWMove oppMove = getRandomMove(playGameState, 2);
                if (oppMove.getSrcPlanet() != null) {
                    playOrders.add(new PWOrder(oppMove.getSrcPlanet().getOwner(),
                            oppMove.getSrcPlanet().getPlanetID(), oppMove.getDstPlanet().getPlanetID(),
                            oppMove.getnShips()));
                }
                // Make first moves
                playGameState.makeMoves(playOrders);
                playGameState.doTimeStep();

                int turnsBefore = playGameState.getTurns();

                while (playGameState.Winner() < 0 && playGameState.getTurns() < (turnsBefore + 100)) {
                    playOrders.clear();
                    // Add my move
                    PWMove myMove = getRandomMove(playGameState, playerID);
                    if (myMove.getSrcPlanet() != null) {
                        playOrders.add(new PWOrder(myMove.getSrcPlanet().getOwner(),
                                myMove.getSrcPlanet().getPlanetID(), myMove.getDstPlanet().getPlanetID(),
                                myMove.getnShips()));
                    }
                    // Add opp move
                    PWMove oppFirstMove = getRandomMove(playGameState, 2);
                    if (oppFirstMove.getSrcPlanet() != null) {
                        playOrders.add(new PWOrder(oppFirstMove.getSrcPlanet().getOwner(),
                                oppFirstMove.getSrcPlanet().getPlanetID(), oppFirstMove.getDstPlanet().getPlanetID(),
                                oppFirstMove.getnShips()));
                    }
                    // Make moves
                    playGameState.makeMoves(playOrders);
                    playGameState.doTimeStep();
                }

                total += evaluate(playGameState, playerID);
            }

            if (bestMove == null || total > bestScore) {
                bestMove = move;
                bestScore = total;
            }
        }

        if (bestMove.getSrcPlanet() != null) {
            orders.add(new PWOrder(playerID, bestMove.getSrcPlanet().getPlanetID(), bestMove.getDstPlanet().getPlanetID(),
                    bestMove.getnShips()));
        }

        return orders;
    }

    double evaluate(PWGameState gameState, int playerIndex) {
        if (gameState.NumShips(1) > gameState.NumShips(2))
            return 1.0;
        else
            return -1.0; 
    }

    PWMove getRandomMove(PWGameState gameState, int playerID) {
        List<PWMove> moves = getMoves(gameState, playerID);
        return moves.get(random.nextInt(moves.size()));
    }

    List<PWMove> getMoves(PWGameState gameState, int playerID) {
        List<PWMove> moves = new ArrayList<PWMove>();
        for (PWPlanet srcPlanet : gameState.myPlanets(playerID)) {
            for (PWPlanet dstPlanet : srcPlanet.getNeighbors()) {
//            for (PWPlanet dstPlanet : srcPlanet.getNeighbors()) {
                if (srcPlanet != dstPlanet && srcPlanet.getNumShips() > 3) {
                    moves.add(new PWMove(srcPlanet, dstPlanet, srcPlanet.getNumShips() / 2));
                    //moves.add(new PWMove(srcPlanet, dstPlanet, srcPlanet.getNumShips() - 1));
                }
            }
        }

        // Add no move
        moves.add(new PWMove(null, null, -1));

        return moves;
    }

}

class PWMove {

    private PWPlanet srcPlanet;
    private PWPlanet dstPlanet;
    private int nShips;

    PWMove(PWPlanet srcPlanet, PWPlanet dstPlanet, int nShips) {
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
}