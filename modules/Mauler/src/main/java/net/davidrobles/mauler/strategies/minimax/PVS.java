package net.davidrobles.mauler.strategies.minimax;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;

/**
 * Principal Variation Search (PVS / NegaScout) with a fixed depth limit.
 *
 * <p>PVS is a refinement of alpha-beta that exploits the assumption that the
 * first child is the best move (the <em>principal variation</em>):
 * <ol>
 *   <li>The first child is searched with the full {@code [alpha, beta]} window.</li>
 *   <li>Every subsequent child is probed with a zero-width <em>null window</em>
 *       {@code [alpha, alpha+1]}. A null-window search is fast because it cuts
 *       off as soon as it finds any move that beats alpha.</li>
 *   <li>If the probe raises alpha (scout fails high, {@code score > alpha}),
 *       the child is re-searched with the full window to get the exact value.</li>
 * </ol>
 *
 * <p>When move ordering is good the vast majority of nodes require only the
 * cheap null-window probe, making PVS strictly faster than plain alpha-beta
 * in practice while producing identical results.
 *
 * <p>The {@link Evaluator} must return scores from the perspective of the
 * player to move (positive = good, negative = bad).
 *
 * <p>Supports iterative deepening via {@link DepthLimitedSearch}: pass a
 * per-move timeout to {@link #move(Game, int)} and the search will deepen
 * until the budget expires, returning the best move found so far.
 *
 * @param <GAME> the game type
 */
public class PVS<GAME extends Game<GAME>> implements DepthLimitedSearch<GAME>
{
    private final Evaluator<GAME> evaluator;
    private final int maxDepth;

    /**
     * Creates a PVS player that searches to terminal positions.
     *
     * @param evaluator the evaluation function for leaf nodes
     */
    public PVS(Evaluator<GAME> evaluator)
    {
        this(evaluator, Integer.MAX_VALUE);
    }

    /**
     * Creates a PVS player with a fixed depth limit.
     *
     * @param evaluator the evaluation function for leaf nodes
     * @param maxDepth  the maximum search depth in plies
     */
    public PVS(Evaluator<GAME> evaluator, int maxDepth)
    {
        this.evaluator = evaluator;
        this.maxDepth  = maxDepth;
    }

    private MoveScore search(GAME game, int depth, int depthLimit, double alpha, double beta)
    {
        if (game.isOver() || depth == depthLimit)
            return new MoveScore(evaluator.evaluate(game, game.getCurPlayer()), -1);

        double bestScore = Double.NEGATIVE_INFINITY;
        int bestMove = -1;

        for (int move = 0; move < game.getNumMoves(); move++)
        {
            GAME child = game.copy();
            child.makeMove(move);

            double score;

            if (move == 0)
            {
                // Full-window search on the principal variation candidate.
                score = -search(child, depth + 1, depthLimit, -beta, -alpha).getScore();
            }
            else
            {
                // Null-window probe: verify this move cannot beat the current best.
                score = -search(child, depth + 1, depthLimit, -(alpha + 1), -alpha).getScore();

                // Scout failed high — move may be better than expected; re-search.
                if (score > alpha && score < beta)
                    score = -search(child, depth + 1, depthLimit, -beta, -alpha).getScore();
            }

            if (score > bestScore)
            {
                bestScore = score;
                bestMove  = move;
            }

            if (bestScore > alpha)
                alpha = bestScore;

            if (bestScore >= beta)
                break;
        }

        return new MoveScore(bestScore, bestMove);
    }

    ////////////////////////
    // DepthLimitedSearch //
    ////////////////////////

    @Override
    public int move(int depthLimit, GAME game)
    {
        return search(game, 0, depthLimit, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).getMove();
    }

    ///////////////
    // Strategy  //
    ///////////////

    @Override
    public boolean isDeterministic()
    {
        return true;
    }

    @Override
    public int move(GAME game)
    {
        return search(game, 0, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).getMove();
    }

    @Override
    public int move(GAME game, int timeout)
    {
        return IterativeDeepening.move(game, this, timeout);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return String.format("PVS(depth=%d, evaluator=%s)", maxDepth, evaluator);
    }
}
