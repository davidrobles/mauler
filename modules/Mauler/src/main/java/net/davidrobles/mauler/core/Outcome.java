package net.davidrobles.mauler.core;

/**
 * The result of a finished game from one player's perspective.
 *
 * <p>Each value of {@link Game#getOutcome()} is one of these constants.
 * {@code NA} is a sentinel for games still in progress; the other three
 * represent terminal states.
 *
 * <p>Utility methods:
 * <ul>
 *   <li>{@link #flip()} — converts between WIN and LOSS (identity for DRAW/NA)</li>
 *   <li>{@link #toScore()} — maps to the standard MCTS score: WIN=1.0, DRAW=0.5, LOSS=0.0</li>
 *   <li>{@link #isTerminal()} — {@code true} for WIN, LOSS, DRAW; {@code false} for NA</li>
 * </ul>
 */
public enum Outcome
{
    /** The player won. */
    WIN,

    /** The player lost. */
    LOSS,

    /** The game ended in a draw. */
    DRAW,

    /** The game has not ended yet (not applicable). */
    NA;

    /**
     * Returns the opposite outcome: WIN↔LOSS. DRAW and NA are returned unchanged.
     *
     * @return the flipped outcome
     */
    public Outcome flip()
    {
        switch (this) {
            case WIN:  return LOSS;
            case LOSS: return WIN;
            default:   return this;
        }
    }

    /**
     * Maps this outcome to a numeric score using the standard MCTS convention:
     * WIN → 1.0, DRAW → 0.5, LOSS → 0.0.
     *
     * @return the numeric score in [0.0, 1.0]
     * @throws IllegalStateException if called on {@link #NA}
     */
    public double toScore()
    {
        switch (this) {
            case WIN:  return 1.0;
            case DRAW: return 0.5;
            case LOSS: return 0.0;
            default:   throw new IllegalStateException("toScore() called on NA");
        }
    }

    /**
     * Returns {@code true} if this outcome represents a finished game
     * (WIN, LOSS, or DRAW), {@code false} for {@link #NA}.
     *
     * @return {@code true} if the game is over
     */
    public boolean isTerminal()
    {
        return this != NA;
    }
}
