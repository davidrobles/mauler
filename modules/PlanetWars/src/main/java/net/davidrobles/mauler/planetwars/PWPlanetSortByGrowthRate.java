package net.davidrobles.mauler.planetwars;

import java.util.Comparator;

public class PWPlanetSortByGrowthRate implements Comparator<PWPlanet> {

    @Override
    public int compare(PWPlanet p1, PWPlanet p2) {
        if (p1.getGrowthRate() < p2.getGrowthRate())
            return -1;
        else if (p1.getGrowthRate() > p2.getGrowthRate())
            return 1;
        return 0;
    }

}
