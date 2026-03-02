package net.davidrobles.mauler.players.minimax;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;

/**
 * Search algorithms that implement this interface search up to a given max depth.
 */
public interface DepthLimitedSearch<GAME extends Game<GAME>> extends Player<GAME>
{
    int move(int maxDepth, GAME game);
}
