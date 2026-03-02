package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

/**
 * Terminal utility function that maps a finished game's outcome to a numeric score.
 *
 * <p>Only evaluates terminal positions ({@link Game#isOver()} must be {@code true});
 * calling {@link #eval} on a non-terminal state throws {@link IllegalStateException}.
 *
 * <p>The no-arg constructor uses the standard minimax convention:
 * <pre>
 *   win = +1.0,  draw = 0.0,  loss = -1.0
 * </pre>
 *
 * For MCTS, where scores accumulate as fractions in {@code [0, 1]}, construct with:
 * <pre>
 *   new UtilFunc(1.0, 0.0, 0.5)   // win=1.0, loss=0.0, draw=0.5
 * </pre>
 *
 * @param <GAME> the game type
 */
public class UtilFunc<GAME extends Game<GAME>> implements EvalFunc<GAME>
{
    private final double win;
    private final double loss;
    private final double draw;

    /**
     * Creates a utility function with the standard minimax scores:
     * win = +1.0, draw = 0.0, loss = -1.0.
     */
    public UtilFunc()
    {
        this(1.0, -1.0, 0.0);
    }

    /**
     * Creates a utility function with custom scores.
     *
     * @param win  score for a win
     * @param loss score for a loss
     * @param draw score for a draw
     */
    public UtilFunc(double win, double loss, double draw)
    {
        this.win  = win;
        this.loss = loss;
        this.draw = draw;
    }

    // -------------------------------------------------------------------------
    // EvalFunc
    // -------------------------------------------------------------------------

    /**
     * Returns the score for {@code player} based on the terminal outcome.
     *
     * @param game   a finished game ({@link Game#isOver()} must be {@code true})
     * @param player the player index to score
     * @return {@code win}, {@code loss}, or {@code draw} as configured
     * @throws IllegalStateException if the game is not over, or the outcome is unexpected
     */
    @Override
    public double eval(GAME game, int player)
    {
        if (!game.isOver())
            throw new IllegalStateException("UtilFunc.eval() called on a non-terminal position");

        switch (game.getOutcome()[player])
        {
            case WIN:  return win;
            case LOSS: return loss;
            case DRAW: return draw;
            default:   throw new IllegalStateException("Unexpected outcome NA for a finished game");
        }
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return String.format("<UtilFunc win=%.1f loss=%.1f draw=%.1f>", win, loss, draw);
    }
}
