package net.davidrobles.mauler.strategies.minimax;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;

/** Search algorithms that implement this interface search up to a given max depth. */
public interface DepthLimitedSearch<GAME extends Game<GAME>> extends Strategy<GAME> {
    int move(int maxDepth, GAME game);
}
