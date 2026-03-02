package net.davidrobles.mauler.planetwars;

import java.util.Comparator;

public class PWFleetSortByTurnsRemaining implements Comparator<PWFleet> {

    @Override
    public int compare(PWFleet f1, PWFleet f2) {
        if (f1.getTurnsRemaining() < f2.getTurnsRemaining())
            return -1;
        else if (f1.getTurnsRemaining() > f2.getTurnsRemaining())
            return 1;
        return 0;
    }

}
