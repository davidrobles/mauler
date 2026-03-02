package net.davidrobles.mauler.planetwars;

import java.util.Comparator;

public class PWPlanetSortByDistance implements Comparator<PWPlanet> {

    private PWGameState gameState;
    private PWPlanet dstPlanet;

    public void setModel(PWGameState gameState) {
        this.gameState = gameState;
    }

    public void setDstPlanet(PWPlanet dstPlanet) {
        this.dstPlanet = dstPlanet;
    }

    @Override
    public int compare(PWPlanet p1, PWPlanet p2) {
        assert gameState != null && dstPlanet != null;
        if (gameState.distance(p1.getPlanetID(), dstPlanet.getPlanetID())
                < gameState.distance(p2.getPlanetID(), dstPlanet.getPlanetID()))
            return -1;
        else if (gameState.distance(p1.getPlanetID(), dstPlanet.getPlanetID())
                > gameState.distance(p2.getPlanetID(), dstPlanet.getPlanetID()))
            return 1;
        return 0;
    }

}
