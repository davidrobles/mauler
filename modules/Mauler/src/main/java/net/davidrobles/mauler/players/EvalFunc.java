package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

/**
 * A heuristic or utility function that scores a game position from one player's perspective.
 *
 * <p>Used by tree-search players (Minimax, AlphaBeta, Negamax) as the leaf evaluation
 * at terminal states or depth-limit cutoffs, and by policy players (GreedyPlayer,
 * EpsilonGreedy) to rank the immediate successors of the current position.
 *
 * <p><b>Return value convention:</b> higher scores are better for {@code player}.
 * The exact range is implementation-defined:
 * <ul>
 *   <li>Terminal utility: {@code +1} win, {@code 0} draw, {@code -1} loss
 *       (see {@link UtilFunc})</li>
 *   <li>Heuristic: any real-valued score where larger means more favorable</li>
 * </ul>
 *
 * <p><b>Terminal vs. non-terminal:</b> implementations must document which
 * game states they accept. {@link UtilFunc} only handles terminal states and
 * throws on non-terminal input; heuristic functions typically handle both.
 *
 * @param <GAME> the game type
 *
 * @see UtilFunc
 * @see GreedyPlayer
 */
public interface EvalFunc<GAME extends Game<GAME>>
{
    /**
     * Scores the given game state from the perspective of {@code player}.
     *
     * @param game   the game state to evaluate
     * @param player the player index whose perspective to score from
     * @return a score where higher values favour {@code player}
     */
    double eval(GAME game, int player);
}
