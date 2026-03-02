package net.davidrobles.mauler.planetwars;

public class PWOrder {

    private int playerID;
    private int sourcePlanet;
    private int destinationPlanet;
    private int numShips;

    public PWOrder(int playerID, int sourcePlanet, int destinationPlanet, int numShips) {
        this.playerID = playerID;
        this.sourcePlanet = sourcePlanet;
        this.destinationPlanet = destinationPlanet;
        this.numShips = numShips;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getSourcePlanet() {
        return sourcePlanet;
    }

    public int getDestinationPlanet() {
        return destinationPlanet;
    }

    public int getNumShips() {
        return numShips;
    }
    
}
