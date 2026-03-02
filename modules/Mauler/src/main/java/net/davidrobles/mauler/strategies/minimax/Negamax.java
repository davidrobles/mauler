package net.davidrobles.mauler.strategies.minimax;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;

/**
 * Negamax search with a fixed depth limit.
 *
 * <p>A simplification of {@link Minimax} that exploits the zero-sum property:
 * the score for the current player is always the negation of the score for the
 * opponent, so a single maximisation pass suffices at every node.
 *
 * <p>The {@link Evaluator} must return scores <em>from the perspective of the
 * player to move</em> at the node being evaluated: positive means good for
 * that player, negative means bad.
 *
 * <p>Supports iterative deepening via {@link DepthLimitedSearch}: pass a
 * per-move timeout to {@link #move(Game, int)} and the search will deepen
 * until the budget expires, returning the best move found so far.
 *
 * @param <GAME> the game type
 */
public class Negamax<GAME extends Game<GAME>> implements DepthLimitedSearch<GAME>
{
    private final Evaluator<GAME> evaluator;
    private final int maxDepth;

    /**
     * Creates a Negamax player that searches to terminal positions.
     *
     * @param evaluator the evaluation function for leaf nodes
     */
    public Negamax(Evaluator<GAME> evaluator)
    {
        this(evaluator, Integer.MAX_VALUE);
    }

    /**
     * Creates a Negamax player with a fixed depth limit.
     *
     * @param evaluator the evaluation function for leaf nodes
     * @param maxDepth  the maximum search depth in plies
     */
    public Negamax(Evaluator<GAME> evaluator, int maxDepth)
    {
        this.evaluator = evaluator;
        this.maxDepth = maxDepth;
    }

    private MoveScore negamax(GAME game, int depth, int depthLimit)
    {
        if (game.isOver() || depth == depthLimit)
            return new MoveScore(evaluator.evaluate(game, game.getCurPlayer()), -1);

        double bestScore = Double.NEGATIVE_INFINITY;
        int bestMove = -1;

        for (int move = 0; move < game.getNumMoves(); move++)
        {
            GAME child = game.copy();
            child.makeMove(move);
            double score = -negamax(child, depth + 1, depthLimit).getScore();

            if (score > bestScore)
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
        return negamax(game, 0, depthLimit).getMove();
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
        return negamax(game, 0, maxDepth).getMove();
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
        return String.format("Negamax(depth=%d, evaluator=%s)", maxDepth, evaluator);
    }
}
