package net.davidrobles.mauler.strategies;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.GameResult;

/**
 * Terminal utility function that maps a finished game's outcome to a numeric score.
 *
 * <p>Only evaluates terminal positions ({@link Game#isOver()} must be {@code true});
 * calling {@link #evaluate} on a non-terminal state throws {@link IllegalStateException}.
 *
 * <p>Use the static factories for the two standard conventions:
 * <pre>
 *   TerminalEvaluator.minimax()  // win=+1,  loss=-1, draw=0    (minimax / alpha-beta)
 *   TerminalEvaluator.mcts()     // win=+1,  loss=0,  draw=0.5  (MCTS score accumulation)
 * </pre>
 *
 * @param <GAME> the game type
 */
public class TerminalEvaluator<GAME extends Game<GAME>> implements Evaluator<GAME>
{
    private final double win;
    private final double loss;
    private final double draw;

    /**
     * Creates a terminal evaluator with the standard minimax scores:
     * win = +1.0, loss = -1.0, draw = 0.0.
     */
    public TerminalEvaluator()
    {
        this(1.0, -1.0, 0.0);
    }

    /**
     * Creates a terminal evaluator with custom scores.
     *
     * @param win  score returned for a win
     * @param loss score returned for a loss
     * @param draw score returned for a draw
     */
    public TerminalEvaluator(double win, double loss, double draw)
    {
        this.win  = win;
        this.loss = loss;
        this.draw = draw;
    }

    // -------------------------------------------------------------------------
    // Static factories
    // -------------------------------------------------------------------------

    /**
     * Returns an evaluator using standard minimax scores: win=+1, loss=-1, draw=0.
     */
    public static <G extends Game<G>> TerminalEvaluator<G> minimax()
    {
        return new TerminalEvaluator<>();
    }

    /**
     * Returns an evaluator using MCTS scores: win=1, loss=0, draw=0.5.
     * Scores accumulate as fractions in [0, 1].
     */
    public static <G extends Game<G>> TerminalEvaluator<G> mcts()
    {
        return new TerminalEvaluator<>(1.0, 0.0, 0.5);
    }

    // -------------------------------------------------------------------------
    // Evaluator
    // -------------------------------------------------------------------------

    /**
     * Returns the score for {@code player} based on the terminal outcome.
     *
     * @param game   a finished game ({@link Game#isOver()} must be {@code true})
     * @param player the player index to score
     * @return {@code win}, {@code loss}, or {@code draw} as configured
     * @throws IllegalStateException if the game is not over
     */
    @Override
    public double evaluate(GAME game, int player)
    {
        if (!game.isOver())
            throw new IllegalStateException("TerminalEvaluator called on a non-terminal position");

        GameResult result = game.getOutcome().orElseThrow()[player];

        switch (result)
        {
            case WIN:  return win;
            case LOSS: return loss;
            case DRAW: return draw;
            default:   throw new IllegalStateException("Unknown GameResult: " + result);
        }
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return String.format("TerminalEvaluator(win=%.1f, loss=%.1f, draw=%.1f)", win, loss, draw);
    }
}
