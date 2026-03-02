package net.davidrobles.mauler.core;

/**
 * Games that implement this interface accept listeners. These listeners
 * and registers
 *
 */
public interface MoveObservable
{
    void registerMoveObserver(MoveObserver observer);

    void removeMoveObserver(MoveObserver observer);

    /**
     * Notifies the registered listeners of changes in
     * the state of the game (i.e. move made).
     */
    void notifyMoveObservers();
}
