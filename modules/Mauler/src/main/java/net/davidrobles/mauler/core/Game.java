package net.davidrobles.mauler.core;

/**
 * Core interface for two-player (and n-player) deterministic board games in the Mauler framework.
 *
 * <p>Games are self-referential generics — {@code GAME extends Game<GAME>} — so that methods like
 * {@link #copy()} and {@link #newInstance()} return the concrete type rather than the raw interface,
 * enabling type-safe use with {@link net.davidrobles.mauler.players.Player Player} and the
 * tournament infrastructure ({@code Match}, {@code Series}, {@code RoundRobin}).
 *
 * <p>The move model is index-based: {@link #getNumMoves()} returns how many legal moves exist, and
 * {@link #makeMove(int)} accepts an index in {@code [0, getNumMoves())}. {@link #getMoves()} returns
 * the same moves as human-readable strings for logging and display purposes.
 *
 * <p>Typical game loop:
 * <pre>{@code
 * while (!game.isOver()) {
 *     int move = player.move(game);   // 0 <= move < game.getNumMoves()
 *     game.makeMove(move);
 * }
 * Outcome[] outcomes = game.getOutcome();
 * }</pre>
 *
 * <p>Implementations should also override {@code equals()} and {@code hashCode()} based on the
 * full game state, which is required for correctness of {@link #copy()} in tests and tree search.
 *
 * @param <GAME> the concrete game type (self-referential generic)
 *
 * @see net.davidrobles.mauler.core.AbstractGame
 * @see net.davidrobles.mauler.core.Outcome
 * @see net.davidrobles.mauler.players.Player
 */
public interface Game<GAME extends Game<GAME>>
{
    /**
     * Returns a deep copy of the current game state. The copy is fully independent —
     * moves made on the copy do not affect the original, and vice versa.
     *
     * <p>Used extensively by AI players to simulate future positions without
     * modifying the live game.
     *
     * @return a deep copy of this game
     */
    GAME copy();

    /**
     * Returns the index of the player whose turn it is. Player indices start at {@code 0}.
     *
     * @return the current player index
     */
    int getCurPlayer();

    /**
     * Returns the legal moves for the current player as human-readable strings,
     * using a game-specific notation (e.g. {@code "e4"}, {@code "a1b2"}).
     *
     * <p>The array is parallel to the index space of {@link #makeMove(int)}: index {@code i}
     * in this array corresponds to passing {@code i} to {@code makeMove}.
     *
     * <p>Returns an empty array when the game is over.
     *
     * @return the legal moves as strings, in the same order as their indices
     */
    String[] getMoves();

    /**
     * Returns the number of legal moves available to the current player.
     * Equivalent to {@code getMoves().length} but may be faster since it avoids
     * allocating the string array.
     *
     * <p>Returns {@code 0} when the game is over.
     *
     * @return the number of legal moves
     */
    int getNumMoves();

    /**
     * Returns the display name of the game (e.g. {@code "Tic-tac-toe"}, {@code "Othello"}).
     *
     * @return the game name
     */
    String getName();

    /**
     * Returns the number of players in this game.
     *
     * @return the number of players
     */
    int getNumPlayers();

    /**
     * Returns the outcome for each player once the game is over. The array length equals
     * {@link #getNumPlayers()}, and {@code getOutcome()[i]} gives the result for player {@code i}.
     *
     * <p>Each entry is one of:
     * <ul>
     *   <li>{@link Outcome#WIN}  — player {@code i} won</li>
     *   <li>{@link Outcome#LOSS} — player {@code i} lost</li>
     *   <li>{@link Outcome#DRAW} — the game ended in a draw</li>
     *   <li>{@link Outcome#NA}   — game is still in progress</li>
     * </ul>
     *
     * @return the outcome array, indexed by player
     */
    Outcome[] getOutcome();

    /**
     * Returns {@code true} if the game has ended (no legal moves remain, or a terminal
     * condition has been reached).
     *
     * @return {@code true} if the game is over
     */
    boolean isOver();

    /**
     * Applies the move at the given index for the current player, advancing the game state.
     * The valid range is {@code [0, getNumMoves())}; implementations should throw
     * {@link IllegalArgumentException} for out-of-range values.
     *
     * @param move the index of the move to apply
     * @throws IllegalArgumentException if {@code move} is out of range
     */
    void makeMove(int move);

    /**
     * Creates a fresh instance of this game type in its initial state, equivalent to
     * constructing a new game with default parameters.
     *
     * <p>Useful when the concrete type is only known through the generic parameter,
     * such as in tournament runners.
     *
     * @return a new game instance in its initial state
     */
    GAME newInstance();

    /**
     * Resets the game to its initial state, as if it were just constructed.
     */
    void reset();
}
