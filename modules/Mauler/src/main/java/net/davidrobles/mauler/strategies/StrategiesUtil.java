package net.davidrobles.mauler.strategies;

import net.davidrobles.mauler.core.Game;

import java.util.Random;

/**
 * Static utility methods for move selection and position evaluation.
 */
public final class StrategiesUtil
{
    private StrategiesUtil() {}

    // -------------------------------------------------------------------------
    // Move selection
    // -------------------------------------------------------------------------

    /**
     * Selects a move using the ε-greedy policy: greedy with probability
     * {@code (1 - epsilon)}, uniformly random with probability {@code epsilon}.
     *
     * @param game     the current game state
     * @param evalFunc evaluation function for greedy selection
     * @param epsilon  exploration probability in {@code [0.0, 1.0]}
     * @param rng      random source for exploration decisions
     * @return a legal move index in {@code [0, game.getNumMoves())}
     */
    public static <GAME extends Game<GAME>> int epsilonGreedyMove(
            GAME game, Evaluator<GAME> evalFunc, double epsilon, Random rng)
    {
        if (rng.nextDouble() > epsilon)
            return greedyMove(game, evalFunc);

        return rng.nextInt(game.getNumMoves());
    }

    /**
     * Selects a move using the ε-greedy policy with a fresh {@link Random}.
     *
     * @param game     the current game state
     * @param evalFunc evaluation function for greedy selection
     * @param epsilon  exploration probability in {@code [0.0, 1.0]}
     * @return a legal move index in {@code [0, game.getNumMoves())}
     */
    public static <GAME extends Game<GAME>> int epsilonGreedyMove(
            GAME game, Evaluator<GAME> evalFunc, double epsilon)
    {
        return epsilonGreedyMove(game, evalFunc, epsilon, new Random());
    }

    /**
     * Selects the move leading to the highest-scored successor state.
     * Ties are broken in favor of the first move encountered.
     *
     * @param game     the current game state
     * @param evalFunc evaluation function to score successor positions
     * @return a legal move index in {@code [0, game.getNumMoves())}
     */
    public static <GAME extends Game<GAME>> int greedyMove(GAME game, Evaluator<GAME> evalFunc)
    {
        int numMoves = game.getNumMoves();
        if (numMoves == 1)
            return 0;

        double bestValue = Double.NEGATIVE_INFINITY;
        int bestMove = 0;

        for (int move = 0; move < numMoves; move++)
        {
            GAME copy = game.copy();
            copy.makeMove(move);
            double value = evalFunc.evaluate(copy, game.getCurPlayer());

            if (value > bestValue)
            {
                bestValue = value;
                bestMove  = move;
            }
        }

        return bestMove;
    }

    // -------------------------------------------------------------------------
    // Evaluation
    // -------------------------------------------------------------------------

    /**
     * Returns a numeric score for {@code player} in a finished game using
     * caller-supplied win/loss/draw values.
     *
     * @param game   a finished game ({@link Game#isOver()} must be {@code true})
     * @param player the player index to score
     * @param win    score for a win
     * @param loss   score for a loss
     * @param draw   score for a draw
     * @return the appropriate score
     * @throws IllegalStateException if the game is not over, or the outcome is unexpected
     */
    public static <GAME extends Game<GAME>> double utility(
            GAME game, int player, double win, double loss, double draw)
    {
        if (!game.isOver())
            throw new IllegalStateException("utility() called on a non-terminal position");

        switch (game.getOutcome().orElseThrow()[player])
        {
            case WIN:  return win;
            case LOSS: return loss;
            case DRAW: return draw;
            default:   throw new IllegalStateException("Unknown GameResult: " + game.getOutcome().orElseThrow()[player]);
        }
    }
}
