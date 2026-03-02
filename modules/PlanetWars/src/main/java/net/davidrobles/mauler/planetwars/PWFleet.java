package net.davidrobles.mauler.planetwars;

public class PWFleet {

    private int owner;
    private int numShips;
    private int sourcePlanet;
    private int destinationPlanet;
    private int totalTripLength;
    private int turnsRemaining;

    public PWFleet(int owner, int numShips, int sourcePlanet, int destinationPlanet, int totalTripLength,
                 int turnsRemaining) {
        this.owner = owner;
        this.numShips = numShips;
        this.sourcePlanet = sourcePlanet;
        this.destinationPlanet = destinationPlanet;
        this.totalTripLength = totalTripLength;
        this.turnsRemaining = turnsRemaining;
    }

    public PWFleet(PWFleet fleet) {
        this.owner = fleet.owner;
        this.numShips = fleet.numShips;
        this.sourcePlanet = fleet.sourcePlanet;
        this.destinationPlanet = fleet.destinationPlanet;
        this.totalTripLength = fleet.totalTripLength;
        this.turnsRemaining = fleet.turnsRemaining;
    }

    public PWFleet(int owner, int numShips) {
        this.owner = owner;
        this.numShips = numShips;
        this.sourcePlanet = -1;
        this.destinationPlanet = -1;
        this.totalTripLength = -1;
        this.turnsRemaining = -1;
    }

    public int getOwner() {
        return owner;
    }

    public int getNumShips() {
        return numShips;
    }

    public int getSourcePlanetID() {
        return sourcePlanet;
    }

    public int getDestinationPlanetID() {
        return destinationPlanet;
    }

    public int getTotalTripLength() {
        return totalTripLength;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public void move() {
        if (turnsRemaining > 0) {
            --turnsRemaining;
        } else {
            turnsRemaining = 0;
        }
    }

    public void kill() {
        owner = 0;
        numShips = 0;
        turnsRemaining = 0;
    }
    
}
