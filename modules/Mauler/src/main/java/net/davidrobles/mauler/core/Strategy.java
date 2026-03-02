package net.davidrobles.mauler.core;

/**
 * A game-playing agent that selects a move from the current game state.
 *
 * <p>Move selection is index-based: {@link #move(Game)} returns an integer in
 * {@code [0, game.getNumMoves())} which is passed directly to
 * {@link Game#makeMove(int)}.
 *
 * <p>Two move methods are provided:
 * <ul>
 *   <li>{@link #move(Game)} — unlimited thinking time (simulation count, fixed depth, etc.)</li>
 *   <li>{@link #move(Game, int)} — bounded by a per-move wall-clock timeout in milliseconds.
 *       The default implementation delegates to {@link #move(Game)};
 *       override it in players that actually use anytime search.</li>
 * </ul>
 *
 * <p>{@link #isDeterministic()} is used by {@link net.davidrobles.mauler.core.Match Match}
 * to decide whether to inject occasional random moves when both players are deterministic
 * — without that injection, two deterministic players would replay the exact same game
 * in every match of a series, making multi-game statistics meaningless.
 *
 * @param <GAME> the game type
 *
 * @see net.davidrobles.mauler.core.Match
 * @see net.davidrobles.mauler.core.Series
 */
public interface Strategy<GAME extends Game<GAME>>
{
    /**
     * Returns {@code true} if this player always makes the same move in the same
     * position (no randomness, no time-dependent search).
     *
     * <p>Used by {@link net.davidrobles.mauler.core.Match} to detect when both
     * players are deterministic and inject random moves to diversify the game sample.
     *
     * @return {@code true} if the player is deterministic
     */
    boolean isDeterministic();

    /**
     * Selects a move for the current position with no time limit.
     *
     * @param game the current game state (must not be over)
     * @return a legal move index in {@code [0, game.getNumMoves())}
     */
    int move(GAME game);

    /**
     * Selects a move for the current position within the given time budget.
     *
     * <p>The default implementation ignores the timeout and delegates to
     * {@link #move(Game)}. Override this method in players that support
     * anytime search (iterative deepening, time-bounded Monte Carlo, etc.).
     *
     * @param game    the current game state (must not be over)
     * @param timeout per-move time limit in milliseconds (positive)
     * @return a legal move index in {@code [0, game.getNumMoves())}
     */
    default int move(GAME game, int timeout)
    {
        return move(game);
    }
}
