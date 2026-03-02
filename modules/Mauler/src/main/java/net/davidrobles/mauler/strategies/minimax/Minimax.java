package net.davidrobles.mauler.strategies.minimax;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;

/**
 * Minimax search with a fixed depth limit.
 *
 * <p>Explores the game tree up to {@code maxDepth} plies and evaluates leaf
 * nodes with the supplied {@link Evaluator}. At each node the current player
 * maximises their score; the opponent minimises it.
 *
 * <p>When constructed without an explicit depth limit the search runs to
 * terminal positions. Use {@link AlphaBeta} for better performance — it
 * produces identical results with far fewer node evaluations.
 *
 * <p>Supports iterative deepening via {@link DepthLimitedSearch}: pass a
 * per-move timeout to {@link #move(Game, int)} and the search will deepen
 * until the budget expires, returning the best move found so far.
 *
 * @param <GAME> the game type
 */
public class Minimax<GAME extends Game<GAME>> implements DepthLimitedSearch<GAME>
{
    private final Evaluator<GAME> evaluator;
    private final int maxDepth;

    /**
     * Creates a Minimax player that searches to terminal positions.
     *
     * @param evaluator the evaluation function for leaf nodes
     */
    public Minimax(Evaluator<GAME> evaluator)
    {
        this(evaluator, Integer.MAX_VALUE);
    }

    /**
     * Creates a Minimax player with a fixed depth limit.
     *
     * @param evaluator the evaluation function for leaf nodes
     * @param maxDepth  the maximum search depth in plies
     */
    public Minimax(Evaluator<GAME> evaluator, int maxDepth)
    {
        this.evaluator = evaluator;
        this.maxDepth = maxDepth;
    }

    private MoveScore minimax(GAME game, int player, int depth, int depthLimit)
    {
        if (game.isOver() || depth == depthLimit)
            return new MoveScore(evaluator.evaluate(game, player), -1);

        boolean maximising = game.getCurPlayer() == player;
        double bestScore = maximising ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        int bestMove = -1;

        for (int move = 0; move < game.getNumMoves(); move++)
        {
            GAME child = game.copy();
            child.makeMove(move);
            double score = minimax(child, player, depth + 1, depthLimit).getScore();

            if (maximising ? score > bestScore : score < bestScore)
            {
                bestScore = score;
                bestMove = move;
            }
        }

        return new MoveScore(bestScore, bestMove);
    }

    ////////////////////////
    // DepthLimitedSearch //
    ////////////////////////

    @Override
    public int move(int depthLimit, GAME game)
    {
        return minimax(game, game.getCurPlayer(), 0, depthLimit).getMove();
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
        return minimax(game, game.getCurPlayer(), 0, maxDepth).getMove();
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
        return String.format("Minimax(depth=%d, evaluator=%s)", maxDepth, evaluator);
    }
}
