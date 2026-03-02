package net.davidrobles.mauler.planetwars;

import java.util.ArrayList;
import java.util.List;

public class PWBotTest implements PWBot {

    private int playerID;

    public PWBotTest(int playerID) {
        this.playerID = playerID;
    }

    @Override
    public List<PWOrder> getOrders(PWGameState gameState)
    {
        List<PWOrder> orders = new ArrayList<PWOrder>();
        
        // (1) If we currently have a fleet in flight, just do nothing.
//        if (gameState.myFleets(playerID).size() >= 1) {
//            return orders;
//        }

        // (2) Find my strongest planet.
        PWPlanet source = null;
        double sourceScore = Double.MIN_VALUE;

        for (PWPlanet p : gameState.myPlanets(playerID)) {
            double score = (double) p.getNumShips();
            if (score > sourceScore) {
                sourceScore = score;
                source = p;
            }
        }

        // (3) Find the weakest enemy or neutral planet.
        PWPlanet dest = null;
        double destScore = Double.MIN_VALUE;

        for (PWPlanet p : gameState.notMyPlanets(playerID)) {
            double score = 1.0 / (1 + p.getNumShips());
            if (score > destScore) {
                destScore = score;
                dest = p;
            }
        }

        // (4) Send half the ships from my strongest planet to the weakest
        // planet that I do not own.
        if (source != null && dest != null) {
            int numShips = source.getNumShips() / 2;
            orders.add(new PWOrder(playerID, source.getPlanetID(), dest.getPlanetID(), numShips));
        }

        return orders;
    }

}

