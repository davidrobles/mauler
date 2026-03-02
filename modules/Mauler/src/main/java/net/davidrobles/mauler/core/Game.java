package net.davidrobles.mauler.core;

public interface Game<GAME extends Game<GAME>> {

    /**
     * Returns a copy of the game.
     * @return a copy of the game
     */
    GAME copy();

    /**
     * Returns the player in turn. The player indices start from 0.
     * @return the index of the player in turn
     */
    int getCurPlayer();

    /**
     * Returns a list of the legal moves. The moves are represented as strings
     * using game specific representation. The main reason to have this
     * method is to save the history of moves made.
     * @return the legal moves
     */
    String[] getMoves();

    /**
     * Returns the number of legal moves for the player in turn.
     * @return the number of legal moves
     */
    int getNumMoves();

    /**
     * The name of the game.
     * @return name of the game
     */
    String getName();

    /**
     * The number of players of this game.
     * @return number of players
     */
    int getNumPlayers();

    /**
     * Returns an array with the outcomes for each of the players.
     * For example, if there are 4 players in the game,
     * the length of the outcomes array will be 4.
     * @return
     */
    Outcome[] getOutcome();

    /**
     * Returns true if the game is over.
     * @return true if the game is over
     */
    boolean isOver();

    /**
     * Makes a move for the player in turn.*
     * For example, if the number of moves is 5, the player in
     * turn must take a move from the set {0, 1, 2, 3, 4}. Any
     * other move is considered an illegal move.
     * @param move the move to take
     */
    void makeMove(int move);

    /**
     * Creates a new game of the same generic type of the current one.
     * @return a new game
     */
    GAME newInstance();

    /**
     * Restarts the game.
     */
    void reset();

}
