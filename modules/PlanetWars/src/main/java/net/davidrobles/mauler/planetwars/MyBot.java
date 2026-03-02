package net.davidrobles.mauler.planetwars;

import java.util.*;

public class MyBot
{
    public static void DoTurn(PlanetWars pw)
    {
        // Sends the game state to the our experimental model
        List<PWOrder> orders = PWAdapter.getOrders(pw, 0);

        // Issues the orders to the real game engine
        for (PWOrder order : orders) {
            pw.IssueOrder(order.getSourcePlanet(), order.getDestinationPlanet(), order.getNumShips());
        }
    }

    public static void main(String[] args) {
        String line = "";
        String message = "";
        int c;
        try {
            while ((c = System.in.read()) >= 0) {
                switch (c) {
                    case '\n':
                        if (line.equals("go")) {
                            PlanetWars pw = new PlanetWars(message);
                            DoTurn(pw);
                            pw.FinishTurn();
                            message = "";
                        } else {
                            message += line + "\n";
                        }
                        line = "";
                        break;
                    default:
                        line += (char) c;
                        break;
                }
            }
        } catch (Exception e) {
            // Owned.
        }
    }
}
