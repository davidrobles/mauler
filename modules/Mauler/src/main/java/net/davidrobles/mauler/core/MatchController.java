package net.davidrobles.mauler.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Step-through controller for a two-player game with a full move history.
 *
 * <p>Maintains a list of game states from the initial position to the latest computed ply. Supports
 * forward navigation (computing new moves via the registered strategies), backward navigation
 * (replaying already-computed states), and jumping to either end of the history.
 *
 * <p>All public methods are {@code synchronized}. Observers are notified on the calling thread
 * after each state change.
 *
 * @param <GAME> the game type (must also implement {@link MoveObservable})
 */
public class MatchController<GAME extends Game<GAME> & MoveObservable> {
    private final Supplier<GAME> gameFactory;
    private final List<Strategy<GAME>> strategies;
    private final int timeout;
    private final List<MatchControllerObserver<GAME>> observers = new ArrayList<>();
    private final List<GAME> gameHistory = new ArrayList<>();
    private final List<String> moveHistory = new ArrayList<>();
    private int currentIndex = 0;

    public MatchController(
            Supplier<GAME> gameFactory, List<Strategy<GAME>> strategies, int timeout) {
        GAME prototype = gameFactory.get();
        if (strategies.size() != prototype.getNumPlayers())
            throw new IllegalArgumentException(
                    "Expected "
                            + prototype.getNumPlayers()
                            + " strategies, got "
                            + strategies.size());

        this.gameFactory = gameFactory;
        this.strategies = strategies;
        this.timeout = timeout;
        reset();
    }

    // -------------------------------------------------------------------------
    // Navigation state
    // -------------------------------------------------------------------------

    /** Returns {@code true} if there is a previous state to navigate to. */
    public synchronized boolean isPrev() {
        return currentIndex > 0;
    }

    /**
     * Returns {@code true} if there is already-computed history ahead of the current position (i.e.
     * the End button should be enabled).
     */
    public synchronized boolean canGoToEnd() {
        return currentIndex < gameHistory.size() - 1;
    }

    /**
     * Returns {@code true} if it is possible to advance — either by replaying an existing state or
     * by computing a new move.
     */
    public synchronized boolean isNext() {
        return currentIndex != gameHistory.size() - 1
                || !gameHistory.get(gameHistory.size() - 1).isOver();
    }

    /** Returns {@code true} if the last computed state is a terminal game state. */
    public synchronized boolean isOver() {
        return gameHistory.get(gameHistory.size() - 1).isOver();
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    public synchronized void reset() {
        currentIndex = 0;
        moveHistory.clear();
        gameHistory.clear();
        gameHistory.add(gameFactory.get());
        notifyObservers();
    }

    public synchronized void start() {
        if (isPrev()) {
            currentIndex = 0;
            notifyObservers();
        }
    }

    public synchronized void prev() {
        if (isPrev()) {
            currentIndex--;
            notifyObservers();
        }
    }

    public synchronized void next() {
        if (!isNext()) return;

        if (currentIndex == gameHistory.size() - 1) {
            // At the latest position: ask the strategy for a move and extend history.
            GAME state = gameHistory.get(currentIndex).copy();
            int moveIndex = strategies.get(state.getCurPlayer()).move(state, timeout);
            moveHistory.add(state.getMoves().get(moveIndex));
            state.makeMove(moveIndex);
            gameHistory.add(state);
        }

        currentIndex++;
        notifyObservers();
    }

    public synchronized void end() {
        if (canGoToEnd()) {
            currentIndex = gameHistory.size() - 1;
            notifyObservers();
        }
    }

    public synchronized void playToEnd() {
        while (isNext()) next();
    }

    public synchronized void navigateTo(int index) {
        currentIndex = index;
        notifyObservers();
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns the game state at the current navigation position. */
    public synchronized GAME getGame() {
        return gameHistory.get(currentIndex);
    }

    /** Returns the game state at the given ply. */
    public synchronized GAME getGame(int ply) {
        return gameHistory.get(ply);
    }

    /** Returns the move string recorded at the given ply. */
    public synchronized String getMove(int ply) {
        return moveHistory.get(ply);
    }

    /** Returns the total number of game states in the history (initial position + plies played). */
    public synchronized int getSize() {
        return gameHistory.size();
    }

    /** Returns the index of the currently displayed game state. */
    public synchronized int getCurrentIndex() {
        return currentIndex;
    }

    // -------------------------------------------------------------------------
    // Observers
    // -------------------------------------------------------------------------

    public synchronized void registerObserver(MatchControllerObserver<GAME> observer) {
        observers.add(observer);
    }

    public synchronized void removeObserver(MatchControllerObserver<GAME> observer) {
        observers.remove(observer);
    }

    public synchronized void notifyObservers() {
        GAME current = gameHistory.get(currentIndex);
        for (MatchControllerObserver<GAME> observer : observers) observer.update(current);
    }
}
