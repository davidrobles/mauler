package net.davidrobles.mauler.planetwars;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PWPlanet {

    private int planetID;
    private int owner;
    private int numShips;
    private int growthRate;
    private double x, y;
    private Color highColor;

    // Hack
    private List<PWPlanet> neighbors = new ArrayList<PWPlanet>();

    public PWPlanet(int planetID, int owner, int numShips, int growthRate, double x, double y) {
        this.planetID = planetID;
        this.owner = owner;
        this.numShips = numShips;
        this.growthRate = growthRate;
        this.x = x;
        this.y = y;
    }

    public PWPlanet(PWPlanet planet) {
        this.planetID = planet.planetID;
        this.owner = planet.owner;
        this.numShips = planet.numShips;
        this.growthRate = planet.growthRate;
        this.x = planet.x;
        this.y = planet.y;
    }

    public int getPlanetID() {
        return planetID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getOwner() {
        return owner;
    }

    public int getGrowthRate() {
        return growthRate;
    }

    public int getNumShips() {
        return numShips;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setNumShips(int numShips) {
        this.numShips = numShips;
    }

    public void addShips(int amount) {
        numShips += amount;
    }

    public void removeShips(int amount) {
        numShips -= amount;
    }

    public void addNeighbor(PWPlanet planet) {
        neighbors.add(planet);
    }

    public List<PWPlanet> getNeighbors() {
        return neighbors;
    }

    public void setHighColor(Color highColor) {
        this.highColor = highColor;
    }

    public Color getHighColor() {
        return highColor;
    }

    @Override
    public String toString() {
        return "Planet {ID:" + planetID + ", Owner:" + owner + ", NumShips:" + numShips
                + ", growthRate:" + growthRate + ", X:" + x + ", Y:" + y + "}"; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PWPlanet pwPlanet = (PWPlanet) o;

        if (planetID != pwPlanet.planetID) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return planetID;
    }
    
}
