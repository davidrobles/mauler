package net.davidrobles.mauler.strategies.minimax;

import net.davidrobles.mauler.core.Game;

/**
 * Iterative deepening for {@link DepthLimitedSearch} implementations.
 *
 * <p>Runs the underlying search at increasing depth limits (1, 2, 3, …) until
 * the time budget is exhausted, returning the best move found in the last
 * <em>completed</em> iteration. Because each iteration subsumes the previous
 * one, the result is at least as good as a fixed-depth search at the deepest
 * completed level.
 *
 * <p>Move 0 is returned as a safe fallback when no iteration completes within
 * the budget — the caller guarantees the game is not over, so move 0 is always
 * legal.
 *
 * <p>Utility class — not instantiable.
 */
public final class IterativeDeepening
{
    private IterativeDeepening() {}

    /**
     * Runs iterative deepening and returns the best move found within the
     * time budget.
     *
     * @param game    the current game state (must not be over)
     * @param search  the depth-limited search to deepen
     * @param timeout the time budget in milliseconds
     * @return the index of the best move found
     */
    public static <GAME extends Game<GAME>> int move(GAME game, DepthLimitedSearch<GAME> search, int timeout)
    {
        if (game.getNumMoves() == 1)
            return 0;

        int bestMove = 0;
        int depth = 1;
        long deadline = System.currentTimeMillis() + timeout;

        while (System.currentTimeMillis() < deadline)
        {
            int move = search.move(depth++, game);

            if (System.currentTimeMillis() < deadline)
                bestMove = move;
            else
                break;
        }

        return bestMove;
    }
}
