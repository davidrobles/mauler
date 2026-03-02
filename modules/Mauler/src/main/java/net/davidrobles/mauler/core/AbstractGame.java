package net.davidrobles.mauler.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGame implements MoveObservable
{
    private List<MoveObserver> observers = new ArrayList<MoveObserver>();

    @Override
    public void registerMoveObserver(MoveObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeMoveObserver(MoveObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyMoveObservers() {
        for (MoveObserver observer : observers)
            observer.moveUpdate();
    }
}
