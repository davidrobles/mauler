package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

/**
 * An evaluation function that scores game positions.
 */
public interface EvalFunc<GAME extends Game<GAME>> {

    /** Scores the given game state from the point of view of the given player. */
    double eval(GAME game, int player);
}
