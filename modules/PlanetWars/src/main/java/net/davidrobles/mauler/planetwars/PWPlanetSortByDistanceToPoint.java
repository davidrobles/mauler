package net.davidrobles.mauler.planetwars;

import java.util.Comparator;

public class PWPlanetSortByDistanceToPoint implements Comparator<PWPlanet>
{
    private PWGameState gameState;
    private PWPoint point;

    public void setModel(PWGameState gameState) {
        this.gameState = gameState;
    }

    public void setPoint(PWPoint point) {
        this.point = point;
    }

    @Override
    public int compare(PWPlanet p1, PWPlanet p2) {
        assert gameState != null && point != null;

        double xx = Math.pow(point.getX() - p1.getX(), 2);
        double yy = Math.pow(point.getY() - p1.getY(), 2);

        double distP1 = Math.sqrt(xx + yy);

        xx = Math.pow(point.getX() - p2.getX(), 2);
        yy = Math.pow(point.getY() - p2.getY(), 2);

        double distP2 = Math.sqrt(xx + yy);
        
        if (distP1 < distP2)
            return -1;
        else if (distP1 > distP2)
            return 1;
        return 0;
    }

}
