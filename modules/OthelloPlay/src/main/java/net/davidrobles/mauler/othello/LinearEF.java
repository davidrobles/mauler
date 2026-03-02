package net.davidrobles.mauler.othello;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.EvalFunc;

/**
 * Linear Evaluation function.
 * @param <GAME> the type of the game to be scored
 */
public interface LinearEF<GAME extends Game<GAME>> extends EvalFunc<GAME>
{
    /**
     * Updates the weights of the linear function with the temporal
     * difference error. This method is called on every time step
     * of an episode.
     * @param game the game
     * @param tdError temporal difference error
     */
    void updateWeights(GAME game, double tdError);
}
