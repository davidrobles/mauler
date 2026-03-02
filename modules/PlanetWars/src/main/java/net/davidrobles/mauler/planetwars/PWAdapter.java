package net.davidrobles.mauler.planetwars;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PWAdapter {

    private static List<PWBot> bots = new ArrayList<PWBot>();

    ///////////////////////////////////
    // CODE TO PLAY IN THE REAL GAME //
    ///////////////////////////////////

    static {
        bots.add(new PWBotHC(1));
        bots.add(new PWBotTest(1));
    }

    // Interface to the real game
    // Don't modify
    public static List<PWOrder> getOrders(PlanetWars pw, int botIndex)
    {
        PWGameState gameState = new PWGameState(makeGameString(pw), bots);
        return bots.get(botIndex).getOrders(gameState);
    }

    ///////////////////////////////////
    // CODE TO PLAY IN THE REAL GAME //
    ///////////////////////////////////

    private static String makeGameString(PlanetWars pw)
    {
        StringBuilder s = new StringBuilder();

        for (Planet p : pw.Planets()) {
            // We can't use String.format here because in certain locales, the ,
            // and . get switched for X and Y (yet just appending them using the
            // default toString methods apparently doesn't switch them?)
            s.append("P " + p.X() + " " + p.Y() + " " + PovSwitch(-1, p.Owner()) +
                    " " + p.NumShips() + " " + p.GrowthRate() + "\n");

        }
        for (Fleet f : pw.Fleets()) {
            s.append("F " + PovSwitch(-1, f.Owner()) + " " + f.NumShips() + " " +
                    f.SourcePlanet() + " " + f.DestinationPlanet() + " " +
                    f.TotalTripLength() + " " + f.TurnsRemaining() + "\n");

        }
        return s.toString();
    }

    public static int PovSwitch(int pov, int playerID) {
        if (pov < 0) return playerID;
        if (playerID == pov) return 1;
        if (playerID == 1) return pov;
        return playerID;
    }

    public static String loadMapToString(String mapFilename)
    {
        StringBuffer gameStateString = new StringBuffer();
        BufferedReader in;

        try {
            in = new BufferedReader(new FileReader(mapFilename));
            String line;
            while ((line = in.readLine()) != null) {
                gameStateString.append(line);
                gameStateString.append("\n");
            }
        } catch (Exception ignored) { }

        return gameStateString.toString();
    }


}
