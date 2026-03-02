package net.davidrobles.mauler.core;

import java.util.List;
import java.util.Optional;

/**
 * Core interface for two-player (and n-player) deterministic board games in the Mauler framework.
 *
 * <p>Games are self-referential generics — {@code GAME extends Game<GAME>} — so that methods like
 * {@link #copy()} returns the concrete type rather than the raw interface,
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
 * GameResult[] outcomes = game.getOutcome().orElseThrow();
 * }</pre>
 *
 * <p>Implementations should also override {@code equals()} and {@code hashCode()} based on the
 * full game state, which is required for correctness of {@link #copy()} in tests and tree search.
 *
 * @param <GAME> the concrete game type (self-referential generic)
 *
 * @see net.davidrobles.mauler.core.ObservableGame
 * @see net.davidrobles.mauler.core.GameResult
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
     * <p>The list is parallel to the index space of {@link #makeMove(int)}: index {@code i}
     * in this list corresponds to passing {@code i} to {@code makeMove}.
     *
     * <p>Returns an empty list when the game is over. The returned list is unmodifiable.
     *
     * @return the legal moves as strings, in the same order as their indices
     */
    List<String> getMoves();

    /**
     * Returns the number of legal moves available to the current player.
     * Equivalent to {@code getMoves().length} but may be faster since it avoids
     * allocating the string array.
     *
     * <p>Returns {@code 0} when the game is over.
     *
     * @return the number of legal moves
     */
    default int getNumMoves()
    {
        return getMoves().size();
    }

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
     * Returns the outcome for each player once the game is over, or an empty
     * {@link Optional} if the game is still in progress.
     *
     * <p>When present, the array length equals {@link #getNumPlayers()}, and
     * {@code getOutcome().orElseThrow()[i]} gives the result for player {@code i}.
     * Each entry is one of {@link GameResult#WIN}, {@link GameResult#LOSS}, or {@link GameResult#DRAW}.
     *
     * @return an {@code Optional} containing the outcome array when the game is over,
     *         or {@link Optional#empty()} if the game is still in progress
     */
    Optional<GameResult[]> getOutcome();

    /**
     * Returns {@code true} if the game has ended (no legal moves remain, or a terminal
     * condition has been reached).
     *
     * <p>The default implementation delegates to {@link #getOutcome()}: returns {@code true}
     * when an outcome is present. Implementations with a cheap terminal flag (e.g. a cached
     * {@code gameOver} boolean) should override this for performance.
     *
     * @return {@code true} if the game is over
     */
    default boolean isOver()
    {
        return getOutcome().isPresent();
    }

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
     * Resets the game to its initial state, as if it were just constructed.
     */
    void reset();
}
