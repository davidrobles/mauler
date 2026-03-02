package net.davidrobles.mauler.planetwars;

import java.util.Comparator;

public class PWFleetSortByNumShips implements Comparator<PWFleet> {

    @Override
    public int compare(PWFleet f1, PWFleet f2) {
        if (f1.getNumShips() < f2.getNumShips())
            return -1;
        else if (f1.getNumShips() > f2.getNumShips())
            return 1;
        return 0;
    }

}
