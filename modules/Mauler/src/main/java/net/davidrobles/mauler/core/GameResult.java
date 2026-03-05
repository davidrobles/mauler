package net.davidrobles.mauler.core;

/**
 * The result of a finished game from one player's perspective.
 *
 * <p>Each value in the array returned by {@link Game#getOutcome()} is one of these three constants.
 * All values represent a terminal state; the in-progress case is represented by {@link
 * java.util.Optional#empty()} at the {@code getOutcome()} level.
 *
 * <p>Utility methods:
 *
 * <ul>
 *   <li>{@link #flip()} — converts WIN↔LOSS (identity for DRAW)
 *   <li>{@link #toScore()} — maps to the standard MCTS score: WIN=1.0, DRAW=0.5, LOSS=0.0
 * </ul>
 */
public enum GameResult {
    /** The player won. */
    WIN,

    /** The player lost. */
    LOSS,

    /** The game ended in a draw. */
    DRAW;

    /**
     * Returns the opposite result: WIN↔LOSS. DRAW is returned unchanged.
     *
     * @return the flipped result
     */
    public GameResult flip() {
        switch (this) {
            case WIN:
                return LOSS;
            case LOSS:
                return WIN;
            default:
                return this;
        }
    }

    /**
     * Maps this result to a numeric score using the standard MCTS convention: WIN → 1.0, DRAW →
     * 0.5, LOSS → 0.0.
     *
     * @return the numeric score in [0.0, 1.0]
     */
    public double toScore() {
        switch (this) {
            case WIN:
                return 1.0;
            case DRAW:
                return 0.5;
            case LOSS:
                return 0.0;
            default:
                throw new IllegalStateException("Unknown GameResult: " + this);
        }
    }
}
