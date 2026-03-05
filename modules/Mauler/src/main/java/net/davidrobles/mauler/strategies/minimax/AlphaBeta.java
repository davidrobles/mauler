package net.davidrobles.mauler.strategies.minimax;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;

/**
 * Alpha-beta pruning search with a fixed depth limit.
 *
 * <p>Produces identical results to {@link Minimax} but prunes branches that cannot influence the
 * outcome, visiting far fewer nodes in practice.
 *
 * <p>Implemented as negamax with alpha-beta: scores are always from the perspective of the player
 * to move, negated when returning to the parent. The {@link Evaluator} must therefore return scores
 * relative to the current player (positive = good, negative = bad).
 *
 * <p>Supports iterative deepening via {@link DepthLimitedSearch}: pass a per-move timeout to {@link
 * #move(Game, int)} and the search will deepen until the budget expires, returning the best move
 * found so far.
 *
 * @param <GAME> the game type
 */
public class AlphaBeta<GAME extends Game<GAME>> implements DepthLimitedSearch<GAME> {
    private final Evaluator<GAME> evaluator;
    private final int maxDepth;

    /**
     * Creates an Alpha-Beta player that searches to terminal positions.
     *
     * @param evaluator the evaluation function for leaf nodes
     */
    public AlphaBeta(Evaluator<GAME> evaluator) {
        this(evaluator, Integer.MAX_VALUE);
    }

    /**
     * Creates an Alpha-Beta player with a fixed depth limit.
     *
     * @param evaluator the evaluation function for leaf nodes
     * @param maxDepth the maximum search depth in plies
     */
    public AlphaBeta(Evaluator<GAME> evaluator, int maxDepth) {
        this.evaluator = evaluator;
        this.maxDepth = maxDepth;
    }

    private MoveScore search(GAME game, int depth, int depthLimit, double alpha, double beta) {
        if (game.isOver() || depth == depthLimit)
            return new MoveScore(evaluator.evaluate(game, game.getCurPlayer()), -1);

        double bestScore = Double.NEGATIVE_INFINITY;
        int bestMove = -1;

        for (int move = 0; move < game.getNumMoves(); move++) {
            GAME child = game.copy();
            child.makeMove(move);
            double score =
                    -search(child, depth + 1, depthLimit, -beta, -Math.max(alpha, bestScore))
                            .getScore();

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;

                if (bestScore >= beta) return new MoveScore(bestScore, bestMove);
            }
        }

        return new MoveScore(bestScore, bestMove);
    }

    ////////////////////////
    // DepthLimitedSearch //
    ////////////////////////

    @Override
    public int move(int depthLimit, GAME game) {
        return search(game, 0, depthLimit, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
                .getMove();
    }

    ///////////////
    // Strategy  //
    ///////////////

    @Override
    public boolean isDeterministic() {
        return true;
    }

    @Override
    public int move(GAME game) {
        return search(game, 0, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
                .getMove();
    }

    @Override
    public int move(GAME game, int timeout) {
        return IterativeDeepening.move(game, this, timeout);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString() {
        return String.format("AlphaBeta(depth=%d, evaluator=%s)", maxDepth, evaluator);
    }
}
