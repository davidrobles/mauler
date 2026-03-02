package net.davidrobles.mauler.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all Mauler games, providing the {@link MoveObservable} observer pattern
 * so that GUI components and other listeners can react to moves in real time.
 *
 * <p>Concrete games should extend this class with themselves as the type parameter:
 * <pre>{@code
 * public class TicTacToe extends AbstractGame<TicTacToe> {
 *     // implement the remaining Game<TicTacToe> methods
 * }
 * }</pre>
 *
 * <p>To notify registered observers after a move is made, call {@link #notifyMoveObservers()}
 * at the end of your {@code makeMove()} implementation:
 * <pre>{@code
 * public void makeMove(int move) {
 *     // ... apply the move ...
 *     notifyMoveObservers();
 * }
 * }</pre>
 *
 * @param <GAME> the concrete game type (self-referential generic, same as in {@link Game})
 *
 * @see Game
 * @see MoveObservable
 * @see MoveObserver
 */
public abstract class AbstractGame<GAME extends Game<GAME>> implements Game<GAME>, MoveObservable
{
    private final List<MoveObserver> observers = new ArrayList<>();

    /**
     * Registers an observer to be notified whenever a move is made.
     *
     * @param observer the observer to register
     */
    @Override
    public void registerMoveObserver(MoveObserver observer)
    {
        observers.add(observer);
    }

    /**
     * Removes a previously registered observer.
     *
     * @param observer the observer to remove
     */
    @Override
    public void removeMoveObserver(MoveObserver observer)
    {
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers that the game state has changed.
     * Call this at the end of {@code makeMove()} in your concrete implementation.
     */
    @Override
    public void notifyMoveObservers()
    {
        for (MoveObserver observer : observers)
            observer.moveUpdate();
    }
}
