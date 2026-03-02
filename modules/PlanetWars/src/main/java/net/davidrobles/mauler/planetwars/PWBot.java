package net.davidrobles.mauler.planetwars;

import java.util.List;

public interface PWBot {

    List<PWOrder> getOrders(PWGameState gameState);
    
}
